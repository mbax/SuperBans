package org.kitteh.superbans.systems.mcbans;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kitteh.superbans.SuperBans;
import org.kitteh.superbans.systems.JSONBanSystemManager;
import org.kitteh.superbans.systems.UserData;
import org.kitteh.superbans.systems.mcbans.MCBansBanData.BanType;

public class MCBansManager extends JSONBanSystemManager {

    private class BanResponseProcessor extends ResponseProcessor {
        @Override
        public void processed(String ID, JSONObject object) {
            final HashMap<String, String> result = MCBansManager.this.JSONToHashMap(object);
            if (result == null) {
                SuperBans.log("[MCBans] Failure to connect");
            } else if (result.get("result").equalsIgnoreCase("y")) {
                SuperBans.log("[MCBans] Added: " + ID);
            } else if (result.get("result").equalsIgnoreCase("a")) {
                SuperBans.log("[MCBans] Player already on list: " + ID);
            } else if (result.get("result").equalsIgnoreCase("n")) {
                SuperBans.log("[MCBans] Could not add: " + ID);
            }
        }
    }

    private class UnbanResponseProcessor extends ResponseProcessor {
        @Override
        public void processed(String name, JSONObject object) {
            final HashMap<String, String> result = MCBansManager.this.JSONToHashMap(object);
            if (result == null) {
                SuperBans.log("[MCBans] Failure to connect");
            } else if ((result.get("result")).equalsIgnoreCase("y")) {
                SuperBans.log("[MCBans] Unbanned " + name);
            } else {
                SuperBans.log("[MCBans] Failed to unban " + name + ": " + result.get("msg"));
            }
        }
    }

    private static String[] processBanLookup(String listing) {
        final String[] result = new String[2];
        final String[] split = listing.split(" .:. ");
        if (split.length > 0) {
            result[0] = split[0];
        } else {
            result[0] = "";
        }
        if (split.length > 1) {
            result[1] = SuperBans.combineSplit(1, split);
        } else {
            result[1] = "";
        }
        return result;
    }

    private static UserData processLookup(JSONObject json) {
        final UserData data = new UserData();
        try {
            final JSONArray local = json.optJSONArray("local");
            if (local == null) {
                SuperBans.log("Unable to properly communicate with MCBans. Check your API Key");
                return data;
            }
            for (int i = 0; i < local.length(); i++) {
                final String[] ban = MCBansManager.processBanLookup(local.getString(i));
                data.getBanList().addBanData(new MCBansBanData(ban[1], MCBansBanData.BanType.LOCAL, ban[0]));
            }
            final JSONArray global = json.optJSONArray("global");
            for (int i = 0; i < global.length(); i++) {
                final String[] ban = MCBansManager.processBanLookup(global.getString(i));
                data.getBanList().addBanData(new MCBansBanData(ban[1], MCBansBanData.BanType.GLOBAL, ban[0]));
            }
        } catch (final JSONException e) {
            e.printStackTrace();
        }
        data.setExtra("mcbans_rep", String.valueOf(json.optDouble("reputation", 10.0)));
        return data;
    }

    private final String url;

    private final boolean initialLogin;

    private final boolean enable_bans, enable_unbans, enable_lookup, enable_loginban, enable_minrep;

    private double minRep;

    private final BanResponseProcessor banResponseProcessor = new BanResponseProcessor();

    private final UnbanResponseProcessor unbanResponseProcessor = new UnbanResponseProcessor();

