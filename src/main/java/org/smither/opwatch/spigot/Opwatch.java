package org.smither.opwatch.spigot;

import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.smither.opwatch.bungee.messaging.dto.SignDestroyEvent;
import org.smither.opwatch.bungee.messaging.dto.WipeSignCommand;
import org.smither.opwatch.bungee.misc.SignWipeResult;
import org.smither.opwatch.bungee.misc.Stringifier;
import org.smither.opwatch.bungee.repos.docs.BungeeLocation;
import org.smither.opwatch.bungee.repos.docs.SignChange;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public final class Opwatch extends JavaPlugin implements Listener {
  private static Opwatch instance;
  PluginChannelListener pcl;
  private FileConfiguration config;
  private Map<UUID, Location> tps = new HashMap<>();

  public static Opwatch getInstance() {
    return instance;
  }

  public void onEnable() {
    instance = this;
    if (!getDataFolder().exists()) {
      getDataFolder().mkdir();
    }
    File configFile = new File(getDataFolder(), "config.yml");
    if (!configFile.exists()) {
      try {
        configFile.createNewFile();
        try (InputStream is = new FileInputStream(new File("src/main/resources/spigotConfig.yml"));
            OutputStream os = new FileOutputStream(configFile)) {
          ByteStreams.copy(is, os);
        }
      } catch (IOException e) {
        throw new RuntimeException("Unable to create configuration file", e);
      }
    }
    pcl = new PluginChannelListener();
    config = getConfig();

    Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    // allow to send to BungeeCord
    Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", pcl);
    // gets a Message from Bungee
    getServer().getPluginManager().registerEvents(this, this);
  }

  @EventHandler
  public void onPlayerJoinEvent(PlayerJoinEvent event) {
    if (tps.containsKey(event.getPlayer().getUniqueId())) {
      event.getPlayer().teleport(tps.get(event.getPlayer().getUniqueId()));
      tps.remove(event.getPlayer().getUniqueId());
    }
  }

  @EventHandler
  public void onSignChangeEvent(SignChangeEvent event) {
    Bukkit.getLogger().log(Level.INFO, "Sign Change Detected");
    if (!String.join("", event.getLines()).replace(" ", "").equals("")) {
      try {
        pcl.sendToBungeeCord(
            event.getPlayer(), "SignAdded", Stringifier.toString(new SignChange(event)));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @EventHandler
  public void onSignDestroy(BlockBreakEvent event) {
    if (event.getBlock().getState().getType().equals(Material.SIGN)
        | event.getBlock().getState().getType().equals(Material.SIGN_POST)) {
      try {
        pcl.sendToBungeeCord(
            event.getPlayer(),
            "SignDestroyed",
            Stringifier.toString(
                new SignDestroyEvent(
                    new BungeeLocation(
                        Bukkit.getServerName(),
                        event.getBlock().getX(),
                        event.getBlock().getY(),
                        event.getBlock().getZ(),
                        event.getBlock().getWorld().getName()),
                    event.getPlayer().getUniqueId())));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @EventHandler
  public void onInventoryClickEvent(InventoryClickEvent event) { // TODO REDO THIS METHOD
    if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.NAME_TAG) {
      Bukkit.getLogger()
          .log(
              Level.INFO,
              "InventoryClickEvent event=>"
                  + event.getCurrentItem().getItemMeta().getDisplayName());
    }
  }

  @EventHandler
  public void onPlayerEditBookEvent(PlayerEditBookEvent event) { // TODO REDO THIS METHOD
    Bukkit.getLogger().log(Level.INFO, "Player edit book event=>" + event.toString());
  }

  SignWipeResult wipeSign(WipeSignCommand com) {
    Bukkit.getLogger().info("Wiping sign");
    Location loc =
        new Location(
            Bukkit.getWorld(com.getSc().getLocation().getWorld()),
            com.getSc().getLocation().getX(),
            com.getSc().getLocation().getY(),
            com.getSc().getLocation().getZ());
    try {
      Bukkit.getLogger().info("Getting sign");
      if (!(loc.getBlock().getState().getType().equals(Material.SIGN)
          | loc.getBlock().getState().getType().equals(Material.SIGN_POST))) {
        Bukkit.getLogger().info("Sign removed: It is now ->" + loc.getBlock().getState().getType());
        return SignWipeResult.removed;
      }
      Sign sign = (Sign) (loc.getBlock().getState());
      Bukkit.getLogger().info("Sign got and cast");
      sign.setLine(0, com.getNewContent()[0]);
      sign.setLine(1, com.getNewContent()[1]);
      sign.setLine(2, com.getNewContent()[2]);
      sign.setLine(3, com.getNewContent()[3]);
      sign.update();
      Bukkit.getLogger().info("Sign wiped");
      return SignWipeResult.success;
    } catch (Exception e) {
      return SignWipeResult.error;
    }
  }

  public void addTP(UUID player, BungeeLocation loc) {
    Bukkit.getLogger().log(Level.INFO, String.format("TP to sign %s:%s", player, loc.toString()));
    for (Player curpl : Bukkit.getOnlinePlayers()) {
      Bukkit.getLogger().info(curpl.getDisplayName() + ": " + curpl.getUniqueId());
    }
    if (Bukkit.getPlayer(player) != null) {
      Bukkit.getLogger().log(Level.INFO, String.format("Player on server"));
      Bukkit.getPlayer(player).teleport(loc.getLocation());
    } else {
      Bukkit.getLogger().log(Level.INFO, String.format("Player NOT on server"));
      tps.put(player, loc.getLocation());
    }
  }
}
