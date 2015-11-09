package cn.ihealthbaby.weitaixinpro;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.data.FHRPackage;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp.AbstractBluetoothListener;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp.BluetoothReceiver;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp.BluetoothScanner;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.FixedRateCountDownTimer;
import cn.ihealthbaby.weitaixinpro.blue.BlueService;
import cn.ihealthbaby.weitaixinpro.blue.BlueServiceEvent;
import de.greenrobot.event.EventBus;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    @Bind(R.id.connectBlue)
    Button connectBlue;//连接按钮
    @Bind(R.id.showBlueNum)
    TextView showBlueNum;
    @Bind(R.id.disConnectBlue)
    Button disConnectBlue;// 断开链接
    /**
     * 语音播放处理
     */
    @Bind(R.id.startVoice)
    Button startVoicePlay;
    @Bind(R.id.stopVoice)
    Button stopVoicePlay;
    @Bind(R.id.saveVoiceFile)
    /**
     * 语音文件的保存
     */
            Button saveVoiceFile;
    @Bind(R.id.unSaveVoiceFile)
    Button unSaveVoiceFile;

    /**
     * 音量的调节
     */
    @Bind(R.id.addVoice)
    Button addVoice;
    @Bind(R.id.reduceVoice)
    Button reduceVoice;
    /**
     * 开始记录心跳
     */
    @Bind(R.id.startHeartRate)
    Button startHeartRate;
    @Bind(R.id.stopHeartRate)
    Button stopHeartRate;
    @Bind(R.id.reconnect)
    Button reconnect;

    private BluetoothDevice devide;
    private BlueService taiXinYiService;
    /**
     * 处理连接状态以及连接失败
     */

    private BluetoothAdapter adapter;
    private BluetoothScanner bluetoothScanner;
    private Set<BluetoothDevice> bondedDevices;
    private FixedRateCountDownTimer readDataTimer;
    private BluetoothDevice connectDevice;
    private BluetoothReceiver bluetoothReceiver;

    /**
     * 使用静态的内部类，不会持有当前对象的引用
     */
    private class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.arg1) {
                    case Constants.MESSAGE_CONNECTION_LOST:
                        Log.i(TAG, "socket连接断开");
                        break;
                    case 1:
                        break;
                }
                super.handleMessage(msg);
            }
        }
    }

    private MyHandler handler = new MyHandler(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_data);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        Intent intent = new Intent(MainActivity.this, BlueService.class);
        bindService(intent, bluetoothConnection, BIND_AUTO_CREATE);
        initTimeTask();
        adapter = BluetoothAdapter.getDefaultAdapter();
        Log.i("", "");
        //蓝牙的监听
        initRecevier();
    }

    private void initRecevier() {

        bluetoothReceiver = new BluetoothReceiver();
        bluetoothReceiver.register(getApplicationContext());
        bluetoothReceiver.setListener(new AbstractBluetoothListener() {


            @Override
            public void onFound(BluetoothDevice remoteDevice, String remoteName, short rssi, BluetoothClass bluetoothClass) {

            }

            @Override
            public void onConnect(BluetoothDevice remoteDevice) {

            }

            @Override
            public void onDisconnect(BluetoothDevice remoteDevice) {

            }

            @Override
            public void onStateOn() {

            }

            @Override
            public void onStateOFF() {

            }

            @Override
            public void onRequestBluetoothEnable() {

            }

            @Override
            public void onDiscoveryStarted() {

            }

            @Override
            public void onDiscoveryFinished() {

            }

            @Override
            public void onPairingRequest(BluetoothDevice remoteDevice, String remoteName, String pairingKey, int pairingVariant) {

            }

            @Override
            public void onActionAclDisConnected() {// 断开链接
                Log.i(TAG, "蓝牙设备断开，蓝牙设备关闭");
            }
        });
    }


    private void initTimeTask() {
        readDataTimer = new FixedRateCountDownTimer(100000, 500) {
            @Override
            protected void onExtra(long duration, long extraTime, long stopTime) {
            }

            @Override
            public void onStart(long startTime) {
            }

            @Override
            public void onRestart() {
            }

            @Override
            public void onTick(long millisUntilFinished, FHRPackage fhrPackage) {
                Log.i("onTick心跳机制", "11111");
                final int fhr = fhrPackage.getFHR1();
                showBlueNum.setText("当前频率：" + fhr + "");
            }

            @Override
            public void onFinish() {
                start();
            }
        };

        readDataTimer.startAt(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bondedDevices = adapter.getBondedDevices();
        for (BluetoothDevice device : bondedDevices) {
            Log.i("deviceInfo:", device.getName() + "---" + device.getAddress());
            if (device.getName().equalsIgnoreCase("IHB2LC9P2UUZ")) {
                connectDevice = device;
            }
        }

    }

    @OnClick(R.id.connectBlue)
    public void connectBlue() {
        //连接设备
        if (taiXinYiService != null) {
            taiXinYiService.connect(connectDevice, false);
            Toast.makeText(getApplicationContext(), "连接成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this
                    , "服务未连接", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.disConnectBlue)
    public void setDisConnectBlue() {
        taiXinYiService.disConnect();
        showBlueNum.setText("已经断开频率");
        Toast.makeText(getApplicationContext(), "断开链接", Toast.LENGTH_SHORT).show();

    }

    @OnClick(R.id.reconnect)
    public void reconnect(){
        taiXinYiService.reConnect(3);
    }

    /**
     * 声音的播放处理
     */
    @OnClick(R.id.startVoice)
    public void startVoicePLay() {
        taiXinYiService.startPlayVoice();

    }

    @OnClick(R.id.stopVoice)
    public void stopVoicePlay() {
        taiXinYiService.stopPlayVoice();
    }

    /**
     * 音量的调节
     */
    @OnClick(R.id.addVoice)
    public void addVoiceSize() {
        taiXinYiService.adjectVoiceSize(10);
    }

    @OnClick(R.id.reduceVoice)
    public void reduceVoiceSize() {
        taiXinYiService.adjectVoiceSize(-10);
    }

    @OnClick(R.id.startHeartRate)
    public void startHeartRate() {
        taiXinYiService.startRecord();
    }

    @OnClick(R.id.stopHeartRate)
    public void stopHeartRate() {
        taiXinYiService.stopRecord();
    }

    /**
     * 声音的保存处理
     */

    @OnClick(R.id.saveVoiceFile)
    public void saveVoiceFile() {
        String recordId = "test" + System.currentTimeMillis();
        try {
            taiXinYiService.saveVoiceFile(getApplicationContext(), recordId);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,"保存语音文件操作失败",Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.unSaveVoiceFile)
    public void unsaveVoiceFile() {
        try {
            taiXinYiService.unSaveVoiceFile();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,"不保存语音文件操作失败",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 当销毁的时候 断开链接
        EventBus.getDefault().unregister(this);
        unbindService(bluetoothConnection);
    }

    /**
     * 初始化蓝牙的服务
     */
    ServiceConnection bluetoothConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            taiXinYiService = ((BlueService.MyBind) service).getService();
            //绑定服务的时候必须进行初始化
            taiXinYiService.init(getApplicationContext());

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            taiXinYiService = null;
        }
    };


    public void onEventMainThread(BlueServiceEvent event) {

        switch (event.getBlueServiceType()) {
            case BlueServiceEvent.connectLost:
                LogUtil.i(TAG, "数据传输的socket 连接失败");
                Toast.makeText(MainActivity.this, "数据传输socket连接失败", Toast.LENGTH_SHORT).show();
                break;
            case BlueServiceEvent.connectFail:
                LogUtil.i(TAG, "设备连接不成功");
                Toast.makeText(MainActivity.this, "设备连接不成功", Toast.LENGTH_SHORT).show();
                break;
        }


    }
}
