package org.kitteh.superbans.systems.mcbouncer;

import org.bukkit.ChatColor;
import org.kitteh.superbans.systems.BanData;

public class MCBouncerBanData implements BanData{
    private final String reason;
    private final String admin;
    private final String server;

    public MCBouncerBanData(String reason, String admin, String server) {
        this.reason = reason;
        this.admin = admin;
        this.server = server;
    }

    @Override
    public String toString() {
        return ChatColor.AQUA + "{mcbouncer}" + this.server + ": " + this.reason + "("+this.admin+")";
    }
}
