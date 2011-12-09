package org.kitteh.superbans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.superbans.commands.BanCommand;
import org.kitteh.superbans.commands.KickCommand;
import org.kitteh.superbans.commands.LookupCommand;
import org.kitteh.superbans.commands.UnbanCommand;
import org.kitteh.superbans.systems.BanSystem;
import org.kitteh.superbans.systems.MetaManager;

public class SuperBans extends JavaPlugin {

    private static SuperBans instance;

    public static String combineSplit(int startIndex, String[] string) {
        final StringBuilder builder = new StringBuilder();
        for (int i = startIndex; i < string.length; i++) {
            builder.append(string[i]);
            builder.append(" ");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public static void Debug(String message, Exception e) {
        SuperBans.instance.debuggle(message, e);
    }

    public static void defaultConfig(String name) {
        final File target = new File(SuperBans.instance.getDataFolder(), name);
        final InputStream source = SuperBans.instance.getResource(name);
        if (source == null) {
            return;
        }
        if (!SuperBans.instance.getDataFolder().exists()) {
            SuperBans.instance.getDataFolder().mkdir();
        }
        try {
            if (!target.exists()) {
                final OutputStream output = new FileOutputStream(target);
                int len;
                final byte[] buf = new byte[1024];
                while ((len = source.read(buf)) > 0) {
                    output.write(buf, 0, len);
                }
                output.close();

            }
        } catch (final Exception ex) {
            SuperBans.Debug("Could not save default config to " + target, ex);
        }
        try {
            source.close();
        } catch (final Exception e) {
            //Meh
        }
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

    public static void messageByNoPerm(String permission, String message) {
        for (final Player player : SuperBans.instance.getServer().getOnlinePlayers()) {
            if ((player != null) && !player.hasPermission(permission)) {
                player.sendMessage(message);
            }
        }
    }

    public static void messageByPerm(String permission, String message) {
        for (final Player player : SuperBans.instance.getServer().getOnlinePlayers()) {
            if ((player != null) && player.hasPermission(permission)) {
                player.sendMessage(message);
            }
        }
    }

    public static void messageByPermExclusion(String permission, String exclude, String message) {
        for (final Player player : SuperBans.instance.getServer().getOnlinePlayers()) {
            if ((player != null) && player.hasPermission(permission) && !player.getName().equals(exclude)) {
                player.sendMessage(message);
            }
        }
    }

    public static void scheduleAsync(Runnable runnable) {
        SuperBans.instance.getServer().getScheduler().scheduleAsyncDelayedTask(SuperBans.instance, runnable);
    }

    public static Server server() {
        return SuperBans.instance.getServer();
    }

    private static void set(SuperBans me) {
        SuperBans.instance = me;
    }

    private final boolean debug = true;

    private MetaManager manager;

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

        SuperBans.defaultConfig("config.yml");

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
        this.getCommand("unban").setExecutor(new UnbanCommand());
        this.getCommand("lookup").setExecutor(new LookupCommand(this.getConfig().getInt("Features.Lookup.MaxLength")));
        this.getCommand("kick").setExecutor(new KickCommand(this));
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
