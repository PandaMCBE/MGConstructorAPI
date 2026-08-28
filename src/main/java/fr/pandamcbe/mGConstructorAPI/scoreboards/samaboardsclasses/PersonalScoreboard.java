package fr.pandamcbe.mGConstructorAPI.scoreboards.samaboardsclasses;

import fr.pandamcbe.mGConstructorAPI.MGConstructorAPI;
import fr.pandamcbe.mGConstructorAPI.arenas.Arena;
import fr.pandamcbe.mGConstructorAPI.arenas.State;
import fr.pandamcbe.mgapi.serverutilapi.scoreboards.ObjectiveSign;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PersonalScoreboard extends fr.pandamcbe.mgapi.serverutilapi.scoreboards.PersonalScoreboard {

    private final MGConstructorAPI main = MGConstructorAPI.getInstance();
    private final List<Arena> arenaList;
    private final List<UUID> waitingPlayers;

    public PersonalScoreboard(List<Arena> arenas, List<UUID> waitingRoom, Player player, String name){
        this.player = player;
        arenaList = arenas;
        waitingPlayers = waitingRoom;
        uuid = player.getUniqueId();
        objectiveSign = new ObjectiveSign("sidebar", "§l§n"+ name);
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
                    if(arena.getState() == State.WAITING) {
                        this.objectiveSign.setLine(1, "§8§m------------");
                            this.objectiveSign.setLine(2, "§7En attente : §f" + waitingPlayers);
                        this.objectiveSign.setLine(3, "§7Débute dans : §f" + arena.getStartingTimer() + "§fs");
                        this.objectiveSign.setLine(4, "§7Arène : §f" + main.getArenaManager().getArena(arena));
                        this.objectiveSign.setLine(5, "§8§m-----------");
                        this.objectiveSign.setLine(6, ip);
                    }
                    this.objectiveSign.updateLines();
            }
    }
}