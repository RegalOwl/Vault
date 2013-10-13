/* This file is part of Vault.

    Vault is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Vault is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Vault.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.milkbowl.vault.economy.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconAPI;

public class Economy_HyperConomy implements Economy {
	private static final Logger log = Logger.getLogger("Minecraft");
	private final String name = "HyperConomy";
	private Plugin plugin = null;
	private HyperConomy hc = null;
	private HyperEconAPI api;

	public Economy_HyperConomy(Plugin plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
		if (hc == null) {
			Plugin hyperconomy = plugin.getServer().getPluginManager().getPlugin("HyperConomy");
			if (hyperconomy != null && hyperconomy.isEnabled() && (Double.parseDouble(hyperconomy.getDescription().getVersion()) >= .974)) {
				hc = (HyperConomy) hyperconomy;
				api = HyperConomy.hyperEconAPI;
				log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
			}
		}
	}
	
	public class EconomyServerListener implements Listener {
		Economy_HyperConomy economy = null;

		public EconomyServerListener(Economy_HyperConomy economy) {
			this.economy = economy;
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(PluginEnableEvent event) {
			if (economy.hc == null) {
				Plugin hyperconomy = plugin.getServer().getPluginManager().getPlugin("HyperConomy");
				if (hyperconomy != null && hyperconomy.isEnabled() && (Double.parseDouble(hyperconomy.getDescription().getVersion()) >= .974)) {
					economy.hc = (HyperConomy) hyperconomy;
					api = HyperConomy.hyperEconAPI;
					log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
				}
			}
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(PluginDisableEvent event) {
			if (economy.hc != null) {
				if (event.getPlugin().getDescription().getName().equals("HyperConomy")) {
					economy.hc = null;
					api = null;
					log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
				}
			}
		}
	}

	@Override
	public boolean isEnabled() {
		if (hc == null) {
			return false;
		} else {
			return hc.isEnabled();
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public double getBalance(String playerName) {
		return api.getBalance(playerName);
	}

	@Override
	public boolean createPlayerAccount(String playerName) {
		return api.createAccount(playerName);
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds");
		}
		if (api.checkAccount(playerName)) {
			if (api.checkFunds(amount, playerName)) {
				api.withdrawAccount(amount, playerName);
				return new EconomyResponse(0, 0, ResponseType.SUCCESS, null);
			} else {
				return new EconomyResponse(0, 0, ResponseType.FAILURE, "Insufficient funds");
			}
		} else {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Account does not exist");
		}
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot deposit negative funds");
		}
		if (api.checkAccount(playerName)) {
			api.depositAccount(amount, playerName);
			return new EconomyResponse(0, 0, ResponseType.SUCCESS, null);
		} else {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Account does not exist");
		}
	}

	@Override
	public String format(double amount) {
		return api.formatMoney(amount);
	}

	@Override
	public String currencyNameSingular() {
		return api.currencyNamePlural();
	}

	@Override
	public String currencyNamePlural() {
		return api.currencyName();
	}

	@Override
	public boolean has(String playerName, double amount) {
		return api.checkFunds(amount, playerName);
	}

	@Override
	public boolean hasAccount(String playerName) {
		return api.checkAccount(playerName);
	}

	@Override
	public int fractionalDigits() {
		return api.fractionalDigits();
	}

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }
    
	@Override
	public EconomyResponse createBank(String name, String player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "HyperConomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse deleteBank(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "HyperConomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "HyperConomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "HyperConomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "HyperConomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "HyperConomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "HyperConomy does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankBalance(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "HyperConomy does not support bank accounts!");
	}

	@Override
	public List<String> getBanks() {
		return new ArrayList<String>();
	}

	@Override
	public boolean hasBankSupport() {
		return false;
	}
}