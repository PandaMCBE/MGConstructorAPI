package fr.pandamcbe.mGConstructorAPI.managers;

import fr.pandamcbe.mGConstructorAPI.MGConstructorAPI;
import fr.pandamcbe.mGConstructorAPI.arenas.Arena;
import fr.pandamcbe.mGConstructorAPI.arenas.MGArena;
import fr.pandamcbe.mGConstructorAPI.arenas.State;
import fr.pandamcbe.mGConstructorAPI.arenas.Team;
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
        MGArena mgArena = (MGArena) arena;
        MGConstructorAPI.getInstance().getScoreboardManager().onLogout(leaver);
        UUID leaverUUID = leaver.getUniqueId();
        waitingRoom.getWaitingRoom().remove(leaverUUID);
        if (mgArena != null) {
            mgArena.getAlluuids().remove(leaverUUID);
            mgArena.getInGamePlayers().remove(leaverUUID);
            team.getTeamByPlayer(leaver, arena).getUuidList().remove(leaverUUID);
            if (mgArena.getAlluuids().size() == 1) {
                Player player = gameManager.getOpponent(leaverUUID, mgArena);
                if (player.isOnline()) {
                    if (!waitingRoom.getWaitingRoom().contains(player.getUniqueId()) && arena.isStarted) {
                        arenaManager.breakOrPlace(mgArena);
                        mgArena.restart();
                        customJoinEvent(player);
                    } else {
                        mgArena.setState(State.WAITING);
                        mgArena.setStartingTimer(30);
                    }
                }
            }
            if (mgArena.getAlluuids().isEmpty()) {
                arenaManager.breakOrPlace(mgArena);
                mgArena.restart();
                waitingRoom.joinAgain(mgArena, arenaList);
            }
        }
    }
}