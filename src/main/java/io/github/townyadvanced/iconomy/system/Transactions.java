package io.github.townyadvanced.iconomy.system;

import io.github.townyadvanced.iconomy.iConomyUnlocked;
import io.github.townyadvanced.iconomy.settings.Settings;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Transactions {

  public void insert(final String from, final String to, final BigDecimal from_balance, final BigDecimal to_balance, final BigDecimal set, final BigDecimal gain, final BigDecimal loss) {

    if(!Settings.transactionLoggingEnabled()) {
      return;
    }
    int i = 1;
    final long timestamp = System.currentTimeMillis() / 1000L;

    final Object[] data = { from, to, from_balance, to_balance, Long.valueOf(timestamp), set, gain, loss };

    Connection conn = null;
    PreparedStatement ps = null;
    try {
      conn = iConomyUnlocked.getBackEnd().getConnection();
      ps = conn.prepareStatement("INSERT INTO " + Settings.getDBTable() + "_Transactions(account_from, account_to, account_from_balance, account_to_balance, `timestamp`, `set`, gain, loss) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

      for(final Object obj : data) {
        ps.setObject(i, obj);
        i++;
      }

      ps.executeUpdate();
    } catch(final SQLException ex) { } finally {
      iConomyUnlocked.getBackEnd().close(conn, ps);
    }
  }
}
