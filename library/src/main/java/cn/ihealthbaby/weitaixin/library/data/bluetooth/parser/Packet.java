package cn.ihealthbaby.weitaixin.library.data.bluetooth.parser;

/**
 * 小小对应的数据包
 */
public class Packet {
    public static final int DATA_HEAD_LEN = 3;

    public static final int VERSION_1 = 1;
    public static final int VERSION_2 = 2;

    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_HEART_RATE = 2;
    public static final int AUDIO_V2_LEN = 101;

    private int version;
    private int type;

    public Packet(int version, int type) {
        this.version = version;
        this.type = type;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean check(){
        return true;
    }
}