    public MCBansManager(SuperBans plugin) {
        super(plugin, "mcbans");
        final File file = new File(plugin.getDataFolder(), "mcbans.yml");
        final FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "mcbans.yml"));
        config.options().copyDefaults(true);
        config.setDefaults(YamlConfiguration.loadConfiguration(plugin.getResource("mcbans.yml")));
        this.url = config.getString("DONOTTOUCH.UNLESSTOLDTO.URL").replace("%API%", config.getString("Settings.APIKEY"));

        this.minRep = config.getDouble("Settings.MinRep.Minimum");
        if (this.minRep == 0.0) {
            final int tmprep = config.getInt("Settings.MinRep.Minimum");
            if (tmprep > 0) {
                this.minRep = tmprep;
                config.set("Settings.MinRep.Minimum", this.minRep);
                SuperBans.log("Fixed your MCBans minimum reputation to be a double.");
            }
        }
        this.enable_bans = config.getBoolean("Features.Ban");
        this.enable_loginban = config.getBoolean("Features.CheckAtLogin");
        this.enable_lookup = config.getBoolean("Features.Lookup");
        this.enable_unbans = config.getBoolean("Features.Unban");
        this.enable_minrep = config.getBoolean("Features.MinRep");
        try {
            config.save(file);
        } catch (final IOException e) {
            SuperBans.Debug("Error: Could not save mcbans.yml", e);
        }
        this.initialLogin = config.getBoolean("ConsiderForInitialConnectionAttempt") || (this.minRep > 0);
    }

    @Override
    public UserData acquireLookup(String name) {
        if (!this.enable_lookup) {
            return new UserData();
        }
        final HashMap<String, String> POSTData = new HashMap<String, String>();
        POSTData.put("player", name);
        POSTData.put("admin", "SuperBans");
        POSTData.put("exec", "playerLookup");
        final JSONObject json = this.MCBansAPICall(POSTData);
        return MCBansManager.processLookup(json);
    }

    @Override
    public void ban(String name, String reason, String admin, String ip, BanType banType) {
        if (!this.enable_bans) {
            return;
        }
        final HashMap<String, String> POSTData = new HashMap<String, String>();
        POSTData.put("player", name);
        POSTData.put("admin", admin);
        POSTData.put("reason", reason);
        POSTData.put("playerip", ip);
        String stringType;
        switch (banType) {
            case GLOBAL:
                stringType = "globalBan";
                break;
            case TEMP:
                stringType = "tempBan";
                break;
            default:
                stringType = "localBan";
        }
        POSTData.put("exec", stringType);
        SuperBans.scheduleAsync(new asyncCall(this.url, POSTData, this.banResponseProcessor, name + ":" + banType));
    }

    @Override
    public void playerPreLogin(PlayerPreLoginEvent event) {
        final HashMap<String, String> POSTData = new HashMap<String, String>();
        POSTData.put("player", event.getName().toLowerCase());
        POSTData.put("playerip", event.getAddress().getHostAddress());
        POSTData.put("exec", "playerConnect");
        final JSONObject object = JSONBanSystemManager.APICall(this.url, POSTData);
        if (this.initialLogin) {
            try {
                final String banStatus = object.getString("banStatus");
                if (!banStatus.equalsIgnoreCase("n")) {
                    final String reason = object.getString("banReason");
                    if (!reason.equals("") && this.enable_loginban) {
                        event.disallow(Result.KICK_BANNED, "Banned: " + reason);
                        SuperBans.log("Disconnected " + event.getName() + " for MCBans ban: " + reason);
                        return;
                    }
                }
                final double rep = object.getDouble("playerRep");
                System.out.println("Rep:" + rep + " MIN:" + this.minRep);
                if ((rep < this.minRep) && this.enable_minrep) {
                    event.disallow(Result.KICK_OTHER, "Your MCBans reputation is too low!");
                    SuperBans.log("Disconnected " + event.getName() + " for low reputation: " + rep);
                    return;
                }
            } catch (final JSONException e) {
                SuperBans.Debug("Failure in MCBans player connect", e);
            }
        }
    }

    @Override
    public void unban(String name, String admin) {
        if (!this.enable_unbans) {
            return;
        }
        final HashMap<String, String> POSTData = new HashMap<String, String>();
        POSTData.put("player", name);
        POSTData.put("exec", "unBan");
        POSTData.put("admin", admin);
        SuperBans.scheduleAsync(new asyncCall(this.url, POSTData, this.unbanResponseProcessor, name));
    }

    private JSONObject MCBansAPICall(HashMap<String, String> POSTData) {
        return JSONBanSystemManager.APICall(this.url, POSTData);
    }
}
