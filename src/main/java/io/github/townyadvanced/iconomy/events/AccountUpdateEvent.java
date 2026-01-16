package io.github.townyadvanced.iconomy.events;

import com.hypixel.hytale.event.IEvent;
import io.github.townyadvanced.iconomy.system.Holdings;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountUpdateEvent(Holdings account, BigDecimal previous, BigDecimal balance,
                                 BigDecimal amount) implements IEvent<Void> {

  public String getAccountName() {

    return this.account.getName();
  }

  public UUID getAccountUUID() {

    return this.account.getUUID();
  }
}
