package gfs.data;

public class ReadMsg {
    public final long txnId;
    public final long timeStamp;
    public final Host[] locs;

    public ReadMsg(long transactionId, long timeStamp, Host[] locs) {
        this.txnId = transactionId;
        this.timeStamp = timeStamp;
        this.locs = locs;
    }
}
