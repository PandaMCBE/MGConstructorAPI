package fr.pandamcbe.mGConstructorAPI;

import fr.pandamcbe.mGConstructorAPI.listeners.ParameterListeners;
import fr.pandamcbe.mGConstructorAPI.managers.ArenaManager;
import fr.pandamcbe.mGConstructorAPI.managers.GameSessionManager;
import fr.pandamcbe.mgapi.serverutilapi.scoreboards.ScoreboardManager;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class MGConstructorAPI extends JavaPlugin {

    private static MGConstructorAPI INSTANCE;
    public LuckPerms luckPerms;
    public ArenaManager arenaManager;
    public GameSessionManager gameManager;
    public Team team;

    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        getServer().getPluginManager().registerEvents(new ParameterListeners(), this);

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        }

        arenaManager = new ArenaManager();
        gameManager = new GameSessionManager();
        team = new Team();

        this.scoreboardManager = new ScoreboardManager();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.scoreboardManager.onDisable();
        super.onDisable();
    }

    public static MGConstructorAPI getInstance(){
        return INSTANCE;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public GameSessionManager getGameManager() {
        return gameManager;
    }

    public Team getTeam() {
        return team;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

}