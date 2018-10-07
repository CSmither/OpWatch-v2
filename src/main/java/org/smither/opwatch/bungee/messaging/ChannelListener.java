package org.smither.opwatch.bungee.messaging;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.smither.opwatch.bungee.Opwatch;
import org.smither.opwatch.bungee.RegexChecker;
import org.smither.opwatch.bungee.messaging.dto.SignDestroyEvent;
import org.smither.opwatch.bungee.messaging.dto.SignWipeReply;
import org.smither.opwatch.bungee.misc.SignState;
import org.smither.opwatch.bungee.misc.SignWipeResult;
import org.smither.opwatch.bungee.repos.SignRepo;
import org.smither.opwatch.bungee.repos.docs.SignChange;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChannelListener implements Listener {
  private static ChannelListener instance;
  private Opwatch plugin;
  private BroadcastManager broadcastManager;
  private SignRepo signRepo;
  private Map<InetSocketAddress, String> servers;

  public ChannelListener() {
    this.servers = new HashMap<>();
    Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
    if (servers != null && servers.size() > 0) {
      for (String serverName : servers.keySet()) {
        this.servers.put(servers.get(serverName).getAddress(), servers.get(serverName).getName());
      }
    }
    signRepo = SignRepo.getInstance();
    plugin = Opwatch.getInstance();
    broadcastManager = BroadcastManager.getInstance();
  }

  /** Read the object from Base64 string. */
  private static Object fromString(String s) throws IOException, ClassNotFoundException {
    byte[] data = Base64.getDecoder().decode(s);
    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
    Object o = ois.readObject();
    ois.close();
    return o;
  }

  public static ChannelListener getInstance() {
    if (instance == null) {
      instance = new ChannelListener();
    }
    return instance;
  }

  public void refreshServers() {
    this.servers = new HashMap<>();
    Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
    if (servers.size() > 0) {
      for (String serverName : servers.keySet()) {
        this.servers.put(servers.get(serverName).getAddress(), servers.get(serverName).getName());
      }
    }
  }

  @EventHandler
  public void onPluginMessage(PluginMessageEvent e) {
    if (!servers.containsKey(e.getSender().getAddress())) {
      refreshServers();
    }
    ProxyServer.getInstance().getLogger().info("There are " + servers.size() + " servers");
    if (e.getTag().equalsIgnoreCase("BungeeCord")) {
      DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));
      try {
        String channel = in.readUTF(); // channel we delivered
        String input = in.readUTF();
        switch (channel) {
          case "SignAdded":
            try {
              SignChange sc = (SignChange) fromString(input);
              sc.getLocation().setServer(servers.get(e.getSender().getAddress()));
              signRepo.save(sc);
              ProxyServer.getInstance()
                  .getLogger()
                  .info(
                      "check regex, matches? "
                          + RegexChecker.getInstance().check(String.join("", sc.getContent())));
              if (RegexChecker.getInstance().check(String.join("", sc.getContent()))) {
                System.out.println("MESSAGE ALERT");
                broadcastManager.sendMessage(
                    new Message(
                        "OPWATCH ALERT!!!",
                        String.format(
                            "Sign %d: \"%s, %s, %s, %s\" at %s %s %d,%d,%d " + "placed by %s",
                            sc.getCode(),
                            sc.getContent()[0],
                            sc.getContent()[1],
                            sc.getContent()[2],
                            sc.getContent()[3],
                            sc.getLocation().getServer(),
                            sc.getLocation().getWorld(),
                            sc.getLocation().getX(),
                            sc.getLocation().getY(),
                            sc.getLocation().getZ(),
                            ProxyServer.getInstance().getPlayer(sc.getPlacer()).getDisplayName())));
              }
            } catch (ClassNotFoundException | IOException exception) {
              ProxyServer.getInstance()
                  .getLogger()
                  .severe("Error creating SignChange from message from spigot");
              broadcastManager.sendMessage(
                  new Message("ERROR", "Failed to convert sign from server"));
            }
            break;
          case "SignWipeReply":
            try {
              SignWipeReply reply = (SignWipeReply) fromString(input);
              signRepo
                  .findByCode(reply.getCode())
                  .setState(
                      reply.getResult() == SignWipeResult.success
                          ? SignState.wiped
                          : reply.getResult() == SignWipeResult.removed
                              ? SignState.removed
                              : SignState.attemptedWipe);
            } catch (ClassNotFoundException | IOException exception) {
              broadcastManager.sendMessage(
                  new Message("ERROR", "Failed to convert sign from server"));
            }
            break;
          case "SignDestroyed":
            try {
              SignDestroyEvent sde = (SignDestroyEvent) fromString(input);
              List<SignChange> scs = signRepo.findByLocation(sde.getLocation());
              for (SignChange sc : scs) {
                sc.setState(SignState.removed);
                sc.setDestroyer(sde.getDestroyer());
                signRepo.update();
              }
            } catch (ClassNotFoundException | IOException exception) {
              broadcastManager.sendMessage(
                  new Message("ERROR", "Failed to convert sign from server"));
            }
            break;
          default:
            break;
        }
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }
  }

  public void sendToBukkit(String channel, String message, String server) {
    ServerInfo targetServer = ProxyServer.getInstance().getServers().get(server);
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(stream);
    try {
      out.writeUTF(channel);
      out.writeUTF(message);
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (!targetServer.sendData("BungeeCord", stream.toByteArray(), true)) {}
  }
}
