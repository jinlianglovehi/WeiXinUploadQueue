package cn.ihealthbaby.weitaixin.library.data.bluetooth.parser;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import cn.ihealthbaby.weitaixin.library.data.bluetooth.AudioPlayer;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.BufferQueue;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.DataStorage;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.data.FHRPackage;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.exception.FHRParseException;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.exception.ParseException;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.exception.ValidationParseException;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.test.Constants;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;

/**
 * Created by liuhongjian on 15/7/17 12:52.
 */
public class Parser {
	//	//state
//	private static final int STATE_WRONG = -1;
//	private static final int STATE_HEAD_OK = 1;
//	private static final int STATE_CONTROLLER_HEART_BEAT_RATE = 2;
//	private static final int STATE_CONTROLLER_SOUND = 3;
//	private static final int STATE_VERSION_INFO = 4;
	//header
	private static final int HEADER_0 = 0X55;
	private static final int HEADER_1 = 0XAA;
	//取后两位
	private static final int LAST2BYTE = 0XFF;
	private static final int LAST2BIT = 0X03;
	//controller
	//心率信息
	@Deprecated
	private static final int CONTROLLER_HEART_BEAT_RATE_V1 = 0X01;
	private static final int CONTROLLER_HEART_BEAT_RATE_V2 = 0X03;
	//声音信息
	@Deprecated
	private static final int CONTROLLER_SOUND_V1 = 0X08;
	private static final int CONTROLLER_SOUND_V2 = 0X09;
	//    private static final int VERSION_1 = 1;
	//    private static final int VERSION_2 = 2;
	//    private static final int CONTROLLER_QUERY_VERSION = 0X56;
	//版本信息
	private static final int CONTROLLER_VERSION_INFO = 0X3F;
	//版本初始信息
	private static final int VERSION_NONE = -1;
	private static final String TAG = "Parser";
	private Handler handler;
	private BufferQueue bufferQueue;
	//
	private byte[] bytes4 = new byte[4];
	private byte[] bytes7 = new byte[7];
	private byte[] bytes321 = new byte[321];
	private byte[] bytes101 = new byte[101];
	private StringBuffer stringBuffer = new StringBuffer();
	//	/**
//	 * 检验buffer头部信息
//	 *
//	 * @param buffer
//	 * @return
//	 */
//	public static boolean parseHead(byte[] buffer) throws ParseHeaderException {
//		if ((buffer[0] & LAST2BYTE) == HEADER_0 && (buffer[1] & LAST2BYTE) == HEADER_1) {
//			return true;
//		} else {
//			throw new ParseHeaderException("WRONG HEADER");
//		}
//	}
//	/**
//	 * 检验buffer控制部分
//	 *
//	 * @param buffer
//	 * @return
//	 */
//	public static void parseController(byte[] buffer) throws ParseControllerException {
//		int controller = buffer[2] & LAST2BYTE;
//		switch (controller) {
//			case CONTROLLER_HEART_BEAT_RATE_V1:
//				break;
//			case CONTROLLER_SOUND_V1:
//				break;
//			case CONTROLLER_HEART_BEAT_RATE_V2:
//				break;
//			case CONTROLLER_SOUND_V2:
//				break;
//			default:
//				throw new ParseControllerException("WRONG CONTROLLER");
//		}
//	}
//	/**
//	 * 检验控制部分
//	 *
//	 * @param buffer
//	 * @return
//	 */
//	public static int parseControllerVersion(byte[] buffer) throws ParseVersionException {
//		int controller = buffer[2] & LAST2BYTE;
//		int state = VERSION_NONE;
//		switch (controller) {
//			case CONTROLLER_VERSION_INFO:
//				state = STATE_VERSION_INFO;
//				break;
//			default:
//				throw new ParseVersionException("WRONG VERSION");
//		}
//		return state;
//	}

	public Parser(Handler handler, BufferQueue bufferQueue) {
		this.handler = handler;
		this.bufferQueue = bufferQueue;
	}

