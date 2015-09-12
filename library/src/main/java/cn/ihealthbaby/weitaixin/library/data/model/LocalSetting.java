package cn.ihealthbaby.weitaixin.library.data.model;

/**
 * Created by liuhongjian on 15/9/12 10:28.
 * 监护设置
 */
public class LocalSetting {
    //自动开始
    private boolean autostart;
    //报警设置
    private boolean policeset;

    public boolean isAutostart() {
        return autostart;
    }

    public void setAutostart(boolean autostart) {
        this.autostart = autostart;
    }

    public boolean isPoliceset() {
        return policeset;
    }

    public void setPoliceset(boolean policeset) {
        this.policeset = policeset;
    }
}
