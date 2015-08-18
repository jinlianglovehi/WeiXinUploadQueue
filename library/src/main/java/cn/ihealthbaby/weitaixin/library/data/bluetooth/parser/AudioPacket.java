package cn.ihealthbaby.weitaixin.library.data.bluetooth.parser;

/**
 * 音频包
 */
public class AudioPacket extends Packet{
    private final byte[] data;
    public AudioPacket(int version,int len) {
        super(version, TYPE_AUDIO);
        data = new byte[len];
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public boolean check(){
        return true;
    }
}