	//	public void parse(byte[] buffer) throws ParseHeaderException, ParseControllerException {
//		if (parseHead(buffer)) {
//			int controller = buffer[2] & LAST2BYTE;
//			switch (controller) {
//				case CONTROLLER_HEART_BEAT_RATE_V1:
//					parseFHBV1(buffer);
//					break;
//				case CONTROLLER_SOUND_V1:
//					SoundPackage soundPackage = new SoundPackage();
//					soundPackage.setVersion("1");
//					break;
//				case CONTROLLER_HEART_BEAT_RATE_V2:
//					parseFHBV2(buffer);
//					break;
//				case CONTROLLER_SOUND_V2:
//					break;
//				default:
//					throw new ParseControllerException("WRONG CONTROLLER");
//			}
//		}
//	}
	public void printBuffer(String tag, byte[] buffer) {
		return;
//		stringBuffer.setLength(0);
//		for (int i = 0; i < buffer.length; i++) {
//			stringBuffer.append(" " + Integer.toHexString(buffer[i] & 0xff));
//		}
//		Log.d(TAG, tag + stringBuffer.toString());
	}

	public void printShort(String tag, short[] buffer) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < buffer.length; i++) {
			stringBuffer.append(" " + Integer.toHexString(buffer[i] & 0xff));
		}
		Log.d(TAG, tag + stringBuffer.toString());
	}

	/**
	 * 协议中中间若干个数据包，最后一个为sum校验数据,校验规则为验证sum%256
	 *
	 * @param buffer
	 * @return
	 */
	public boolean validateData(byte[] buffer) throws ValidationParseException {
		int sum = 0;
		for (int i = 0; i < buffer.length - 1; i++) {
			sum += buffer[i];
		}
		if ((sum - buffer[buffer.length - 1]) % 256 == 0) {
			return true;
		} else {
			LogUtil.v(TAG, "validateData::%s %s", sum % 256, buffer[buffer.length - 1]);
			throw new ValidationParseException();
		}
	}

	public void parsePackageData1(InputStream mmInStream) throws IOException, ParseException {
//		byte[] soundDataBufferV1 = new byte[321];
//		byte[] fetalDataBufferV1 = new byte[4];
//		byte[] soundDataBufferV2 = new byte[101];
//		byte[] fetalDataBufferV2 = new byte[7];
		byte[] oneByte = new byte[1];
		while (true) {
			System.out.println(mmInStream.available());
			if (mmInStream.available() < 324) {
				continue;
			}
			mmInStream.read(oneByte);
//			printBuffer("oneByte:", oneByte);
			if (translate(oneByte[0]) == 0x55) {
				mmInStream.read(oneByte);
//				printBuffer("oneByte:", oneByte);
				if (translate(oneByte[0]) == 0xaa) {
					mmInStream.read(oneByte);
//					printBuffer("oneByte:", oneByte);
					switch (translate(oneByte[0])) {
						//v1,胎心
						case CONTROLLER_HEART_BEAT_RATE_V1:
							byte[] fetalDataBufferV1 = bytes4;
							mmInStream.read(fetalDataBufferV1);
//									printBuffer("f:", fetalDataBufferV1);
							bufferQueue.add(fetalDataBufferV1, BufferQueue.DATA_FHR);
							validateData(fetalDataBufferV1);
							FHRPackage FHRPackage1 = parseFHR(fetalDataBufferV1, "1");
							LogUtil.v(TAG, "run::%s", FHRPackage1);
							break;
						//v2,胎心
						case CONTROLLER_HEART_BEAT_RATE_V2:
							byte[] fetalDataBufferV2 = bytes7;
							mmInStream.read(fetalDataBufferV2);
//									printBuffer("f:", fetalDataBufferV2);
							validateData(fetalDataBufferV2);
							FHRPackage FHRPackage2 = parseFHR(fetalDataBufferV2, "2");
							LogUtil.v(TAG, "run::%s", FHRPackage2);
							break;
						//v1,声音
						case CONTROLLER_SOUND_V1:
							byte[] soundDataBufferV1 = bytes321;
							mmInStream.read(soundDataBufferV1);
//							printBuffer("s:", soundDataBufferV1);
							validateData(soundDataBufferV1);
							byte[] bytes1 = parseSound(soundDataBufferV1, "1");
							AudioPlayer.getInstance().playAudioTrack(bytes1, 0, bytes1.length);
							break;
						//v2,声音
						case CONTROLLER_SOUND_V2:
							byte[] soundDataBufferV2 = bytes101;
							mmInStream.read(soundDataBufferV2);
//							printBuffer("s:", soundDataBufferV2);
							validateData(soundDataBufferV2);
							byte[] bytes2 = parseSound(soundDataBufferV2, "2");
							AudioPlayer.getInstance().playAudioTrack(bytes2, 0, bytes2.length);
							break;
						default:
							throw new ParseException("no such controller");
					}
				}
			}
		}
	}

	public FHRPackage parseFHR(byte[] buffer, String version) throws FHRParseException {
		switch (version) {
			case "1":
				return parseFHBV1(buffer);
			case "2":
				return parseFHBV2(buffer);
			default:
				throw new FHRParseException("no such version");
		}
	}

	private int translate(byte oneByte) {
		return oneByte & 0xff;
	}

	private FHRPackage parseFHBV1(byte[] buffer) {
		FHRPackage FHRPackage1 = DataStorage.fhrPackagePool;
		FHRPackage1.setTime(System.currentTimeMillis());
		FHRPackage1.setVersion("1");
		FHRPackage1.setFHR1(buffer[0] & LAST2BYTE);
		FHRPackage1.setAFM(buffer[1] == 0);
		FHRPackage1.setSignalStrength(buffer[2] & LAST2BIT);
		return FHRPackage1;
	}

	private FHRPackage parseFHBV2(byte[] buffer) {
		FHRPackage FHRPackage2 = DataStorage.fhrPackagePool;
		FHRPackage2.setTime(System.currentTimeMillis());
		FHRPackage2.setVersion("2");
		FHRPackage2.setFHR1(buffer[0] & LAST2BYTE);
		FHRPackage2.setFHR2(buffer[1] & LAST2BYTE);
		FHRPackage2.setTOCO(buffer[2] & LAST2BYTE);
		FHRPackage2.setAFM((buffer[3] & LAST2BYTE) == 0);
		return FHRPackage2;
	}

	public void parsePackageData(InputStream mmInStream) throws IOException, ParseException {
//		byte[] soundDataBufferV1 = new byte[321];
//		byte[] fetalDataBufferV1 = new byte[4];
//		byte[] soundDataBufferV2 = new byte[101];
//		byte[] fetalDataBufferV2 = new byte[7];
		bufferQueue.start();
		byte[] oneByte = new byte[1];
		while (true) {
//			System.out.println(mmInStream.available());
//			System.out.println(bufferQueue.toString());
			if (mmInStream.available() < 324) {
				continue;
			}
			mmInStream.read(oneByte);
			printBuffer("oneByte:", oneByte);
			if (translate(oneByte[0]) == 0x55) {
				mmInStream.read(oneByte);
				printBuffer("oneByte:", oneByte);
				if (translate(oneByte[0]) == 0xaa) {
					mmInStream.read(oneByte);
					printBuffer("oneByte:", oneByte);
					switch (translate(oneByte[0])) {
						//v1,胎心
						case CONTROLLER_HEART_BEAT_RATE_V1:
							if (bufferQueue.getVersion() == null) {
								bufferQueue.setVersion("1");
							}
							byte[] fetalDataBufferV1 = new byte[4];
							mmInStream.read(fetalDataBufferV1);
//									printBuffer("f:", fetalDataBufferV1);
							bufferQueue.add(fetalDataBufferV1, BufferQueue.DATA_FHR);
							break;
						//v2,胎心
						case CONTROLLER_HEART_BEAT_RATE_V2:
							if (bufferQueue.getVersion() == null) {
								bufferQueue.setVersion("2");
							}
							byte[] fetalDataBufferV2 = new byte[7];
							mmInStream.read(fetalDataBufferV2);
//									printBuffer("f:", fetalDataBufferV2);
							bufferQueue.add(fetalDataBufferV2, BufferQueue.DATA_FHR);
							break;
						//v1,声音
						case CONTROLLER_SOUND_V1:
//							if (bufferQueue.getVersion() == null) {
//								bufferQueue.setVersion("1");
//							}
//							byte[] soundDataBufferV1 = new byte[321];
//							mmInStream.read(soundDataBufferV1);
////							printBuffer("s:", soundDataBufferV1);
//							bufferQueue.add(soundDataBufferV1, BufferQueue.DATA_SOUND);
							int[] voice = getVoice(mmInStream);
							byte[] v = intForByte(ByteUtil.analysePackage(voice));
							Message message1 = Message.obtain(handler);
							message1.what = Constants.MESSAGE_VOICE;
							message1.obj = v;
							message1.sendToTarget();
							break;
						//v2,声音
						case CONTROLLER_SOUND_V2:
//							if (bufferQueue.getVersion() == null) {
//								bufferQueue.setVersion("2");
//							}
//							byte[] soundDataBufferV2 = new byte[101];
//							mmInStream.read(soundDataBufferV2);
////							printBuffer("s:", soundDataBufferV2);
//							bufferQueue.add(soundDataBufferV2, BufferQueue.DATA_SOUND);
							int[] voiceAd = getVoiceAd(mmInStream);
							byte[] adv = intForByte(ByteUtil.anylyseData(voiceAd, 1));
							Message message2 = Message.obtain(handler);
							message2.what = Constants.MESSAGE_VOICE;
							message2.obj = adv;
							message2.sendToTarget();
							break;
						default:
							throw new ParseException("no such controller");
					}
				}
			}
		}
	}

	/**
	 * 解析声音数据
	 *
	 * @param buffer
	 * @return
	 */
	public byte[] parseSound(byte[] buffer, String version) throws ParseException {
		byte[] bytes = null;
		switch (version) {
			case "1":
				bytes = parseSoundV1(buffer);
				break;
			case "2":
				bytes = parseSoundV2(buffer);
				break;
			default:
				break;
		}
		// TODO: 15/8/12 音频解码
		return bytes;
	}

	/**
	 * 获取一个原始的胎声包。
	 *
	 * @param inputStream
	 * @return
	 */
	private int[] getVoice(InputStream inputStream) {
		int[] ints = new int[321];
		for (int i = 0; i < 321; i++) {
			try {
				ints[i] = inputStream.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ints;
	}

	private int[] getVoiceAd(InputStream inputStream) {
		int[] ints = new int[101];
		for (int i = 0; i < 101; i++) {
			try {
				ints[i] = inputStream.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ints;
	}

	/**
	 * int数组转成byte数组。
	 *
	 * @param ints
	 * @return
	 */
	private byte[] intForByte(int[] ints) {
		int size = ints.length;
		byte[] shorts = new byte[size];
		for (int i = 0; i < size; i++) {
			shorts[i] = (byte) ints[i];
		}
		return shorts;
	}

	private byte[] parseSoundV2(byte[] buffer) {
		return buffer;
	}

	private byte[] parseSoundV1(byte[] buffer) throws ParseException {
		if (buffer.length != 321) {
			throw new ParseException();
		}
		byte[] bytes = new byte[buffer.length - 1];
		for (int i = 0; i < (bytes.length - 1) / 5; i++) {
			bytes[i + 0] = ((byte) (buffer[i + 5] & 0xC0 + buffer[i] & 0x3F));
			bytes[i + 1] = ((byte) (buffer[i] & 0x03 + buffer[i + 5] & 0x30 + buffer[i + 1] & 0xF0));
			bytes[i + 2] = ((byte) (buffer[i + 1] & 0x0F + buffer[i + 5] & 0x0C + buffer[i + 2] & 0xC0));
			bytes[i + 3] = ((byte) (buffer[i + 2] & 0x3F + buffer[i + 5] & 0xC0));
			bytes[i + 4] = (byte) (buffer[i + 5] & 0x03 + buffer[i + 4]);
		}
		return bytes;
	}
}
