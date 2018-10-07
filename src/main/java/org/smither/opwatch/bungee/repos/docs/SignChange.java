package org.smither.opwatch.bungee.repos.docs;

import org.bukkit.event.block.SignChangeEvent;
import org.hibernate.annotations.Cascade;
import org.smither.opwatch.bungee.misc.SignState;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(indexes = {@Index(name = "IDX_SC_code", columnList = "code")})
public class SignChange implements Serializable {
  @OneToOne
  @JoinColumn()
  @Cascade(org.hibernate.annotations.CascadeType.ALL)
  private BungeeLocation location;

  @Id
  @Column(nullable = false, unique = true)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int code;

  private String[] content;

  @Column(nullable = false)
  private UUID placer;

  private Date time;
  private SignState state;
  private boolean checked;
  private String wiper;
  private UUID destroyer;

  public SignChange() {}

  public SignChange(BungeeLocation location, String[] content, UUID placer, Date time) {
    this.location = location;
    this.content = content;
    this.placer = placer;
    this.time = time;
    this.state = SignState.original;
    this.checked = false;
  }

  public SignChange(SignChangeEvent event) {
    location = new BungeeLocation(event.getBlock().getLocation());
    content = event.getLines();
    placer = event.getPlayer().getUniqueId();
    time = new Date();
    state = SignState.original;
    checked = false;
  }

  public String getWiper() {
    return wiper;
  }

  public void setWiper(String wiper) {
    this.wiper = wiper;
  }

  public UUID getDestroyer() {
    return destroyer;
  }

  public void setDestroyer(UUID destroyer) {
    this.destroyer = destroyer;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public boolean isChecked() {
    return checked;
  }

  public BungeeLocation getLocation() {
    return location;
  }

  public void setLocation(BungeeLocation location) {
    this.location = location;
  }

  public String[] getContent() {
    return content;
  }

  public void setContent(String[] content) {
    this.content = content;
  }

  public UUID getPlacer() {
    return placer;
  }

  public void setPlacer(UUID placer) {
    this.placer = placer;
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public SignState getState() {
    return state;
  }

  public void setState(SignState state) {
    this.state = state;
  }

  public boolean getChecked() {
    return checked;
  }

  public void setChecked(boolean checked) {
    this.checked = checked;
  }
}
