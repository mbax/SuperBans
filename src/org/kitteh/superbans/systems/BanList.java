package org.kitteh.superbans.systems;

import java.util.ArrayList;

public class BanList {
    private final ArrayList<BanData> bans;

    public BanList() {
        this.bans = new ArrayList<BanData>();
    }

    public void addBanData(BanData data) {
        synchronized (this.bans) {
            this.bans.add(data);
        }
    }

    public ArrayList<String> getList() {
        ArrayList<BanData> banList;
        synchronized (this.bans) {
            banList = new ArrayList<BanData>(this.bans);
        }
        final ArrayList<String> list = new ArrayList<String>();
        for (final BanData ban : banList) {
            final String banString = ban.toString();
            if (banString != null) {
                list.add(ban.toString());
            }
        }
        return list;
    }
}