package io.github.townyadvanced.iconomy.util;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.Player;

import javax.annotation.Nonnull;

public class Permissions {

  public static boolean hasPermission(@Nonnull final CommandContext ctx, final String node) {

    return hasPermission(ctx, node, false);
  }

  public static boolean hasPermission(@Nonnull final CommandContext ctx, final String node, final boolean silent) {

    if(ctx.sender() instanceof final Player player) {

      final boolean hasPermission = player.hasPermission(node);
      if(!hasPermission && !silent) {
        Messaging.sendErrorMessage(ctx, "You do not have the permission to use that command.");
      }
      return hasPermission;
    }
    return true;
  }
}
