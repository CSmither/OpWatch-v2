package org.smither.opwatch.bungee.messaging;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import net.md_5.bungee.api.ProxyServer;
import org.smither.opwatch.bungee.Opwatch;

public class IRCHandler implements Broadcaster {
  private static IRCHandler instance;
  private PurpleBot ircBot;
  private PurpleIRC purple;
  private Opwatch opwatch = Opwatch.getInstance();

  public IRCHandler() {
    ProxyServer.getInstance()
        .getLogger()
        .info(
            String.join(
                ",",
                ((PurpleIRC)
                        ProxyServer.getInstance().getPluginManager().getPlugin("PurpleBungeeIRC"))
                    .ircBots.keySet()));
  }

  public static IRCHandler getInstance() {
    if (instance == null) {
      instance = new IRCHandler();
    }
    return instance;
  }

  void sendIRC(String msg) {
    ProxyServer.getInstance()
        .getLogger()
        .info(
            "Bots: "
                + String.join(
                    ",",
                    ((PurpleIRC)
                            ProxyServer.getInstance()
                                .getPluginManager()
                                .getPlugin("PurpleBungeeIRC"))
                        .ircBots.keySet()));
    if (ircBot == null) {
      purple =
          (PurpleIRC) ProxyServer.getInstance().getPluginManager().getPlugin("PurpleBungeeIRC");
      if (purple == null) {
        opwatch.getLogger().severe("PURPLE IRC NOT LOADED! ! !");
      }
      ircBot = purple.ircBots.get(opwatch.getConfig().get("PurpleBotName"));
      if (ircBot != null) {
        opwatch.getLogger().info("Hooked to Purple");
      }
    }
    if (ircBot != null) {
      ircBot.asyncIRCMessage((String) opwatch.getConfig().get("PurpleChannel"), msg);
    }
  }

  @Override
  public void broadcast(Message message) {
    sendIRC(message.getHeader() + ": " + message.getContent());
  }
}
