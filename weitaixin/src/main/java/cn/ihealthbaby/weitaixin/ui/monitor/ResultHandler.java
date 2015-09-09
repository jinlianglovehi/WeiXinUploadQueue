package cn.ihealthbaby.weitaixin.ui.monitor;

import java.util.List;

/**
 * Created by liuhongjian on 15/9/9 12:20.
 */
public class ResultHandler {
	public static String listToArrayString(List<Integer> fhrs) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("[");
		for (int i = 0; i < fhrs.size() - 2; i++) {
			stringBuffer.append(fhrs.get(i));
			stringBuffer.append(",");
		}
		stringBuffer.append("]");
		return stringBuffer.toString();
	}
}
