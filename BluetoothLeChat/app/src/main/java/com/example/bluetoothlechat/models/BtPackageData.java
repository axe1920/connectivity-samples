package com.example.bluetoothlechat.models;

public class BtPackageData {
    private int serialNo;
    private int subPkgCount;
    private boolean lastReceived;

    private  byte msgType;
    private BtSubPackageData[] subPkgs;
    private int maxSeq;

    public BtPackageData(int serialNo, int count) {
        this.serialNo = serialNo;
        this.subPkgs = new BtSubPackageData[count];
    }

    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    public int getSubPkgCount() {
        return subPkgCount;
    }

    public void setSubPkgCount(int subPkgCount) {
        this.subPkgCount = subPkgCount;
    }

    public boolean isLastReceived() {
        return lastReceived;
    }

    public void setLastReceived(boolean lastReceived) {
        this.lastReceived = lastReceived;
    }

    public byte getMsgType() {
        return msgType;
    }

    public void setMsgType(byte msgType) {
        this.msgType = msgType;
    }

    public byte[] assembleSubPackages(){
        byte[] ret;
        int len = 0;
        for (int i = 0; i < subPkgs.length; i++){
            len += subPkgs[i].getData().length - 1;
        }
        ret = new byte[len + 1];
        if (len > 0) {
            ret[0] = subPkgs[0].getData()[0];
        }
        int pos = 1;
        for (int i = 0; i < subPkgs.length; i++){
            byte[] subData = subPkgs[i].getData();
            System.arraycopy(subData, 1, ret, pos, subData.length -1);
            pos += subData.length -1;
        }
        return ret;
    }

    public void addSubPackage(BtSubPackageData subPackageData){
        subPkgs[subPackageData.getSequence() - 1] = subPackageData;
    }

    public synchronized void confirmPkgSent(int sequence){
        subPkgs[sequence - 1].setConfirmed(true);
    }

    public BtSubPackageData[] getSubPkgs() {
        return subPkgs;
    }
}
