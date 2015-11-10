package cn.ihealthbaby.weitaixinpro.filequeue;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by jinliang on 15/11/9.
 */
public class TestUploadActivity extends Activity {
    UploadFileUtils utils ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(null);
        utils = new UploadFileUtils(getApplicationContext());

        utils.add(new FileModel("", 0l));

        //utils.endExecute();



    }
}
