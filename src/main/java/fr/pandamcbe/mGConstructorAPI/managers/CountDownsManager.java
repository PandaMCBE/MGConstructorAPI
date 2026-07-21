package fr.pandamcbe.mGConstructorAPI.managers;

import fr.pandamcbe.mGConstructorAPI.MGConstructorAPI;
import fr.pandamcbe.mGConstructorAPI.arenas.MGArena;
import fr.pandamcbe.mGConstructorAPI.arenas.State;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class CountDownsManager {

    private final MGArena arena; //Instanciation de la classe Arena avec une variable de type privée et constante
    private final ArenaManager arenaManager = MGConstructorAPI.getInstance().getArenaManager();
    public CountDownsManager(MGArena arena) {
        this.arena = arena; //Initialisation de la variable arena avec celle qui est donnée par le constructeur
    }

    public void runNewGame(BukkitRunnable runnable,List<UUID> waitingRoom, Player pls) {
        if (arena == null || arena.getAlluuids().size() <= 1) { //Si l'arène est null ou qu'elle ne contient pas de joueurs ou un seul
            runnable.cancel(); //Annuler le runnable
            return; //Arrêter l'exécution du code
        }

        if (!pls.isOnline()) { //Si un joueur n'est pas en ligne
            arenaManager.playerQuits(pls); //Appliquer cette méthode pour cet objet Joueur de la liste
        }
        switch (arena.getStartingTimer()){
            case 10:
                pls.sendMessage(arenaManager.getPrefix()+ " §3§l> §b§lLa partie commence dans §n" + arena.getStartingTimer() + " §r§d§lsecondes !"); //Afficher à tous les joueurs, l'état de démarrage du jeu
                pls.playSound(pls.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f); //Joueur le son à tous les joueurs
                break;
            case 1:
                pls.sendMessage(arenaManager.getPrefix()+" §3§l> §b§lLa partie commence dans §n1 §r§d§lseconde ! ");
                pls.playSound(pls.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f); //Même chose que timerEvent mais spécialement pour la dernière seconde
                break;
            case 0:
                runnable.cancel(); //Arrêter le timer
                MGConstructorAPI.getInstance().getScoreboardManager().onLogout(pls); //Retirer le scoreboard précédemment associé au joueur quand la partie va commencer une manche
                waitingRoom.remove(pls.getUniqueId()); //Retirer les joueurs de la liste de file d'attente

                break;
            default:
                break;
        }
    }

    public void runNewRound(BukkitRunnable runnable, List<UUID> waitingRoom, int timer, Sound countingSound, Sound finalSound) {
        if (arena == null || arena.getAlluuids().size() <= 1) //Si l'objet Arena est null ou sa liste d'ensemble de joueurs a une taille inférieure ou égale à 1
        {
            runnable.cancel(); //Annuler le timer
            return;// Arrêter l'exécution du code
        }
        for (UUID uuid : arena.getAlluuids()) { //Pour chaque joueur dans la liste d'ensemble de l'arène
            Player p = Bukkit.getPlayer(uuid);
            if (!p.isOnline()) { //Si un joueur n'est pas en ligne
                arenaManager.playerQuits(p); //Appliquer cette méthode pour cet objet Joueur de la liste
            }

            if (timer > 0) {
                p.playSound(p.getLocation(), countingSound, 1.0f, 1.0f);
                if (timer > 5) {
                    p.sendTitle("§a§l" + timer, "");
                } else {
                    switch (timer) {
                        case 5:
                            if(MGConstructorAPI.getInstance().getScoreboardManager().scoreboards.containsKey(uuid)) {
                                MGConstructorAPI.getInstance().getScoreboardManager().onLogout(p);
                            }
                            p.sendTitle("§e§l" + timer, "");
                            break;
                        case 4:
                            p.sendTitle("§6§l" + timer, "");
                            break;
                        default:
                            break;
                    }
                    if (timer <= 3) {
                        p.sendTitle("§c§l" + timer, "");
                    }
                }
            } else {
                runnable.cancel();
                p.sendTitle("§a§lGO!", "");
                p.playSound(p.getLocation(), finalSound, 1.0f, 1.0f);
                waitingRoom.remove(p.getUniqueId()); //Retirer les joueurs de la liste de file d'attente
                if (!arena.getInGamePlayers().contains(p.getUniqueId())) { //Si la liste des joueurs non éliminés ne contient pas le joueur
                    arena.getInGamePlayers().add(p.getUniqueId()); //Ajouter le joueur à la liste
                }
            }
            if (!arena.getState().equals(State.PLAYING)) {
                arena.setState(State.PLAYING);
            }
        }


    }
}