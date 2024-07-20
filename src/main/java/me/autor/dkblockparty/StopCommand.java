package me.autor.dkblockparty;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

public class StopCommand implements CommandExecutor {

    private final FileConfiguration config;
    private final File configFile;

    public StopCommand(FileConfiguration config, File configFile) {
        this.config = config;
        this.configFile = configFile;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        boolean v22rtus = config.getBoolean("bp");

        if (v22rtus){
            // pane see configis falseks
            config.set("bp", false);
            sender.sendMessage("BP on nüüd välja lülitatud.");
        } else {
            // pane see configis trueks
            config.set("bp", true);
            sender.sendMessage("BP on nüüd sisse lülitatud.");
        }

        try {
            config.save(configFile);
        } catch (IOException e) {
            sender.sendMessage("Viga konfiguratsioonifaili salvestamisel.");
            e.printStackTrace();
        }

        return true;
    }
}
