package net.oldschoolminecraft.dv;

import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.WeatherListener;

public class WeatherHandler extends WeatherListener
{
    public void onWeatherChange(WeatherChangeEvent event)
    {
        event.setCancelled(true);
    }
}
