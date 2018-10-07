package org.smither.opwatch.bungee.messaging.dto;

import org.smither.opwatch.bungee.repos.docs.BungeeLocation;

import java.io.Serializable;
import java.util.UUID;

public class SignDestroyEvent implements Serializable {
  BungeeLocation location;
  UUID destroyer;

  public SignDestroyEvent() {}

  public SignDestroyEvent(BungeeLocation location, UUID destroyer) {
    this.location = location;
    this.destroyer = destroyer;
  }

  public BungeeLocation getLocation() {
    return location;
  }

  public void setLocation(BungeeLocation location) {
    this.location = location;
  }

  public UUID getDestroyer() {
    return destroyer;
  }

  public void setDestroyer(UUID destroyer) {
    this.destroyer = destroyer;
  }
}
