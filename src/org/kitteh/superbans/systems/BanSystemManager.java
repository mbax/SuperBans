package org.kitteh.superbans.systems;

import java.util.HashMap;

import org.bukkit.event.player.PlayerPreLoginEvent;
import org.kitteh.superbans.SuperBans;
import org.kitteh.superbans.systems.mcbans.MCBansBanData.BanType;

public abstract class BanSystemManager {
    private static void setName(String name) {
        BanSystemManager.internalSystemName = name;
    }

    private final HashMap<String, UserData> cache;

    protected SuperBans plugin;

    private static String internalSystemName;

    protected static String getName() {
        return BanSystemManager.internalSystemName;
    }

    public BanSystemManager(SuperBans plugin, String name) {
        this.plugin = plugin;
        this.cache = new HashMap<String, UserData>();
        BanSystemManager.setName(name);
    }

    public abstract void ban(String name, String reason, String admin, String ip, BanType banType);

    public UserData lookup(String name) {
        UserData data = this.cache.get(name);
        if ((data == null) || data.old()) {
            data = this.acquireLookup(name);
            this.cache.put(name, data);
        }
        return data;
    }

    public abstract void playerPreLogin(PlayerPreLoginEvent event);

    public abstract void unban(String name, String admin);

    protected abstract UserData acquireLookup(String name);
}