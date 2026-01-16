package io.github.townyadvanced.iconomy.events;

import com.hypixel.hytale.event.ICancellable;
import com.hypixel.hytale.event.IEvent;
import io.github.townyadvanced.iconomy.system.Holdings;

public class AccountResetEvent implements ICancellable, IEvent<Void> {

  private final Holdings account;
  private boolean cancelled = false;

  public AccountResetEvent(final Holdings account) {

    this.account = account;
  }

  public String getAccountName() {

    return this.account.getName();
  }

  public Holdings getAccount() {

    return account;
  }

  public boolean isCancelled() {

    return this.cancelled;
  }

  public void setCancelled(final boolean cancelled) {

    this.cancelled = cancelled;
  }
}
