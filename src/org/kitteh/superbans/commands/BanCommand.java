package org.kitteh.superbans.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kitteh.superbans.SuperBans;
import org.kitteh.superbans.systems.mcbans.MCBansBanData.BanType;

public class BanCommand implements CommandExecutor {

    private final SuperBans plugin;

    public BanCommand(SuperBans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final boolean ban = sender.hasPermission("superbans.ban");
        final boolean tempban = sender.hasPermission("superbans.tempban");
        if (ban || tempban) {
            if (args.length > 0) {
                String reason;
                if (args.length > 1) {
                    reason = SuperBans.combineSplit(1, args);
                } else {
                    reason = "No reason specified";
                }

                BanType banType = BanType.TEMP;
                boolean banning = false;
                if (label.equals("BAN")) {
                    banType = BanType.GLOBAL;
                    banning = true;
                } else if (label.equalsIgnoreCase("ban")) {
                    banType = BanType.LOCAL;
                    banning = true;
                }
                if ((banning && !ban) || (!banning && !tempban)) {
                    sender.sendMessage(ChatColor.RED + "I'm afraid you're not qualified");
                    return true;
                }
                final Player target = this.plugin.getServer().getPlayer(args[0]);
                String ip;
                String name;
                if (target != null) {
                    name = target.getName();
                    ip = target.getAddress().getAddress().getHostAddress();
                    target.kickPlayer(ChatColor.RED + "Banned: " + ChatColor.WHITE + reason);
                } else {
                    name = args[0];
                    ip = "0.0.0.0";
                }
                this.plugin.getManager().ban(name, reason, sender.getName(), ip, banType);
            } else {
                sender.sendMessage(ChatColor.RED + "Forgot a name");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "I'm afraid you're not qualified");
        }
        return true;
    }

}
