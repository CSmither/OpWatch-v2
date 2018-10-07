package org.smither.opwatch.spigot;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.smither.opwatch.bungee.messaging.dto.SignWipeReply;
import org.smither.opwatch.bungee.messaging.dto.TpCommand;
import org.smither.opwatch.bungee.messaging.dto.WipeSignCommand;
import org.smither.opwatch.bungee.misc.SignWipeResult;
import org.smither.opwatch.bungee.misc.Stringifier;

import java.io.*;

public class PluginChannelListener implements PluginMessageListener {

  @Override
  public void onPluginMessageReceived(String channel, Player player, byte[] message) {
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
    try {
      switch (in.readUTF().toLowerCase()) {
        case "wipesign":
          WipeSignCommand wsc = (WipeSignCommand) Stringifier.fromString(in.readUTF());
          SignWipeResult result = Opwatch.getInstance().wipeSign(wsc);
          sendToBungeeCord(
              player,
              "SignWipeReply",
              Stringifier.toString(
                  new SignWipeReply(wsc.getSc().getCode(), wsc.getSc().getLocation(), result)));
          break;
        case "tpplayer":
          TpCommand tpc = (TpCommand) Stringifier.fromString(in.readUTF());
          Opwatch.getInstance().addTP(tpc.getPlayer(), tpc.getLoc());
          break;
      }
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void sendToBungeeCord(Player p, String channel, String sub) {
    Opwatch.getPlugin(Opwatch.class).getLogger().info(channel + " : " + sub);
    ByteArrayOutputStream b = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(b);
    try {
      out.writeUTF(channel);
      out.writeUTF(sub);
    } catch (IOException e) {
      e.printStackTrace();
    }
    p.sendPluginMessage(Opwatch.getPlugin(Opwatch.class), "BungeeCord", b.toByteArray());
  }
}
