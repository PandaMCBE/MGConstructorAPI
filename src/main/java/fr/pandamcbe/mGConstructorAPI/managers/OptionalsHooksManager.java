package fr.pandamcbe.mGConstructorAPI.managers;

import fr.pandamcbe.mGConstructorAPI.arenas.Arena;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class OptionalsHooksManager {

    public void startCountDown(Arena mgArena){}

    public void breakOrPlace(Arena arena){}

    public void playerQuits(Arena arena, Player leaver) {}

    public void respawning(Arena arena, Player player) {}

    public String getPrefix() {return null;}

    public ChatColor getColor() {return null;}

    public void customWinEvent(Object o){};

    public void customJoinEvent(Player player){}
}