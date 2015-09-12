package cn.ihealthbaby.weitaixin.library.data.model;

/**
 * Created by liuhongjian on 15/9/12 10:28.
 * 监护设置
 */
public class LocalSetting {
    //自动开始
    private boolean autostart;
    //报警设置
    private boolean alertInterval;
    //监护设置，选择时间间隔 position
    private int selectPosition;
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

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    public boolean isAutostart() {
        return autostart;
    }

    public void setAutostart(boolean autostart) {
        this.autostart = autostart;
    }

    public boolean isAlertInterval() {
        return alertInterval;
    }

    public void setAlertInterval(boolean alertInterval) {
        this.alertInterval = alertInterval;
    }


}
