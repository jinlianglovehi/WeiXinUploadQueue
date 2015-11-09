package cn.ihealthbaby.weitaixinpro.blue;

/**
 * Created by jinliang on 15/11/7.
 * 蓝牙服务的实体消息类型
 */
public class BlueServiceEvent {
    private int  blueServiceType ;// 发送消息的类型

   // 失去连接
    public static final int connectLost =0x01 ;
    //连接设备失败
    public static final int connectFail =0x02;

    /**
     * 事件的类型
     */
    //开始播放声音
    // public static final int stopPlayVoice = 0x01;
    //直接用方法调用
    //public static final int adjustVoiceVolumn = 0x02;
    //停止播放声音
    //public static final int startPlayVoice = 0x03;

    //开始语音写入语音文件

    //开始保存语音的文件
//    public static final int  startSaveVoiceFile =0x08;
//    //不保存语音文件
//    public static final int   unSaveVoiceFile =0x09;


    private  String recordId ;//记录的id

    private int  voiceVolume;//传输的是蓝牙的声音的大小


    public BlueServiceEvent(int blueServiceType) {
        this.blueServiceType = blueServiceType;
    }

    public BlueServiceEvent(int blueServiceType, String recordId) {
        this.blueServiceType = blueServiceType;
        this.recordId = recordId;
    }

    public int getBlueServiceType() {
        return blueServiceType;
    }

    public void setBlueServiceType(int blueServiceType) {
        this.blueServiceType = blueServiceType;
    }

    public int getVoiceVolume() {
        return voiceVolume;
    }

    public void setVoiceVolume(int voiceVolume) {
        this.voiceVolume = voiceVolume;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }
}
