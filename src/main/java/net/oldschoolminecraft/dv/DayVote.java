package net.oldschoolminecraft.dv;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DayVote extends JavaPlugin {


    private static DayVote instance;

    private VoteConfig config;
    private Vote vote;
    private long lastVote;
    private long lastStartVote;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void onEnable() {
        instance = this;
        config = new VoteConfig(new File(getDataFolder(), "config.yml"));
        lastVote = UnixTime.now() - 250;
        getCommand("vote").setExecutor(new VoteCommand());

        System.out.println("DayVote version: "+ getDescription().getVersion() + " enabled!");
        System.out.println("Last Vote Time: " + lastVote);
        System.out.println("Current Unix Time: " + UnixTime.now());
        System.out.println("Vote Cooldown Setting: " + config.getConfigOption("cooldownSeconds"));
        System.out.println("Can start vote? " + (canStartVote() ? "Yes" : "No"));
    }

    @Override
    public void onDisable() {
        forceCancelVote();
        System.out.println("DayVote version: "+ getDescription().getVersion() + " disabled!");
    }

    public Vote getActiveVote() {
        return vote;
    }

    public synchronized boolean canStartVote() {
        long timeSinceLastVote = (UnixTime.now() - lastVote);
        int cooldown = (int) config.getConfigOption("cooldownSeconds");
        return timeSinceLastVote >= cooldown;
    }

    public int getCooldownTimeLeft() {
        long timeSinceLastVote = (UnixTime.now() - lastVote);
        int cooldown = (int) config.getConfigOption("cooldownSeconds");
        return (int) (cooldown-timeSinceLastVote);
    }

    public int getVoteTimeLeft() {
        long timeSinceLastVoteStart = (UnixTime.now() - lastStartVote);
        int voteDurationSeconds = (int) config.getConfigOption("voteDurationSeconds");
        return (int) (voteDurationSeconds-timeSinceLastVoteStart);
    }

    public String formatTime(final long seconds) {
        final long minute = TimeUnit.SECONDS.toMinutes(seconds);
        final long second = TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.SECONDS.toMinutes(seconds) * 60L;
        return minute + "m" + second + "s";
    }

    public synchronized Vote startNewVote() {
        if (!canStartVote()) return null;
        vote = new Vote();
        broadcast(String.valueOf(config.getConfigOption("messages.started")));
        int voteDurationSeconds = (int) config.getConfigOption("voteDurationSeconds");
//        Bukkit.getScheduler().scheduleAsyncDelayedTask(this, this::processVote, voteDurationSeconds * 20L);
        scheduler.schedule(this::processVote, voteDurationSeconds, TimeUnit.SECONDS);
        lastStartVote = UnixTime.now();
        return vote;
    }

    public synchronized void processVote() {
        if (vote == null) {
            startNewVote();
            return;
        }

        if (vote.didVotePass()) {
            broadcast(String.valueOf(config.getConfigOption("messages.succeeded")));
            Bukkit.getServer().getWorld("world").setTime(0);
        }
        else {
            broadcast(String.valueOf(config.getConfigOption("messages.failed")));
        }
        resetVote();
    }


    private synchronized void resetVote() {
        vote = null;
        lastVote = UnixTime.now();
    }

    private synchronized void forceCancelVote() {
        if (vote != null) {
            resetVote();
        }
    }

    private void broadcast(String msg) {
        for (Player all : getServer().getOnlinePlayers())
            all.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    public VoteConfig getConfig() {
        return config;
    }

    public static DayVote getInstance() {
        return instance;
    }
}

