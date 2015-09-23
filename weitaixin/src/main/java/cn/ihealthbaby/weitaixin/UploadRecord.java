package cn.ihealthbaby.weitaixin;

import android.app.Dialog;

import java.util.UUID;

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
    public static void uploadRecord(final BaseActivity context, AdviceItem adviceItem) {
        final CustomDialog customDialog = new CustomDialog();
        Dialog dialog = customDialog.createDialog1(context, "上传中...");
        dialog.show();
        AdviceForm adviceForm = new AdviceForm();
        UUID uuid = UUID.randomUUID();
        adviceForm.setClientId(uuid + "");
        adviceForm.setTestTime(adviceItem.getTestTime());
        adviceForm.setTestTimeLong(adviceItem.getTestTimeLong());
//            adviceForm.setData();
//            adviceForm.setAskPurpose();
//            adviceForm.setDataType();
//            adviceForm.setDeviceType();
//            adviceForm.setFeeling();
//            adviceForm.setFetalTonePath();
//            adviceForm.setLatitude();
//            adviceForm.setLongitude();
        ApiManager.getInstance().adviceApi.uploadData(adviceForm,
                new DefaultCallback<Long>(context, new AbstractBusiness<Long>() {
                    @Override
                    public void handleData(Long data) {
                        ToastUtil.show(context, "上传成功");
                    }

                    @Override
                    public void handleClientError(Exception e) {
                        super.handleClientError(e);
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleException(Exception e) {
                        super.handleException(e);
                        customDialog.dismiss();
                    }
                }), context);

    }

}


