package fr.pandamcbe.mGConstructorAPI.arenas;

import org.bukkit.Location;

import java.util.List;

public abstract class MGArena extends Arena {

    public List<Location> spawns;

    public List<Location> getSpawns() {
        return spawns;
    }

    public void setSpawns(List<Location> Spawns) {
        spawns = Spawns;
    }

    public abstract void nextMGRound(Arena arena, Object o);

}
