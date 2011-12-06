package org.kitteh.superbans;

import java.util.logging.Level;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.superbans.commands.BanCommand;
import org.kitteh.superbans.commands.LookupCommand;
import org.kitteh.superbans.commands.UnbanCommand;
import org.kitteh.superbans.systems.BanSystem;
import org.kitteh.superbans.systems.MetaManager;

public class SuperBans extends JavaPlugin {

    public static String combineSplit(int startIndex, String[] string) {
        final StringBuilder builder = new StringBuilder();
        for (int i = startIndex; i < string.length; i++) {
            builder.append(string[i]);
            builder.append(" ");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public static MetaManager getManager() {
        return SuperBans.instance.manager;
    }

    public static String getVersion() {
        return SuperBans.instance.getDescription().getVersion();
    }

    public static void log(String message) {
        SuperBans.instance.getServer().getLogger().info("[SuperBans] " + message);
    }

    public static void messageByPerm(String permission, String message) {
        for (final Player player : SuperBans.instance.getServer().getOnlinePlayers()) {
            if ((player != null) && player.hasPermission(permission)) {
                player.sendMessage(message);
            }
        }
    }

    public static Server server() {
        return SuperBans.instance.getServer();
    }

    private static void set(SuperBans me) {
        SuperBans.instance = me;
    }

    private final boolean debug = true;

    private MetaManager manager;

    private static SuperBans instance;

    public static void Debug(String message, Exception e) {
        SuperBans.instance.debuggle(message, e);
    }

    public static void scheduleAsync(Runnable runnable) {
        SuperBans.instance.getServer().getScheduler().scheduleAsyncDelayedTask(SuperBans.instance, runnable);
    }

    public boolean debug() {
        return this.debug;
    }

    @Override
    public void onDisable() {
        SuperBans.log("Disabled!");
    }

    @Override
    public void onEnable() {
        SuperBans.set(this);

        this.manager = new MetaManager(this);
        this.manager.enable(BanSystem.MCBANS);

        this.getServer().getPluginManager().registerEvent(Type.PLAYER_PRELOGIN, new PlayerListener() {
            @Override
            public void onPlayerPreLogin(PlayerPreLoginEvent event) {
                SuperBans.getManager().playerPreLogin(event);
            }
        }, Priority.Normal, this);

        this.getConfig().options().copyDefaults(true);

        final BanCommand banCommand = new BanCommand(this);
        this.getCommand("ban").setExecutor(banCommand);
        //this.getCommand("tempban").setExecutor(banCommand);
        this.getCommand("unban").setExecutor(new UnbanCommand(this));
        this.getCommand("lookup").setExecutor(new LookupCommand(this, this.getConfig().getInt("Features.Lookup.MaxLength")));
        this.saveConfig();

        SuperBans.log("Version " + this.getDescription().getVersion() + " enabled!");
    }

    private void debuggle(String message, Exception e) {
        if (this.debug) {
            this.getServer().getLogger().log(Level.WARNING, message, e);
        } else {
            SuperBans.log(message);
        }
    }

}
