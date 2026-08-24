package fr.pandamcbe.mGConstructorAPI.managers;

import fr.pandamcbe.commons.DuelAccount;
import fr.pandamcbe.mGConstructorAPI.MGConstructorAPI;
import fr.pandamcbe.mGConstructorAPI.Team;
import fr.pandamcbe.mGConstructorAPI.arenas.Arena;
import fr.pandamcbe.mGConstructorAPI.arenas.TeamArena;
import fr.pandamcbe.mgapi.serverutilapi.enums_strings.AllNames;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public abstract class DuelPlayerManager extends OptionalsHooksManager {

    private final GameSessionManager gameManager = MGConstructorAPI.getInstance().getGameManager();
    private final ArenaManager arenaManager = MGConstructorAPI.getInstance().getArenaManager();
    private final WaitingRoom waitingRoomManager = MGConstructorAPI.getInstance().getWaitingRoomManager();
    private final Team team = MGConstructorAPI.getInstance().getTeam();

    public void lookForArena(List<Arena> arenaList, DuelAccount duelAccount, Player p) {
        if (p != null) {

            if (duelAccount != null) {
                String getDuelMode = duelAccount.getGamemode();
                if (arenaManager.getArenaByPlayer(findOpponent(duelAccount, p), arenaList) == null) {
                    Arena nextArena = arenaManager.getNextArena(arenaList, getDuelMode);
                    arenaManager.joinArena(arenaList, nextArena, p);
                } else {
                    Arena rightArena = arenaManager.getArenaByPlayer(findOpponent(duelAccount, p), arenaList);
                    arenaManager.joinArena(arenaList, rightArena, p);
                }
            }
        }
    }

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
        waitingRoomManager.getWaitingRoom().remove(leaverUUID);

        if (teamArena != null) {
            teamArena.getAlluuids().remove(leaverUUID);
            team.getTeamByPlayer(leaver, teamArena).getUuidList().remove(leaverUUID);

            if (teamArena.getAlluuids().size() == 1) {
                Player winner = gameManager.getOpponent(leaverUUID, teamArena);
                if (winner.isOnline()) {
                    if (!waitingRoomManager.getWaitingRoom().contains(winner.getUniqueId()) && teamArena.isStarted) {
                        breakOrPlace(teamArena);
                        teamArena.restart();
                        Team pTeam = team.getTeamByPlayer(winner, teamArena);
                        pTeam.getUuidList().remove(winner.getUniqueId());
                        ByteArrayOutputStream a = new ByteArrayOutputStream();
                        DataOutputStream out3 = new DataOutputStream(a);
                        try {
                            out3.writeUTF("Connect");
                            out3.writeUTF(AllNames.LOBBY.getName());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        winner.sendPluginMessage(MGConstructorAPI.getInstance(), "BungeeCord", a.toByteArray());
                    }
                }
            }
            if (teamArena.getAlluuids().isEmpty()) {
                breakOrPlace(teamArena);
                teamArena.restart();
                waitingRoomManager.addWaitingPlayerToArena(teamArena, arenaList);
            }
        }
    }
}
