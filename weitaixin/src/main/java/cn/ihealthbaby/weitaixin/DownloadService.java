package cn.ihealthbaby.weitaixin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.tools.AsynUploadEngine;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.ui.MeMainFragmentActivity;

/**
 * Created by chenweihua on 2015/10/26.
 */
public class DownloadService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    //标题
    private int titleId = 0;

    //文件存储
    private File updateDir = null;
    private File updateFile = null;

    //通知栏
    private NotificationManager updateNotificationManager = null;
    private Notification updateNotification = null;
    //通知栏跳转Intent
    private Intent updateIntent = null;
    private PendingIntent updatePendingIntent = null;

    //下载状态
    private final static int DOWNLOAD_COMPLETE = 0;
    private final static int DOWNLOAD_FAIL = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //获取传值
        titleId = R.string.app_name;
        //创建文件
        if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
            updateDir = new File(Environment.getExternalStorageDirectory(), Global.downloadDir);
            updateFile = new File(updateDir.getPath(), getResources().getString(R.string.app_name) + ".apk");
            //增加权限;
            if (!updateDir.exists()) {
                updateDir.mkdirs();
            }
            if (!updateFile.exists()) {
                try {
                    updateFile.createNewFile();
                } catch (Exception e) {
                    ToastUtil.show(this, "创建文件失败");
                    e.printStackTrace();
                    stopSelf();
                    return super.onStartCommand(intent,flags,startId);
                }
            } else {
                updateFile.delete();
                if (!updateFile.exists()) {
                    try {
                        updateFile.createNewFile();
                    } catch (Exception e) {
                        ToastUtil.show(this, "创建文件失败");
                        e.printStackTrace();
                        stopSelf();
                        return super.onStartCommand(intent,flags,startId);
                    }
                }
            }
        }else{
            ToastUtil.show(this, "没有SD卡");
            stopSelf();
            return super.onStartCommand(intent,flags,startId);
        }


        this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        this.updateNotification = new Notification();

        updateIntent = new Intent();
        updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
        updateNotification.icon = R.mipmap.ic_launcher;
        updateNotification.tickerText = "开始下载";
        updateNotification.setLatestEventInfo(DownloadService.this, getResources().getString(R.string.app_name), "等待下载 0%", updatePendingIntent);
        updateNotificationManager.notify(0, updateNotification);

        new DownloadTaskRunnable().run();
//        new Thread(new DownloadTaskRunnable()).start();

        return super.onStartCommand(intent, flags, startId);
    }

    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD_COMPLETE:
                    Uri uri = Uri.fromFile(updateFile);
                    Intent installIntent = new Intent(Intent.ACTION_VIEW);
                    installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
                    updatePendingIntent = PendingIntent.getActivity(DownloadService.this, 0, installIntent, 0);

                    updateNotification.defaults = Notification.DEFAULT_SOUND;
                    updateNotification.setLatestEventInfo(DownloadService.this, getResources().getString(R.string.app_name), "下载完成，点击安装。", updatePendingIntent);
                    updateNotificationManager.notify(0, updateNotification);

                    //停止服务
                    stopSelf();
                    break;

                case DOWNLOAD_FAIL:
                    //下载失败
                    updateNotification.setLatestEventInfo(DownloadService.this, getResources().getString(R.string.app_name), "下载失败。", updatePendingIntent);
                    updateNotificationManager.notify(0, updateNotification);
                    ToastUtil.show(DownloadService.this, "请求超时");
                    //停止服务
                    stopSelf();
                    break;

                default:
                    stopSelf();
            }
        }
    };

    Message message = updateHandler.obtainMessage();
    class DownloadTaskRunnable /*implements Runnable*/ {
//        Message message = updateHandler.obtainMessage();

        public void run() {
            message.what = DOWNLOAD_FAIL;
            try {
                boolean isSuccess = downloadFile(Global.downloadURL, updateFile);
//                if (isSuccess) {
//                    message.what = DOWNLOAD_COMPLETE;
//                    updateHandler.sendMessage(message);
//                } else {
//                    message.what = DOWNLOAD_FAIL;
//                    updateHandler.sendMessage(message);
//                }
            } catch (Exception ex) {
                ex.printStackTrace();
                message.what = DOWNLOAD_FAIL;
                updateHandler.sendMessage(message);
            }
        }
    }

    boolean isDown=false;
    public boolean downloadFile(final String downloadUrl, final File saveFile) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.post(downloadUrl, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {

                    FileOutputStream fos = null;
                    ByteArrayInputStream bais = null;

                    try {
                        fos = new FileOutputStream(saveFile, false);
                        byte buffer[] = new byte[4096];
                        int readsize = 0;

                        bais = new ByteArrayInputStream(responseBody);

                        while ((readsize = bais.read(buffer)) > 0) {
                            fos.write(buffer, 0, readsize);
                        }

                        isDown=true;
                        message.what = DOWNLOAD_COMPLETE;
                        updateHandler.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                        isDown=false;
                        message.what = DOWNLOAD_FAIL;
                        updateHandler.sendMessage(message);
                    } finally {
                        if (bais != null) {
                            try {
                                bais.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();// 把错误信息打印出轨迹来
                isDown=false;
                message.what = DOWNLOAD_FAIL;
                updateHandler.sendMessage(message);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);

                long upSize = (long) bytesWritten * 100 / totalSize;
//              LogUtil.d("downloadCount", "downloadCount==> " + upSize);
                updateNotification.setLatestEventInfo(DownloadService.this, getResources().getString(R.string.app_name), "正在下载 " + upSize + "%", updatePendingIntent);
                updateNotificationManager.notify(0, updateNotification);
            }
        });

        return isDown;
    }



    public boolean downloadFile7(String downloadUrl, File saveFile) throws Exception {
        int downloadCount = 0;
        int currentSize = 0;
        long totalSize = 0;
        int updateTotalSize = 0;

        HttpURLConnection httpConnection = null;
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            URL url = new URL(downloadUrl);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
            if (currentSize > 0) {
                httpConnection.setRequestProperty("RANGE", "bytes=" + currentSize + "-");
            }
            httpConnection.setConnectTimeout(20000);
            httpConnection.setReadTimeout(30000);
            LogUtil.d("httpConn3", "httpConn3==> ");
            httpConnection.connect();
            LogUtil.d("httpConn", "httpConn==> ");



            int responseCode = httpConnection.getResponseCode();
            LogUtil.d("httpConnresponseCode", "httpConnresponseCode==> "+responseCode);
            if (responseCode == 200) {
                updateTotalSize = httpConnection.getContentLength();
                LogUtil.d("httpConn77", "httpConn77==> ");
                is = httpConnection.getInputStream();
                LogUtil.d("httpConn55", "httpConn55==> ");

                fos = new FileOutputStream(saveFile, false);
                byte buffer[] = new byte[4096];
                int readsize = 0;
                LogUtil.d("httpConn2", "httpConn2==> ");
                while ((readsize = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, readsize);
                    totalSize += readsize;

                    int upSize = (int) totalSize * 100 / updateTotalSize;

                    LogUtil.d("downloadCount", "downloadCount==> "+ upSize);

                    if ((downloadCount == 0) || upSize - 2 > downloadCount) {
                        downloadCount += 2;
                        updateNotification.setLatestEventInfo(DownloadService.this, getResources().getString(R.string.app_name), "正在下载 " + upSize + "%", updatePendingIntent);
                        updateNotificationManager.notify(0, updateNotification);
                    }
                }
                return true;
            } else {
                ToastUtil.show(this, "服务器错误：" + responseCode);
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
            return false;
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }


}

