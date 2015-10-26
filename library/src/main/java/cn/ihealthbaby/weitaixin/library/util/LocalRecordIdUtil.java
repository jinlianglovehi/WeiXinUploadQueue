package cn.ihealthbaby.weitaixin.library.util;

import android.content.Context;
import android.text.TextUtils;

import java.util.UUID;

/**
 * Created by liuhongjian on 15/10/14 17:42.
 */
public class LocalRecordIdUtil {
	public static String generateId() {
		String uuid = UUID.randomUUID().toString().replace("-", "");
		return uuid;
	}

	public static String generateAndSaveId(Context context) {
		SPUtil.clearUUID(context);
		String uuid = generateId();
		SPUtil.setUUID(context, uuid);
		return uuid;
	}

	public static String getOrGenerateSaveId(Context context) {
		String uuid = getSavedId(context);
		if (TextUtils.isEmpty(uuid)) {
			uuid = generateAndSaveId(context);
		}
		return uuid;
	}

	public static String getSavedId(Context context) {
		return SPUtil.getUUID(context);
	}
}
