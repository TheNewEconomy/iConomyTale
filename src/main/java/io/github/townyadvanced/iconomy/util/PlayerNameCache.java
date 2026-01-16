package io.github.townyadvanced.iconomy.util;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;

import java.util.ArrayList;
import java.util.List;

public class PlayerNameCache {

  static long FIVE_MINUTES = 1000 * 60 * 5;
  static long time = 0;
  static List<String> names = new ArrayList<>();

  public static List<String> getPlayerNames() {

    final boolean notOld = time + FIVE_MINUTES > System.currentTimeMillis();
    if(notOld) { return names; }

    names.clear();
    time = System.currentTimeMillis();
    for(final PlayerRef ref : Universe.get().getPlayers()) {
      if(ref == null || ref.getUsername() == null) { continue; }
      names.add(ref.getUsername());
    }

    return names;
  }
}
