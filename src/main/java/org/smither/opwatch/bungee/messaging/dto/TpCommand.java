package org.smither.opwatch.bungee.messaging.dto;

import org.smither.opwatch.bungee.repos.docs.BungeeLocation;

import java.io.Serializable;
import java.util.UUID;

public class TpCommand implements Serializable {
  private BungeeLocation loc;
  private UUID player;

  public TpCommand(BungeeLocation loc, UUID player) {
    this.loc = loc;
    this.player = player;
  }

  public BungeeLocation getLoc() {
    return loc;
  }

  public void setLoc(BungeeLocation loc) {
    this.loc = loc;
  }

  public UUID getPlayer() {
    return player;
  }

  public void setPlayer(UUID player) {
    this.player = player;
  }
}
