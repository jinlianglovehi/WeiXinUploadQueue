package cn.ihealthbaby.weitaixin.library.data.model;

/**
 * Created by liuhongjian on 15/9/12 10:28.
 * 监护设置
 */
public class LocalSetting {
    //自动开始
    private boolean autostart;
    //报警设置
    private boolean alert;
    //监护时间
    private int monitorTime;
    //自动上传
    private boolean auto_uploading;

    //默认监护 间隔时间
    public static int DEFAULT_MONITOR_TIME = 5;

    //默认选中 position
    public static int DEFAULT_SELECT_POSITION = 0;

    public boolean isAuto_uploading() {
        return auto_uploading;
    }

    public void setAuto_uploading(boolean auto_uploading) {
        this.auto_uploading = auto_uploading;
    }

    public int getMonitorTime() {
        return monitorTime;
    }

    public void setMonitorTime(int monitorTime) {
        this.monitorTime = monitorTime;
    }


    public boolean isAutostart() {
        return autostart;
    }

    public void setAutostart(boolean autostart) {
        this.autostart = autostart;
    }

    public boolean isAlert() {
        return alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }
}
