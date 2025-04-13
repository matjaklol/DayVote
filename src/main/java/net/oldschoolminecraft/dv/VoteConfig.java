package net.oldschoolminecraft.dv;

import org.bukkit.util.config.Configuration;

import java.io.File;

public class VoteConfig extends Configuration {

    public VoteConfig(File file) {
        super(file);
        reload();
    }

    public void reload() {
        load();
        write();
        save();
    }

    public void write() {
        generateConfigOption("allowRainVote", true);
        generateConfigOption("allowThunder", false);
        generateConfigOption("cooldownSeconds", 180);
        generateConfigOption("RainCooldownSeconds", 3600);
        generateConfigOption("voteDurationSeconds", 60);
        generateConfigOption("rainDurationTicks", 12000);
        generateConfigOption("thunderDurationTicks", 12000);
        generateConfigOption("yesVotePercentageRequired", 40);
        generateConfigOption("yesRainVotePercentageRequired", 60);


        generateConfigOption("messages.started", "&1[&bOSM&1] &7Vote for day has started! &a/vote yes &7or &c/vote no&7.");
        generateConfigOption("messages.succeeded", "&1[&bOSM&1] &7Vote succeeded! Time will be set to day.");
        generateConfigOption("messages.failed", "&1[&bOSM&1] &7Vote failed! Time will not be changed.");
        generateConfigOption("messages.startedRain", "&1[&bOSM&1] &7Vote for rain has started! &a/vote yes &7or &c/vote no&7.");
        generateConfigOption("messages.succeededRain", "&1[&bOSM&1] &7Vote succeeded! Rain will be turned on.");
        generateConfigOption("messages.alreadyRaining", "&1[&bOSM&1] &7Vote succeeded but its already raining.");
        generateConfigOption("messages.failedRain", "&1[&bOSM&1] &7Vote failed! Rain storm will not be turned on.");
    }

    private void generateConfigOption(String key, Object defaultValue) {
        if (this.getProperty(key) == null) this.setProperty(key, defaultValue);
        final Object value = this.getProperty(key);
        this.removeProperty(key);
        this.setProperty(key, value);
    }

    public Object getConfigOption(String key) {
        return this.getProperty(key);
    }

    public Object getConfigOption(String key, Object defaultValue) {
        Object value = getConfigOption(key);
        if (value == null) value = defaultValue;
        return value;
    }
}
