package cn.ihealthbaby.weitaixinpro.filequeue;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixinpro.R;

/**
 * Created by jinliang on 15/11/9.
 */
public class TestUploadActivity extends Activity {
    private UploadFileUtils utils;
    @Bind(R.id.startQueue)
    Button startQueue;
    @Bind(R.id.addQueue)
    Button addQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testupload);
        ButterKnife.bind(this);
       // utils = new UploadFileUtils(getApplicationContext());
       // utils.run();

    }
    @OnClick(R.id.startQueue)
    public void startQueue(){
      //  utils.add(new FileModel(""+System.currentTimeMillis(),1),1);
    }

    @OnClick(R.id.addQueue)
    public void  addMethod(){
        Toast.makeText(getApplicationContext(),"新建队列",Toast.LENGTH_SHORT).show();
        Log.i("addQueue", "添加执行任务");

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
