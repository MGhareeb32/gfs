package gfs.data;

public class WriteMsg {
    public final long txnId;
    public final long timeStamp;
    public final Host loc;
    public final long seqNum;

    public WriteMsg(long transactionId, long timeStamp,
                    Host loc, long seqNum) {

        this.txnId = transactionId;
        this.timeStamp = timeStamp;
        this.loc = loc;
        this.seqNum = seqNum;
    }
}
