package org.smither.opwatch.bungee.repos;

import net.md_5.bungee.api.ProxyServer;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.smither.opwatch.bungee.repos.docs.BungeeLocation;
import org.smither.opwatch.bungee.repos.docs.SignChange;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SignRepo {
  private static SessionFactory factory;
  private static SignRepo instance;
  /* Method to CREATE an employee in the database */

  private Session session;

  private SignRepo() {
    session = factory.openSession();
    session.beginTransaction();
  }

  public static SignRepo getInstance() {
    if (instance == null) {
      factory =
          new Configuration()
              .addPackage(
                  "org.smither.opwatch.bungee.repos.docs") // the fully qualified package name
              .addAnnotatedClass(SignChange.class)
              .addAnnotatedClass(BungeeLocation.class)
              .configure()
              .buildSessionFactory();
      instance = new SignRepo();
    }
    return instance;
  }

  @Override
  public void finalize() {
    close();
  }

  public void close() {
    if (session.isOpen()) {
      session.close();
    }
  }

  public void update() {
    session.getTransaction().commit();
    session.beginTransaction();
  }

  public void save(SignChange sc) {
    Integer signID = null;
    try {
      session.persist(sc);
      update();
    } catch (HibernateException e) {
      if (session.getTransaction() != null) session.getTransaction().rollback();
      e.printStackTrace();
    }
  }

  public List<SignChange> findByChecked(boolean b) {
    try {
      List<SignChange> scs =
          session
              .createQuery("FROM SignChange WHERE checked = :checked")
              .setParameter("checked", b)
              .list();
      update();
      return scs;
    } catch (HibernateException e) {
      if (session.getTransaction() != null) session.getTransaction().rollback();
      e.printStackTrace();
    }
    return null;
  }

  public SignChange findByCode(int id) {
    try {
      SignChange sc =
          (SignChange)
              session
                  .createQuery("FROM SignChange WHERE code = :code")
                  .setParameter("code", id)
                  .list()
                  .get(0);
      update();
      return sc;
    } catch (HibernateException e) {
      if (session.getTransaction() != null) session.getTransaction().rollback();
      e.printStackTrace();
    }
    return null;
  }

  public List<SignChange> findByLocation(BungeeLocation loc) {
    Optional sc;
    try {
      ProxyServer.getInstance()
          .getLogger()
          .info(
              String.format(
                  "FROM BungeeLocation WHERE server LIKE %s AND world LIKE %s AND x = %d AND y = %d AND z = %d",
                  loc.getServer(), loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()));
      List<BungeeLocation> locAct =
          session
              .createQuery(
                  "FROM BungeeLocation WHERE server LIKE :server AND world LIKE :world AND x = :x AND y = :y AND z = :z")
              .setParameter("server", loc.getServer())
              .setParameter("world", loc.getWorld())
              .setParameter("x", loc.getX())
              .setParameter("y", loc.getY())
              .setParameter("z", loc.getZ())
              .list();
      List<SignChange> scs = new ArrayList<>();
      for (BungeeLocation loc1 : locAct) {
        scs.addAll(
            session
                .createQuery("FROM SignChange WHERE location = :location")
                .setParameter("location", loc1)
                .list());
        update();
      }
      return scs;
    } catch (HibernateException e) {
      if (session.getTransaction() != null) session.getTransaction().rollback();
      e.printStackTrace();
    }
    return new ArrayList<>();
  }
}
