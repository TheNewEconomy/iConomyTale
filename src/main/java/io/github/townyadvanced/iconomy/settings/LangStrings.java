package io.github.townyadvanced.iconomy.settings;

import io.github.townyadvanced.commentedconfiguration.CommentedConfiguration;
import io.github.townyadvanced.iconomy.util.FileMgmt;

import java.nio.file.Path;

public class LangStrings {

  private static CommentedConfiguration config, newConfig;

  public static void loadLangFile(final Path configPath) throws Exception {

    if(FileMgmt.checkOrCreateFile(configPath.toString())) {

      // read the lang.yml into memory
      config = new CommentedConfiguration(configPath);
		if(!config.load()) { throw new Exception("Failed to load lang.yml!"); }

      setDefaults(configPath);
      config.save();
    }
  }

  /**
   * Builds a new config reading old config data.
   */
  private static void setDefaults(final Path configPath) {

    newConfig = new CommentedConfiguration(configPath);
    newConfig.load();

    for(final LangFile root : LangFile.values()) {
		if(root.getComments().length > 0) {
			newConfig.addComment(root.getRoot(), root.getComments());
		} else {
			setNewProperty(root.getRoot(), (config.get(root.getRoot().toLowerCase()) != null)
										   ? config.get(root.getRoot().toLowerCase())
										   : root.getDefault());
		}
    }

    config = newConfig;
    newConfig = null;
  }

  private static void setNewProperty(final String root, Object value) {

    if(value == null) {
      value = "";
    }
    newConfig.set(root.toLowerCase(), value.toString());
  }

  private static String getString(final LangFile node) {

    return config.getString(node.getRoot().toLowerCase(), node.getDefault());
  }

  public static String moneyPrefix() {

    return getString(LangFile.LANG_MONEY_PREFIX);
  }

  public static String personalBalance(final String balance) {

    return String.format(getString(LangFile.LANG_PERSONAL_BALANCE), balance);
  }

  public static String personalRank(final String rank) {

    return String.format(getString(LangFile.LANG_PERSONAL_RANK), rank);
  }

  public static String personalCredit(final String amount) {

    return String.format(getString(LangFile.LANG_PERSONAL_CREDIT), amount);
  }

  public static String personalDebit(final String amount) {

    return String.format(getString(LangFile.LANG_PERSONAL_DEBIT), amount);
  }

  public static String personalSet(final String amount) {

    return String.format(getString(LangFile.LANG_PERSONAL_SET), amount);
  }

  public static String playerBalance(final String name, final String balance) {

    return String.format(getString(LangFile.LANG_PLAYER_BALANCE), name, balance);
  }

  public static String playerRank(final String name, final String rank) {

    return String.format(getString(LangFile.LANG_PLAYER_RANK), name, rank);
  }

  public static String playerDebit(final String name, final String amount) {

    return String.format(getString(LangFile.LANG_PLAYER_DEBIT), name, amount);
  }

  public static String playerCredit(final String name, final String amount) {

    return String.format(getString(LangFile.LANG_PLAYER_CREDIT), name, amount);
  }

  public static String playerSet(final String name, final String amount) {

    return String.format(getString(LangFile.LANG_PLAYER_SET), name, amount);
  }

  public static String playerReset(final String name) {

    return String.format(getString(LangFile.LANG_PLAYER_RESET), name);
  }

  public static String accountAlreadyExist() {

    return getString(LangFile.LANG_ERROR_ACCOUNT_ALREADY_EXISTS);
  }

  public static String noAccountFound(final String name) {

    return String.format(getString(LangFile.LANG_ERROR_NO_ACCOUNT_FOUND), name);
  }

  public static String cannotSendSelf() {

    return getString(LangFile.LANG_PAYMENT_SELF);
  }

  public static String paymentTo(final String amount, final String name) {

    return String.format(getString(LangFile.LANG_PAYMENT_TO), amount, name);
  }

  public static String paymentFrom(final String name, final String amount) {

    return String.format(getString(LangFile.LANG_PAYMENT_FROM), name, amount);
  }

  public static String notEnoughFunds() {

    return getString(LangFile.LANG_ERROR_CANNOT_AFFORD);
  }

  public static String accountCreated(final String name) {

    return String.format(getString(LangFile.LANG_ACCOUNTS_CREATE), name);
  }

  public static String accountRemoved(final String name) {

    return String.format(getString(LangFile.LANG_ACCOUNTS_REMOVED), name);
  }

  public static String accountsEmptied() {

    return getString(LangFile.LANG_ACCOUNTS_EMPTIED);
  }

  public static String accountsPurged() {

    return getString(LangFile.LANG_ACCOUNTS_PURGED);
  }

  public static String accountHiddenStatus(final String status) {

    return String.format(getString(LangFile.LANG_ACCOUNTS_STATUS), status);
  }

  public static String statsHeader() {

    return getString(LangFile.LANG_STATS_HEADER);
  }

  public static String statsTotal(final String curr, final String total) {

    return String.format(getString(LangFile.LANG_STATS_TOTAL), curr, total);
  }

  public static String statsAverage(final String curr, final String avg) {

    return String.format(getString(LangFile.LANG_STATS_AVERAGE), curr, avg);
  }

  public static String statsAccounts(final String num) {

    return String.format(getString(LangFile.LANG_STATS_ACCOUNTS), num);
  }

  public static String topHeader(final String num) {

    return String.format(getString(LangFile.LANG_TOP_HEADER), num);
  }

  public static String topEmpty() {

    return getString(LangFile.LANG_TOP_EMPTY);
  }

  public static String topLine(final String num, final String name, final String amount) {

    return String.format(getString(LangFile.LANG_TOP_LINE), num, name, amount);
  }
}
