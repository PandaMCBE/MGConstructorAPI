package fr.pandamcbe.mGConstructorAPI.managers;

import fr.pandamcbe.mGConstructorAPI.MGConstructorAPI;
import fr.pandamcbe.mGConstructorAPI.Team;
import fr.pandamcbe.mGConstructorAPI.arenas.Arena;
import fr.pandamcbe.mGConstructorAPI.arenas.MGArena;
import fr.pandamcbe.mGConstructorAPI.arenas.State;
import fr.pandamcbe.mGConstructorAPI.arenas.TeamArena;
import org.bukkit.Bukkit;
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
        joinArena(arenaList, nextArena, p);
    }

    public void joinArena(List<Arena> arenaList, Arena nextArena, Player p) {
        UUID uuid = p.getUniqueId();

        if(nextArena == null || nextArena.getAlluuids().size() < nextArena.getMinimumPlayers()){
            waitingRoom.waiting(arenaList, nextArena, p);
        }

        if (nextArena != null) {
            waitingRoom.addWaitingPlayerToArena(nextArena, arenaList);
            if (!nextArena.getAlluuids().contains(uuid) && nextArena.getAlluuids().size() != nextArena.getMaxPlayersAllowed()) {
                nextArena.getAlluuids().add(uuid);
            } else waitingRoom.waiting(arenaList, nextArena, p);
            if(nextArena.getAlluuids().size() < nextArena.getMaxPlayersAllowed()) {
                p.teleport(Bukkit.getPlayer(waitingRoom.getWaitingRoom().getFirst()).getLocation());
            }
            //Comment être sûr que les joueurs d'un duel soient toujours mis en relation même après avoir été placé en attente ?
            //Envisager le retrait de joinArena de ArenaManager pour le placer dans MGPlayerManager et MGDuelManager qui devraient
            //d'ailleurs trouver un meilleur nom.
            team.addPlayer((TeamArena) nextArena, p);

            arenaManager.startArena(nextArena, p);
        }
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