package io.github.townyadvanced.iconomy.util;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import fi.sulku.hytale.TinyMsg;
import io.github.townyadvanced.iconomy.settings.LangStrings;

import javax.annotation.Nonnull;
import java.awt.*;

public class Messaging {

  public static void send(final CommandContext ctx, final String message) {

    if(ctx == null) { return; }
    colorize(ctx, message);
  }

  public static void sendErrorMessage(@Nonnull final CommandContext ctx, final String message) {

    ctx.sendMessage(Message.raw(message).color(Color.RED));
  }

  public static void sendMoneyPrefixedMsg(@Nonnull final CommandContext ctx, final String message) {

    colorize(ctx, LangStrings.moneyPrefix() + message);
  }

  public static void colorize(@Nonnull final CommandContext ctx, final String message) {

    ctx.sendMessage(TinyMsg.parse(message));
  }
}