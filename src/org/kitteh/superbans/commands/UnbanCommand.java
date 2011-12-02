package org.kitteh.superbans.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.kitteh.superbans.SuperBans;

public class UnbanCommand implements CommandExecutor {

    private final SuperBans plugin;

    public UnbanCommand(SuperBans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("superbans.unban")) {
            if (args.length > 0) {
                this.plugin.getManager().unban(args[0], sender.getName());
                sender.sendMessage("Unbanning " + args[0]);
            } else {
                sender.sendMessage(ChatColor.RED + "Forgot a name");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "I'm afraid you're not qualified");
        }
        return true;
    }
}
