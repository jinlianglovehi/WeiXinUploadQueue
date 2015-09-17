package cn.ihealthbaby.weitaixin.library.data.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "RECORD".
*/
public class RecordDao extends AbstractDao<Record, Long> {

    public static final String TABLENAME = "RECORD";

    /**
     * Properties of entity Record.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property LocalRecordId = new Property(1, String.class, "localRecordId", false, "LOCAL_RECORD_ID");
        public final static Property UserId = new Property(2, long.class, "userId", false, "USER_ID");
        public final static Property UserName = new Property(3, String.class, "userName", false, "USER_NAME");
        public final static Property SerialNumber = new Property(4, String.class, "serialNumber", false, "SERIAL_NUMBER");
        public final static Property UploadState = new Property(5, int.class, "uploadState", false, "UPLOAD_STATE");
        public final static Property RecordStartTime = new Property(6, java.util.Date.class, "recordStartTime", false, "RECORD_START_TIME");
        public final static Property Duration = new Property(7, Long.class, "duration", false, "DURATION");
        public final static Property RecordData = new Property(8, String.class, "recordData", false, "RECORD_DATA");
        public final static Property SoundPath = new Property(9, String.class, "soundPath", false, "SOUND_PATH");
        public final static Property Feeling = new Property(10, Integer.class, "feeling", false, "FEELING");
        public final static Property Purpose = new Property(11, Integer.class, "purpose", false, "PURPOSE");
    };

    private DaoSession daoSession;


    public RecordDao(DaoConfig config) {
        super(config);
    }
    
    public RecordDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"RECORD\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"LOCAL_RECORD_ID\" TEXT NOT NULL UNIQUE ," + // 1: localRecordId
                "\"USER_ID\" INTEGER NOT NULL ," + // 2: userId
                "\"USER_NAME\" TEXT NOT NULL ," + // 3: userName
                "\"SERIAL_NUMBER\" TEXT NOT NULL ," + // 4: serialNumber
                "\"UPLOAD_STATE\" INTEGER NOT NULL ," + // 5: uploadState
                "\"RECORD_START_TIME\" INTEGER," + // 6: recordStartTime
                "\"DURATION\" INTEGER," + // 7: duration
                "\"RECORD_DATA\" TEXT," + // 8: recordData
                "\"SOUND_PATH\" TEXT," + // 9: soundPath
                "\"FEELING\" INTEGER," + // 10: feeling
                "\"PURPOSE\" INTEGER);"); // 11: purpose
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"RECORD\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Record entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getLocalRecordId());
        stmt.bindLong(3, entity.getUserId());
        stmt.bindString(4, entity.getUserName());
        stmt.bindString(5, entity.getSerialNumber());
        stmt.bindLong(6, entity.getUploadState());
 
        java.util.Date recordStartTime = entity.getRecordStartTime();
        if (recordStartTime != null) {
            stmt.bindLong(7, recordStartTime.getTime());
        }
 
        Long duration = entity.getDuration();
        if (duration != null) {
            stmt.bindLong(8, duration);
        }
 
        String recordData = entity.getRecordData();
        if (recordData != null) {
            stmt.bindString(9, recordData);
        }
 
        String soundPath = entity.getSoundPath();
        if (soundPath != null) {
            stmt.bindString(10, soundPath);
        }
 
        Integer feeling = entity.getFeeling();
        if (feeling != null) {
            stmt.bindLong(11, feeling);
        }
 
        Integer purpose = entity.getPurpose();
        if (purpose != null) {
            stmt.bindLong(12, purpose);
        }
    }

    @Override
    protected void attachEntity(Record entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Record readEntity(Cursor cursor, int offset) {
        Record entity = new Record( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // localRecordId
            cursor.getLong(offset + 2), // userId
            cursor.getString(offset + 3), // userName
            cursor.getString(offset + 4), // serialNumber
            cursor.getInt(offset + 5), // uploadState
            cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)), // recordStartTime
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7), // duration
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // recordData
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // soundPath
            cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10), // feeling
            cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11) // purpose
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Record entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setLocalRecordId(cursor.getString(offset + 1));
        entity.setUserId(cursor.getLong(offset + 2));
        entity.setUserName(cursor.getString(offset + 3));
        entity.setSerialNumber(cursor.getString(offset + 4));
        entity.setUploadState(cursor.getInt(offset + 5));
        entity.setRecordStartTime(cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)));
        entity.setDuration(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
        entity.setRecordData(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setSoundPath(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setFeeling(cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10));
        entity.setPurpose(cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Record entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Record entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
