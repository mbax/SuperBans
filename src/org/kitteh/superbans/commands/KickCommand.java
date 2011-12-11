package org.kitteh.superbans.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kitteh.superbans.SuperBans;

public class KickCommand implements CommandExecutor {

    final private SuperBans plugin;
    final private String perm = "superbans.kick";

    public KickCommand(SuperBans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(this.perm)) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "/kick playername {reason}");
                return true;
            }
            final Player target = this.plugin.getServer().getPlayer(args[0]);
            if (target != null) {
                final StringBuilder kick = new StringBuilder();
                kick.append("icked");
                if (args.length > 1) {
                    kick.append(": ").append(ChatColor.WHITE).append(SuperBans.combineSplit(1, args));
                }
                final String message = kick.toString();
                SuperBans.messageByNoPerm(this.perm, ChatColor.RED + target.getName() + " k" + message);
                SuperBans.messageByPermExclusion(this.perm, sender.getName(), ChatColor.RED + "[" + sender.getName() + "] " + target.getName() + " k" + message);
                SuperBans.log(ChatColor.stripColor(target.getName() + " k" + message));
                target.kickPlayer(ChatColor.RED + "K" + message);
            }
        } else {
            sender.sendMessage(ChatColor.RED + "I'm sorry " + sender.getName() + " I'm afraid I can't do that");
        }
        return true;
    }

}
