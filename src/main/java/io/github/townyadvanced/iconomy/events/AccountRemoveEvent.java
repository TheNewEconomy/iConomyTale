package io.github.townyadvanced.iconomy.events;

import com.hypixel.hytale.event.ICancellable;
import com.hypixel.hytale.event.IEvent;

public class AccountRemoveEvent implements ICancellable, IEvent<Void> {

  private final String account;
  private boolean cancelled = false;

  public AccountRemoveEvent(final String account) {

    this.account = account;
  }

  public String getAccountName() {

    return this.account;
  }

  public boolean isCancelled() {

    return this.cancelled;
  }

  public void setCancelled(final boolean cancelled) {

    this.cancelled = cancelled;
  }
}
