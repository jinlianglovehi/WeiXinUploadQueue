package cn.ihealthbaby.weitaixinpro.filequeue;

/**
 * Created by jinliang on 15/11/9.
 * 本地的文件实体的封装
 */
public class FileModel {
    private long userId;
    private String localRecordId;

    public FileModel(String localRecordId, long userId) {
        this.localRecordId = localRecordId;
        this.userId = userId;
    }
    public String getLocalRecordId() {
        return localRecordId;
    }

    public void setLocalRecordId(String localRecordId) {
        this.localRecordId = localRecordId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
