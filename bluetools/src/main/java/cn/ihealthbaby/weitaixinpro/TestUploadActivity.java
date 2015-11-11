package cn.ihealthbaby.weitaixinpro;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixinpro.filequeue.UploadFileUtils;
import cn.ihealthbaby.weitaixinpro.runablequeue.FileQueue;
import cn.ihealthbaby.weitaixinpro.runablequeue.SimpleTask;

/**
 * Created by jinliang on 15/11/9.
 */
public class TestUploadActivity extends Activity {
    private static final String TAG = TestUploadActivity.class.getSimpleName();
    @Bind(R.id.addTaskStopOtherTask)
    Button addTaskStopOtherTask;
    @Bind(R.id.openOtherTask)
    Button openOtherTask;

    private UploadFileUtils utils;
    @Bind(R.id.startQueue)
    Button startQueue;
    @Bind(R.id.addQueue)
    Button addQueue;

    private int taskSum = 0;
    private FileQueue fileQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testupload);
        ButterKnife.bind(this);
        fileQueue = new FileQueue();

    }

    @OnClick(R.id.startQueue)
    public void startQueue() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                fileQueue.start();
            }
        };
        thread.start();
    }

    @OnClick(R.id.addQueue)
    public void addMethod() {
        taskSum = taskSum + 1;
        fileQueue.addTask(
                new SimpleTask(taskSum, TAG + taskSum) {
                    @Override
                    public void start() {
                        Log.i("执行fileTask 任务", "---执行新的任务---");
                    }
                }
        );
    }

    @OnClick(R.id.openOtherTask)
    public void openOtherTaskClick() {
        fileQueue.openOtherTask();
    }

    @OnClick(R.id.addTaskStopOtherTask)
    public void addTaskStopOtherTask() {
        fileQueue.startTaskStopOther(
                new SimpleTask() {
                    @Override
                    public void start() {
                        Log.i(TAG,"头条任务");
                    }
                }
        );

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void fileQueue() {

        fileQueue.addTask(
                new Runnable() {
                    @Override
                    public void run() {
                        Log.i("测试Task", "测试Task执行");
                    }
                }
        );

    }


}
