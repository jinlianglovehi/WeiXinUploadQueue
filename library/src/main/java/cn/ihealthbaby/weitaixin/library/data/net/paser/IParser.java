package cn.ihealthbaby.weitaixin.library.data.net.paser;

import java.lang.reflect.Type;

import cn.ihealthbaby.client.Result;

/**
 * Created by Think on 2015/6/25.
 */
public interface IParser {
	<T> Result<T> parse(String json, Type type) throws Exception;
}