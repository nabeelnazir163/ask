package com.zillion.android.askaalim.models;

import java.io.Serializable;

public class inbox_model implements Serializable {

    private long sending_timeStamp;
    private boolean seen;

    public inbox_model( long timestamp, boolean seen) {
        this.sending_timeStamp = timestamp;
        this.seen = seen;
    }

    public inbox_model() {
    }

    public long getSending_timeStamp() {
        return sending_timeStamp;
    }

    public void setSending_timeStamp(long sending_timeStamp) {
        this.sending_timeStamp = sending_timeStamp;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
