package cn.ihealthbaby.weitaixin.tools;

import android.app.Dialog;
import android.content.Context;

import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.form.AdviceForm;
import cn.ihealthbaby.client.model.AdviceItem;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;

/**
 * Created by Think on 2015/9/2.
 */
public class UploadRecord {

    public static void uploadRecord(final BaseActivity context, AdviceItem adviceItem){

        final CustomDialog customDialog = new CustomDialog();
        Dialog dialog = customDialog.createDialog1(context, "上传中...");
        dialog.show();

        AdviceForm adviceForm = new AdviceForm();
        adviceForm.setClientId(adviceItem.getId() + "");
        adviceForm.setTestTime(adviceItem.getTestTime());
        adviceForm.setTestTimeLong(adviceItem.getTestTimeLong());
        adviceForm.setGestationalWeeks(adviceItem.getGestationalWeeks());
//            adviceForm.setData();
//            adviceForm.setAskPurpose();
//            adviceForm.setDataType();
//            adviceForm.setDeviceType();
//            adviceForm.setFeeling();
//            adviceForm.setFetalTonePath();
//            adviceForm.setLatitude();
//            adviceForm.setLongitude();

        ApiManager.getInstance().adviceApi.uploadData(adviceForm, new HttpClientAdapter.Callback<Long>() {
            @Override
            public void call(Result<Long> t) {
                if (t.isSuccess()) {
                    Long data = t.getData();
                    ToastUtil.show(context, "上传成功");
                } else {
                    ToastUtil.show(context, t.getMsgMap() + "");
                }
                customDialog.dismiss();
            }
        }, context);
    }

}


