package cn.ihealthbaby.weitaixinpro;

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
import cn.ihealthbaby.weitaixin.library.data.bluetooth.parser.Parser;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.Constants;


/**
 * Created by jinliang on 15/11/5.
 */
@SuppressLint("LongLogTag")
public class TaiXinYiBluetoothService extends Service {
    private static final String TAG = "TaiXinYiBluetoothService";
    // 蓝牙连接的三个状态
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_CONNECTING = 1; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 2;  // now connected to a remote device
    private BluetoothAdapter mAdapter;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private BluetoothDevice bindDevice;

    private boolean secure=false;
    private int mState;
    private Parser parser;
    private int RETRY_TIMES = 3;


    /**
     * 服务的消息类型的总结
     */

    /**
     * 设备连接与否 通过蓝牙的链接状态
     */
    public static boolean isBluetoothOpen = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        Log.i(TAG, "--TaiXinYiBluetoothService  oncreate Method--");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "--onDestoryMethod()----");
    }

    /**
     * 初始化的变量
     *
     * @param
     * @return
     */

    public void init(Context context) {
        // mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
       /// parser = new Parser(context);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    private MyBinder myBinder = new MyBinder();

    public class MyBinder extends Binder {

        public TaiXinYiBluetoothService getBluetoothService() {
            return TaiXinYiBluetoothService.this;
        }
    }


    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        return mState;
    }

    /**
     * Set the current state of the chat connection
     *
     * @param state An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        mState = state;
        // Give the new state to the Handler so the UI Activity can update
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device, boolean issecure) {
        // Cancel any thread attempting to make a connection
        if (mState != STATE_NONE) {
            return;
        }
        bindDevice = device;
        secure = issecure;
        mConnectThread = new ConnectThread(device, issecure);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }


    //=========================下面是原有的方法 需要改进================
    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
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
        setState(STATE_CONNECTED);
    }

    /**
     * Stop all threads
     */
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
        setState(STATE_NONE);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Unable to connect device. Indicate that the connection attempt failed and notify the UI
     * Activity.
     */
    private void connectionFailed() {
        // Send a failure message back to the Activity
        //mHandler.obtainMessage(Constants.MESSAGE_STATE_FAIL, Constants.MESSAGE_CANNOT_CONNECT, -1).sendToTarget();
        //EventBus.getDefault().post(new BlueToothServiceEvent(ConstantUtils.connectionFailed));
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
       // EventBus.getDefault().post(new BlueToothServiceEvent(ConstantUtils.connectionLost));
    }

    /**
     * This thread runs while attempting to make an outgoing connection with a device. It runs
     * straight through; the connection either succeeds or fails.
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
            synchronized (TaiXinYiBluetoothService.this) {
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
     * This thread runs during a connection with a remote device. It handles all incoming and
     * outgoing transmissions.
     */
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
}
