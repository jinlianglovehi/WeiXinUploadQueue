package cn.ihealthbaby.weitaixinpro;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixinpro.runablequeue.FileQueue;
import cn.ihealthbaby.weitaixinpro.runablequeue.ComparableRunnable;
/**
 * Created by jinliang on 15/11/9.
 */
public class TestUploadActivity extends Activity {
    private static final String TAG = TestUploadActivity.class.getSimpleName();
    @Bind(R.id.priority)
    EditText etPriority;
    private FileQueue fileQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testupload);
        ButterKnife.bind(this);
        fileQueue = new FileQueue();
    }
    @OnClick(R.id.addTask)
    public void addTask() {
        int priority = Integer.parseInt(etPriority.getText().toString());
        ComparableRunnable task = new ComparableRunnable(priority) {
            @Override
            public void run() {
                try {
                    Log.d(TAG, this.getPriority() + "");
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        fileQueue.add(task);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        fileQueue.stop();
    }
}
