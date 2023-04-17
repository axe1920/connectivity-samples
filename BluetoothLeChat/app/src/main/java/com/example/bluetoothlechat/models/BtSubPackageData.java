package com.example.bluetoothlechat.models;

import java.util.concurrent.locks.ReentrantLock;

public class BtSubPackageData {
    private short sequence;
    private int pkgSN;
    private int dataLength;

    private byte control;

    ReentrantLock sentLock;
    ReentrantLock confirmLock;
    private boolean sent;
    private byte[] data;
    private int validFlag;
    private short subPkgCount;

    private byte messageType;

    private int retry;

    private long sendTimestamp;

    private boolean confirmed;
    public BtSubPackageData(int validFlag, int pkgSN, short sequence) {
        this(sequence,(byte) 0, null, pkgSN, (short)0, (byte) 0);
        this.validFlag = validFlag;

    }
    public BtSubPackageData(short sequence,byte control, byte[] data, int pkgSN, short subPkgCount, byte messageType) {
        this.sequence = sequence;
        //this.dataLength = dataLength;
        this.control = control;
        this.data = data;
        this.pkgSN = pkgSN;
        this.subPkgCount = subPkgCount;
        this.messageType = messageType;
        this.sentLock = new ReentrantLock();
        this.confirmLock = new ReentrantLock();
    }
    public BtSubPackageData(short sequence, byte control, byte[] data, int pkgSN, short subPkgCount) {
        this(sequence, control, data, pkgSN, subPkgCount, (byte) 0);

    }

    public short getSequence() {
        return sequence;
    }

    public int getDataLength() {
        return dataLength;
    }

    public byte getControl() {
        return control;
    }


    public byte[] getData() {
        return data;
    }


    public int getValidFlag() {
        return validFlag;
    }

    public int getPkgSN() {
        return pkgSN;
    }

    public short getSubPkgCount() {
        return subPkgCount;
    }

    public boolean isSent() {
        sentLock.lock();
        try {
            return this.sent;
        } finally {
            sentLock.unlock();
        }
    }

    public void setSent(boolean sent) {
        sentLock.lock();
        try {
            this.sent = sent;
        } finally {
            sentLock.unlock();
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public long getSendTimestamp() {
        return sendTimestamp;
    }

    public void setSendTimestamp(long sendTimestamp) {
        this.sendTimestamp = sendTimestamp;
    }

    public static int verify(byte[] data){
        if (data[0] != 0x5a) return 1;
        if (data.length < 10) return  2;

        int bcc = calcCheckSum(data, 0, data.length - 2);
        short checksum = data[data.length - 1];
        checksum = (short) (checksum << 8);
        checksum |= data[data.length - 2];
        return checksum == bcc? 0: 3;
    }
    public static int calcCheckSum(byte[]data, int offset, int len){
        int bcc = 0;
        for (int i = offset ;i < offset + len; i++){
            bcc ^= data[i];
        }
        return bcc;
    }
    public byte getMessageType() {
        return messageType;
    }

    public static BtSubPackageData parseData(byte[] data){
        int result = verify(data);
        BtSubPackageData ret = null;
        switch (result){
            case 0: {
                int mask = (1 << 8) - 1;
                int len = (1 << 8) - 1;
                len &= data[1];

                len |= (mask & data[2]) << 8;
                byte control = data[3];
                short subPkgCount = data[4];
                int pkgSN = (1 << 8) - 1;
                pkgSN &= data[5];

                pkgSN |= (mask & data[6]) << 8;
                short sequence = data[7];

                byte[] pd = new byte[len - 6];

                System.arraycopy(data, 9, pd, 0, len - 6);
                ret = new BtSubPackageData(sequence,  control, pd, pkgSN, subPkgCount, data[8]);

            }
            break;
            case 1:
            case 2:
                ret = new BtSubPackageData(result, 0, (short) 0);
                break;
            case 3: {
                int pkgSN = data[7];
                pkgSN = pkgSN << 8;
                pkgSN += data[6];
                short sequence = data[8];
                ret = new BtSubPackageData(result, pkgSN, sequence);
            }
        }
        return ret;
    }

    public byte[] constructSendingData(){
        int dataLen = 11 + (data == null? 0:data.length);
        byte[] ret = new byte[dataLen];
        ret[0] = 0x5a;
        ret[1] = (byte) (dataLen - 5);
        ret[2] = (byte) ((dataLen - 5) >> 8);
        ret[3] = control;
        ret[4] = (byte) subPkgCount;
        ret[5] = (byte)pkgSN;
        ret[6] = (byte)(pkgSN >> 8);
        ret[7] = (byte) sequence;
        ret[8] = messageType;
        if (data != null) {
            System.arraycopy(data, 0, ret, 9, data.length);
        }
        int checksum = calcCheckSum(ret, 0, dataLen - 2);
        ret[dataLen - 2] = (byte) checksum;
        ret[dataLen - 1] = (byte) (checksum >> 8);
        return ret;
    }
    private void saveInt(byte[] pd, int offset, int len, int data){

    }
    private void saveShort(byte[] pd, int offset, int len,  short data){

    }
}
