package org.kitteh.superbans.systems;

import java.util.ArrayList;

import org.bukkit.event.player.PlayerPreLoginEvent;
import org.kitteh.superbans.SuperBans;
import org.kitteh.superbans.systems.mcbans.MCBansBanData.BanType;
import org.kitteh.superbans.systems.mcbans.MCBansManager;

public class MetaManager extends BanSystemManager {

    private MCBansManager mcbans;
    ArrayList<BanSystem> enabledSystems;

    public MetaManager(SuperBans plugin) {
        super(plugin, "Meta");
        this.enabledSystems= new ArrayList<BanSystem>();
        this.enabledSystems.add(BanSystem.MCBANS);//LOL HAX
        if (this.enabledSystems.contains(BanSystem.MCBANS)) {
            this.mcbans = new MCBansManager(plugin);
        }
    }

    @Override
    public void ban(String name, String reason, String admin, String ip, BanType banType) {
        if (this.enabledSystems.contains(BanSystem.MCBANS)) {
            this.mcbans.ban(name, reason, admin, ip, banType);
        }
    }

    @Override
    public void disable() {

    }

    public void disable(BanSystem system) {
        if (this.enabledSystems.contains(system)) {
            if (system.equals(BanSystem.MCBANS)) {
                this.mcbans.disable();
            }
            this.enabledSystems.remove(system);
        }
    }

    public void enable(BanSystem system) {
        this.enabledSystems.add(system);
    }

    @Override
    public void playerPreLogin(PlayerPreLoginEvent event) {
        if (this.enabledSystems.contains(BanSystem.MCBANS)) {
            this.mcbans.playerPreLogin(event);
        }
    }

    @Override
    public void unban(String name, String admin) {
        if (this.enabledSystems.contains(BanSystem.MCBANS)) {
            this.mcbans.unban(name, admin);
        }
    }

    @Override
    protected UserData acquireLookup(String name) {
        UserData data = new UserData();
        if (this.enabledSystems.contains(BanSystem.MCBANS)) {
            data = this.mcbans.acquireLookup(name);
        }
        //TEMP UGLY
        return data;
    }

}
