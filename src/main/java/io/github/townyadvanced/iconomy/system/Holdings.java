package io.github.townyadvanced.iconomy.system;

import io.github.townyadvanced.iconomy.events.AccountResetEvent;
import io.github.townyadvanced.iconomy.events.AccountSetEvent;
import io.github.townyadvanced.iconomy.events.AccountUpdateEvent;
import io.github.townyadvanced.iconomy.iConomyUnlocked;
import io.github.townyadvanced.iconomy.settings.Settings;
import io.github.townyadvanced.iconomy.util.EventUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

public class Holdings {

  private final UUID uuid;
  private final String SQLTable = Settings.getDBTable();
  private String name = "";
  private BigDecimal balance = null;

  public Holdings(final UUID uuid, final String name) {

    this.uuid = uuid;
    this.name = name;
  }

  /**
   * Holdings name.
   *
   * @return name of this Holding
   */
  public String getName() {

    return this.name;
  }

  /**
   * Holdings UUID.
   *
   * @return UUID of this Holding
   */
  public UUID getUUID() {

    return this.uuid;
  }

  /**
   * Get the balance for this Holding.
   *
   * @return the balance.
   */
  public BigDecimal balance() {

    if(balance == null) { balance = get(); }
    return balance;
  }

  private synchronized BigDecimal get() {

    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement ps = null;
    BigDecimal balance = Settings.getDefaultBalance();
    try {
      conn = iConomyUnlocked.getBackEnd().getConnection();
      ps = conn.prepareStatement("SELECT * FROM " + SQLTable + " WHERE username = ? LIMIT 1");
      ps.setString(1, this.name);
      rs = ps.executeQuery();

      if(rs.next()) { balance = rs.getBigDecimal("balance"); }
    } catch(final Exception ex) {
      iConomyUnlocked.getPlugin().getLogger().atWarning().log("Failed to grab holdings: " + ex);
    } finally {
      iConomyUnlocked.getBackEnd().close(conn, ps, rs);
    }
    return balance;
  }

  public synchronized void set(final BigDecimal balance) {

    this.balance = balance;

    final AccountSetEvent event = new AccountSetEvent(this, balance);
    EventUtil.dispatch(event, AccountSetEvent.class);

    Connection conn = null;
    PreparedStatement ps = null;
    try {
      conn = iConomyUnlocked.getBackEnd().getConnection();
      ps = conn.prepareStatement("UPDATE " + SQLTable + " SET balance = ? WHERE username = ?");
      ps.setBigDecimal(1, balance);
      ps.setString(2, this.name);
      ps.executeUpdate();

    } catch(final Exception ex) {
      iConomyUnlocked.getPlugin().getLogger().atWarning().log("Failed to set holdings: " + ex);
    } finally {
      iConomyUnlocked.getBackEnd().close(conn, ps);
    }
  }

  public synchronized void add(final BigDecimal amount) {

    final BigDecimal balance = balance();
    final BigDecimal ending = balance.add(amount);

    callEventAndSetHoldings(amount, balance, ending);
  }

  public synchronized void subtract(final BigDecimal amount) {

    final BigDecimal balance = balance();
    final BigDecimal ending = balance.subtract(amount);

    callEventAndSetHoldings(amount, balance, ending);
  }

  public synchronized void divide(final BigDecimal amount) {

    final BigDecimal balance = balance();
    final BigDecimal ending = balance.divide(amount, 2, RoundingMode.FLOOR);

    callEventAndSetHoldings(amount, balance, ending);
  }

  public synchronized void multiply(final BigDecimal amount) {

    final BigDecimal balance = balance();
    final BigDecimal ending = balance.multiply(amount);

    callEventAndSetHoldings(amount, balance, ending);
  }

  /**
   * Reset Holdings to default, if the Event is not cancelled.
   */
  public void reset() {

    final AccountResetEvent event = new AccountResetEvent(this);
    EventUtil.dispatch(event, AccountResetEvent.class);
    if(event.isCancelled()) {
      return;
    }

    set(Settings.getDefaultBalance());
  }

  private void callEventAndSetHoldings(final BigDecimal amount, final BigDecimal previousBalance, final BigDecimal newBalance) {

    final AccountUpdateEvent event = new AccountUpdateEvent(this, previousBalance, newBalance, amount);
    EventUtil.dispatch(event, AccountUpdateEvent.class);
    set(newBalance);
  }

  /**
   * Is this balance negative?
   *
   * @return true if negative.
   */
  public boolean isNegative() {

    return get().compareTo(BigDecimal.ZERO) < 0;
  }

  /**
   * Does this Holding have this amount or more?
   *
   * @param amount the amount to test for.
   *
   * @return true if the balance is sufficient.
   */
  public boolean hasEnough(final BigDecimal amount) {

    return get().compareTo(amount) >= 0;
  }

  /**
   * Is the balance over the amount?
   *
   * @param amount the amount to test for.
   *
   * @return true if balance is higher.
   */
  public boolean hasOver(final BigDecimal amount) {

    return get().compareTo(amount) > 0;
  }

  /**
   * Is the balance under the amount?
   *
   * @param amount the amount to test for.
   *
   * @return true if balance is lower.
   */
  public boolean hasUnder(final BigDecimal amount) {

    return get().compareTo(amount) < 0;
  }

  public String toString() {

    return String.format(Settings.getEconomyFormat(), get());
  }
}
