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
    private final String perm = "superbans.ban";

    public BanCommand(SuperBans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(this.perm)) {
            if (args.length > 0) {
                String reason;
                if (args.length > 1) {
                    reason = SuperBans.combineSplit(1, args);
                } else {
                    reason = "No reason specified";
                }
                BanType banType = BanType.LOCAL;
                if (label.equals("BAN")) {
                    banType = BanType.GLOBAL;
                }
                final Player target = this.plugin.getServer().getPlayer(args[0]);
                String ip;
                String name;
                if (target != null) {
                    name = target.getName();
                    ip = target.getAddress().getAddress().getHostAddress();
                    target.kickPlayer(ChatColor.RED + "Banned: " + ChatColor.WHITE + reason);
                    SuperBans.messageByNoPerm(this.perm, ChatColor.RED + name + " banned: " + ChatColor.WHITE + reason);
                } else {
                    name = args[0];
                    ip = "0.0.0.0";
                }
                SuperBans.messageByPermExclusion(this.perm, sender.getName(), ChatColor.RED + "[" + sender.getName() + "] " + name + " banned: " + ChatColor.WHITE + reason);
                sender.sendMessage(ChatColor.RED + "Banned " + name);
                SuperBans.getManager().ban(name, reason, sender.getName(), ip, banType);
                SuperBans.log(sender.getName() + " banned " + name);
            } else {
                sender.sendMessage(ChatColor.RED + "Forgot a name");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "I'm afraid you're not qualified");
        }
        return true;
    }

}
