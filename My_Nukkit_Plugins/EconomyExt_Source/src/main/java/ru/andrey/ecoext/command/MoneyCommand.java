package ru.andrey.ecoext.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import me.onebone.economyapi.EconomyAPI;
import ru.andrey.ecoext.EconomyExtPlugin;

import java.util.Locale;

public class MoneyCommand extends Command {

    private final EconomyExtPlugin plugin;

    public MoneyCommand(EconomyExtPlugin plugin) {
        super("money", "Управление деньгами", "/money set <ник> <сумма> | /money give <ник> <сумма>", new String[]{"eco"});
        this.plugin = plugin;
        setPermission("economyext.creator");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        String name = sender.getName();
        if (sender instanceof Player) {
            if (!plugin.isCreator(name) && !sender.isOp()) {
                sender.sendMessage("§8[§dСоздатель§8] §cДоступ только для §dСоздателя§c.");
                return true;
            }
        }

        if (args.length == 0) {
            sender.sendMessage("§8[§dСоздатель§8] §7Использование: §f/money set <ник> <сумма> §7| §f/money give <ник> <сумма>");
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 3) {
                sender.sendMessage("§8[§dСоздатель§8] §7Использование: §f/money set <ник> <сумма>");
                return true;
            }
            String target = args[1];
            double amount;
            try {
                amount = parse(args[2]);
            } catch (Exception ex) {
                sender.sendMessage("§8[§dСоздатель§8] §cНекорректная сумма.");
                return true;
            }
            if (amount < 0) {
                sender.sendMessage("§8[§dСоздатель§8] §cСумма не может быть отрицательной.");
                return true;
            }

            EconomyAPI api = EconomyAPI.getInstance();
            try {
                double current = api.myMoney(target);
                double diff = amount - current;
                if (diff >= 0) {
                    api.addMoney(target, diff);
                } else {
                    api.reduceMoney(target, Math.abs(diff));
                }
                sender.sendMessage("§8[§dСоздатель§8] §aБаланс §f" + target + " §aустановлен на §6" + format(amount));
            } catch (Exception ex) {
                sender.sendMessage("§8[§dСоздатель§8] §cНе удалось установить баланс: §7" + ex.getMessage());
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (args.length < 3) {
                sender.sendMessage("§8[§dСоздатель§8] §7Использование: §f/money give <ник> <сумма>");
                return true;
            }
            String target = args[1];
            double amount;
            try {
                amount = parse(args[2]);
            } catch (Exception ex) {
                sender.sendMessage("§8[§dСоздатель§8] §cНекорректная сумма.");
                return true;
            }
            if (amount <= 0) {
                sender.sendMessage("§8[§dСоздатель§8] §cСумма должна быть больше нуля.");
                return true;
            }

            EconomyAPI api = EconomyAPI.getInstance();
            try {
                api.addMoney(target, amount);
                sender.sendMessage("§8[§dСоздатель§8] §aВыдано §6" + format(amount) + " §f" + target);
                Player online = plugin.getServer().getPlayerExact(target);
                if (online != null) {
                    online.sendMessage("§8[§dСоздатель§8] §aПолучено §6" + format(amount));
                }
            } catch (Exception ex) {
                sender.sendMessage("§8[§dСоздатель§8] §cНе удалось выдать монеты: §7" + ex.getMessage());
            }
            return true;
        }

        sender.sendMessage("§8[§dСоздатель§8] §7Использование: §f/money set <ник> <сумма> §7| §f/money give <ник> <сумма>");
        return true;
    }

    private double parse(String s) {
        s = s.replace(" ", "").replace(",", ".");
        return Double.parseDouble(s);
    }

    private String format(double amount) {
        if (Math.abs(amount - Math.rint(amount)) < 1e-9) {
            return String.format(Locale.US, "%,d", (long) Math.rint(amount));
        }
        return String.format(Locale.US, "%,.2f", amount).replace(',', ' ');
    }
}
