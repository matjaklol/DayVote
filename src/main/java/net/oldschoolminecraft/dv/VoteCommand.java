package net.oldschoolminecraft.dv;

import com.earth2me.essentials.User;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class VoteCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload") && (sender.isOp() || sender.hasPermission("DayVote.Reload")))
        {
            DayVote.getInstance().getConfig().reload();
            sender.sendMessage(ChatColor.GREEN + "Reloaded configuration");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reset") && (sender.isOp() || sender.hasPermission("DayVote.Reset")))
        {
            DayVote.getInstance().processVote();
            sender.sendMessage(ChatColor.GREEN + "Reset vote status");
            return true;
        }

        if (!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Vote vote = DayVote.getInstance().getActiveVote();

        if (args.length == 0)
        {
            if (vote == null) // no vote active
                sender.sendMessage(ChatColor.RED + "No vote is active. Use: " + ChatColor.GRAY + "/vote day" + ChatColor.RED + " to start a vote.");
            else sender.sendMessage(ChatColor.RED + "A vote is currently active. Use: " + ChatColor.GRAY + "/vote <yes/no>" + ChatColor.RED + " to vote.");
            return true;
        }

        if (args[0].equalsIgnoreCase("day"))
        {
            if (vote != null) countYesVote(vote, (Player)sender);
            else {
                vote = DayVote.getInstance().startNewVote();
                if (vote == null) // cooldown prevented new vote start
                {
                    sender.sendMessage(ChatColor.RED + "You have to wait to start another vote.");
                    return true;
                }
                countYesVote(vote, (Player)sender);
            }
        }

        if (args[0].equalsIgnoreCase("yes"))
        {
            if (vote != null) countYesVote(vote, (Player)sender);
            else sender.sendMessage(ChatColor.RED + "No vote is active. Use: " + ChatColor.GRAY + "/vote day" + ChatColor.RED + " to start a vote.");
        }

        if (args[0].equalsIgnoreCase("no"))
        {
            if (vote != null) countNoVote(vote, (Player)sender);
            else sender.sendMessage(ChatColor.RED + "No vote is active. Use: " + ChatColor.GRAY + "/vote day" + ChatColor.RED + " to start a vote.");
        }

        return true;
    }

    private void countYesVote(Vote vote, Player ply)
    {
        if (vote.hasVoted(ply))
        {
            ply.sendMessage(ChatColor.RED + "You have already voted.");
            return;
        }
        vote.incrementYes(ply);
        ply.sendMessage(ChatColor.GRAY + "Your vote has been counted.");
    }

    private void countNoVote(Vote vote, Player ply)
    {
        if (vote.hasVoted(ply))
        {
            ply.sendMessage(ChatColor.RED + "You have already voted.");
            return;
        }
        vote.incrementNo(ply);
        ply.sendMessage(ChatColor.GRAY + "Your vote has been counted.");
    }
}
