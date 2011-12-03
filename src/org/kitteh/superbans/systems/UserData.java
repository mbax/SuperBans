package org.kitteh.superbans.systems;

import java.util.Date;
import java.util.HashMap;

public class UserData {
    private final Date creationTime;

    private final HashMap<String, String> extra;
    private final BanList banList;

    public UserData() {
        this.creationTime = new Date();
        this.banList = new BanList();
        this.extra = new HashMap<String, String>();
    }

    public HashMap<String, Object> cloneExtra() {
        synchronized (this.extra) {
            return new HashMap<String, Object>(this.extra);
        }
    }

    public BanList getBanList() {
        return this.banList;
    }

    public Object getExtra(String key) {
        synchronized (this.extra) {
            return this.extra.get(key);
        }
    }

    public boolean old() {
        return (this.creationTime.getTime() + 300000) < ((new Date()).getTime());
    }

    public void setExtra(String key, String value) {
        synchronized (this.extra) {
            this.extra.put(key, value);
        }
    }
}
