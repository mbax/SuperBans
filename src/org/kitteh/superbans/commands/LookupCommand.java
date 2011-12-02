package org.kitteh.superbans.commands;

import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.kitteh.superbans.SuperBans;
import org.kitteh.superbans.systems.UserData;

public class LookupCommand implements CommandExecutor {

    private final SuperBans plugin;

    public LookupCommand(SuperBans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("superbans.lookup")) {
            if (args.length > 0) {
                final UserData data = this.plugin.getManager().lookup(args[0]);
                final StringBuilder info = new StringBuilder();
                info.append("Player ").append(args[0]);
                for (final Entry<String, Object> entry : data.cloneExtra().entrySet()) {
                    if (entry.getValue() instanceof String) {
                        info.append(", ").append(entry.getKey()).append(":").append(entry.getValue());
                    }
                }
                sender.sendMessage(ChatColor.AQUA + info.toString());
                for (final String ban : data.getBanList().getList()) {
                    sender.sendMessage(ban);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Forgot a name");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "I'm afraid you're not qualified");
        }
        return true;
    }
}
