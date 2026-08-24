package fr.pandamcbe.mGConstructorAPI.managers;

import fr.pandamcbe.mGConstructorAPI.MGConstructorAPI;
import fr.pandamcbe.mGConstructorAPI.Team;
import fr.pandamcbe.mGConstructorAPI.arenas.Arena;
import fr.pandamcbe.mGConstructorAPI.arenas.MGArena;
import fr.pandamcbe.mGConstructorAPI.arenas.State;
import fr.pandamcbe.mGConstructorAPI.arenas.TeamArena;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public abstract class MGPlayerManager extends OptionalsHooksManager{

    private final GameSessionManager gameManager = MGConstructorAPI.getInstance().getGameManager();
    private final ArenaManager arenaManager = MGConstructorAPI.getInstance().getArenaManager();
    private final WaitingRoom waitingRoom = MGConstructorAPI.getInstance().getWaitingRoomManager();
    private final Team team = MGConstructorAPI.getInstance().getTeam();

    public void lookForArena(List<Arena> arenaList, String mgmode, Player p){
        MGArena nextArena = (MGArena) arenaManager.getNextArena(arenaList, mgmode);
        arenaManager.joinArena(arenaList, nextArena, p);
    }


    public void whenPlayerDisconnects(Player leaver, Arena arena, List<Arena> arenaList) {
        TeamArena teamArena = (TeamArena) arena;
        MGConstructorAPI.getInstance().getScoreboardManager().onLogout(leaver);
        UUID leaverUUID = leaver.getUniqueId();
        waitingRoom.getWaitingRoom().remove(leaverUUID);
        if (teamArena != null) {
            teamArena.getAlluuids().remove(leaverUUID);
            teamArena.getInGamePlayers().remove(leaverUUID);
            team.getTeamByPlayer(leaver, (TeamArena) arena).getUuidList().remove(leaverUUID);
            if (teamArena.getAlluuids().size() == 1) {
                Player player = gameManager.getOpponent(leaverUUID, teamArena);
                if (player.isOnline()) {
                    if (!waitingRoom.getWaitingRoom().contains(player.getUniqueId()) && arena.isStarted) {
                        arenaManager.breakOrPlace(teamArena);
                        teamArena.restart();
                        customJoinEvent(player);
                    } else {
                        teamArena.setState(State.WAITING);
                        teamArena.setStartingTimer(30);
                    }
                }
            }
            if (teamArena.getAlluuids().isEmpty()) {
                arenaManager.breakOrPlace(teamArena);
                teamArena.restart();
                waitingRoom.addWaitingPlayerToArena(teamArena, arenaList);
            }
        }
    }
}