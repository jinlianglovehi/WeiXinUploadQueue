package cn.ihealthbaby.weitaixin.library.data.net.adapter;

import android.util.Base64;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.ihealthbaby.client.HttpClientAdapter;

/**
 * @author zuoge85 on 15/6/23.
 */
public abstract class AbstractHttpClientAdapter implements HttpClientAdapter {
	public static final String ENCODING = "UTF-8";
	public static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
	protected final String serverUrl;
	protected String accountToken;

	public AbstractHttpClientAdapter(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public static String format(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT);
		dateFormat.setLenient(false);
		return dateFormat.format(date);
	}

	public String getAccountToken() {
		return accountToken;
	}

	public void setAccountToken(String accountToken) {
		this.accountToken = accountToken;
	}

	protected String toString(Object entry) {
		if (entry == null) {
			return null;
		}
		if (entry instanceof Date) {
			return format((Date) entry);
		} else if (entry instanceof byte[]) {
			return Base64.encodeToString((byte[]) entry, Base64.URL_SAFE);
		} else {
			return entry.toString();
		}
	}
}
