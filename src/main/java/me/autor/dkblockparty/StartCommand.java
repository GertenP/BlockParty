package me.autor.dkblockparty;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

public class StartCommand implements CommandExecutor {

    private final FileConfiguration config;

    public StartCommand(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Seda käsku saab kasutada ainult mängija.");
            return false;
        }

        Player player = (Player) sender;
        player.getInventory().clear();

        boolean bpEnabled = config.getBoolean("bp");

        if (!bpEnabled) {
            player.sendMessage(ChatColor.RED + "BP ei ole lubatud.");
            return false;
        }

        int x = config.getInt("coordinates.x");
        int z = config.getInt("coordinates.z");
        String worldName = config.getString("maailma_nimi");
        Random rand = new Random();
        int randomNumber = rand.nextInt(9) + 1; // Arvud vahemikus 1 kuni 9
        String path = String.format("C:\\Users\\gerte\\Desktop\\DKBlockParty\\src\\main\\java\\me\\autor\\dkblockparty\\p%d.schem", randomNumber);
        File file = new File(path);
        ClipboardFormat format = ClipboardFormats.findByFile(file);

        if (format == null) {
            player.sendMessage(ChatColor.RED + "Failiformaat ei ole toetatud!");
            return false;
        }

        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            Clipboard clipboard = reader.read();

            World bukkitWorld = Bukkit.getWorld(worldName);
            if (bukkitWorld == null) {
                player.sendMessage(ChatColor.RED + "Maailma ei leitud!");
                return false;
            }

            com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(bukkitWorld);

            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(weWorld, -1)) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(x, 70, z))
                        .ignoreAirBlocks(false)
                        .build();
                Operations.complete(operation);

                // Wait for 3 seconds before giving blocks and starting countdown
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        giveRandomBlocks(player, bukkitWorld, x, z);
                        startCountdown(player, bukkitWorld, x, z);
                    }
                }.runTaskLater(DKBlockParty.getPlugin(DKBlockParty.class), 60); // 60 ticks = 3 seconds

            } catch (WorldEditException e) {
                player.sendMessage(ChatColor.RED + "WorldEdit'i tõrge: " + e.getMessage());
            }
        } catch (IOException e) {
            player.sendMessage(ChatColor.RED + "Faili lugemisel tekkis viga: " + e.getMessage());
            return false;
        }

        return true;
    }

    Random rand = new Random();
    int a = rand.nextInt(40);
    int b = rand.nextInt(40);

    private void giveRandomBlocks(Player player, World world, int x, int z) {
        Block block = world.getBlockAt(x + a + 1, 69, z - b + 1);
        Material blockMaterial = block.getType();
        ItemStack blockItem = new ItemStack(blockMaterial);

        for (int i = 0; i <= 8; i++) {
            player.getInventory().setItem(i, blockItem);
        }
    }

    private void startCountdown(Player player, World world, int x, int z) {
        Block block = world.getBlockAt(x + a + 1, 69, z - b + 1);
        Material blockMaterial = block.getType();
        ItemStack blockItem = new ItemStack(blockMaterial);
        new BukkitRunnable() {
            int countdown = 5;

            @Override
            public void run() {
                if (countdown > 0) {
                    player.sendTitle(Integer.toString(countdown), "", 7, 7, 7);
                    countdown--;
                } else {
                    removeBlocks(world, 209, -112, 257, -160, blockItem.getType());
                    cancel();
                }
            }
        }.runTaskTimer(DKBlockParty.getPlugin(DKBlockParty.class), 0, 20);
    }

    private void removeBlocks(World world, int x1, int z1, int x2, int z2, Material blockMaterial) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                    for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
                        Block block = world.getBlockAt(x, 69, z);
                        if (block.getType() != blockMaterial) {
                            block.setType(Material.AIR);
                        }
                    }
                }
            }
        }.runTask(DKBlockParty.getPlugin(DKBlockParty.class));
    }
}
