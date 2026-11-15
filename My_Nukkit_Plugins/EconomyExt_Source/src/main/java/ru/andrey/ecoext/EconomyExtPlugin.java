package ru.andrey.ecoext;

import cn.nukkit.plugin.PluginBase;
import ru.andrey.ecoext.command.GiveCaseCommand;
import ru.andrey.ecoext.command.MoneyCommand;
import ru.andrey.ranks.Rank;
import ru.andrey.ranks.RanksPlugin;

public class EconomyExtPlugin extends PluginBase {

    private RanksPlugin ranks;

    @Override
    public void onEnable() {
        ranks = (RanksPlugin) getServer().getPluginManager().getPlugin("Ranks");
        getServer().getCommandMap().register("economyext", new MoneyCommand(this));
        getServer().getCommandMap().register("economyext", new GiveCaseCommand(this));
        getLogger().info("EconomyExt включён. Команды: /money, /givecase (только Создатель)");
    }

    public boolean isCreator(String name) {
        if (ranks == null || name == null) {
            return false;
        }
        Rank rank = ranks.getRankManager().getRank(name);
        return rank != null && rank == Rank.CREATOR;
    }

    public RanksPlugin getRanks() {
        return ranks;
    }
}
