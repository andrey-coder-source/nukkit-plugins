package ru.andrey.ecoext.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import ru.andrey.cases.CaseManager;
import ru.andrey.cases.CaseType;
import ru.andrey.cases.CasesPlugin;
import ru.andrey.ecoext.EconomyExtPlugin;

public class GiveCaseCommand extends Command {

    private final EconomyExtPlugin plugin;

    public GiveCaseCommand(EconomyExtPlugin plugin) {
        super("givecase", "Выдать кейс или ключ себе", "/givecase <кейс> [кол-во] [key]");
        this.plugin = plugin;
        setPermission("economyext.creator");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§8[§dСоздатель§8] §cКоманда доступна только игрокам.");
            return true;
        }

        String name = player.getName();
        if (!plugin.isCreator(name) && !player.isOp()) {
            player.sendMessage("§8[§dСоздатель§8] §cДоступ только для §dСоздателя§c.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§8[§dСоздатель§8] §7Использование: §f/givecase <кейс> [кол-во] [key]");
            return true;
        }

        CasesPlugin casesPlugin = (CasesPlugin) plugin.getServer().getPluginManager().getPlugin("Cases");
        if (casesPlugin == null) {
            player.sendMessage("§8[§dСоздатель§8] §cПлагин Cases не найден.");
            return true;
        }

        CaseManager manager = casesPlugin.getManager();
        CaseType type = manager.getCase(args[0]);
        if (type == null) {
            player.sendMessage("§8[§dСоздатель§8] §cКейс не найден: §f" + args[0] + "§c. Доступны: free, common, elite");
            return true;
        }

        int amount = 1;
        if (args.length >= 2) {
            try {
                amount = Integer.parseInt(args[1]);
                if (amount < 1 || amount > 999) {
                    amount = 1;
                }
            } catch (NumberFormatException ignored) {
                amount = 1;
            }
        }

        boolean key = args.length >= 3
                && (args[2].equalsIgnoreCase("key") || args[2].equalsIgnoreCase("ключ")
                || args[2].equalsIgnoreCase("keys") || args[2].equalsIgnoreCase("ключи"));

        String error = manager.give(player, type.getId(), amount, key);
        if (error != null) {
            player.sendMessage("§8[§dСоздатель§8] §c" + error);
            return true;
        }

        String what = key ? "ключ" + (amount == 1 ? "" : amount % 10 >= 2 && amount % 10 <= 4 && (amount % 100 < 10 || amount % 100 >= 20) ? "а" : "ей") : "кейс" + (amount == 1 ? "" : amount % 10 >= 2 && amount % 10 <= 4 && (amount % 100 < 10 || amount % 100 >= 20) ? "а" : "ов");
        player.sendMessage("§8[§dСоздатель§8] §aВыдано себе: §f" + amount + " " + what + " §8(§f" + type.getDisplay() + "§8)");
        return true;
    }
}
