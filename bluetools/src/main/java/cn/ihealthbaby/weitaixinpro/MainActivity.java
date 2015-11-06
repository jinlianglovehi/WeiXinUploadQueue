package cn.ihealthbaby.weitaixinpro;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
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

import java.lang.ref.WeakReference;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.data.FHRPackage;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp.BluetoothScanner;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.FixedRateCountDownTimer;


public class MainActivity extends Activity {

    @Bind(R.id.connectBlue)
    Button connectBlue;//连接按钮
    @Bind(R.id.showBlueNum)
    TextView showBlueNum;
    @Bind(R.id.disConnectBlue)
    Button disConnectBlue;// 断开链接

    private BluetoothDevice devide;
    private TaiXinYiBluetoothService taiXinYiService;
    /**
     * 处理连接状态以及连接失败
     */

    private BluetoothAdapter adapter;
    private BluetoothScanner bluetoothScanner;
    private Set<BluetoothDevice> bondedDevices;
    private FixedRateCountDownTimer readDataTimer;
    private BluetoothDevice connectDevice;

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
                switch (msg.arg1){
                    case Constants.MESSAGE_CONNECTION_LOST:
                        Toast.makeText(MainActivity.this,"连接断开",Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(MainActivity.this, TaiXinYiBluetoothService.class);
        bindService(intent, bluetoothConnection, BIND_AUTO_CREATE);
        initTimeTask();
        adapter = BluetoothAdapter.getDefaultAdapter();
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
                showBlueNum.setText("当前频率："+fhr + "");
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

        if(taiXinYiService!=null) {
            taiXinYiService.connect(connectDevice, false);
            Toast.makeText(getApplicationContext(),"连接成功",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this
            ,"服务未连接",Toast.LENGTH_SHORT).show();
        }
    }
    @OnClick(R.id.disConnectBlue)
    public void setDisConnectBlue(){
        if(taiXinYiService!=null){
            taiXinYiService.stop();
            showBlueNum.setText("已经断开频率");
            Toast.makeText(getApplicationContext(),"断开链接",Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 初始化蓝牙的服务
     */
    ServiceConnection bluetoothConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            taiXinYiService = ((TaiXinYiBluetoothService.MyBinder) service).getBluetoothService();
            taiXinYiService.init(getApplicationContext(), handler);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            taiXinYiService=null;
        }
    };

}
