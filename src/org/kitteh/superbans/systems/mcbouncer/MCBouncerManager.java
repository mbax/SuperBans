package org.kitteh.superbans.systems.mcbouncer;

import org.bukkit.event.player.PlayerPreLoginEvent;
import org.kitteh.superbans.SuperBans;
import org.kitteh.superbans.systems.JSONBanSystemManager;
import org.kitteh.superbans.systems.UserData;
import org.kitteh.superbans.systems.mcbans.MCBansBanData.BanType;

/**Unimplemented
 * @author Matt
 *
 */
public class MCBouncerManager extends JSONBanSystemManager{

    public MCBouncerManager(SuperBans plugin, String name) {
        super(plugin, name);
    }

    @Override
    public void ban(String name, String reason, String admin, String ip, BanType banType) {
        
    }

    @Override
    public void disable() {
        
    }

    @Override
    public void playerPreLogin(PlayerPreLoginEvent event) {
        
    }

    @Override
    public void unban(String name, String admin) {
        
    }

    @Override
    protected UserData acquireLookup(String name) {
        return null;
    }

}
