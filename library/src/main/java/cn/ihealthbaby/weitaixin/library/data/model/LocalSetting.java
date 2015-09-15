package cn.ihealthbaby.weitaixin.library.data.model;

/**
 * Created by liuhongjian on 15/9/12 10:28.
 * 监护设置
 */
public class LocalSetting {
    //自动开始
    private boolean autoStart;
    //报警设置
    private boolean alert;
    //间隔时间，间隔时间之内不重复报警
    private int alertInterval;
    //自动上传
    private boolean autoUploading;

    //默认监护 间隔时间
    public static int DEFAULT_MONITOR_TIME = 5;


    public boolean isAutoUploading() {
        return autoUploading;
    }

    public void setAutoUploading(boolean autoUploading) {
        this.autoUploading = autoUploading;
    }

    public int getAlertInterval() {
        return alertInterval;
    }

    public void setAlertInterval(int alertInterval) {
        this.alertInterval = alertInterval;
    }


    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public boolean isAlert() {
        return alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }
}
