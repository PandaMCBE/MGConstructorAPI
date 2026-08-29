package fr.pandamcbe.mGConstructorAPI.managers;

import fr.pandamcbe.mGConstructorAPI.MGConstructorAPI;
import fr.pandamcbe.mGConstructorAPI.arenas.Arena;
import fr.pandamcbe.mGConstructorAPI.arenas.State;
import fr.pandamcbe.mGConstructorAPI.scoreboards.samaboardsclasses.PersonalScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class WaitingRoom extends OptionalsHooksManager{

    public Queue<Map<String, List<UUID>>> queue;
    public Location location;

    public WaitingRoom(){
        this.location = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        this.queue = new ConcurrentLinkedQueue<>();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Queue<Map<String, List<UUID>>> getQueue() {
        return queue;
    }

    public void setQueue(Queue<Map<String, List<UUID>>> queue) {
        this.queue = queue;
    }

    final private ArenaManager arenaManager = MGConstructorAPI.getInstance().getArenaManager();

    public List<UUID> getWaitingListByPlayer(List<Arena> arenaList, UUID uuid) {
        String waitingReason = waitingReason(arenaList, uuid);
        List<Map<String, List<UUID>>> filtered = queue.stream().filter(stringListMap ->
                (stringListMap.containsKey(waitingReason))).collect(Collectors.toList());
        if(!filtered.isEmpty()) {
            for (Map<String, List<UUID>> map : filtered) {
                if(map.get(waitingReason).contains(uuid)) {
                    return map.get(waitingReason);
                }
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

    public void waiting(List<Arena> arenaList, Arena arena, Player p){
        String waitingReason = waitingReason(arenaList, p.getUniqueId());
            List<Map<String, List<UUID>>> filtered = queue.stream().filter(stringListMap ->
                (stringListMap.containsKey(waitingReason) && !stringListMap.get(waitingReason).contains(p.getUniqueId()) && stringListMap.get(waitingReason).size() < arena.getMaxPlayersAllowed())).collect(Collectors.toList());
            if(!filtered.isEmpty()) {
                for (Map<String, List<UUID>> map : filtered) {
                    if (!map.isEmpty()) {
                        map.get(waitingReason).add(p.getUniqueId());
                    }
                }
            } else {
                Map<String, List<UUID>> map = new HashMap<>();
                List<UUID> list = new ArrayList<>();
                list.add(p.getUniqueId());
                map.put(waitingReason, list);
                queue.add(map);
            }
            if(waitingReason.equalsIgnoreCase("noArenaAvailable")) {
                p.sendMessage("§b§lMG-Land > §c§lAucune arène n'est disponible pour le moment... :( Vous avez été placé en file d'attente.");
            } else if(waitingReason.equalsIgnoreCase("notEnoughPlayers")) {
                if(!arena.getState().equals(State.WAITING)){
                    arena.setState(State.WAITING);
                }
                p.sendMessage("§b§lMG-Land > §c§lNombre de joueurs minimum : §e§l" + arena.getMinimumPlayers());
            }

        p.teleport(getLocation());
        MGConstructorAPI.getInstance().getScoreboardManager().onLogin(new PersonalScoreboard(arenaList, getWaitingListByPlayer(arenaList, p.getUniqueId()), p, getPrefix()), p);

    }

    public void addWaitingPlayerToArena(Arena nextArena, List<Arena> arenaList){
        if (!queue.isEmpty()) {
            Map<String, List<UUID>> waitingQueue = queue.poll();
            if (waitingQueue.keySet().stream().findAny().isPresent()) {
                List<UUID> waitingList = waitingQueue.get(waitingQueue.keySet().stream().findAny().get());
                for (UUID waiting : waitingList) {
                    if (arenaManager.getArenaByPlayer(waiting, arenaList) == null) {
                        arenaManager.joinArena(arenaList, nextArena, Bukkit.getPlayer(waiting));
                    }
                }
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
