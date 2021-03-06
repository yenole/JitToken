package com.yenole.blockchain.wallet.transaction.eos;

import java.util.Collection;


/**
 * Created by swapnibble on 2017-09-12.
 */

public class EOSByteWriter {
    private byte[] _buf;
    private int _index;

    public EOSByteWriter(int capacity) {
        _buf = new byte[capacity];
        _index = 0;
    }

    public EOSByteWriter(byte[] buf) {
        _buf = buf;
        _index = buf.length;
    }

    private void ensureCapacity(int capacity) {
        if (_buf.length - _index < capacity) {
            byte[] temp = new byte[_buf.length * 2 + capacity];
            System.arraycopy(_buf, 0, temp, 0, _index);
            _buf = temp;
        }
    }

    public void put(byte b) {
        ensureCapacity(1);
        _buf[_index++] = b;
    }

    public void putShortLE(short value) {
        ensureCapacity(2);
        _buf[_index++] = (byte) (0xFF & (value));
        _buf[_index++] = (byte) (0xFF & (value >> 8));
    }

    public void putIntLE(int value) {
        ensureCapacity(4);
        _buf[_index++] = (byte) (0xFF & (value));
        _buf[_index++] = (byte) (0xFF & (value >> 8));
        _buf[_index++] = (byte) (0xFF & (value >> 16));
        _buf[_index++] = (byte) (0xFF & (value >> 24));
    }


    public void putLongLE(long value) {
        ensureCapacity(8);
        _buf[_index++] = (byte) (0xFFL & (value));
        _buf[_index++] = (byte) (0xFFL & (value >> 8));
        _buf[_index++] = (byte) (0xFFL & (value >> 16));
        _buf[_index++] = (byte) (0xFFL & (value >> 24));
        _buf[_index++] = (byte) (0xFFL & (value >> 32));
        _buf[_index++] = (byte) (0xFFL & (value >> 40));
        _buf[_index++] = (byte) (0xFFL & (value >> 48));
        _buf[_index++] = (byte) (0xFFL & (value >> 56));
    }


    public void putBytes(byte[] value) {
        ensureCapacity(value.length);
        System.arraycopy(value, 0, _buf, _index, value.length);
        _index += value.length;
    }

    public void putBytes(byte[] value, int offset, int length) {
        ensureCapacity(length);
        System.arraycopy(value, offset, _buf, _index, length);
        _index += length;
    }


    public byte[] toBytes() {
        byte[] bytes = new byte[_index];
        System.arraycopy(_buf, 0, bytes, 0, _index);
        return bytes;
    }

    public int length() {
        return _index;
    }


    public void putString(String value) {
        if (null == value) {
            putVariableUInt(0);
            return;
        }

        // array count 는 variable int 로 넣어야 한다.
        putVariableUInt(value.length());
        putBytes(value.getBytes());
    }

//    public void putCollection(Collection<? extends EosType.Packer> collection) {
//        if (null == collection) {
//            putVariableUInt(0);
//            return;
//        }
//
//        // element count 는 variable int 로 넣어야 한다.
//        putVariableUInt(collection.size());
//
//        for (EosType.Packer type : collection) {
//            type.pack(this);
//        }
//    }

    public void putVariableUInt(long val) {

        do {
            byte b = (byte) ((val) & 0x7f);
            val >>= 7;
            b |= (((val > 0) ? 1 : 0) << 7);
            put(b);
        } while (val != 0);
    }

}
