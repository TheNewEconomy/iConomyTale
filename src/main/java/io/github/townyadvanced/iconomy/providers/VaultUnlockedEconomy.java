package io.github.townyadvanced.iconomy.providers;

import io.github.townyadvanced.iconomy.iConomyUnlocked;
import io.github.townyadvanced.iconomy.settings.Settings;
import io.github.townyadvanced.iconomy.system.Account;
import net.milkbowl.vault2.economy.AccountPermission;
import net.milkbowl.vault2.economy.Economy;
import net.milkbowl.vault2.economy.EconomyResponse;
import net.milkbowl.vault2.economy.EconomyResponse.ResponseType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class VaultUnlockedEconomy implements Economy {

  private final iConomyUnlocked plugin;

  public VaultUnlockedEconomy(final iConomyUnlocked plugin) {

    this.plugin = plugin;
  }

  @Override
  public boolean isEnabled() {

    return plugin != null && plugin.isEnabled();
  }

  @Override
  public @NotNull String getName() {

    return "iConomyUnlocked";
  }

  @Override
  public boolean hasSharedAccountSupport() {

    return false;
  }

  @Override
  public boolean hasMultiCurrencySupport() {

    return false;
  }

  @Override
  public @NotNull int fractionalDigits(final String pluginName) {

    return Settings.getVaultFractionalDigits();
  }

  @Override
  public @NotNull String format(final BigDecimal amount) {

    return Settings.format(amount);
  }

  @Override
  public @NotNull String format(final String pluginName, final BigDecimal amount) {

    return format(amount);
  }

  @Override
  public @NotNull String format(final BigDecimal amount, final String currency) {

    return format(amount);
  }

  @Override
  public @NotNull String format(final String pluginName, final BigDecimal amount, final String currency) {

    return format(amount);
  }

  @Override
  public boolean hasCurrency(final String currency) {

    return currency.equalsIgnoreCase(Settings.getCurrencyName());
  }

  @Override
  public @NotNull String getDefaultCurrency(final String pluginName) {

    return Settings.getCurrencyName();
  }

  @Override
  public @NotNull String defaultCurrencyNamePlural(final String pluginName) {

    return Settings.getCurrencyName();
  }

  @Override
  public @NotNull String defaultCurrencyNameSingular(final String pluginName) {

    return Settings.getCurrencyName();
  }

  @Override
  public Collection<String> currencies() {

    return Collections.singleton(Settings.getCurrencyName());
  }

  @Override
  public boolean createAccount(final UUID accountID, final String name) {

    return createAccount(accountID, name, !Settings.isNonPlayerAccountName(name));
  }

  @Override
  public boolean createAccount(final UUID accountID, final String name, final String worldName) {

    return createAccount(accountID, name);
  }

  @Override
  public boolean createAccount(@NotNull final UUID accountID, @NotNull final String name, final boolean player) {

    return iConomyUnlocked.getAccounts().create(accountID, name, !player);
  }

  @Override
  public boolean createAccount(@NotNull final UUID accountID, @NotNull final String name, @NotNull final String worldName, final boolean player) {

    return createAccount(accountID, name, player);
  }

  @Override
  public Map<UUID, String> getUUIDNameMap() {

    return iConomyUnlocked.getAccounts().getUUIDNameMap();
  }

  @Override
  public Optional<String> getAccountName(final UUID accountID) {

    if(iConomyUnlocked.getAccounts().exists(accountID)) {
      return Optional.of(Account.getAccount(accountID).getName());
    }
    return Optional.empty();
  }

  @Override
  public boolean hasAccount(final UUID accountID) {

    return iConomyUnlocked.getAccounts().exists(accountID);
  }

  @Override
  public boolean hasAccount(final UUID accountID, final String worldName) {

    return hasAccount(accountID);
  }

  @Override
  public boolean renameAccount(final UUID accountID, final String name) {

    return iConomyUnlocked.getAccounts().get(accountID).setName(name);
  }

  @Override
  public boolean renameAccount(final String plugin, final UUID accountID, final String name) {

    return renameAccount(accountID, name);
  }

  @Override
  public boolean deleteAccount(final String plugin, final UUID accountID) {

    iConomyUnlocked.getAccounts().get(accountID).remove();
    return true;
  }

  @Override
  public boolean accountSupportsCurrency(final String plugin, final UUID accountID, final String currency) {

    return currency.equalsIgnoreCase(Settings.getCurrencyName());
  }

  @Override
  public boolean accountSupportsCurrency(final String plugin, final UUID accountID, final String currency, final String world) {

    return currency.equalsIgnoreCase(Settings.getCurrencyName());
  }

  @Override
  public @NotNull BigDecimal getBalance(final String pluginName, final UUID accountID) {

    @Nullable final Account account = Account.getAccount(accountID);
	  if(account == null) { return BigDecimal.ZERO; }
    return BigDecimal.valueOf(account.getHoldings().balance());
  }

  @Override
  public @NotNull BigDecimal getBalance(final String pluginName, final UUID accountID, final String world) {

    return getBalance(pluginName, accountID);
  }

  @Override
  public @NotNull BigDecimal getBalance(final String pluginName, final UUID accountID, final String world, final String currency) {

    return getBalance(pluginName, accountID);
  }

  @Override
  public boolean has(final String pluginName, final UUID accountID, final BigDecimal amount) {

    return getBalance(pluginName, accountID).compareTo(amount) != -1;
  }

  @Override
  public boolean has(final String pluginName, final UUID accountID, final String worldName, final BigDecimal amount) {

    return has(pluginName, accountID, amount);
  }

  @Override
  public boolean has(final String pluginName, final UUID accountID, final String worldName, final String currency, final BigDecimal amount) {

    return has(pluginName, accountID, amount);
  }

  @Override
  public @NotNull EconomyResponse withdraw(final String pluginName, final UUID accountID, final BigDecimal amount) {

    final Account account = Account.getAccount(accountID);
	  if(account == null) {
		  return new EconomyResponse(amount, BigDecimal.ZERO, ResponseType.FAILURE, "No account found.");
	  }

	  if(!account.getHoldings().hasEnough(amount.doubleValue())) {
		  return new EconomyResponse(amount, getBalance(pluginName, accountID), ResponseType.FAILURE, "Not enough funds.");
	  }

    account.getHoldings().subtract(amount.doubleValue());
    iConomyUnlocked.getTransactions().insert(account.getName(), "[Vault]", 0.0D, account.getHoldings().balance(), 0.0D, 0.0D, amount.doubleValue());
    return new EconomyResponse(amount, getBalance(pluginName, accountID), ResponseType.SUCCESS, null);
  }

  @Override
  public @NotNull EconomyResponse withdraw(final String pluginName, final UUID accountID, final String worldName, final BigDecimal amount) {

    return withdraw(pluginName, accountID, amount);
  }

  @Override
  public @NotNull EconomyResponse withdraw(final String pluginName, final UUID accountID, final String worldName, final String currency, final BigDecimal amount) {

    return withdraw(pluginName, accountID, amount);
  }

  @Override
  public @NotNull EconomyResponse deposit(final String pluginName, final UUID accountID, final BigDecimal amount) {

    final Account account = Account.getAccount(accountID);
	  if(account == null) {
		  return new EconomyResponse(amount, BigDecimal.ZERO, ResponseType.FAILURE, "No account found.");
	  }

    account.getHoldings().add(amount.doubleValue());
    iConomyUnlocked.getTransactions().insert("[Vault]", account.getName(), 0.0D, account.getHoldings().balance(), 0.0D, amount.doubleValue(), 0.0D);
    return new EconomyResponse(amount, getBalance(pluginName, accountID), ResponseType.SUCCESS, null);
  }

  @Override
  public @NotNull EconomyResponse deposit(final String pluginName, final UUID accountID, final String worldName, final BigDecimal amount) {

    return deposit(pluginName, accountID, amount);
  }

  @Override
  public @NotNull EconomyResponse deposit(final String pluginName, final UUID accountID, final String worldName, final String currency, final BigDecimal amount) {

    return deposit(pluginName, accountID, amount);
  }

  @Override
  public boolean createSharedAccount(final String pluginName, final UUID accountID, final String name, final UUID owner) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isAccountOwner(final String pluginName, final UUID accountID, final UUID uuid) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean setOwner(final String pluginName, final UUID accountID, final UUID uuid) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isAccountMember(final String pluginName, final UUID accountID, final UUID uuid) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean addAccountMember(final String pluginName, final UUID accountID, final UUID uuid) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean addAccountMember(final String pluginName, final UUID accountID, final UUID uuid,
                                  final AccountPermission... initialPermissions) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean removeAccountMember(final String pluginName, final UUID accountID, final UUID uuid) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean hasAccountPermission(final String pluginName, final UUID accountID, final UUID uuid, final AccountPermission permission) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean updateAccountPermission(final String pluginName, final UUID accountID, final UUID uuid, final AccountPermission permission,
                                         final boolean value) {
    // TODO Auto-generated method stub
    return false;
  }

}
