package gfs.data;

import java.io.Serializable;

public class WriteMsg implements Serializable {

    private static final long serialVersionUID = 8605716990677642964L;

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
