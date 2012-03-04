package org.kitteh.superbans.systems;

import java.util.ArrayList;

import org.bukkit.event.player.PlayerPreLoginEvent;
import org.kitteh.superbans.SuperBans;
import org.kitteh.superbans.systems.mcbans.MCBansBanData.BanType;
import org.kitteh.superbans.systems.mcbans.MCBansManager;
import org.kitteh.superbans.systems.mcbouncer.MCBouncerManager;

public class MetaManager extends BanSystemManager {

    private MCBansManager mcbans;
    private MCBouncerManager mcbouncer;
    private ExceptionsManager exceptions;
    ArrayList<BanSystem> enabledSystems;

    public MetaManager(SuperBans plugin) {
        super(plugin, "Meta");
        this.enabledSystems = new ArrayList<BanSystem>();
        if(plugin.getConfig().getBoolean("Systems.MCBans", false)){
            this.enabledSystems.add(BanSystem.MCBANS);
            this.mcbans = new MCBansManager(plugin);
        }
        if(plugin.getConfig().getBoolean("Systems.MCBouncer", false)){
            this.enabledSystems.add(BanSystem.MCBOUNCER);
            this.mcbouncer = new MCBouncerManager(plugin);
        }
        this.enabledSystems.add(BanSystem.EXCEPTIONS);//LOL HAX
        this.exceptions = new ExceptionsManager();//LOL HAX
    }

    @Override
    public void ban(String name, String reason, String admin, String ip, BanType banType) {
        if (this.enabledSystems.contains(BanSystem.MCBANS)) {
            this.mcbans.ban(name, reason, admin, ip, banType);
        }
        if (this.enabledSystems.contains(BanSystem.MCBOUNCER)) {
            this.mcbouncer.ban(name, reason, admin, ip, banType);
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
            if (system.equals(BanSystem.MCBOUNCER)) {
                this.mcbouncer.disable();
            }
            this.enabledSystems.remove(system);
        }
    }

    public void enable(BanSystem system) {
        this.enabledSystems.add(system);
    }

    @Override
    public void playerPreLogin(PlayerPreLoginEvent event) {
        if (this.enabledSystems.contains(BanSystem.EXCEPTIONS) && this.exceptions.exception(event.getName())) {
            return;
        }
        if (this.enabledSystems.contains(BanSystem.MCBANS)) {
            this.mcbans.playerPreLogin(event);
        }
        if (this.enabledSystems.contains(BanSystem.MCBOUNCER)) {
            this.mcbouncer.playerPreLogin(event);
        }
    }

    @Override
    public void unban(String name, String admin) {
        if (this.enabledSystems.contains(BanSystem.MCBANS)) {
            this.mcbans.unban(name, admin);
        }
        if (this.enabledSystems.contains(BanSystem.MCBOUNCER)) {
            this.mcbouncer.unban(name, admin);
        }
    }

    @Override
    protected UserData acquireLookup(String name) {
        UserData data = new UserData();
        if (this.enabledSystems.contains(BanSystem.MCBANS)) {
            data = this.mcbans.acquireLookup(name);
        }
        if (this.enabledSystems.contains(BanSystem.MCBOUNCER)) {
            data = this.mcbouncer.acquireLookup(name);
        }
        //TEMP UGLY
        return data;
    }

}
