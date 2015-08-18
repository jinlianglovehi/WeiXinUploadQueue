package cn.ihealthbaby.weitaixin.library.data.bluetooth.parser;

import java.io.IOException;
import java.io.InputStream;

import cn.ihealthbaby.weitaixin.library.data.bluetooth.exception.ParseException;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.exception.ParseExpireVersionException;

/**
 *
 */
public class Decode {


    private ByteArrayBuffer buffer = new ByteArrayBuffer();
    private byte[] decodeTemp = new byte[512];

    /**
     * 柱塞流
     *
     * @param in
     * @throws IOException
     * @throws ParseException
     */
    public void decode(InputStream in) throws IOException, ParseException {
        int b = in.read();
        buffer.write(b);
        if (in.available() > 0) {
            read(in);
        }

        //解码
        int bufferSize = buffer.size();
        if (bufferSize >= Packet.DATA_HEAD_LEN) {
            byte[] buf = buffer.getBuf();
            //检查包头和读取协议版本
            // 0x55,  0xaa
            if ((buf[0] & 0xFF) == 0x55 && (buf[1] & 0xFF) == 0xaa) {
                switch (buf[2] & 0xFF) {
                    case 0x08:
                        throw new ParseExpireVersionException(Packet.VERSION_1, Packet.TYPE_AUDIO);
                    case 0x01:
                        throw new ParseExpireVersionException(Packet.VERSION_1, Packet.TYPE_HEART_RATE);
                    case 0x09:
                        if (bufferSize >= (Packet.AUDIO_V2_LEN + Packet.DATA_HEAD_LEN)) {
                            AudioPacket audioPacket = new AudioPacket(Packet.VERSION_2, Packet.AUDIO_V2_LEN);
                            buffer.read(audioPacket.getData(), Packet.DATA_HEAD_LEN, Packet.AUDIO_V2_LEN);
                            handler(audioPacket);
                        }
                        break;
                    case 0x03:
//                        version = Packet.VERSION_2;
//                        type = Packet.TYPE_HEART_RATE;

                        break;
                    default:
                        throw new ParseException("错误的类型 type byte2:" + buf[2]);
                }
            } else {
                throw new ParseException("错误的包头 byte0:" + buf[0] + ",byte1:" + buf[1]);
            }
        }
    }

    private void handler(AudioPacket audioPacket) {

    }

    private void readV2Audio() {

    }

    private void read(InputStream in) throws IOException {
        int len = in.available();
        byte[] temp = len <= decodeTemp.length ? decodeTemp : new byte[len];
        int readLen = in.read(temp, 0, len);
        if (readLen != len) {
            throw new IOException("读取错误,读取长度和可以读取长度不一致 readLen:" + readLen + ",len:" + len);
        }
        buffer.write(temp, 0, len);
    }
}
