package fr.pandamcbe.mGConstructorAPI.arenas;

import fr.pandamcbe.mGConstructorAPI.managers.OptionalsHooksManager;
import fr.pandamcbe.mGConstructorAPI.managers.WaitingRoom;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;

public abstract class Arena extends OptionalsHooksManager implements Cloneable{
    public List<UUID> Alluuids;
    public List<UUID> inGamePlayers;
    public Map<Location, Material> blocks;
    public boolean isStarted;
    public String name;
    public String mode;
    public State state;
    public Location middlespawn;
    public Map<UUID, Integer> wonRounds;
    public int rounds;
    public int maxRounds;
    public int timerCycle;
    public int startingTimer;
    public int maxPlayersAllowed;
    public int minimumPlayers;
    public WaitingRoom waitingRoom;
//    private World world;

    public List<UUID> getAlluuids() {
        return Alluuids;
    }

    public void setAlluuids(List<UUID> alluuids) {
        Alluuids = alluuids;
    }

    public List<UUID> getInGamePlayers() {
        return inGamePlayers;
    }

    public void setInGamePlayers(List<UUID> inGamePlayers) {
        this.inGamePlayers = inGamePlayers;
    }

    public Map<Location, Material> getBlocks() {
        return blocks;
    }

    public void setBlocks(Map<Location, Material> blocks) {
        this.blocks = blocks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public Location getMiddlespawn() {
        return middlespawn;
    }

    public void setMiddlespawn(Location middlespawn) {
        this.middlespawn = middlespawn;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public Map<UUID, Integer> getWonRounds() {
        return wonRounds;
    }

    public void setWonRounds(Map<UUID, Integer> wonRounds) {
        this.wonRounds = wonRounds;
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    public void setMaxRounds(int maxRounds) {
        this.maxRounds = maxRounds;
    }

    public int getTimerCycle() {
        return timerCycle;
    }

    public void setTimerCycle(int timerCycle) {
        this.timerCycle = timerCycle;
    }

    public int getStartingTimer() {
        return startingTimer;
    }

    public void setStartingTimer(int startingTimer) {
        this.startingTimer = startingTimer;
    }

    public int getMaxPlayersAllowed() {
        return maxPlayersAllowed;
    }

    public void setMaxPlayersAllowed(int maxPlayersAllowed) {
        this.maxPlayersAllowed = maxPlayersAllowed;
    }

    public int getMinimumPlayers() {
        return minimumPlayers;
    }

    public void setMinimumPlayers(int minimumPlayers) {
        this.minimumPlayers = minimumPlayers;
    }

    public WaitingRoom getWaitingRoom() {
        return waitingRoom;
    }

    public void setWaitingRoom(WaitingRoom waitingRoom) {
        this.waitingRoom = waitingRoom;
    }

    public void restart() {
        this.Alluuids = new ArrayList<>();
        this.inGamePlayers = new ArrayList<>();
        this.blocks = new HashMap<>();
        this.isStarted = false;
        this.state = State.FINISHED;
        this.wonRounds = new HashMap<>();
        this.rounds = 0;
        this.timerCycle = 0;
        this.startingTimer = 30;
        ((TeamArena) this).resetTeams();
    }

    public Arena clone() {
        try {
            return (Arena) super.clone();
        } catch (CloneNotSupportedException e){
            e.printStackTrace();
        }
        return null;
    }

//    public abstract void OnChangingRoundCore(Arena arena, List<UUID> waitingPlayers, List<Arena> arenaList,Player winner);
}