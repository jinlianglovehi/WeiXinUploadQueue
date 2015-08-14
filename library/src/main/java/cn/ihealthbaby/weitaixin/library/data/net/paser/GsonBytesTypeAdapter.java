package cn.ihealthbaby.weitaixin.library.data.net.paser;

import android.util.Base64;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * @author zuoge85 on 15/7/16.
 */
public final class GsonBytesTypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
	public GsonBytesTypeAdapter() {
	}

	public JsonElement serialize(byte[] data, Type typeOfSrc, JsonSerializationContext context) {
		;
		return new JsonPrimitive(Base64.encodeToString(data, Base64.URL_SAFE));
//        return new JsonPrimitive(Base64.encodeBase64URLSafeString(data));
	}

	public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		if (!(json instanceof JsonPrimitive)) {
			throw new JsonParseException("The date should be a string value");
		}
		return Base64.decode(json.getAsString(), Base64.DEFAULT);
//        return Base64.decodeBase64(json.getAsString());
	}
}
