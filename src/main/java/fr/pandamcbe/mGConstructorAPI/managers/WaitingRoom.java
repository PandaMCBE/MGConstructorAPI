package fr.pandamcbe.mGConstructorAPI.managers;

import fr.pandamcbe.mGConstructorAPI.MGConstructorAPI;
import fr.pandamcbe.mGConstructorAPI.arenas.Arena;
import fr.pandamcbe.mGConstructorAPI.arenas.State;
import fr.pandamcbe.mGConstructorAPI.scoreboards.samaboardsclasses.PersonalScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WaitingRoom extends OptionalsHooksManager {

    public Queue<List<UUID>> queue;
    public Location location;

    public WaitingRoom() {
        this.location = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        this.queue = new ConcurrentLinkedQueue<>();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Queue<List<UUID>> getQueue() {
        return queue;
    }

    public void setQueue(Queue<List<UUID>> queue) {
        this.queue = queue;
    }


    final private ArenaManager arenaManager = MGConstructorAPI.getInstance().getArenaManager();

    public List<UUID> getWaitingListByPlayer(UUID uuid) {
        for (List<UUID> list : queue) {
            if (list.contains(uuid)) {
                return list;
            }
        }
        return null;
    }

    public String waitingReason(List<Arena> arenaList, UUID uuid){
        Arena arena = arenaManager.getArenaByPlayer(uuid, arenaList);
        if(arena == null) {
            return "noArenaAvailable";
        } else if(arena.getAlluuids().size() < arena.getMinimumPlayers()){
            return "notEnoughPlayers";
        }
        return null;
    }

    public void waiting(List<Arena> arenaList, Arena arena, UUID uuid) {
        String waitingReason = waitingReason(arenaList, uuid);
        System.out.println("waitingReason ?");
        if(waitingReason != null) {
            Player p = Bukkit.getPlayer(uuid);
            System.out.println("waitingReason isn't null");
            if (waitingReason.equalsIgnoreCase("noArenaAvailable")) {
                if (queue.isEmpty()) {
                    queue.add(new ArrayList<>());
                }
                for (List<UUID> list : queue) {
                    if (!list.contains(uuid) && list.size() < arena.getMaxPlayersAllowed()) {
                        list.add(uuid);
                    } else {
                        List<UUID> newGroup = new ArrayList<>();
                        newGroup.add(uuid);
                        queue.add(newGroup);
                    }
                }
                p.sendMessage("§b§lMG-Land > §cAucune arène n'est disponible pour le moment... :( Vous avez été placé en file d'attente.");
            } else if (waitingReason.equalsIgnoreCase("notEnoughPlayers")) {
                if (!arena.getState().equals(State.WAITING)) {
                    arena.setState(State.WAITING);
                }
                p.sendMessage("§b§lMG-Land > §cNombre de joueurs minimum : §e" + arena.getMinimumPlayers());
            }
            p.teleport(getLocation());
            MGConstructorAPI.getInstance().getScoreboardManager().onLogin(new PersonalScoreboard(arenaList, p, getPrefix()), p);
        }
    }

    public void addQueueToArena(Arena nextArena, List<Arena> arenaList) {
        if (!queue.isEmpty()) {
            List<UUID> waitingList = queue.poll();
            for (UUID waiting : waitingList) {
                arenaManager.joinArena(arenaManager, arenaList, nextArena, waiting);
            }
        }
    }

    public void addWaitingPlayerToArena(Arena arena, List<Arena> arenaList){
        if(!queue.isEmpty()) {
            List<UUID> waitingList = queue.stream().findFirst().get();
            arenaManager.joinArena(arenaManager, arenaList, arena, waitingList.getLast());
        }
    }

    public WaitingRoom clone() {
        try {
            return (WaitingRoom) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

}