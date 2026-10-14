package fr.pandamcbe.mGConstructorAPI.scoreboards.samaboardsclasses;

import fr.pandamcbe.mGConstructorAPI.MGConstructorAPI;
import fr.pandamcbe.mGConstructorAPI.arenas.Arena;
import fr.pandamcbe.mGConstructorAPI.arenas.State;
import fr.pandamcbe.mGConstructorAPI.managers.ArenaManager;
import fr.pandamcbe.mgapi.serverutilapi.scoreboards.ObjectiveSign;
import org.bukkit.entity.Player;

import java.util.List;

public class PersonalScoreboard extends fr.pandamcbe.mgapi.serverutilapi.scoreboards.PersonalScoreboard {

    private final MGConstructorAPI main = MGConstructorAPI.getInstance();
    private final List<Arena> arenaList;

    public PersonalScoreboard(List<Arena> arenas, Player player, ArenaManager arenaManager){
        this.player = player;
        arenaList = arenas;
        uuid = player.getUniqueId();
        objectiveSign = new ObjectiveSign("sidebar", "§l§n"+ arenaManager.getColor()+arenaManager.getPrefix());
        reloadData();
        objectiveSign.addReceiver(player);
    }

    @Override
    public void reloadData() {

    }
    @Override
    public void setLines(String ip) {

        Arena arena = main.getArenaManager().getArenaByPlayer(player.getUniqueId(), arenaList);

            if(player != null) {//Scoreboard en attente
                    if(arena.getState() == State.WAITING || arena.getState() == State.STARTING) {
                        this.objectiveSign.setLine(1, "§8§m------------");
                            this.objectiveSign.setLine(2, "§7En attente : §f" + waitingCount(arena));
                        this.objectiveSign.setLine(3, "§7Débute dans : §f" + arena.getStartingTimer() + "§fs");
                        this.objectiveSign.setLine(4, "§7Arène : §f" + main.getArenaManager().getArena(main.getArenaManager().getNextArena(arenaList, arena.getMode())));
                        this.objectiveSign.setLine(5, "§8§m-----------");
                        this.objectiveSign.setLine(6, ip);
                    }
                    this.objectiveSign.updateLines();
            }
    }

    public int waitingCount (Arena arena) {
        if (arena.getWaitingRoom().getWaitingListByPlayer(uuid) != null) {
            return arena.getWaitingRoom().getWaitingListByPlayer(uuid).size();
        } else {
            return arena.getAlluuids().size();
        }
    }
}