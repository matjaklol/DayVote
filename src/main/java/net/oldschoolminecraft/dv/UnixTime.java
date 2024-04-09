package net.oldschoolminecraft.dv;

public class UnixTime
{
    public static long now()
    {
        return (System.currentTimeMillis() / 1000L);
    }
}
