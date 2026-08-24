package fr.pandamcbe.mGConstructorAPI.managers;

import fr.pandamcbe.mGConstructorAPI.MGConstructorAPI;
import fr.pandamcbe.mGConstructorAPI.arenas.Arena;
import fr.pandamcbe.mGConstructorAPI.scoreboards.samaboardsclasses.PersonalScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WaitingRoom extends OptionalsHooksManager{

    public List<UUID> waitingRoom;
    public Location location;

    public WaitingRoom(){
        this.location = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        this.waitingRoom = new ArrayList<>();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<UUID> getWaitingRoom() {
        return waitingRoom;
    }

    public void setWaitingRoom(List<UUID> waitingRoom) {
        this.waitingRoom = waitingRoom;
    }

    final private ArenaManager arenaManager = MGConstructorAPI.getInstance().getArenaManager();

    public void waiting(List<Arena> arenaList, Arena arena, Player p){

        if(arena == null) {
            p.sendMessage("§b§lMG-Land > §c§lAucune arène n'est disponible :( Revenez plus-tard !"); //Si la méthode n'avait pas pu retourner une arène, celle-ci est donc null car aucune n'est dispo, on l'envoie par message au nouveau joueur (tous les joueurs qui sont passés par cette méthode).
        } else if (arena.getAlluuids().size() < arena.getMinimumPlayers()) {
            p.sendMessage("§b§lMG-Land > §c§lNombre de joueurs minimum : §e§l" + arena.getMinimumPlayers());
        }
        if(!getWaitingRoom().contains(p.getUniqueId())) {
            getWaitingRoom().add(p.getUniqueId());
        }
        if(Bukkit.getOnlinePlayers().contains(p)) {
            p.teleport(getLocation());
            MGConstructorAPI.getInstance().getScoreboardManager().onLogin(new PersonalScoreboard(arenaList, getWaitingRoom(), p, getPrefix()), p);
        }

    }

    public void addWaitingPlayerToArena(Arena nextArena, List<Arena> arenaList){
        if (!waitingRoom.isEmpty()) { //Si la taille du nombre de joueurs dans la liste de file d'attente est supérieure à 2
            for (UUID waiting : waitingRoom) { //Pour chaque joueur contenu dans la file d'attente
                if (arenaManager.getArenaByPlayer(waiting, arenaList) == null) //S'il n'existe pas d'arène où ces joueurs contenus en attente sont présents
                    arenaManager.joinArena(arenaList, nextArena, Bukkit.getPlayer(waiting)); //Ajouter ces joueurs dans la prochaine partie
            }
        }
    }

    public WaitingRoom clone() {
        try {
            return (WaitingRoom) super.clone();
        } catch (CloneNotSupportedException e){
            e.printStackTrace();
        }
        return null;
    }

}
