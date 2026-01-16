package io.github.townyadvanced.iconomy.listener;


import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.townyadvanced.iconomy.iConomyUnlocked;

public class PlayerReadyListener {

  /**
   * Handles the event when a player joins the server. The method ensures that an account is created
   * or retrieved for the player. If an error occurs during the creation or retrieval of the
   * account, a warning message is logged.
   *
   * @param event The PlayerReadyEvent triggered when a player joins the server. Contains
   *              information about the player and their reference.
   */
  public static void onEvent(final PlayerReadyEvent event) {

    final Ref<EntityStore> ref = event.getPlayerRef();
    final PlayerRef plrRef = ref.getStore().getComponent(ref, PlayerRef.getComponentType());

    if(iConomyUnlocked.getAccounts().get(plrRef.getUuid(), plrRef.getUsername(), true) == null) {
      iConomyUnlocked.getPlugin().getLogger().atWarning().log("Error creating / grabbing account for: " + plrRef.getUsername());
    }
  }
}
