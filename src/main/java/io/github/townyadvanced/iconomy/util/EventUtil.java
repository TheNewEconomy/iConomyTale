package io.github.townyadvanced.iconomy.util;
/*
 * IslandSurvival
 * Copyright (C) 2025 Daniel "creatorfromhell" Vidmar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;

/**
 * EventUtil
 *
 * @author creatorfromhell
 * @since 0.0.1.0
 */
public class EventUtil {

  public static <T extends IEvent<Void>> void dispatch(final T event, final Class<T> clazz) {

    final IEventDispatcher<T, T> dispatcher = HytaleServer.get().getEventBus().dispatchFor(clazz);

    if(dispatcher.hasListener()) {
      dispatcher.dispatch(event);
    }
  }
}