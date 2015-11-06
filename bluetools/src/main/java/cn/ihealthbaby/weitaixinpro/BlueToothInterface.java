package cn.ihealthbaby.weitaixinpro;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

/**
 * Created by jinliang on 15/11/6.
 */
public interface BlueToothInterface {
    /**
     * 传输数据处理连接与断开链接
     */
    public void connect(BluetoothDevice device, boolean isSafe);
    public void reConnect(int retryTime) ;
    public void disConnect();

    /**
     * 录制处理
     */
    public void startRecord(Context context, String recordId);
    public void stopRecord();

    /**
     * 声音处理
     */
    public void startVoice(Context context, String recordId) ;
    public void stopVoice() ;
    public void adjectVoiceSize(int rediect, int size);


}
