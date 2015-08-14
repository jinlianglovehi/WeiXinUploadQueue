package cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.error;

import com.android.volley.VolleyError;

/**
 * Created by liuhongjian on 15/7/23 15:32.
 */
public class UnsupportRequestMethod extends VolleyError {
	public UnsupportRequestMethod(String cause) {
		super(cause);
	}

	public UnsupportRequestMethod() {
	}
}
