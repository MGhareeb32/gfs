package gfs.data;

import java.io.Serializable;

public class ReadMsg implements Serializable {

    private static final long serialVersionUID = -4163199930519524109L;

    public final long txnId;
    public final long timeStamp;
    public final Host[] locs;

    public ReadMsg(long transactionId, long timeStamp, Host[] locs) {
        this.txnId = transactionId;
        this.timeStamp = timeStamp;
        this.locs = locs;
    }
}
