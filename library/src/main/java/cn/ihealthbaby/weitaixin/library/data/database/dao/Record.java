package cn.ihealthbaby.weitaixin.library.data.database.dao;

import android.content.Context;

import java.io.File;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "RECORD".
 */
public class Record {

     /**
	 * 自增id
	 */
    private Long id;
    /** Not-null value. */
     /**
	 * 本地记录id,对应AdviceItem的jianceId
	 */
    private String localRecordId;
     /**
	 * 用户id
	 */
    private long userId;
    /** Not-null value. */
     /**
	 * 用户名
	 */
    private String userName;
    /** Not-null value. */
     /**
	 * 对应AdviceItem的serialNum
	 */
    private String serialNumber;
     /**
	 * 上传状态
	 */
    private int uploadState;
     /**
	 * 监测开始时间,对应AdviceItem的testTime
	 */
    private java.util.Date recordStartTime;
     /**
	 * 监测时长,对应AdviceItem的testTimeLong
	 */
    private Long duration;
     /**
	 * 监测记录的数据结构,JSON格式
	 */
    private String recordData;
     /**
	 * 本地音频文件路径
	 */
    private String soundPath;
     /**
	 * 监护心情,对应AdviceItem的feeling
	 */
    private Integer feeling;
     /**
	 * 监护目的,对应AdviceItem的pupose
	 */
    private Integer purpose;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient RecordDao myDao;


    // KEEP FIELDS - put your custom fields here
	//需要讨论
//	private static final int ONE = 0x1;
//	public static final int UPLOAD_STATE_HAS_DATA = ONE << 0;
//  public static final int UPLOAD_STATE_HAS_SOUND = ONE << 1;
//	public static final int UPLOAD_STATE_IS_UPLOADING = ONE << 2;
//	public static final int UPLOAD_STATE_IS_UPLOADED = ONE << 3;
//	public static final int UPLOAD_STATE_DATA = UPLOAD_STATE_HAS_DATA;
//	public static final int UPLOAD_STATE_SOUND = UPLOAD_STATE_HAS_SOUND;
	public static final int UPLOAD_STATE_LOCAL =  1;
	public static final int UPLOAD_STATE_UPLOADING = 2;
    public static final int UPLOAD_STATE_CLOUD = 4;
//	public static final int SERVICE_STATUS_ = 5;
//	//0"问医生", 1"等待回复", 2"已回复", 3"需上传"
//	public static final int SERVICE_STATUS_ASK_DOCTOR = 0;
//	public static final int SERVICE_STATUS_WAIT_REPLY = 1;
//	public static final int SERVICE_STATUS_REPLYED = 2;
//	public static final int SERVICE_STATUS_LOCAL = 3;
    // KEEP FIELDS END

    public Record() {
    }

    public Record(Long id) {
        this.id = id;
    }

    public Record(Long id, String localRecordId, long userId, String userName, String serialNumber, int uploadState, java.util.Date recordStartTime, Long duration, String recordData, String soundPath, Integer feeling, Integer purpose) {
        this.id = id;
        this.localRecordId = localRecordId;
        this.userId = userId;
        this.userName = userName;
        this.serialNumber = serialNumber;
        this.uploadState = uploadState;
        this.recordStartTime = recordStartTime;
        this.duration = duration;
        this.recordData = recordData;
        this.soundPath = soundPath;
        this.feeling = feeling;
        this.purpose = purpose;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getRecordDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getLocalRecordId() {
        return localRecordId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setLocalRecordId(String localRecordId) {
        this.localRecordId = localRecordId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    /** Not-null value. */
    public String getUserName() {
        return userName;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /** Not-null value. */
    public String getSerialNumber() {
        return serialNumber;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getUploadState() {
        return uploadState;
    }

    public void setUploadState(int uploadState) {
        this.uploadState = uploadState;
    }

    public java.util.Date getRecordStartTime() {
        return recordStartTime;
    }

    public void setRecordStartTime(java.util.Date recordStartTime) {
        this.recordStartTime = recordStartTime;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getRecordData() {
        return recordData;
    }

    public void setRecordData(String recordData) {
        this.recordData = recordData;
    }

    public String getSoundPath() {
        return soundPath;
    }

    public void setSoundPath(String soundPath) {
        this.soundPath = soundPath;
    }

    public Integer getFeeling() {
        return feeling;
    }

    public void setFeeling(Integer feeling) {
        this.feeling = feeling;
    }

    public Integer getPurpose() {
        return purpose;
    }

    public void setPurpose(Integer purpose) {
        this.purpose = purpose;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
	public boolean hasData() {
//		return uploadState & UPLOAD_STATE_HAS_DATA;
		return false;
	}
	public boolean hasSound(Context context) {
        return new File(soundPath).exists();
    }
    // KEEP METHODS END

}