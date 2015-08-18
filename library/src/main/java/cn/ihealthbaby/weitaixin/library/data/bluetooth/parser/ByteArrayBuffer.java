package cn.ihealthbaby.weitaixin.library.data.bluetooth.parser;

import java.io.ByteArrayOutputStream;

import cn.ihealthbaby.weitaixin.library.data.bluetooth.exception.ParseException;

/**
 * 字节缓冲
 */
public class ByteArrayBuffer extends ByteArrayOutputStream {
    public byte[] getBuf() {
        return buf;
    }

    public void read(byte[] data, int starIndex, int len) throws ParseException {
        if (data.length < len) {
            throw new ParseException(String.format("目标 data 长度小于[%d]", len));
        }
        int packetLen = starIndex + len;
        if (packetLen > count) {
            throw new ParseException(String.format("需要长度[%d]大于缓冲器长度[%d]", packetLen, count));
        }

        //复制
        System.arraycopy(buf, starIndex, data, 0, len);
        //向前移动
        int lastLen = count - packetLen;
        if (lastLen > 0) {
            System.arraycopy(buf, packetLen, buf, 0, lastLen);
        }

        count = lastLen;
    }
}
