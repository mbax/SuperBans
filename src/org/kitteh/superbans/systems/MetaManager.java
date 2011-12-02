package org.kitteh.superbans.systems;

import org.bukkit.event.player.PlayerPreLoginEvent;
import org.kitteh.superbans.SuperBans;
import org.kitteh.superbans.systems.mcbans.MCBansBanData.BanType;
import org.kitteh.superbans.systems.mcbans.MCBansManager;

public class MetaManager extends BanSystemManager {

    MCBansManager mcbans;

    public MetaManager(SuperBans plugin) {
        super(plugin, "Meta");
        //TEMP UGLY
        this.mcbans = new MCBansManager(plugin);
    }

    @Override
    public void ban(String name, String reason, String admin, String ip, BanType banType) {
        //TEMP UGLY
        this.mcbans.ban(name, reason, admin, ip, banType);
    }

    @Override
    public void playerPreLogin(PlayerPreLoginEvent event) {
        //TEMP UGLY
        this.mcbans.playerPreLogin(event);
    }

    @Override
    public void unban(String name, String admin) {
        //TEMP UGLY
        this.mcbans.unban(name, admin);
    }

    @Override
    protected UserData acquireLookup(String name) {
        return null;
    }

}
