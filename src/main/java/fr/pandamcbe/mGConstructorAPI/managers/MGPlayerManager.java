package fr.pandamcbe.mGConstructorAPI.managers;

import fr.pandamcbe.mGConstructorAPI.MGConstructorAPI;
import fr.pandamcbe.mGConstructorAPI.Team;
import fr.pandamcbe.mGConstructorAPI.arenas.Arena;
import fr.pandamcbe.mGConstructorAPI.arenas.State;
import fr.pandamcbe.mGConstructorAPI.arenas.TeamArena;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public abstract class MGPlayerManager extends OptionalsHooksManager{

    private final GameSessionManager gameManager = MGConstructorAPI.getInstance().getGameManager();
    private final ArenaManager arenaManager = MGConstructorAPI.getInstance().getArenaManager();
    private final Team team = MGConstructorAPI.getInstance().getTeam();

    public void whenPlayerDisconnects(Player leaver, Arena arena, List<Arena> arenaList) {
        TeamArena teamArena = (TeamArena) arena;
        MGConstructorAPI.getInstance().getScoreboardManager().onLogout(leaver);
        UUID leaverUUID = leaver.getUniqueId();
        if (teamArena != null) {

            List<UUID> waitingList = arena.getWaitingRoom().getWaitingListByPlayer(leaverUUID);
            if(waitingList != null) {
                waitingList.remove(leaverUUID);
            } else {
                teamArena.getAlluuids().remove(leaverUUID);
                teamArena.getInGamePlayers().remove(leaverUUID);
                team.getTeamByPlayer(leaver, (TeamArena) arena).getUuidList().remove(leaverUUID);
                if (teamArena.getAlluuids().size() == 1) {
                    Player player = gameManager.getOpponent(leaverUUID, teamArena);
                    if (player.isOnline()) {
                        if (arena.getState() == State.PLAYING) {
                            arenaManager.breakOrPlace(teamArena);
                        }
                        teamArena.restart();
                        arena.getWaitingRoom().addQueueToArena(teamArena, arenaList);
                        customJoinEvent(player);
                    } else {
                        teamArena.setState(State.WAITING);
                        teamArena.setStartingTimer(30);
                    }
                } else if((teamArena.getState().equals(State.STARTING) || teamArena.getState().equals(State.WAITING))) {
                    teamArena.getWaitingRoom().addWaitingPlayerToArena(teamArena, arenaList);
                }
            }

            }
        }
}