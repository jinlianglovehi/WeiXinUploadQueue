package cn.ihealthbaby.weitaixin;


import android.content.Context;
import android.content.Intent;

import java.io.File;

import cn.ihealthbaby.weitaixin.ui.widget.PayDialog;

/**
 * Created by chenweihua on 2015/10/16.
 */
public class DownloadAPKUtils {

    public Context context;
    public boolean isForce=false;

    public DownloadAPKUtils(Context context,boolean isForce) {
        this.context = context;
        this.isForce = isForce;
    }

    public void showDownDialog(boolean isFlag) {
        if (isFlag) {
            PayDialog payDialog = new PayDialog(context, new String[]{"有最新版本哦", "取消", "确定"});
            payDialog.setOperationAction(new PayDialog.OperationAction() {
                @Override
                public void payYes(Object... obj) {
                    Intent updateIntent = new Intent(context, DownloadService.class);
                    context.startService(updateIntent);
                }

                @Override
                public void payNo(Object... obj) {
                    if (isForce) {
                        System.exit(0);
                    }
                }
            });
            payDialog.show();
        } else {
            File updateFile = new File(Global.downloadDir, context.getResources().getString(R.string.app_name) + ".apk");
            if (updateFile.exists()) {
                //当不需要的时候，清除之前的下载文件，避免浪费用户空间
                updateFile.delete();
            }
        }
    }

}



