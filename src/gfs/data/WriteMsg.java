package gfs.data;

public class WriteMsg {
    public final long txnId;
    public final long timeStamp;
    public final Host loc;

    public WriteMsg(long transactionId, long timeStamp, Host loc) {
        this.txnId = transactionId;
        this.timeStamp = timeStamp;
        this.loc = loc;
    }
}
