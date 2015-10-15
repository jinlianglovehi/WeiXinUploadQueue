package cn.ihealthbaby.weitaixin.library.util;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import java.util.Timer;
import java.util.TimerTask;

import cn.ihealthbaby.weitaixin.library.data.bluetooth.data.FHRPackage;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;

/**
 * Created by liuhongjian on 15/9/22 15:23.
 */
public abstract class FixedRateCountDownTimer {
	private final static String TAG = "FixedRateCountDownTimer";
	private static final int END = 1;
	private final long interval;
	protected Timer timer;
	protected TimerTask timerTask;
	protected long start;
	protected long resume;
	protected long period;
	boolean stopFlag = false;
	private long duration;
	private long stop;
	private boolean paused;
	private boolean cancled;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!cancled || !paused) {
				long time = SystemClock.elapsedRealtime();
				if (time >= stop) {
					onFinish();
					cancel();
				} else {
//					LogUtil.d(TAG, "beforetick" + SystemClock.elapsedRealtime());
					FHRPackage fhrPackage = (FHRPackage) msg.obj;
					onTick(stop - time, fhrPackage);
//					LogUtil.d(TAG, "aftertick" + SystemClock.elapsedRealtime());
				}
			}
		}
	};
	private long pause;
	private long pausedTime;
	/**
	 * 计数器
	 */
	private int counter;
	/**
	 * 需要计数的次数
	 */
	private int count;

	public FixedRateCountDownTimer(long duration, long interval) {
		this.duration = duration;
		this.interval = interval;
		period = interval / 100;
	}

	// TODO: 15/9/22 需实现
	public void extra(long extra) {
		duration += extra;
		stop += extra;
		count = (int) (duration / interval);
		onExtra(duration, extra, stop);
	}

	protected abstract void onExtra(long newDuration, long extraTime, long stopTime);

	public void pause() {
		pause = SystemClock.elapsedRealtime();
		paused = true;
	}

	public long getConsumedTime() {
		return SystemClock.elapsedRealtime() - stop + duration - getPausedTime();
	}

	public long getPausedTime() {
		if (paused) {
			return pausedTime + SystemClock.elapsedRealtime() - pause;
		} else {
			return pausedTime;
		}
	}

	public long getInterval() {
		return interval;
	}

	public void resume() {
		if (paused) {
			resume = SystemClock.elapsedRealtime();
			stop = resume - pause + stop;
			pausedTime = pausedTime + pause - resume;
			paused = false;
		} else {
			throw new IllegalStateException("必须先调用pause(),才能调用resume()");
		}
	}

	public void startAt(long offset) {
		start = SystemClock.elapsedRealtime();
		stop = start + duration - offset;
		count = (int) (duration / interval);
		counter = ((int) (offset / interval));
		LogUtil.d(TAG, "start:%s,stop:%s,count:%s,counter:%s", start, stop, count, counter);
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				long left = stop - SystemClock.elapsedRealtime();
				if (left < interval * (count - counter)) {
					Message message = handler.obtainMessage();
					message.obj = DataStorage.fhrPackage;
					handler.sendMessage(message);
					counter++;
//					LogUtil.d("FixedRateCountDownTimer", "counter:" + counter);
				}
			}
		};
		onStart(start);
		timer.scheduleAtFixedRate(timerTask, 0, period);
	}

	public abstract void onStart(long startTime);

	public abstract void onRestart();

	public abstract void onTick(long millisUntilFinished, FHRPackage fhrPackage);

	public abstract void onFinish();

	public void cancel() {
		cancled = true;
		if (timer != null) {
			timer.cancel();
		}
		handler.removeMessages(0);
	}

	public void restart() {
		cancel();
		start();
		onRestart();
	}

	public void start() {
		startAt(0);
	}

	public long getDuration() {
		return duration;
	}
}
