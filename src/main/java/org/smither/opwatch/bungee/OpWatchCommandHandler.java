package org.smither.opwatch.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Command;
import org.smither.opwatch.bungee.messaging.ChannelListener;
import org.smither.opwatch.bungee.messaging.dto.TpCommand;
import org.smither.opwatch.bungee.messaging.dto.WipeSignCommand;
import org.smither.opwatch.bungee.misc.MCApi;
import org.smither.opwatch.bungee.misc.SignState;
import org.smither.opwatch.bungee.misc.Stringifier;
import org.smither.opwatch.bungee.repos.SignRepo;
import org.smither.opwatch.bungee.repos.docs.SignChange;

import java.io.IOException;
import java.util.*;

public class OpWatchCommandHandler extends Command {
  String[] wipeMsg;
  private Opwatch opwatch = Opwatch.getInstance();
  private SignRepo signRepo = SignRepo.getInstance();
  private ChannelListener channelListener = ChannelListener.getInstance();

  public OpWatchCommandHandler(String string) {
    super(string);
    List<String> wipeMsg = new ArrayList<String>();
    for (Object line : opwatch.getConfig().getList("WipeMsg")) {
      wipeMsg.add((String) line);
    }
    this.wipeMsg = wipeMsg.toArray(new String[4]);
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    Map<String, String> props = new HashMap<>();
    for (String arg : Arrays.copyOfRange(args, 1, args.length)) {
      String[] kv = arg.split(":");
      if (kv.length == 2) {
        props.put(kv[0], kv[1]);
      }
    }
    switch (args[0].toLowerCase()) {
      case "reload":
        Opwatch.getInstance().onReload();
      case "viewsign":
        if (props.containsKey("id")) {
          SignChange sc = signRepo.findByCode(Integer.parseInt(props.get("id")));
          sender.sendMessage(
              new ComponentBuilder(
                      String.format(
                          "%d: \"%s, %s, %s, %s\" at %s %s %d,%d,%d " + "placed by %s%s",
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
                          MCApi.getUsername(sc.getPlacer()),
                          sc.getState() == SignState.wiped
                              ? ", Has been Wiped by " + sc.getWiper()
                              : sc.getState() == SignState.attemptedWipe
                                  ? ", Wipe was attempted by" + sc.getWiper()
                                  : sc.getState() == SignState.removed
                                      ? ", Has been destroyed by "
                                          + ProxyServer.getInstance()
                                              .getPlayer(sc.getDestroyer())
                                              .getDisplayName()
                                      : ""))
                  .create());
        }
        break;
      case "viewsigns":
        List<SignChange> signs = signRepo.findByChecked(false);
        for (SignChange sc : signs) {
          sender.sendMessage(
              new ComponentBuilder(
                      String.format(
                          "%d: \"%s, %s, %s, %s\" at %s %s %d,%d,%d " + "placed by %s%s",
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
                          MCApi.getUsername(sc.getPlacer()),
                          sc.getState() == SignState.wiped
                              ? ", Has been Wiped by " + sc.getWiper()
                              : sc.getState() == SignState.attemptedWipe
                                  ? ", Wipe was attempted by" + sc.getWiper()
                                  : sc.getState() == SignState.removed
                                      ? ", Has been destroyed by "
                                          + MCApi.getUsername(sc.getDestroyer())
                                      : ""))
                  .create());
          sc.setChecked(true);
        }
        signRepo.update();
        break;
      case "wipesign":
        if ((props.containsKey("id") || props.containsKey("i"))) {
          int id = Integer.parseInt(props.containsKey("id") ? props.get("id") : props.get("i"));
          SignChange sc = signRepo.findByCode(id);
          sc.setState(SignState.attemptedWipe);
          sc.setWiper(sender.getName());
          signRepo.update();
          try {
            channelListener.sendToBukkit(
                "wipeSign",
                Stringifier.toString(
                    new WipeSignCommand(
                        sc,
                        Opwatch.getInstance()
                            .getConfig()
                            .getStringList("WipeMsg")
                            .toArray(new String[] {}))),
                sc.getLocation().getServer());
          } catch (IOException e) {
            e.printStackTrace();
          }
          sender.sendMessage(
              new ComponentBuilder(
                      String.format(
                          "%d: \"%s, %s, %s, %s\" at %s %s %d,%d,%d " + "placed by %s%s",
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
                          MCApi.getUsername(sc.getPlacer()),
                          sc.getState() == SignState.wiped
                              ? ", Has been Wiped by " + sc.getWiper()
                              : sc.getState() == SignState.attemptedWipe
                                  ? ", Wipe was attempted by" + sc.getWiper()
                                  : sc.getState() == SignState.removed
                                      ? ", Has been destroyed by "
                                          + MCApi.getUsername(sc.getDestroyer())
                                      : ""))
                  .create());
        }
        break;
      case "tpsign":
        if ((props.containsKey("id") || props.containsKey("i"))
            && (props.containsKey("player") || props.containsKey("p"))) {
          int id = Integer.parseInt(props.containsKey("id") ? props.get("id") : props.get("i"));
          String player = props.containsKey("player") ? props.get("player") : props.get("p");
          SignChange sc = signRepo.findByCode(id);
          sc.setState(SignState.attemptedWipe);
          try {
            channelListener.sendToBukkit(
                "tpPlayer",
                Stringifier.toString(
                    new TpCommand(
                        sc.getLocation(),
                        ProxyServer.getInstance().getPlayer(player).getUniqueId())),
                sc.getLocation().getServer());
            ProxyServer.getInstance()
                .getPlayer(player)
                .connect(
                    ProxyServer.getInstance().getServerInfo(sc.getLocation().getServer()),
                    ServerConnectEvent.Reason.valueOf("PLUGIN"));
          } catch (IOException e) {
            e.printStackTrace();
          }
          sender.sendMessage(
              new ComponentBuilder(
                      String.format(
                          "Teleporting %s to Sign %d on %s %s",
                          ProxyServer.getInstance().getPlayer(player).getUniqueId(),
                          sc.getCode(),
                          sc.getLocation().getServer(),
                          sc.getLocation().getWorld()))
                  .create());
        }
        break;
      case "about":
        sender.sendMessage(
            new ComponentBuilder(
                    opwatch.getInstance().getDescription().getName()
                        + ("\nAuthor: " + opwatch.getInstance().getDescription().getAuthor())
                        + ("\nVersion: " + opwatch.getInstance().getDescription().getVersion()))
                .create());
        break;
      case "help":
        sender.sendMessage(
            new ComponentBuilder(
                    "OpWatch Command Help:"
                        + "\nAllowed abbreviations:"
                        + "\n  id=>i"
                        + "\n  player=>p"
                        + "\nCommands:"
                        + "\n  /ow ViewSign id:<id>"
                        + "\n  /ow ViewSigns"
                        + "\n  /ow TpSign id:<id> player:<player>")
                .create());
        break;
    }
  }
}
