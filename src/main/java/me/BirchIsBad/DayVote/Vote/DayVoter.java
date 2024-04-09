package me.BirchIsBad.DayVote.Vote;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;

public class DayVoter implements CommandExecutor
{
    private int yesVotes;
    private int noVotes;
    private final ArrayList<String> votedList;
    private boolean rainCooldown;
    private boolean isVoteOn;

    public DayVoter()
    {
        this.yesVotes = 0;
        this.noVotes = 0;
        this.votedList = new ArrayList<String>();
        this.rainCooldown = false;
        this.isVoteOn = false;
    }

    protected void addYesVote()
    {
        ++this.yesVotes;
    }

    protected void addNoVote()
    {
        ++this.noVotes;
    }

    public boolean isPositiveVoteResult()
    {
        Bukkit.getLogger().info(String.format("yesVotes == %d", this.yesVotes));
        Bukkit.getLogger().info(String.format("noVotes == %d", this.noVotes));
        return this.yesVotes > this.noVotes;
    }

    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage("You must be a player to use this command!");
            return true;
        }
        final Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("startvote"))
        {
            if (player.hasPermission("DayVote.StartVote"))
            {
                if (args.length == 0)
                {
                    player.sendMessage(ChatColor.RED + "You must specify a type of vote!");
                    return false;
                }
                if (args[0].equalsIgnoreCase("day")) return this.startDayVote(player);
            } else player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
        }
        if (cmd.getName().equalsIgnoreCase("vote"))
        {
            if (args.length > 0 && args[0].equalsIgnoreCase("day")) return startDayVote(player);

            Bukkit.getLogger().info(player.getName() + " has attempted to vote");
            if (player.hasPermission("DayVote.Vote"))
            {
                Bukkit.getLogger().info(String.format("vote command, args: == %s", String.join(", ", (CharSequence[]) args)));
                if (args.length == 0)
                {
                    if (!isVoteOn)
                    {
                        sender.sendMessage(ChatColor.RED + "There is no vote! Use " + ChatColor.GRAY + "/vote day" + ChatColor.RED + " to start a vote.");
                        return true;
                    } else player.sendMessage(ChatColor.RED + "A vote is in progress. Usage: /vote <yes/no>");
                } else {
                    if (args[0].equalsIgnoreCase("yes")) return this.voteYes(player);
                    if (args[0].equalsIgnoreCase("no")) return this.voteNo(player);
                    if (args[0].equalsIgnoreCase("day")) return this.startDayVote(player);
                }
            } else player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
        }
        return false;
    }

    private boolean startDayVote(final Player player)
    {
        final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("RainVote"), () -> DayVoter.this.rainCooldown = false, 15000L);
        if (!this.rainCooldown)
        {
            Bukkit.getLogger().info("Starting vote for DAY...");
            Bukkit.getLogger().info(String.format("yesVotes == %d", this.yesVotes));
            player.sendMessage(ChatColor.GRAY + "You started the vote!");
            Bukkit.broadcastMessage(ChatColor.BLUE + "[" + ChatColor.AQUA + "OSM" + ChatColor.BLUE + "] " + ChatColor.GRAY + "Vote for day has been started. " + ChatColor.GREEN + "/vote yes" + ChatColor.GRAY + " or " + ChatColor.RED + "/vote no");
            this.isVoteOn = true;
            this.rainCooldown = true;
            scheduler.scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("RainVote"), () ->
            {
                if (DayVoter.this.isPositiveVoteResult())
                {
                    Bukkit.broadcastMessage(ChatColor.BLUE + "[" + ChatColor.AQUA + "OSM" + ChatColor.BLUE + "] " + ChatColor.GRAY + "Vote succeeded time is set to Day.");
                    Bukkit.getWorld("world").setTime(0L);
                    yesVotes = 0;
                    noVotes = 0;
                } else Bukkit.broadcastMessage(ChatColor.BLUE + "[" + ChatColor.AQUA + "OSM" + ChatColor.BLUE + "] " + ChatColor.GRAY + "Vote failed time is staying as is.");
                DayVoter.this.votedList.clear();
                DayVoter.this.isVoteOn = false;
            }, 1300L);
            return true;
        }
        player.sendMessage(ChatColor.GRAY + "The vote is on cooldown, try again later.");
        Bukkit.getLogger().info(String.format("Ateempted to start vote for DAY but it is on cooldown", new Object[0]));
        return false;
    }

    private boolean voteYes(final Player player)
    {
        if (!this.isVoteOn)
        {
            player.sendMessage(ChatColor.GRAY + "There is no vote in progress!");
            return false;
        }
        if (!this.votedList.contains(player.getName()))
        {
            player.sendMessage(ChatColor.GREEN + "You have voted");
            this.votedList.add(player.getName());
            Bukkit.getLogger().info("Voting Yes...");
            final int yv = this.yesVotes;
            this.addYesVote();
            Bukkit.getLogger().info(String.format("yesVotes before: %d, after: %d", yv, this.yesVotes));
            return true;
        }
        player.sendMessage(ChatColor.GRAY + "You have already voted!");
        return false;
    }

    private boolean voteNo(final Player player)
    {
        if (!this.isVoteOn)
        {
            player.sendMessage(ChatColor.GRAY + "There is no vote in progress!");
            return false;
        }
        if (!this.votedList.contains(player.getName()))
        {
            player.sendMessage(ChatColor.GREEN + "You have voted");
            this.votedList.add(player.getName());
            Bukkit.getLogger().info("Voting No...");
            final int nv = this.noVotes;
            this.addNoVote();
            Bukkit.getLogger().info(String.format("noVotes before: %d, after: %d", nv, this.noVotes));
            return true;
        }
        player.sendMessage(ChatColor.GRAY + "You have already voted!");
        return false;
    }
}