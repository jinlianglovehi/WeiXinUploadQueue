package cn.ihealthbaby.weitaixinpro.blue;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.ihealthbaby.weitaixin.library.data.bluetooth.exception.ParseException;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import de.greenrobot.event.EventBus;

/**
 * Created by jinliang on 15/11/7.
 */
public class BlueService extends Service implements BlueToothInterface {
    private static final String TAG = BlueService.class.getSimpleName();



    MyParser parser;
    InputStream inputStream = null;

    /**
     * 蓝牙相关的属性
     */
    private BluetoothAdapter mAdapter;
    private BluetoothDevice bindDevice;
    private boolean secure;
    private int RETRY_TIMES = 3;


    /**
     * 线程
     */
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    public void init(Context context){
        parser = new MyParser(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new MyBind();
    }

    public class MyBind extends Binder {
        public  BlueService getService() {
            return BlueService.this;
        }


    }

    @Override
    public synchronized void connect(BluetoothDevice device, boolean issecure) {
        bindDevice = device;
        secure = issecure;
        mConnectThread = new ConnectThread(device, issecure);
        mConnectThread.start();
    }

    @Override
    public void reConnect(int retryTime) {

        RETRY_TIMES = retryTime;
        connect(bindDevice, secure);
    }

    @Override
    public void disConnect() {
        stop();
    }

    @Override
    public void startRecord() {
        parser.setStartRecord(true);
    }

    @Override
    public void stopRecord() {
        parser.setStartRecord(false);
    }

    @Override
    public void startPlayVoice() {
        parser.playVoice(true);
    }


    @Override
    public void stopPlayVoice() {
        parser.playVoice(false);
    }

    @Override
    public void adjectVoiceSize(int rediect) {
        parser.adjectVoiceSize(rediect);
    }

    @Override
    public void saveVoiceFile(Context context, String recordId) {
        parser.saveVoiceFile(context, recordId);
    }

    @Override
    public void unSaveVoiceFile() {
        parser.stopSaveVoiceFile();
    }

    /**
     * 线程
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            setName("ConnectThread");
            Log.d(TAG, "create ConnectedThread, secureType:" + secure);
            mmDevice = device;
            BluetoothSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";
            //用给定的设备获取BluetoothSocket,分两种类型
            try {
                if (secure) {
                    tmp = device.createRfcommSocketToServiceRecord(Constants.COMMON_UUID);
                } else {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(Constants.COMMON_UUID);
                    LogUtil.d(TAG, "" + "COMMON_UUID：" + Constants.COMMON_UUID);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + " create() failed", e);
            }
            mmSocket = tmp;
            //

        }

        @SuppressLint("LongLogTag")
        public void run() {
            Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();
            // Make a connection to the BluetoothSocket
            label1:
            for (int i = 0; i < RETRY_TIMES; i++) {
                try {
                    // 连接的方法,阻塞或者异常
                    mmSocket.connect();
                    break label1;
                } catch (IOException e) {
                    LogUtil.e(TAG, "" + "connectionFailed：" + e.toString());
                    if (i == RETRY_TIMES - 1) {
                        try {
                            // Close the socket
                            mmSocket.close();
                        } catch (IOException e1) {
                            Log.e(TAG, "unable to close() " + mSocketType + " socket during connection failure", e1);
                        }
                        connectionFailed();
                        return;
                    }
                }


            }
            // Reset the ConnectThread because we're done
            synchronized (BlueService.this) {
                mConnectThread = null;
            }
            // Start the connected thread
            connected(mmSocket, mmDevice, mSocketType);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 连接失败的方法调用
     */
    private void connectionFailed() {

        EventBus.getDefault().post(new BlueServiceEvent(BlueServiceEvent.connectFail))
        ;
    }

    //设备连接失败
    private void connectionLost() {
        EventBus.getDefault().post(new BlueServiceEvent(BlueServiceEvent.connectLost));
    }

    private synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final String socketType) {
        Log.d(TAG, "connected, Socket Type:" + socketType);
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket, socketType);
        mConnectedThread.start();
    }

    //数据传输的线程
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private volatile boolean start = true;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            setName("ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {

            while (start) {

                Log.i(TAG, "连接中-----" + mmSocket.isConnected());
                // 没有断开重连的机制。
                try {
                    parser.parsePackageData(mmInStream);
                    Log.i(TAG, "mmInStream.装配数据:" + mmInStream.available());
                } catch (IOException e) {
                    Log.e(TAG, "disconnected");
                    start = false;
                    Log.i(TAG, "连接终止-----" + mmSocket.isConnected());
                    connectionLost();
                    break;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public synchronized void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                // Share the sent message back to the UI Activity
                //  mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer)
                //         .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                //更改run内循环的标记
                start = false;
                //中断
                this.interrupt();
                //等待
                this.join();
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void un_command_cancel() {
            start = false;

        }
    }

    public synchronized void stop() {

        Log.d(TAG, "stop");
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }
}
