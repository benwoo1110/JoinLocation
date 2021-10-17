package dev.benergy10.joinlocation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class JoinLocation extends JavaPlugin implements Listener {

    private static final String JOIN_LOCATION_KEY = "join-location";

    private FileConfiguration config;
    private Location joinLocation;

    @Override
    public void onEnable() {
        setupConfig();
        setupCommands();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private void setupCommands() {
        getCommand("setjoinlocation").setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You need to be a player to run this command.");
                return true;
            }
            Player player = ((Player) sender);
            config.set(JOIN_LOCATION_KEY, player.getLocation());
            saveConfig();
            return true;
        });

        getCommand("joinlocation").setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You need to be a player to run this command.");
                return true;
            }
            Player player = ((Player) sender);
            teleportToJoinLocation(player);
            return true;
        });
    }

    private void setupConfig() {
        saveDefaultConfig();
        config = getConfig();

        try {
            if (config.contains(JOIN_LOCATION_KEY)) {
                joinLocation = config.getLocation(JOIN_LOCATION_KEY);
            }
        } catch (IllegalArgumentException ignored) {
            ignored.printStackTrace();
            // TODO Wait for world load.
            return;
        }

        if (joinLocation == null) {
            World defaultWorld = Bukkit.getWorlds().get(0);
            config.set(JOIN_LOCATION_KEY, defaultWorld.getSpawnLocation());
        }

        saveConfig();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        teleportToJoinLocation(event.getPlayer());
    }

    private void teleportToJoinLocation(Player player) {
        if (player.hasPermission("joinlocation.joinlocation") && !player.getLocation().equals(joinLocation)) {
            player.teleport(joinLocation);
        }
    }
}
