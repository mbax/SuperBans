package org.kitteh.superbans.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.kitteh.superbans.SuperBans;

public class UnbanCommand implements CommandExecutor {

    private final String perm = "superbans.unban";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(this.perm)) {
            if (args.length > 0) {
                SuperBans.getManager().unban(args[0], sender.getName());
                sender.sendMessage("Unbanning " + args[0]);
                SuperBans.messageByPermExclusion(this.perm, sender.getName(), ChatColor.RED + sender.getName() + " unbanned " + args[0]);
                SuperBans.log(sender.getName() + " unbanned " + args[0]);
            } else {
                sender.sendMessage(ChatColor.RED + "Forgot a name");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "I'm afraid you're not qualified");
        }
        return true;
    }
}
