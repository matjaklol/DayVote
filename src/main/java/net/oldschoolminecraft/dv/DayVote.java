package net.oldschoolminecraft.dv;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DayVote extends JavaPlugin
{
    private static DayVote instance;

    private VoteConfig config;
    private Vote vote;
    private long lastVote;
    private Essentials essentials;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void onEnable()
    {
        instance = this;
        config = new VoteConfig(new File(getDataFolder(), "config.yml"));
        lastVote = UnixTime.now() - 250L;
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

    public synchronized boolean canStartVote()
    {
        if(vote != null){
            int voteCooldown = (int) config.getConfigOption("cooldownSeconds", 150);
            voteCooldown += (int) config.getConfigOption("voteDurationSeconds", 65);
            
            //Failsafe. If vote is stuck in purgatory, check to see when it was initiated and whether
            //or not we should start a new vote. 
            if(UnixTime.now() - vote.getVoteStartTime() > voteDuration){
                return true;
            }
        }
        
        long timeSinceLastVote = (UnixTime.now() - lastVote);
        int cooldown = (int) config.getConfigOption("cooldownSeconds");
        return timeSinceLastVote >= cooldown;
    }

    public synchronized Vote startNewVote()
    {
        if (!canStartVote()) return null;
        vote = new Vote();
        broadcast(String.valueOf(config.getConfigOption("messages.started")));
        int voteDurationSeconds = (int) config.getConfigOption("voteDurationSeconds");
//        Bukkit.getScheduler().scheduleAsyncDelayedTask(this, this::processVote, voteDurationSeconds * 20L);
        scheduler.schedule(this::processVote, voteDurationSeconds, TimeUnit.SECONDS);
        return vote;
    }

    public synchronized void processVote()
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

    private synchronized void resetVote()
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
