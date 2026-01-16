package io.github.townyadvanced.iconomy.events;

import com.hypixel.hytale.event.IEvent;
import io.github.townyadvanced.iconomy.system.Holdings;

import java.math.BigDecimal;

public record AccountSetEvent(Holdings account, BigDecimal balance) implements IEvent<Void> {

  public String getAccountName() {

    return this.account.getName();
  }
}
