package fr.pandamcbe.mGConstructorAPI.managers;

import fr.pandamcbe.mGConstructorAPI.MGConstructorAPI;
import fr.pandamcbe.mGConstructorAPI.Team;
import fr.pandamcbe.mGConstructorAPI.arenas.Arena;
import fr.pandamcbe.mGConstructorAPI.arenas.MGArena;
import fr.pandamcbe.mGConstructorAPI.arenas.State;
import fr.pandamcbe.mGConstructorAPI.arenas.TeamArena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class ArenaManager extends OptionalsHooksManager {

    private final WaitingRoom waitingRoomManager = MGConstructorAPI.getInstance().getWaitingRoomManager();
    private final Team team = MGConstructorAPI.getInstance().getTeam();

    public Arena getArenaByPlayer(UUID uuid, List<Arena> arenaList) {
        if(uuid != null) {
            for (Arena arena : arenaList) {
                if (arena.getAlluuids().contains(uuid)) {
                    return arena;
                }
            }
        }
        return null;
    }

    public Arena getNextArena(List<Arena> arenaList, String mgmode) { //Récupérer une arène non démarrée
        for (Arena arena : arenaList) { //Pour chaque objet Arena qui sont dans la liste définie de toutes les arènes existantes
            MGArena mgArena = (MGArena) arena;
            if (mgArena.getMode().equals(mgmode)) {
                if (mgArena.getState().equals(State.STARTING) || mgArena.getState().equals(State.WAITING) ||
                        mgArena.getState().equals(State.FINISHED) && mgArena.getAlluuids().size() < mgArena.getMaxPlayersAllowed()) { //Si l'arène n'a pas une liste d'ensemble de joueurs vide et si l'arène n'est pas démarrée. En gros, c'est pour repérer des arènes où des joueurs sont encore en attente, donc pas démarrée mais contient quand même des personnes dans sa liste.
                    return mgArena; //retourner l'arène qui respecte ces conditions et arrêter la boucle
                }
            }
        }
        return null; //retourner null par défaut si aucune arène n'est disponible
    }

    public String getArena(Arena arena){
        if(arena == null) { //Si la recherche d'arène libre n'a rien retourné -> null
            return "§cIndisponible"; //Retourner un message indiquant l'indisponibilité
        }else {
            return arena.getName(); //Sinon, afficher le nom de l'arène sélectionnée
        }
    }

    public void joinArena(List<Arena> arenaList, Arena nextArena, Player p) {
        UUID uuid = p.getUniqueId();

        if(nextArena == null || nextArena.getAlluuids().size() < nextArena.getMinimumPlayers()){
            waitingRoomManager.waiting(arenaList, nextArena, p);
        }

        if (nextArena != null) {
            waitingRoomManager.addWaitingPlayerToArena(nextArena, arenaList);
            if (!nextArena.getAlluuids().contains(uuid) && nextArena.getAlluuids().size() != nextArena.getMaxPlayersAllowed()) {
                nextArena.getAlluuids().add(uuid);
            } else waitingRoomManager.waiting(arenaList, nextArena, p);
            if(nextArena.getAlluuids().size() < nextArena.getMaxPlayersAllowed()) {
                p.teleport(Bukkit.getPlayer(waitingRoomManager.getWaitingRoom().getFirst()).getLocation());
            }
            //Comment être sûr que les joueurs d'un duel soient toujours mis en relation même après avoir été placé en attente ?
            //Envisager le retrait de joinArena de ArenaManager pour le placer dans MGPlayerManager et MGDuelManager qui devraient
            //d'ailleurs trouver un meilleur nom.
            team.addPlayer((TeamArena) nextArena, p);

            startArena(nextArena, p);
        }
    }

    public void startArena(Arena nextArena, Player p) {
        if (nextArena.getAlluuids().contains(p.getUniqueId())) { //Si la liste d'ensemble de l'arène contient le joueur
            if (waitingRoomManager.getWaitingRoom().size() >= nextArena.getMinimumPlayers() && !nextArena.getState().equals(State.STARTING)) { //Si la liste de file d'attente depuis la classe Main est supérieur ou égale 2. C'est pour lancer une partie même n'étant peu.
                nextArena.setState(State.STARTING); //Déclarer l'arène comme étant démarrée
                nextArena.setStarted(true);
                startCountDown(nextArena);
            }
        }
    }

    public void breakingPlacedBlocks(Arena arena){
        if(!arena.getBlocks().isEmpty()) {
            for (Location loc : arena.getBlocks().keySet()) {
                loc.getBlock().setType(Material.AIR);
            }
        }
    }

    public void placeBrokenBlocks(Arena arena){
        if(!arena.getBlocks().isEmpty()) { //Si la map obtenue par le getter getBlocks n'est pas vide
            for (Location loc : arena.getBlocks().keySet()) { //Pour toutes les locations par toutes les clés de type location de la map "blocks"
                Material material = arena.getBlocks().get(loc); //Initialisation d'un objet Material à une valeur d'une clé de type location de la map "blocks". En gros obtenir le type de block correspondant à la location qui est retournée par la boucle For de la map blocks.
                loc.getBlock().setType(material); //Définition du type de block qu'est la location récupérée par la boucle parmi les clés de la map "blocks" par celui qui lui est associé en valeur
            } //En gros, tous les blocs qui ont été cassés et placés dans la map par leur coordonées et leur type sont replacées sur les locations qui correspondent à chaque clé.
        }
    }
}