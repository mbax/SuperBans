package org.kitteh.superbans.systems.mcbans;

import org.bukkit.ChatColor;
import org.kitteh.superbans.systems.BanData;

public class MCBansBanData implements BanData {
    public enum BanType {
        GLOBAL {
            @Override
            public String toString() {
                return ChatColor.RED + "G" + ChatColor.AQUA;
            }
        },
        LOCAL {
            @Override
            public String toString() {
                return ChatColor.GREEN + "L" + ChatColor.AQUA;
            }
        },
        TEMP {
            @Override
            public String toString() {
                return ChatColor.AQUA + "T" + ChatColor.AQUA;
            }
        };
    }

    private final String reason;
    private final String server;
    private final BanType type;

    public MCBansBanData(String reason, BanType type, String server) {
        this.reason = reason;
        this.type = type;
        this.server = server;
    }

    @Override
    public String toString() {
        return ChatColor.AQUA + "{mcbans}[" + this.type + "]" + this.server + ": " + this.reason;
    }
}
