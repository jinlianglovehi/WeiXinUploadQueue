package cn.ihealthbaby.weitaixin.library.data.bluetooth;

import android.os.Handler;
import android.os.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import cn.ihealthbaby.weitaixin.library.codec.Adpcm0;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.data.FHRPackage;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.exception.FHRParseException;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.exception.ParseException;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.exception.ValidationParseException;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.parser.Parser;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.test.Constants;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;

/**
 * Created by liuhongjian on 15/8/12 09:51.
 */
public class BufferQueue {
	public static final int DATA_FHR = 1;
	public static final int DATA_SOUND = 2;
	private final static String TAG = "BufferQueue";
	private BlockingQueue<byte[]> soundQueue = new LinkedBlockingQueue<>();
	private BlockingQueue<byte[]> fhrQueue = new LinkedBlockingQueue<>();
	private String version;
	private Thread fhrParser;
	private Thread soundParser;
	private int count2;
	private Handler handler;
	private Parser parser;

	public BufferQueue(Handler handler) {
		this.handler = handler;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void start() {
		stop();
		fhrParser = new Thread(new FHRParser(fhrQueue));
		soundParser = new Thread(new SoundParser(soundQueue));
		fhrParser.start();
		soundParser.start();
	}

	public void stop() {
		// TODO: 15/8/12
		fhrParser.interrupt();
		soundParser.interrupt();
	}

	public void add(byte[] buffer, int type) {
		switch (type) {
			case DATA_FHR:
				fhrQueue.add(buffer);
				break;
			case DATA_SOUND:
				soundQueue.add(buffer);
				break;
		}
	}

	private void save(FHRPackage fhrPackage) {
		// TODO: 15/8/12
		DataStorage.fhrPackages.add(fhrPackage);
		System.out.println(DataStorage.fhrPackages.size());
	}

	private void save(byte[] bytes) {
		// TODO: 15/8/12
		System.out.println(count2++);
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("BufferQueue{");
		sb.append("version='").append(version).append('\'');
		sb.append(", soundQueueSize=").append(soundQueue.size());
		sb.append(", fhrQueueSize=").append(fhrQueue.size());
		sb.append('}');
		return sb.toString();
	}

	public Parser getParser() {
		return parser;
	}

	public void setParser(Parser parser) {
		this.parser = parser;
	}

	class FHRParser implements Runnable {
		private BlockingQueue<byte[]> queue;

		public FHRParser(BlockingQueue<byte[]> queue) {
			this.queue = queue;
		}

		@Override
		public void run() {
			while (true) {
				try {
					byte[] buffer = queue.take();
					parser.validateData(buffer);
					FHRPackage fhrPackage = parser.parseFHR(buffer, getVersion());
					LogUtil.v(TAG, "run::%s", fhrPackage);
					Message message = Message.obtain(handler);
					message.what = Constants.MESSAGE_READ;
					message.obj = fhrPackage;
					message.sendToTarget();
					save(fhrPackage);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (FHRParseException e) {
					e.printStackTrace();
				} catch (ValidationParseException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class SoundParser implements Runnable {
		private BlockingQueue<byte[]> queue;

		public SoundParser(BlockingQueue<byte[]> queue) {
			this.queue = queue;
			AudioPlayer.getInstance().play();
		}

		@Override
		public void run() {
			while (true) {
				try {
					byte[] buffer = queue.take();
					parser.printBuffer("buffer", buffer);
//					parser.validateData(buffer);
					byte[] bytes = parser.parseSound(buffer, getVersion());
					play(decode(bytes));
//					parser.printShort("decoded Sound Data",  data);
//					play(data);
//					save(data);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ValidationParseException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}

		private void play(short[] bytes) {
			AudioPlayer.getInstance().playAudioTrack(bytes, 0, bytes.length);
		}

		private short[] decode(byte[] bytes) {
			// TODO: 15/8/12
			return Adpcm0.anylyseData(bytes, 1);
		}
	}
}
