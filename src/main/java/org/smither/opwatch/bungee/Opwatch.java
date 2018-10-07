package org.smither.opwatch.bungee;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import org.smither.opwatch.bungee.messaging.BroadcastManager;
import org.smither.opwatch.bungee.messaging.ChannelListener;
import org.smither.opwatch.bungee.messaging.IRCHandler;
import org.smither.opwatch.bungee.repos.SignRepo;

import java.io.*;

public class Opwatch extends net.md_5.bungee.api.plugin.Plugin {
  private static Opwatch instance;
  public boolean debug = true;
  private Configuration config;
  private ChannelListener cl;

  public Opwatch() {
    instance = this;
    cl = ChannelListener.getInstance();
  }

  public static Opwatch getInstance() {
    if (instance == null) {
      instance = new Opwatch();
    }
    return instance;
  }

  @Override
  public void onEnable() {
    if (!getDataFolder().exists()) {
      getDataFolder().mkdir();
    }
    File configFile = new File(getDataFolder(), "config.yml");
    if (!configFile.exists()) {
      try {
        configFile.createNewFile();
        try (InputStream is = getResourceAsStream("bungeeConfig.yml");
            OutputStream os = new FileOutputStream(configFile)) {
          ByteStreams.copy(is, os);
        }
      } catch (IOException e) {
        throw new RuntimeException("Unable to create configuration file", e);
      }
    }
    try {
      config =
          ConfigurationProvider.getProvider(YamlConfiguration.class)
              .load(new File(getDataFolder(), "config.yml"));
      org.hibernate.cfg.Configuration c = new org.hibernate.cfg.Configuration();
      c.configure();
      c.setProperty(
          "hibernate.connection.url",
          "jdbc:mysql://"
              + config.getString("databaseHost")
              //              + ":"
              //              + config.getString("databasePort")
              + "/"
              + config.getString("databaseName"));
      c.setProperty("hibernate.connection.username", config.getString("databaseUser"));
      c.setProperty("hibernate.connection.password", config.getString("databasePass"));

    } catch (IOException e) {
      e.printStackTrace();
    }
    debug = getConfig().getBoolean("debug");
    getProxy().getPluginManager().registerListener(instance, cl);
    getProxy().registerChannel("BungeeCord");
    getProxy().getPluginManager().registerCommand(instance, new OpWatchCommandHandler("OpWatch"));
    getProxy().getPluginManager().registerCommand(instance, new OpWatchCommandHandler("ow"));
    getLogger().info("OPWATCH ready to go!");
    BroadcastManager.getInstance().register("irc", IRCHandler.getInstance());
  }

  @EventHandler
  public void onServerConnectEvent(ServerConnectEvent event) {
    cl.refreshServers();
  }

  @EventHandler
  public void onServerDisconnectEvent(ServerDisconnectEvent event) {
    cl.refreshServers();
  }

  public Configuration getConfig() {
    return config;
  }

  @Override
  public void onDisable() {
    try {
      ConfigurationProvider.getProvider(YamlConfiguration.class)
          .save(getConfig(), new File(getDataFolder(), "config.yml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    SignRepo.getInstance().close();
  }

  public void onReload() {
    onEnable();
  }
}
