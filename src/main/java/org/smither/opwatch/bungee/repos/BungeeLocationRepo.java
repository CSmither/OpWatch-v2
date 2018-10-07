package org.smither.opwatch.bungee.repos;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.smither.opwatch.bungee.repos.docs.BungeeLocation;

public class BungeeLocationRepo {
  private static SessionFactory factory;
  private static BungeeLocationRepo instance;
  /* Method to CREATE an employee in the database */

  private Session session;

  private BungeeLocationRepo() {
    session = factory.openSession();
    session.beginTransaction();
  }

  public static BungeeLocationRepo getInstance() {
    if (instance == null) {
      factory =
          new Configuration()
              .addPackage(
                  "org.smither.opwatch.bungee.repos.docs") // the fully qualified package name
              .addAnnotatedClass(BungeeLocation.class)
              .configure()
              .buildSessionFactory();
      instance = new BungeeLocationRepo();
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

  public void save(BungeeLocationRepo bl) {
    try {
      session.persist(bl);
      update();
    } catch (HibernateException e) {
      if (session.getTransaction() != null) session.getTransaction().rollback();
      e.printStackTrace();
    }
  }

  public BungeeLocation findByLocation(BungeeLocation loc) {
    try {
      BungeeLocation bl =
          (BungeeLocation)
              session
                  .createQuery("FROM BungeeLocation WHERE id = :id")
                  .setParameter("id", loc.getId())
                  .list()
                  .get(0);
      update();
      return bl;
    } catch (HibernateException e) {
      if (session.getTransaction() != null) session.getTransaction().rollback();
      e.printStackTrace();
    }
    return null;
  }
}
