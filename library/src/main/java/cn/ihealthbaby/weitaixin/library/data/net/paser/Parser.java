package cn.ihealthbaby.weitaixin.library.data.net.paser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

import cn.ihealthbaby.client.Result;

/**
 * Created by Liu hongjian on 2015/6/25. <p/> 统一的parser
 */
public class Parser implements IParser {
	public static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static Parser instance;
	private Gson gson;

	private Parser() {
		GsonBytesTypeAdapter bytesTypeAdapter = new GsonBytesTypeAdapter();
		gson = new GsonBuilder().setDateFormat(FORMAT)
				       .registerTypeAdapter(byte[].class, bytesTypeAdapter).create();
	}

	public static Parser getInstance() {
		if (instance == null) {
			synchronized (Parser.class) {
				if (instance == null) {
					instance = new Parser();
				}
			}
		}
		return instance;
	}

	@Override
	public <T> Result<T> parse(String json, Type type) throws Exception {
		return gson.fromJson(json, type);
	}
}
