package me.autor.dkblockparty;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class DKBlockParty extends JavaPlugin {

    private static DKBlockParty instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        FileConfiguration config = getConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        getCommand("start").setExecutor(new StartCommand(config));
        getCommand("stop").setExecutor(new StopCommand(config, configFile));
    }

    public static DKBlockParty getPlugin() {
        return instance;
    }
}
