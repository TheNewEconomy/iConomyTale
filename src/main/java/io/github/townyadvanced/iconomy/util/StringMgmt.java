package io.github.townyadvanced.iconomy.util;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class StringMgmt {

  public static String[] remFirstArg(final String[] arr) {

    return remArgs(arr, 1);
  }

  public static String[] remLastArg(final String[] arr) {

    return subArray(arr, 0, arr.length - 1);
  }

  public static String[] remArgs(final String[] arr, final int startFromIndex) {

    if(arr.length == 0) { return arr; } else if(arr.length < startFromIndex) {
      return new String[0];
    } else {
      final String[] newSplit = new String[arr.length - startFromIndex];
      System.arraycopy(arr, startFromIndex, newSplit, 0, arr.length - startFromIndex);
      return newSplit;
    }
  }

  public static String[] subArray(final String[] arr, final int start, final int end) {

    if(arr.length == 0) { return arr; } else if(end < start) { return new String[0]; } else {
      final int length = end - start;
      final String[] newSplit = new String[length];
      System.arraycopy(arr, start, newSplit, 0, length);
      return newSplit;
    }
  }

  /**
   * Returns strings that start with a string
   *
   * @param list         strings to check
   * @param startingWith string to check with list
   *
   * @return strings from list that start with startingWith
   */
  public static List<String> filterByStart(final List<String> list, final String startingWith) {

    if(list == null || startingWith == null) {
      return Collections.emptyList();
    }
    return list.stream().filter(name->name != null && name.toLowerCase(Locale.ROOT).startsWith(startingWith.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
  }


  /**
   * Checks text against two variables, if it equals at least one returns true.
   *
   * @param text The text that we were provided with.
   *
   * @return <code>Boolean</code> - True or false based on text.
   */
  public static boolean is(final String text, final String[] is) {

    for(final String s : is) {
      if(text.equalsIgnoreCase(s)) {
        return true;
      }
    }
    return false;
  }
}
