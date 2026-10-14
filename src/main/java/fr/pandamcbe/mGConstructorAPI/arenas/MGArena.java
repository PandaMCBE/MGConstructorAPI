package fr.pandamcbe.mGConstructorAPI.arenas;

import org.bukkit.Location;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class MGArena extends Arena {

    public List<Location> spawns;

    public Map<UUID, Location> spawnByPlayer;

    public List<Location> getSpawns() {
        return spawns;
    }

    public void setSpawns(List<Location> Spawns) {
        spawns = Spawns;
    }

    public Map<UUID, Location> getSpawnByPlayer() {
        return spawnByPlayer;
    }

    public void setSpawnByPlayer(Map<UUID, Location> spawnByPlayer) {
        this.spawnByPlayer = spawnByPlayer;
    }

    public abstract void nextRound(MGArena arena, Object o);

}
