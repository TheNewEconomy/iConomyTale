package io.github.townyadvanced.iconomy;

import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.common.semver.SemverRange;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import io.github.townyadvanced.iconomy.commands.MoneyCommand;
import io.github.townyadvanced.iconomy.listener.PlayerReadyListener;
import io.github.townyadvanced.iconomy.providers.VaultUnlockedEconomy;
import io.github.townyadvanced.iconomy.settings.LangStrings;
import io.github.townyadvanced.iconomy.settings.Settings;
import io.github.townyadvanced.iconomy.system.Accounts;
import io.github.townyadvanced.iconomy.system.BackEnd;
import io.github.townyadvanced.iconomy.system.Transactions;
import net.cfh.vault.VaultUnlockedServicesManager;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

public class iConomyUnlocked extends JavaPlugin {

  private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
  private static iConomyUnlocked plugin;
  private static BackEnd backend = null;
  private static Accounts accounts = null;
  private static Transactions transactions = null;
  private static iConomyUnlocked instance;


  public iConomyUnlocked(@Nonnull final JavaPluginInit init) {

    super(init);
    LOGGER.atInfo().log("Hello from " + this.getName() + " version " + this.getManifest().getVersion().toString());
    instance = this;

  }

  public static iConomyUnlocked getPlugin() {

    return plugin;
  }

  public static BackEnd getBackEnd() {

    return backend;
  }

  public static Accounts getAccounts() {

    return accounts;
  }

  public static Transactions getTransactions() {

    return transactions;
  }

  @Override
  protected void setup() {

    LOGGER.atInfo().log("Setting up plugin " + this.getName());

    registerEconomy();

    try {

      loadConfig();
      loadLangFile();

      backend = new BackEnd();
      backend.setupAccountTable();
      final List<String> tablesUpdated = backend.updateTables();

      accounts = new Accounts();

      if(!tablesUpdated.isEmpty()) {
        accounts.updateAccountsForNewTables(tablesUpdated);
      }

      transactions = new Transactions();
      backend.setupTransactionTable();

      registerCommands();
      registerListeners();
    } catch(final Exception e) {
      disableWithMessage(e.getMessage());
    }

    //TODO: Update checking.
  }

  public void loadConfig() throws Exception {

    Settings.loadConfig(getDataDirectory().resolve("config.yml"), getVersion());
  }

  public void loadLangFile() throws Exception {

    LangStrings.loadLangFile(getDataDirectory().resolve("lang.yml"));
  }

  public String getVersion() {

    return this.getManifest().getVersion().toString();
  }

  /**
   * Registers economy support for the iConomyUnlocked plugin, specifically enabling or disabling
   * support for the VaultUnlocked plugin based on its availability.
   *
   * This method checks if the VaultUnlocked plugin is present using the plugin manager. If the
   * plugin is available, VaultUnlocked support is enabled by registering it with the
   * VaultUnlockedServicesManager. Otherwise, support is disabled, and a log message is recorded.
   *
   * Logging is used to indicate whether support has been enabled or disabled.
   */
  private void registerEconomy() {

    if(HytaleServer.get().getPluginManager().hasPlugin(PluginIdentifier.fromString("TheNewEconomy:VaultUnlocked"), SemverRange.WILDCARD)) {
      LOGGER.atInfo().log("VaultUnlocked is installed, enabling VaultUnlocked support.");

      VaultUnlockedServicesManager.get().economy(new VaultUnlockedEconomy(this));
    } else {
      LOGGER.atInfo().log("VaultUnlocked is not installed, disabling VaultUnlocked support.");
    }
  }

  private void registerCommands() {

    final MoneyCommand cmd = new MoneyCommand();
    final PluginCommand command = getCommand("money");
    command.setExecutor(cmd);
    command.setTabCompleter(cmd);
  }

  private void registerListeners() {

    this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, PlayerReadyListener::onEvent);
  }

  private void disableWithMessage(final String message) {

    getLogger().atSevere().log(message);
    getLogger().atSevere().log("Disabling iConomyUnlocked...");
    super.shutdown0(true);
  }

  @Override
  public void shutdown() {

    try {
      backend.connectionPool().dispose();
      getLogger().atInfo().log("Plugin disabled.");
    } catch(final Exception e) {
      getLogger().atSevere().log("Plugin disabled.");
    } finally {
      transactions = null;
      accounts = null;
      backend = null;
    }
  }
}
