package me.realized.tokenmanager.command.commands.subcommands;

import me.realized.tokenmanager.TokenManagerPlugin;
import me.realized.tokenmanager.command.BaseCommand;
import me.realized.tokenmanager.util.NumberUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.milkbowl.vault.economy.Economy;

import java.util.OptionalLong;

public class ExchangeCommand extends BaseCommand {

    private Economy economy;

    public ExchangeCommand(final TokenManagerPlugin plugin) {
        super(plugin, "exchange", "exchange <amount>", null, 2, false, "exchangeTokens");
        this.economy = plugin.getEconomy();
    }

    @Override
    protected void execute(final CommandSender sender, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, true, "ERROR.not-a-player");
            return;
        }
        Player player = (Player) sender;
        if (plugin.getConfiguration().isExchaningEnabled) {
            final OptionalLong amountOpt = NumberUtil.parseLong(args[1]);

            if (!amountOpt.isPresent() || amountOpt.getAsLong() <= 0) {
                sendMessage(sender, true, "ERROR.invalid-amount", "input", args[1]);
                return;
            }

            long requiredAmount = amountOpt.getAsLong();
            double eBalance = plugin.getEconomy().getBalance(player);
            double missingAmount = requiredAmount - eBalance;

            if (missingAmount > 0) {
                sendMessage(player, true, "ERROR.exchange-balance-not-enough", "needed", missingAmount);
                return;
            }

            if (requiredAmount < plugin.getConfiguration().getMinMoneyToExchange()) {
                sendMessage(player, true, "ERROR.exchange-too-small", "min-amount", plugin.getConfiguration().getMinMoneyToExchange());
                return;
            }
            plugin.getEconomy().withdrawPlayer(player, requiredAmount);

            long tokens = requiredAmount * plugin.getConfiguration().getExchangeValue();
            plugin.addTokens(player, tokens);
            sendMessage(player, true, "COMMAND.token.exchange-successful", "money", requiredAmount, "amount", tokens);
        } else {
            sendMessage(player, true, "ERROR.exchanging-not-enabled");
        }
    }

}
