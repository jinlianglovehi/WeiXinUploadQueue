package cn.ihealthbaby.weitaixinpro.filequeue;

/**
 * Created by liuhongjian on 15/9/25 23:03.
 */
public class UploadEvent {
	public static final int RESULT_SUCCESS = 1;
	public static final int RESULT_FAIL = 2;
	private int result;
	private String localRecordId;
	private String key;
	private String token;

	public UploadEvent(int result) {
		this.result = result;
	}

	public UploadEvent(int result, String localRecordId, String key, String token) {
		this.result = result;
		this.localRecordId = localRecordId;
		this.key = key;
		this.token = token;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getLocalRecordId() {
		return localRecordId;
	}

	public void setLocalRecordId(String localRecordId) {
		this.localRecordId = localRecordId;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("UploadEvent{");
		sb.append("key='").append(key).append('\'');
		sb.append(", result=").append(result);
		sb.append(", localRecordId='").append(localRecordId).append('\'');
		sb.append(", token='").append(token).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
