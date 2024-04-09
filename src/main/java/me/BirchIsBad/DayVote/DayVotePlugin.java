package me.BirchIsBad.DayVote;

import me.BirchIsBad.DayVote.Vote.DayVoter;
import org.bukkit.plugin.java.JavaPlugin;

public class DayVotePlugin extends JavaPlugin
{
    public void onEnable() {
        final DayVoter executor = new DayVoter();
        this.getCommand("vote").setExecutor(executor);
        this.getCommand("startvote").setExecutor(executor);
    }

    public void onDisable() {
    }
}
