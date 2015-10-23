/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.ihealthbaby.weitaixin.library.data.bluetooth.exception.ParseException;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.parser.Parser;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.Constants;

/**
 * 两个线程,用来连接蓝牙设备
 */
public class PseudoBluetoothService {
	// 蓝牙连接的三个状态
	public static final int STATE_NONE = 0;       // we're doing nothing
	public static final int STATE_CONNECTING = 1; // now initiating an outgoing connection
	public static final int STATE_CONNECTED = 2;  // now connected to a remote device
	private static final String TAG = "PseudoBluetoothService";
	private static PseudoBluetoothService instance;
	private final BluetoothAdapter mAdapter;
	private final Handler mHandler;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private int mState;
	private Parser parser;
	private int RETRY_TIMES = 3;

	/**
	 * Constructor. Prepares a new BluetoothChat session.
	 *
	 * @param context The UI Activity Context
	 * @param handler A Handler to send messages back to the UI Activity
	 */
	private PseudoBluetoothService(Context context, Handler handler) {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = STATE_NONE;
		mHandler = handler;
		parser = new Parser(context, mHandler);
	}

	public static PseudoBluetoothService getInstance(Context context, Handler handler) {
		if (instance == null) {
			instance = new PseudoBluetoothService(context, handler);
		}
		return instance;
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
		Log.d(TAG, "setState() " + mState + " -> " + state);
		mState = state;
		// Give the new state to the Handler so the UI Activity can update
		mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
	}

	/**
	 * Start the ConnectThread to initiate a connection to a remote device.
	 *
	 * @param device The BluetoothDevice to connect
	 * @param secure Socket Security type - Secure (true) , Insecure (false)
	 */
	public synchronized void connect(BluetoothDevice device, boolean secure) {
		Log.d(TAG, "connect to: " + device);
		// Cancel any thread attempting to make a connection
		if (mState != STATE_NONE) {
			return;
		}
//			if (mConnectThread != null) {
//				mConnectThread.cancel();
//				mConnectThread = null;
//			}
		// Cancel any thread currently running a connection
//		if (mConnectedThread != null) {
//			mConnectedThread.cancel();
//			mConnectedThread = null;
//		}
		// Start the thread to connect with the given device
		mConnectThread = new ConnectThread(device, secure);
		mConnectThread.start();
		setState(STATE_CONNECTING);
	}

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
		mHandler.obtainMessage(Constants.MESSAGE_STATE_FAIL, Constants.MESSAGE_CANNOT_CONNECT, -1).sendToTarget();
	}

	/**
	 * Indicate that the connection was lost and notify the UI Activity.
	 */
	private void connectionLost() {
		// Send a failure message back to the Activity
		mHandler.obtainMessage(Constants.MESSAGE_STATE_FAIL, Constants.MESSAGE_CONNECTION_LOST, -1).sendToTarget();
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
		}

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
			synchronized (PseudoBluetoothService.this) {
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
			Log.d(TAG, "create ConnectedThread: " + socketType);
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "temp sockets not created", e);
			} catch (Exception e) {
				e.printStackTrace();
			}
			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectedThread");
			while (start) {
				try {
					if (mmInStream != null && mmInStream.available() < 324) {
						continue;
					}
					parser.parsePackageData(mmInStream);
				} catch (IOException e) {
					Log.e(TAG, "disconnected", e);
					connectionLost();
					break;
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * Write to the connected OutStream.
		 *
		 * @param buffer The bytes to write
		 */
		public synchronized void write(byte[] buffer) {
			try {
				mmOutStream.write(buffer);
				// Share the sent message back to the UI Activity
				mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer)
						.sendToTarget();
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
				Log.e(TAG, "close() of connect socket failed", e);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
