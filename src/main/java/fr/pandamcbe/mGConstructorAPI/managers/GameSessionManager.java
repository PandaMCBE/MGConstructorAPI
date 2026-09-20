package fr.pandamcbe.mGConstructorAPI.managers;

import fr.pandamcbe.commons.ProfilAccount;
import fr.pandamcbe.commons.StatsAccount;
import fr.pandamcbe.mGConstructorAPI.MGConstructorAPI;
import fr.pandamcbe.mGConstructorAPI.Team;
import fr.pandamcbe.mGConstructorAPI.arenas.Arena;
import fr.pandamcbe.mGConstructorAPI.arenas.MGArena;
import fr.pandamcbe.mGConstructorAPI.arenas.TeamArena;
import fr.pandamcbe.mgapi.Main;
import fr.pandamcbe.mgapi.core.localmemory.DirtyManager;
import fr.pandamcbe.mgapi.core.localmemory.MemAccountsManipulation;
import fr.pandamcbe.mgapi.serverutilapi.enums_strings.AccountType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class GameSessionManager extends OptionalsHooksManager {

    public List<UUID> frozens = new ArrayList<>();
    private final ArenaManager arenaManager = MGConstructorAPI.getInstance().getArenaManager();
    private final MemAccountsManipulation memoryProvider = Main.getInstance().manipulation;
    private final String serverName = Main.getInstance().serverName;
    private final DirtyManager dirtyManager = new DirtyManager();
    private final Team team = MGConstructorAPI.getInstance().getTeam();

    public void teleport(Arena nextArena) {
        if (nextArena != null) { //Si l'arène n'est pas null
            if(nextArena instanceof MGArena) {
                MGArena mgArena = (MGArena) nextArena;
                for (int i = 0; i < mgArena.getAlluuids().size(); i++) { //Pour chaque élément contenu dans liste d'ensemble de joueur de l'arène en fonction de sa taille
                    UUID uuid = mgArena.getAlluuids().get(i); //Initialisation d'un objet Joueur à un élément de la liste
                    Location spawn = mgArena.getSpawns().get(i); //Initialisation d'un objet Location à spawn parmi ceux qui sont présents dans la liste getSpawns, associé à l'élément de la boucle
                    Bukkit.getPlayer(uuid).teleport(spawn); //Téléporter le joueur à son spawn affilié
                }
            } else if(nextArena instanceof TeamArena){
                TeamArena teamArena = (TeamArena) nextArena;
                System.out.println("teleportation ?");
                teamArena.getTeams().forEach(team-> team.getUuidList().forEach(uuid -> Bukkit.getPlayer(uuid).teleport(teamArena.getSpawnByTeam().get(team.getColor()))));
            }
        }
    }

    public void nextRound(List<Arena> arenaList,Player winner, Map<UUID, Map<String, Object>> mainMap, int Coins, int Elos) {
        Arena arena = MGConstructorAPI.getInstance().getArenaManager().getArenaByPlayer(winner.getUniqueId(), arenaList);
        if (arena.getAlluuids().size() > 1) {
            StatsAccount statsAccount = (StatsAccount) memoryProvider.getStatsAccount(mainMap, winner.getUniqueId(), AccountType.statsKey(serverName));
            ProfilAccount profilAccount = (ProfilAccount) memoryProvider.getProfilAccount(mainMap, winner.getUniqueId(), AccountType.PROFIL.getKeyPrefix());
            profilAccount.setCoins(profilAccount.getCoins() + Coins);
            statsAccount.setElos(statsAccount.getElos()+ Elos);
            dirtyManager.markDirty(winner.getUniqueId(), AccountType.statsKey(serverName), statsAccount);
            dirtyManager.markDirty(winner.getUniqueId(), AccountType.PROFIL.getKeyPrefix(), profilAccount);
            ifLessThanMaxRounds(arena, winner);
            ifLastRound(arena);
        }
    }

    public void ifLastRound(Arena arena) {
        List<UUID> nearMaxPoints = arena.getInGamePlayers().stream().filter(uuid -> arena.getWonRounds().get(uuid) == (arena.maxRounds - 1)).collect(Collectors.toList());
        nearMaxPoints.forEach(uuid -> arena.getWonRounds().put(uuid, arena.getWonRounds().get(uuid) + 1));
        if(!nearMaxPoints.isEmpty()) {
            if (arena.getWonRounds().keySet().stream().anyMatch(uuid -> arena.getWonRounds().get(uuid) == arena.maxRounds)) {
                if (tieOpponentExists(arena)) {
                    nearMaxPoints.forEach(uuid -> arena.getWonRounds().put(uuid, arena.getWonRounds().get(uuid) - 1));
                    for (UUID uuid : arena.getInGamePlayers()) {
                        Player pls = Bukkit.getPlayer(uuid);
                        pls.sendTitle("§eEgalité !", "§6Recommencez");
                        pls.sendMessage(getPrefix() + " > §lOn vous a retiré 1 point à tous ! §dRejouez la partie et que le meilleur gagne !");
                    }
                    startCountDown(arena);
                } else {
                    Optional<UUID> winner = arena.getInGamePlayers().stream().filter(uuid -> arena.getWonRounds().get(uuid) == arena.maxRounds).findAny();
                    winner.ifPresent(uuid -> customWinEvent(Bukkit.getPlayer(uuid)));
                }
            }
        }
    }

    public void ifLessThanMaxRounds(Arena arena, Player winner) {
        if (arena.getWonRounds().get(winner.getUniqueId()) < (arena.maxRounds -1 )) { //Si le joueur a un nombre de manches remportées inférieur à 5 dans la map wonRounds
            arena.getWonRounds().put(winner.getUniqueId(), arena.getWonRounds().get(winner.getUniqueId()) + 1); //Ajouter 1 à la valeur de la clé du joueur
        }
    }

    public void eliminate(Player victim, List<Arena> arenaList) {
        Arena arena = arenaManager.getArenaByPlayer(victim.getUniqueId(), arenaList); //Récupération de l'arène où se trouve le joueur éliminé
        MGArena victimArena = (MGArena) arena;
        victim.teleport(victimArena.getMiddlespawn()); //Téléportation du joueur à la location au centre de l'arène
        victim.setGameMode(GameMode.SPECTATOR); //Mode du joueur en spectateur
        victimArena.getInGamePlayers().remove(victim.getUniqueId()); //Retirement du joueur de la liste des non éliminés
    }

    public void checkWinCore(Player winner, List<Arena> arenaList) {
        Arena arena = arenaManager.getArenaByPlayer(winner.getUniqueId(), arenaList); //Récupération de l'arène où se trouve le joueur éliminé
        MGArena winnerArena = (MGArena) arena;
        if (winnerArena.getAlluuids().size() >= 2) { //Si la liste d'ensemble de joueurs de l'arène a une taille supérieure ou égale à deux (parce qu'il ne faudrait pas qu'il soit seul dans la partie, sinon ça ne compte pas)
            winnerArena.getAlluuids().forEach(uuid -> Bukkit.getPlayer(uuid).sendMessage(getPrefix() + " > §l§3Téléportation dans 10s !"));
            Bukkit.getScheduler().runTaskLater(MGConstructorAPI.getInstance(), () -> { //Mise en place d'un scheduler qui s'exécutera après un délai de 10s
                winnerArena.restart(); //Redémarrer l'arène pour qu'elle soit marquée comme inoccupée
                for (UUID uuids : winnerArena.getAlluuids()) {
                    winnerArena.getWaitingRoom().addQueueToArena(winnerArena, arenaList); //Faire rejoindre tous les joueurs en attente à cette arène, si aucune ne leur était disponible jusque-là
                    customJoinEvent(Bukkit.getPlayer(uuids)); //Faire rejoindre la salle d'attente au joueur
                }
            }, 10 * 20);
        }
    }

    public void nextRoundTeam(List<Arena> arenaList, Team team, Map<UUID, Map<String, Object>> mainMap, int Coins, int Elos) {
        TeamArena arena = (TeamArena) MGConstructorAPI.getInstance().getArenaManager().getArenaByPlayer(team.getUuidList().getFirst(), arenaList);
        if (arena.getAlluuids().size() > 1) {
            for(UUID winnerUuid : team.getUuidList()) {
                StatsAccount statsAccount = (StatsAccount) memoryProvider.getStatsAccount(mainMap, winnerUuid, AccountType.statsKey(serverName));
                ProfilAccount profilAccount = (ProfilAccount) memoryProvider.getProfilAccount(mainMap, winnerUuid, AccountType.PROFIL.getKeyPrefix());
                profilAccount.setCoins(profilAccount.getCoins() + Coins);
                statsAccount.setElos(statsAccount.getElos()+ Elos);
                dirtyManager.markDirty(winnerUuid, AccountType.statsKey(serverName), statsAccount);
                dirtyManager.markDirty(winnerUuid, AccountType.PROFIL.getKeyPrefix(), profilAccount);
            }
            ifLessThanMaxRoundsTeam(arena, team);
            ifLastRoundTeam(arena);
        }
    }

    public void ifLessThanMaxRoundsTeam(TeamArena arena, Team winnerTeam) {
        if (winnerTeam.getPoints() < (arena.maxRounds - 1)) { //Si le joueur a un nombre de manches remportées inférieur à 5 dans la map wonRounds
            winnerTeam.setPoints(winnerTeam.getPoints() + 1); //Ajouter 1 à la valeur de la clé du joueur
        }
    }

    public void ifLastRoundTeam(TeamArena arena) {
        List<Team> nearMaxPoints = arena.getTeams().stream().filter(team -> team.getPoints() == (arena.maxRounds - 1)).collect(Collectors.toList());
        if (!nearMaxPoints.isEmpty()) {
            nearMaxPoints.forEach(team -> team.setPoints(team.getPoints() + 1));
            if (arena.getTeams().stream().anyMatch(team -> team.getPoints() == arena.maxRounds)) {
                if (tieOpponentTeamExists(arena)) {
                    nearMaxPoints.forEach(team -> team.setPoints(team.getPoints() - 1));
                    for (UUID uuid : arena.getAlluuids()) {
                        Player pls = Bukkit.getPlayer(uuid);
                        pls.sendTitle("§eEgalité !", "§6Recommencez");
                        pls.sendMessage(getPrefix() + " > §lOn vous a retiré 1 point à tous ! §dRejouez la manche et que le meilleur gagne !");
                    }
                    startCountDown(arena);

                } else {
                    Optional<Team> winner = arena.getTeams().stream().filter(team -> team.getPoints() == arena.maxRounds).findAny();
                    winner.ifPresent(team -> customWinEvent(winner.get()));
                }
            }
        }
    }

    public void eliminateTeam(UUID victimUUID, List<Arena> arenaList) {
        Arena arena = arenaManager.getArenaByPlayer(victimUUID, arenaList);
        Team victimTeam = team.getTeamByPlayer(Bukkit.getPlayer(victimUUID), (TeamArena) arena);
        TeamArena victimArena = (TeamArena) arena;
        long eliminatedTeammates = victimTeam.getUuidList().stream().filter(uuid1 -> !victimArena.inGamePlayers.contains(uuid1)).count();
        int maxPlayersByTeam = victimTeam.getMaximum(victimArena.maxPlayersAllowed, victimArena.totalTeams);
            if(eliminatedTeammates < maxPlayersByTeam) {
                eliminate(Bukkit.getPlayer(victimUUID), arenaList);
            }
            if(eliminatedTeammates == maxPlayersByTeam) {
                victimArena.getAlluuids().forEach(uuid1 -> Bukkit.getPlayer(uuid1).sendMessage(getPrefix() + " > §l§3La team §r" + victimTeam.getColor()+victimTeam.getString()+ " §l§3a perdue ! §c§l-1000 aura."));
            }
    }

    public void checkWinCoreTeam(Team winnerTeam, List<Arena> arenaList) {
        Arena arena = arenaManager.getArenaByPlayer(winnerTeam.getUuidList().get(0), arenaList); //Récupération de l'arène où se trouve le joueur éliminé
        TeamArena winnerArena = (TeamArena) arena;
        if (winnerArena.getTeams().size() >= 2) { //Si la liste des teams de l'arène a une taille supérieure ou égale à deux (parce qu'il ne faudrait pas qu'elle soit seule dans la partie, sinon ça ne compte pas)
            winnerTeam.getUuidList().forEach(uuid -> Bukkit.getPlayer(uuid).sendMessage(getPrefix() + " > §l§3Une nouvelle partie va débuter dans 10s !"));
            Bukkit.getScheduler().runTaskLater(MGConstructorAPI.getInstance(), () -> { //Mise en place d'un scheduler qui s'exécutera après un délai de 10s
                winnerArena.restart();
                for (UUID uuids : winnerTeam.getUuidList()) {
                    winnerArena.getWaitingRoom().addQueueToArena(winnerArena, arenaList);
                    customJoinEvent(Bukkit.getPlayer(uuids)); //Faire rejoindre la salle d'attente au joueur
                    //Redémarrer l'arène pour qu'elle soit marquée comme inoccupée
                     //Faire rejoindre tous les joueurs en attente à cette arène, si aucune ne leur était disponible jusque-là
                }
            }, 10 * 20);
        }
    }

    public void clearStuffandHealth(Player p){
        p.getInventory().clear(); //Inventaire du joueur nettoyé
        p.getInventory().setArmorContents(null); //Contenants des armures mis à null pour les nettoyer
        for (PotionEffect effect : p.getActivePotionEffects()) { //Pour chaque effet de potion que possède le joueur
            p.removePotionEffect(effect.getType()); //Retirer tous les effets
        }
        p.setHealth(20.0); //Guérison du joueur pour reprendre depuis sa dernière partie si elle a existé
    }

    public void onDeath(Arena arena, GameMode mode, int time, int cycle, Player player) {

        if (arena != null) { //Si l'objet arène n'est pas null
            player.setGameMode(GameMode.SPECTATOR); //Mettre le joueur en mode spectateur
            player.getInventory().clear(); //Nettoyer son inventaire
            player.teleport(arena.getMiddlespawn()); //Téléporter le joueur mort aux coordonnées du centre de l'arène
            new BukkitRunnable() { //Nouveau runnable
                int timer = time; //initialisation d'une variable timer de type integer à 10

                @Override
                public void run() {
                    if (!player.isOnline()) { //Si le joueur mort n'est pas en ligne sur le serveur
                        cancel(); //Annuler le runnable
                        playerQuits(arena, player);
                        return; //Arrêter l'exécution du reste du code
                    }

                    if (timer >= 1) { //Si la variable timer est supérieure ou égale à 1
                        player.sendMessage("§cRespawn dans :" + timer); //Envoyer au joueur l'avancée du timer avec son respawn
                    } else {
                        player.setGameMode(mode); //Mettre le joueur en mode aventure
                        respawning(arena, player); //Appliquer le constructeur de la classe GiveKitsUtils donnant son kit au joueur

                        cancel(); //Arrêter le runnable
                    }
                    if(arena.getTimerCycle() > 0) {
                        if (arena.getTimerCycle() == 60 * cycle) //Si le timer du cycle de l'arène en cours est à 180
                            cancel(); //Annuler le runnable
                    }
                    timer--; //Enlever à la variable timer à chaque exécution du runnable
                }

            }.runTaskTimer(MGConstructorAPI.getInstance(), 20L, 20L); //Exécution après une seconde de délai du runnable toutes les secondes
        }
    }

    public Player getOpponent(UUID uuid, Arena arena) {
        for(UUID uuids : arena.getAlluuids()) { //Pour chaque uuid contenu dans la liste d'ensemble de l'arène en cours
            if (uuid != uuids) //Si la uuid du joueur en paramètre n'est pas égale à celle d'un élément parmi la liste d'ensemble de l'arène en cours
                return Bukkit.getPlayer(uuids); //retourner le joueur qui n'a pas la même uuid que le joueur en paramètre en stoppant la boucle
        }
        return Bukkit.getPlayer(uuid); //retourner le joueur en paramètre par défaut
    }

    public Team getOpponentTeam(Team team, TeamArena arena){
        for(Team otherteam : arena.getTeams()) {
            if(team != otherteam){
                return otherteam;
            }
        }
        return team;
    }

    public boolean tieOpponentExists(Arena arena){
        return arena.getWonRounds().keySet().stream().filter(p5 -> arena.getWonRounds().get(p5) == 5).count() > 1;
    }

    public boolean tieOpponentTeamExists(TeamArena arena){
        return arena.getTeams().stream().filter(team5 -> team5.getPoints() == 5).count() > 1;
    }

}
