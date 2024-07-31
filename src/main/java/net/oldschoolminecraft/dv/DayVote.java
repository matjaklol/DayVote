package net.oldschoolminecraft.dv;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class DayVote extends JavaPlugin
{
    private static DayVote instance;

    private VoteConfig config;
    private Vote vote;
    private long lastVote;
    private Essentials essentials;
    private Timer timer = new Timer();

    public void onEnable()
    {
        instance = this;
        config = new VoteConfig(new File(getDataFolder(), "config.yml"));
        lastVote = UnixTime.now() - 250;
        getCommand("vote").setExecutor(new VoteCommand());

        System.out.println("DayVote started.");
        System.out.println("Set last vote time to: " + lastVote);
        System.out.println("Current unix time: " + UnixTime.now());
        System.out.println("Vote cooldown: " + config.getConfigOption("cooldownSeconds"));
        System.out.println("Can start vote? " + (canStartVote() ? "Yes" : "No"));
    }

    public void onDisable() {}

    public Vote getActiveVote()
    {
        return vote;
    }

    public boolean canStartVote()
    {
        long timeSinceLastVote = (UnixTime.now() - lastVote);
        int cooldown = (int) config.getConfigOption("cooldownSeconds");
        return timeSinceLastVote >= cooldown;
    }

    public Vote startNewVote()
    {
        if (!canStartVote()) return null;
        vote = new Vote();
        broadcast(String.valueOf(config.getConfigOption("messages.started")));
        int voteDurationSeconds = (int) config.getConfigOption("voteDurationSeconds");
//        Bukkit.getScheduler().scheduleAsyncDelayedTask(this, this::processVote, voteDurationSeconds * 20L);
        TimerTask voteTimerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                processVote();
                timer.cancel();
            }
        };
        timer.schedule(voteTimerTask, (1000L * voteDurationSeconds));
        return vote;
    }

    public void processVote()
    {
        if (vote == null)
        {
            startNewVote();
            return;
        }
        if (vote.didVotePass())
        {
            broadcast(String.valueOf(config.getConfigOption("messages.succeeded")));
            getServer().getWorld("world").setTime(0L);
        } else broadcast(String.valueOf(config.getConfigOption("messages.failed")));
        resetVote();
    }

    private void resetVote()
    {
        vote = null;
        lastVote = UnixTime.now();
    }

    private void broadcast(String msg)
    {
        for (Player ply : getServer().getOnlinePlayers())
            ply.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    public VoteConfig getConfig()
    {
        return config;
    }

    public Essentials getEssentials()
    {
        return essentials;
    }

    public static DayVote getInstance()
    {
        return instance;
    }
}
