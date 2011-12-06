package org.kitteh.superbans.commands;

import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.kitteh.superbans.SuperBans;
import org.kitteh.superbans.systems.UserData;

public class LookupCommand implements CommandExecutor {

    private final SuperBans plugin;

    private final int max;

    public LookupCommand(SuperBans plugin, int max) {
        this.plugin = plugin;
        this.max = max;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("superbans.lookup")) {
            if (args.length > 0) {
                final UserData data = SuperBans.getManager().lookup(args[0]);
                final StringBuilder info = new StringBuilder();
                info.append("Player ").append(args[0]);
                for (final Entry<String, Object> entry : data.cloneExtra().entrySet()) {
                    if (entry.getValue() instanceof String) {
                        info.append(", ").append(entry.getKey()).append(":").append(entry.getValue());
                    }
                }
                sender.sendMessage(ChatColor.AQUA + info.toString());

                int offset = 0;
                if (args.length > 1) {
                    offset = Integer.valueOf(args[1]);
                }
                final Iterator<String> iterator = data.getBanList().getList().iterator();
                for (int x = 0; (x < offset) && iterator.hasNext(); x++) {
                    iterator.next();
                }
                if (!iterator.hasNext()) {
                    sender.sendMessage(ChatColor.GREEN + "No bans to list");
                }
                for (int x = 0; (x < this.max) && iterator.hasNext(); x++) {
                    sender.sendMessage(iterator.next());
                }
                if (iterator.hasNext()) {
                    sender.sendMessage(ChatColor.RED + "And many more than I can list");
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
