package gfs.data;

public class MsgNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;
    private int[] msgNum = null;

    public int[] getMsgNum() {
        return msgNum;
    }

    public void setMsgNum(int[] msgNum) {
        this.msgNum = msgNum;
    }

}
