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
        generateConfigOption("cooldownSeconds", 180);
        generateConfigOption("voteDurationSeconds", 60);
        generateConfigOption("yesVotePercentageRequired", 40);

        generateConfigOption("messages.started", "&1[&bServer&1] &7Vote for day has started! &a/vote yes &7or &c/vote no.");
        generateConfigOption("messages.succeeded", "&1[&bServer&1] &7Vote succeeded! Time will be set to day.");
        generateConfigOption("messages.failed", "&1[&bServer&1] &7Vote failed! Time will not be changed.");
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
