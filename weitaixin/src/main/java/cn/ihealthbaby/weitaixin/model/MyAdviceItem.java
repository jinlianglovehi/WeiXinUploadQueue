package cn.ihealthbaby.weitaixin.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Think on 2015/9/10.
 */
public class MyAdviceItem implements Serializable {
	/**
	 * 检测信息的id
	 */
	private long id;
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
	private int testTimeLong;
	/**
	 * 咨询的状态 1 提交但为咨询 2咨询未回复 3 咨询已回复 4 咨询已删除
	 */
	private int status;
	//0本地  1云端
	private int isNativeRecord;
	private String feeling;
	private String purpose;
	private long userid;
	private String rdata;
	private String path;
	private int uploadstate;
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

	public int getIsNativeRecord() {
		return isNativeRecord;
	}

	public void setIsNativeRecord(int isNativeRecord) {
		this.isNativeRecord = isNativeRecord;
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
				       ", isNativeRecord=" + isNativeRecord +
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

	public void setUserid(long userid) {
		this.userid = userid;
	}
}
