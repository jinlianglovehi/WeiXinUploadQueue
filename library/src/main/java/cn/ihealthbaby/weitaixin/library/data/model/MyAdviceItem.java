package cn.ihealthbaby.weitaixin.library.data.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Think on 2015/9/10.
 */
public class MyAdviceItem implements Serializable {

	// 1本地   2云端   3正在上传
	public static final int UPLOADSTATE_NATIVE_RECORD = 1;
	public static final int UPLOADSTATE_CLOUD_RECORD = 2;
	public static final int UPLOADSTATE_UPLOADING_RECORD = 3;


	//0"问医生", 1"等待回复", 2"已回复", 3"需上传"
	public static final int STATUS_ASK_DOCTOR= 0;
	public static final int STATUS_WAIT_REPLY = 1;
	public static final int STATUS_REPLYED= 2;
	public static final int STATUS_NATIVE = 3;


	/**
	 * 检测信息的id
	 */
	private long id = -1;
	/**
	 * 孕周
	 */
	private String gestationalWeeks;
	/**
	 * 检测时间
	 */
	private Date testTime;
	/**
	 * 检测时长
	 */
	private int testTimeLong = -1;
	/**
	 * 咨询的状态 1 提交但为咨询 2咨询未回复 3 咨询已回复 4 咨询已删除
	 * "问医生", "等待回复", "已回复", "需上传"
	 */
	private int status= STATUS_NATIVE;
//	//100本地  1云端
//	private int isNativeRecord = -1;
	private String feeling;
	private String purpose;
	private String url;
	private long userid = -1;
	private String rdata;
	private String path;
	private int uploadstate = -1; // 1本地   2云端   3正在上传
	private String serialnum;
	private String jianceid;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getGestationalWeeks() {
		return gestationalWeeks;
	}

	public void setGestationalWeeks(String gestationalWeeks) {
		this.gestationalWeeks = gestationalWeeks;
	}

	public Date getTestTime() {
		return testTime;
	}

	public void setTestTime(Date testTime) {
		this.testTime = testTime;
	}

	public int getTestTimeLong() {
		return testTimeLong;
	}

	public void setTestTimeLong(int testTimeLong) {
		this.testTimeLong = testTimeLong;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


	public String getFeeling() {
		return feeling;
	}

	public void setFeeling(String feeling) {
		this.feeling = feeling;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public String getRdata() {
		return rdata;
	}

	public void setRdata(String rdata) {
		this.rdata = rdata;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getUploadstate() {
		return uploadstate;
	}

	public void setUploadstate(int uploadstate) {
		this.uploadstate = uploadstate;
	}

	public String getSerialnum() {
		return serialnum;
	}

	public void setSerialnum(String serialnum) {
		this.serialnum = serialnum;
	}

	public String getJianceid() {
		return jianceid;
	}

	public void setJianceid(String jianceid) {
		this.jianceid = jianceid;
	}

	@Override
	public String toString() {
		return "MyAdviceItem{" +
				       "id=" + id +
				       ", gestationalWeeks='" + gestationalWeeks + '\'' +
				       ", testTime=" + testTime +
				       ", testTimeLong=" + testTimeLong +
				       ", status=" + status +
				       ", feeling='" + feeling + '\'' +
				       ", purpose='" + purpose + '\'' +
				       ", userid=" + userid +
				       ", rdata='" + rdata + '\'' +
				       ", path='" + path + '\'' +
				       ", uploadstate=" + uploadstate +
				       ", serialnum='" + serialnum + '\'' +
				       ", jianceid='" + jianceid + '\'' +
				       '}';
	}
}
