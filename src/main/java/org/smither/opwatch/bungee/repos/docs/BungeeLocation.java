package org.smither.opwatch.bungee.repos.docs;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table()
public class BungeeLocation implements Serializable {

  @Id
  @Column
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false, length = 128)
  private String server;

  @Column(nullable = false)
  private int x;

  @Column(nullable = false)
  private int y;

  @Column(nullable = false)
  private int z;

  @Column(nullable = false, length = 128)
  private String world;

  public BungeeLocation() {}

  public BungeeLocation(String server, int x, int y, int z, String world) {
    this.server = server;
    this.x = x;
    this.y = y;
    this.z = z;
    this.world = world;
  }

  public BungeeLocation(Location location) {
    x = location.getBlockX();
    y = location.getBlockY();
    z = location.getBlockZ();
    world = location.getWorld().getName();
  }

  public long getId() {
    return id;
  }

  public String getServer() {
    return server;
  }

  public void setServer(String server) {
    this.server = server;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public int getZ() {
    return z;
  }

  public void setZ(int z) {
    this.z = z;
  }

  public String getWorld() {
    return world;
  }

  public void setWorld(String world) {
    this.world = world;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BungeeLocation that = (BungeeLocation) o;
    return x == that.x
        && y == that.y
        && z == that.z
        && Objects.equals(server, that.server)
        && Objects.equals(world, that.world);
  }

  @Override
  public int hashCode() {
    return Objects.hash(server, x, y, z, world);
  }

  public Location getLocation() {
    return new Location(Bukkit.getWorld(world), getX(), getY(), getZ());
  }
}
