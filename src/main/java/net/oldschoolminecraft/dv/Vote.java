package net.oldschoolminecraft.dv;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Vote
{
    private int yes, no;
    private ArrayList<String> voters;

    public Vote()
    {
        voters = new ArrayList<>();
        yes = 0;
        no = 0;
    }

    public void incrementYes(Player player)
    {
        yes++;
        voters.add(player.getName());
    }

    public void incrementNo(Player player)
    {
        no++;
        voters.add(player.getName());
    }

    public boolean hasVoted(Player player)
    {
        return voters.contains(player.getName());
    }

    public boolean didVotePass()
    {
        if (no > yes) return false;
        double percentage = calculatePercentage(yes, voters.size());
        int required = (int) DayVote.getInstance().getConfig().getConfigOption("yesVotePercentageRequired");
        System.out.println("Vote finished with percentage: " + percentage + " (" + required + " required)");
        return percentage >= required;
    }

    private double calculatePercentage(double obtained, double total)
    {
        return obtained * 100 / total;
    }
}
