package fr.pandamcbe.mGConstructorAPI.arenas;

import org.bukkit.Location;

public abstract class DuelArena extends Arena{

    public Location red_goal;
    public Location blue_goal;

    public Location getRed_goal() {
        return red_goal;
    }

    public void setRed_goal(Location red_goal) {
        this.red_goal = red_goal;
    }

    public Location getBlue_goal() {
        return blue_goal;
    }

    public void setBlue_goal(Location blue_goal) {
        this.blue_goal = blue_goal;
    }

    public abstract void nextDuelRound(Arena arena, Object o);

}