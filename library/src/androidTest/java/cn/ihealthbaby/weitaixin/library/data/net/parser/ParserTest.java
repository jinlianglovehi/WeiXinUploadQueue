package cn.ihealthbaby.weitaixin.library.data.net.parser;

import android.test.InstrumentationTestCase;

import junit.framework.Assert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.library.data.net.paser.Parser;

/**
 * Created by liuhongjian on 15/7/23 21:46.
 */
public class ParserTest extends InstrumentationTestCase {
	private String s;
	private Type type;

	public static ApiType type(Type raw, Type... types) {
		return new ApiType(raw, types);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		s = "{\"data\":{\"id\":1,\"typeId\":1,\"name\":\"顾文昌\",\"telephone\":\"111111111\",\"mobile\":\"13161401474\",\"password\":\"123456\",\"headPic\":\"aaaa\",\"birthday\":\"2015-06-22 13:26:16\",\"createTime\":\"2015-06-23 13:26:21\",\"deliveryTime\":\"2015-06-27 13:26:42\",\"accountToken\":\"8kaJ8L1G8e1e3n0E4Se2boaM0S936V590m174wesbKaP0OcZfj616G3Q0f0A0A6V\",\"serviceInfo\":{\"doctorName\":\"顾文昌\",\"areaInfo\":\"北京-北京-海淀\",\"hospitalName\":\"春伟科技\",\"serialnum\":\"IHB2LC99FNQC\"}}}";
		type = (Type) type(Result.class, type(User.class));
	}

	public void test() {
		int i = 1;
		int j = 1;
		Assert.assertEquals(i, j);
	}

	@Override
	protected void runTest() throws Throwable {
		Result<Object> parse = Parser.getInstance().parse(s, type);
		assertNotNull(parse);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	private static class ApiType implements ParameterizedType {
		private final Type[] types;
		private final Type raw;

		public ApiType(Type raw, Type... types) {
			this.raw = raw;
			this.types = types;
		}

		@Override
		public Type[] getActualTypeArguments() {
			return types;
		}

		@Override
		public Type getRawType() {
			return raw;
		}

		@Override
		public Type getOwnerType() {
			return null;
		}
	}
}
