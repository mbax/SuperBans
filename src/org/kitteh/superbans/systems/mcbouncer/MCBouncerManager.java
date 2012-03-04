package org.kitteh.superbans.systems.mcbouncer;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.kitteh.superbans.SuperBans;
import org.kitteh.superbans.systems.JSONBanSystemManager;
import org.kitteh.superbans.systems.UserData;
import org.kitteh.superbans.systems.mcbans.MCBansBanData.BanType;

/**
 * Unimplemented
 * 
 * @author Matt
 * 
 */
public class MCBouncerManager extends JSONBanSystemManager {

    private final String MCBOUNCER_URL="http://mcbouncer.com/";
    private String API_KEY;
    private boolean enable_bans;
    private boolean enable_loginban;
    private boolean enable_lookup;
    private boolean enable_unbans;
    
    public MCBouncerManager(SuperBans plugin) {
        super(plugin, "mcbouncer");
        SuperBans.defaultConfig("mcbouncer.yml");
        final File file = new File(plugin.getDataFolder(), "mcbouncer.yml");
        final FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        this.API_KEY = config.getString("Settings.APIKEY");
        this.enable_bans = config.getBoolean("Features.Ban");
        this.enable_loginban = config.getBoolean("Features.CheckAtLogin");
        this.enable_lookup = config.getBoolean("Features.Lookup");
        this.enable_unbans = config.getBoolean("Features.Unban");
    }

    @Override
    public void ban(String name, String reason, String admin, String ip, BanType banType) {
        if (!this.enable_bans) {
            return;
        }
    }

    @Override
    public void disable() {

    }

    @Override
    public void playerPreLogin(PlayerPreLoginEvent event) {
        if (!this.enable_loginban) {
            return;
        }
    }

    @Override
    public void unban(String name, String admin) {
        if (!this.enable_unbans) {
            return;
        }
    }

    @Override
    public UserData acquireLookup(String name) {
        if (!this.enable_lookup) {
            return new UserData();
        }
        
        return null;
    }
    
}
