package io.github.townyadvanced.iconomy.system;

import io.github.townyadvanced.iconomy.iConomyUnlocked;
import io.github.townyadvanced.iconomy.settings.Settings;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Accounts {

  private final String SQLTable = Settings.getDBTable();

  /**
   * Check if an Account exists with this uuid.
   *
   * @param uuid the UUID to check
   *
   * @return true if an Account exists.
   */
  public boolean exists(final UUID uuid) {

    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement ps = null;
    boolean exists = false;
    try {
      conn = iConomyUnlocked.getBackEnd().getConnection();
      ps = conn.prepareStatement("SELECT * FROM " + SQLTable + " WHERE uuid = ? LIMIT 1");
      ps.setString(1, uuid.toString());
      rs = ps.executeQuery();
      exists = rs.next();
    } catch(final Exception ex) {
      exists = false;
    } finally {
      iConomyUnlocked.getBackEnd().close(conn, ps, rs);
    }
    return exists;
  }

  /**
   * Check if an Account exists with this name.
   *
   * @param name the name to check
   *
   * @return true if an Account exists.
   */
  public boolean exists(final String name) {

    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement ps = null;
    boolean exists = false;
    try {
      conn = iConomyUnlocked.getBackEnd().getConnection();
      ps = conn.prepareStatement("SELECT * FROM " + SQLTable + " WHERE username = ? LIMIT 1");
      ps.setString(1, name);
      rs = ps.executeQuery();
      exists = rs.next();
    } catch(final Exception ex) {
      exists = false;
    } finally {
      iConomyUnlocked.getBackEnd().close(conn, ps, rs);
    }
    return exists;
  }

  /**
   * Create an Account.
   *
   * @param uuid the Account uuid.
   * @param name the Account name.
   *
   * @return true if successful.
   */
  public boolean create(final UUID uuid, final String name, final boolean nonPlayer) {

    Connection conn = null;
    PreparedStatement ps = null;
    try {
      conn = iConomyUnlocked.getBackEnd().getConnection();
      ps = conn.prepareStatement("INSERT INTO " + SQLTable + "(uuid, username, balance, hidden, nonplayer) VALUES (?, ?, ?, 0, ?)");
      ps.setString(1, uuid.toString());
      ps.setString(2, name);
      ps.setBigDecimal(3, Settings.getDefaultBalance());
      ps.setBoolean(4, nonPlayer);
      ps.executeUpdate();
    } catch(final Exception e) {
      return false;
    } finally {
      iConomyUnlocked.getBackEnd().close(conn, ps);
    }
    return true;
  }

  /**
   * Imports an Account.
   *
   * @param name    the Account name.
   * @param balance the balance.
   *
   * @return true if successful.
   */
  public boolean importAccount(final String uuidraw, final String name, final BigDecimal balance, final boolean hidden) {

    final UUID uuid = UUID.fromString(uuidraw);
    if(uuid == null) { return false; }

    if(exists(uuid)) {
      final Account account = Account.getAccount(uuid);
      account.setName(name);
      account.getHoldings().set(balance);
      account.setHidden(hidden);
      account.setNonPlayer(Settings.isNonPlayerAccountName(name));
      return true;
    }

    Connection conn = null;
    PreparedStatement ps = null;
    try {
      conn = iConomyUnlocked.getBackEnd().getConnection();
      ps = conn.prepareStatement("INSERT INTO " + SQLTable + "(uuid, username, balance, hidden, nonplayer) VALUES (?, ?, ?, ?, ?)");
      ps.setString(1, uuid.toString());
      ps.setString(2, name);
      ps.setBigDecimal(3, balance);
      ps.setBoolean(4, hidden);
      ps.setBoolean(5, Settings.isNonPlayerAccountName(name));
      ps.executeUpdate();
    } catch(final Exception e) {
      return false;
    } finally {
      iConomyUnlocked.getBackEnd().close(conn, ps);
    }
    return true;
  }


  /**
   * Remove the user Account with this uuid.
   *
   * @param uuid the UUID of the account.
   *
   * @return true if successful.
   */
  public boolean remove(final UUID uuid) {

    Connection conn = null;
    PreparedStatement ps = null;
    try {
      conn = iConomyUnlocked.getBackEnd().getConnection();
      ps = conn.prepareStatement("DELETE FROM " + SQLTable + " WHERE uuid = ? LIMIT 1");
      ps.setString(1, uuid.toString());
      ps.executeUpdate();
    } catch(final Exception e) {
      return false;
    } finally {
      iConomyUnlocked.getBackEnd().close(conn, ps);
    }
    return true;
  }

  /**
   * Remove ALL matching Accounts with this uuid..
   *
   * @return true if successful.
   */
  public boolean removeCompletely(final String name) {

    Connection conn = null;
    PreparedStatement ps = null;
    try {
      conn = iConomyUnlocked.getBackEnd().getConnection();
      ps = conn.prepareStatement("DELETE FROM " + SQLTable + " WHERE uuid = ? LIMIT 1");
      ps.setString(1, name);
      ps.executeUpdate();
    } catch(final Exception e) {
      return false;
    } finally {
      iConomyUnlocked.getBackEnd().close(conn, ps);
    }
    return true;
  }

  /**
   * Delete all accounts with default holdings
   *
   * @return true if successful.
   */
  public boolean purge() {

    Connection conn = null;
    PreparedStatement ps = null;
    try {
      conn = iConomyUnlocked.getBackEnd().getConnection();
      ps = conn.prepareStatement("DELETE FROM " + SQLTable + " WHERE balance = ?");
      ps.setBigDecimal(1, Settings.getDefaultBalance());
      ps.executeUpdate();
    } catch(final Exception e) {
      return false;
    } finally {
      iConomyUnlocked.getBackEnd().close(conn, ps);
    }
    return true;
  }

  /**
   * Removes all accounts from the database. ## Do not use this ##
   *
   * @return true if successful.
   */
  public boolean emptyDatabase() {

    Connection conn = null;
    PreparedStatement ps = null;
    try {
      conn = iConomyUnlocked.getBackEnd().getConnection();
      ps = conn.prepareStatement("TRUNCATE TABLE " + SQLTable);
      ps.executeUpdate();
    } catch(final Exception e) {
      return false;
    } finally {
      iConomyUnlocked.getBackEnd().close(conn, ps);
    }
    return true;
  }

  /**
   * Fetch a list of all Account balances.
   *
   * @return a list of balances.
   */
  public List<BigDecimal> values() {

    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement ps = null;
    final List<BigDecimal> Values = new ArrayList<>();
    try {
      conn = iConomyUnlocked.getBackEnd().getConnection();
      ps = conn.prepareStatement("SELECT balance FROM " + SQLTable);
      rs = ps.executeQuery();

      while(rs.next()) { Values.add(rs.getBigDecimal("balance")); }

    } catch(final Exception e) {
      return null;
    } finally {
      iConomyUnlocked.getBackEnd().close(conn, ps, rs);
    }
    return Values;
  }

  /**
   * Fetch X top non-hidden account names with balances.
   *
   * @param amount the number of accounts to return.
   *
   * @return a map of top accounts.
   */
  public LinkedHashMap<String, Double> ranking(final int amount) {

    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement ps = null;
    final LinkedHashMap<String, Double> Ranking = new LinkedHashMap<String, Double>();
    try {
      conn = iConomyUnlocked.getBackEnd().getConnection();
      ps = conn.prepareStatement("SELECT username,balance FROM " + SQLTable + " WHERE hidden = 0 "
                                 + (Settings.hideNonPlayerAccountsInRankings()? "AND nonplayer = 0 " : "")
                                 + "ORDER BY balance DESC LIMIT ?");
      ps.setInt(1, amount);
      rs = ps.executeQuery();

      while(rs.next()) {
        Ranking.put(rs.getString("username"), Double.valueOf(rs.getDouble("balance")));
      }
    } catch(final Exception e) {
      iConomyUnlocked.getPlugin().getLogger().atWarning().log(e.getMessage());
      return null;
    } finally {
      iConomyUnlocked.getBackEnd().close(conn, ps, rs);
    }
    return Ranking;
  }

  public Map<UUID, String> getUUIDNameMap() {

    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement ps = null;
    final Map<UUID, String> map = new ConcurrentHashMap<>();
    try {
      conn = iConomyUnlocked.getBackEnd().getConnection();
      ps = conn.prepareStatement("SELECT uuid,username FROM " + SQLTable);
      rs = ps.executeQuery();

      while(rs.next()) { map.put(UUID.fromString(rs.getString("uuid")), rs.getString("username")); }
    } catch(final Exception e) {
      iConomyUnlocked.getPlugin().getLogger().atWarning().log(e.getMessage());
      return null;
    } finally {
      iConomyUnlocked.getBackEnd().close(conn, ps, rs);
    }
    return map;
  }

  /**
   * Get an Account by uuid and name. Creates one if it doesn't exist.
   *
   * @param uuid the uuid of the Account.
   * @param name the name of the Account.
   *
   * @return an Account or null if unable.
   */
  public Account get(final UUID uuid, final String name) {

    return get(uuid, name, false);
  }

  /**
   * Get an Account by uuid and name, with special awareness for players who have changed their name
   * since their last log in. Creates one if it doesn't exist.
   *
   * @param uuid            the uuid of the Account.
   * @param name            the name of the Account.
   * @param playerJoinEvent true when fired from a player join event.
   *
   * @return an Account or null if unable.
   */
  public Account get(final UUID uuid, final String name, final boolean playerJoinEvent) {

    if(exists(uuid)) {
      if(!playerJoinEvent) {
        return new Account(uuid, name);
      } else {
        final Account account = Account.getAccount(uuid);
        final String oldName = account.getName();
        if(!oldName.equals(name)) {
          account.setName(name);
          iConomyUnlocked.getPlugin().getLogger().atInfo().log(
                  String.format("iConomyUnlocked has found a player with UUID %s has changed their name from %s to %s.", uuid.toString(), oldName, name));
          iConomyUnlocked.getPlugin().getLogger().atInfo().log("iConomyUnlocked's database will be altered to reflect this change.");
        }
        return account;
      }
    }
    if(!create(uuid, name, Settings.isNonPlayerAccountName(name))) {
      return null;
    }

    return new Account(uuid, name);
  }

  @Nullable
  public Account get(final String name) {

    int id = 0;
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement ps = null;
    boolean exists = false;
    try {
      conn = iConomyUnlocked.getBackEnd().getConnection();
      ps = conn.prepareStatement("SELECT * FROM " + SQLTable + " WHERE username = ? LIMIT 1");
      ps.setString(1, name);
      rs = ps.executeQuery();
      exists = rs.next();
      if(exists) { id = rs.getInt("id"); }
    } catch(final Exception ex) {
      exists = false;
    } finally {
      iConomyUnlocked.getBackEnd().close(conn, ps, rs);
    }
    if(exists) {
      final UUID uuid = getUUID(id);
      if(uuid != null) { return get(uuid, name); }
    }
    return null;
  }

  @Nullable
  public Account get(final UUID uuid) {

    int id = 0;
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement ps = null;
    boolean exists = false;
    try {
      conn = iConomyUnlocked.getBackEnd().getConnection();
      ps = conn.prepareStatement("SELECT * FROM " + SQLTable + " WHERE uuid = ? LIMIT 1");
      ps.setString(1, uuid.toString());
      rs = ps.executeQuery();
      exists = rs.next();
      if(exists) { id = rs.getInt("id"); }
    } catch(final Exception ex) {
      exists = false;
    } finally {
      iConomyUnlocked.getBackEnd().close(conn, ps, rs);
    }
    if(exists) {
      final String name = getName(id);
      if(!name.isEmpty()) { return get(uuid, name); }
    }
    return null;
  }

  private UUID getUUID(final int id) {

    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement ps = null;
    UUID uuid = null;
    try {
      conn = iConomyUnlocked.getBackEnd().getConnection();
      ps = conn.prepareStatement("SELECT * FROM " + SQLTable + " WHERE id = ? LIMIT 1");
      ps.setInt(1, id);
      rs = ps.executeQuery();
      if(rs.next()) { uuid = UUID.fromString(rs.getString("uuid")); }
    } catch(final Exception ex) {
      return null;
    } finally {
      iConomyUnlocked.getBackEnd().close(conn, ps, rs);
    }
    return uuid;
  }

  private String getName(final int id) {

    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement ps = null;
    String name = "";
    try {
      conn = iConomyUnlocked.getBackEnd().getConnection();
      ps = conn.prepareStatement("SELECT * FROM " + SQLTable + " WHERE id = ? LIMIT 1");
      ps.setInt(1, id);
      rs = ps.executeQuery();
      if(rs.next()) { name = rs.getString("username"); }
    } catch(final Exception ex) {
      return null;
    } finally {
      iConomyUnlocked.getBackEnd().close(conn, ps, rs);
    }
    return name;
  }

  public void updateAccountsForNewTables(final List<String> tablesUpdated) {

    if(tablesUpdated.contains("nonplayer")) {
      // non player account status
      for(final String name : new ArrayList<>(getUUIDNameMap().values())) {
        if(!Settings.isNonPlayerAccountName(name)) { continue; }
        iConomyUnlocked.getPlugin().getLogger().atInfo().log("Account " + name + " is being marked as a non player account.");
        get(name).setNonPlayer(true);
      }
    }
  }
}
