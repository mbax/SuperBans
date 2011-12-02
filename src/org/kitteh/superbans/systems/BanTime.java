package org.kitteh.superbans.systems;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BanTime {
    private final Date date;
    private final String string;

    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

    public BanTime(Date date) {
        this.date = date;
        this.string = this.dateformat.format(this.date);
    }

    public Date getDate() {
        return this.date;
    }

    @Override
    public String toString() {
        return this.string;
    }
}
