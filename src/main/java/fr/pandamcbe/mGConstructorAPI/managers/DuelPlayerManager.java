package fr.pandamcbe.mGConstructorAPI.managers;

import fr.pandamcbe.commons.DuelAccount;
import fr.pandamcbe.mGConstructorAPI.MGConstructorAPI;
import fr.pandamcbe.mGConstructorAPI.Team;
import fr.pandamcbe.mGConstructorAPI.arenas.Arena;
import fr.pandamcbe.mGConstructorAPI.arenas.State;
import fr.pandamcbe.mGConstructorAPI.arenas.TeamArena;
import fr.pandamcbe.mgapi.Main;
import fr.pandamcbe.mgapi.serverutilapi.enums_strings.AllNames;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public abstract class DuelPlayerManager extends OptionalsHooksManager {

    private final GameSessionManager gameManager = MGConstructorAPI.getInstance().getGameManager();
    private final ArenaManager arenaManager = MGConstructorAPI.getInstance().getArenaManager();
    private final Team team = MGConstructorAPI.getInstance().getTeam();

//    public void lookForArena(List<Arena> arenaList, DuelAccount duelAccount, Player p) {
//        if (p != null) {
//
//            if (duelAccount != null) {
//                String getDuelMode = duelAccount.getGamemode();
//                if (arenaManager.getArenaByPlayer(findOpponent(duelAccount, p), arenaList) == null) {
//                    Arena nextArena = arenaManager.getNextArena(arenaList, getDuelMode);
//                    arenaManager.joinArena(arenaList, nextArena, p);
//                } else {
//                    Arena rightArena = arenaManager.getArenaByPlayer(findOpponent(duelAccount, p), arenaList);
//                    arenaManager.joinArena(arenaList, rightArena, p);
//                }
//            }
//        }
//    }

    private UUID findOpponent(DuelAccount duelAccount, Player p){
        if(!p.getUniqueId().equals(duelAccount.getPlayer1UUID())){
            return duelAccount.getPlayer1UUID();
        } else {
            return duelAccount.getPlayer2UUID();
        }
    }

    public void whenPlayerDisconnects(Player leaver, Arena arena, List<Arena> arenaList) {
        TeamArena teamArena = (TeamArena) arena;
        MGConstructorAPI.getInstance().getScoreboardManager().onLogout(leaver);
        UUID leaverUUID = leaver.getUniqueId();

        if (teamArena != null) {
            List<UUID> waitingList = arena.getWaitingRoom().getWaitingListByPlayer(arenaList, leaverUUID);
            waitingList.remove(leaverUUID);

            teamArena.getAlluuids().remove(leaverUUID);
            teamArena.getInGamePlayers().remove(leaverUUID);
            team.getTeamByPlayer(leaver, teamArena).getUuidList().remove(leaverUUID);

            if (teamArena.getAlluuids().size() == 1) {
                Player winner = gameManager.getOpponent(leaverUUID, teamArena);
                if (winner.isOnline()) {
                    if (!waitingList.contains(winner.getUniqueId()) && teamArena.getState() == State.PLAYING) {
                        breakOrPlace(teamArena);
                        teamArena.restart();
                        Team pTeam = team.getTeamByPlayer(winner, teamArena);
                        pTeam.getUuidList().remove(winner.getUniqueId());
                        Main.getInstance().getProxyMessaging().connectToAnotherServer(AllNames.LOBBY.getName(), winner);
                        arena.getWaitingRoom().addWaitingPlayerToArena(teamArena, arenaList);

                    }
                }
            }
        }
    }
}
