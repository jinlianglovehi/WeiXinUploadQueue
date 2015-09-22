package cn.ihealthbaby.weitaixin.library.util;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import java.util.Timer;
import java.util.TimerTask;

import cn.ihealthbaby.weitaixin.library.log.LogUtil;

/**
 * Created by liuhongjian on 15/9/22 15:23.
 */
public abstract class FixedRateCountDownTimer {
	private static final int END = 1;
	public final Timer timer;
	private final long interval;
	public TimerTask timerTask;
	public long start;
	public long resume;
	public long period;
	private long duration;
	private long stop;
	private boolean paused;
	private boolean cancled;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			synchronized (FixedRateCountDownTimer.this) {
				if (!cancled || !paused) {
					long time = SystemClock.elapsedRealtime();
					if (time >= stop) {
						onFinish();
						cancel();
					} else {
						onTick(stop - time);
					}
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
		count = (int) (duration / interval);
		period = interval / 100;
		timer = new Timer();
	}

	// TODO: 15/9/22 需实现
	public void extra(long extra) {
		duration += extra;
		stop += extra;
		onExtra(duration, extra, stop);
	}

	protected abstract void onExtra(long duration, long extraTime, long stopTime);

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
		timerTask = new TimerTask() {
			@Override
			public void run() {
				long left = stop - SystemClock.elapsedRealtime();
				if (left < interval * (count - counter)) {
					handler.sendEmptyMessage(0);
					counter++;
					LogUtil.d("FixedRateCountDownTimer", "counter:" + counter);
				}
			}
		};
		onStart(start);
		timer.scheduleAtFixedRate(timerTask, 0, period);
	}

	public abstract void onStart(long startTime);

	public abstract void onRestart();

	public abstract void onTick(long millisUntilFinished);

	public abstract void onFinish();

	public void cancel() {
		cancled = true;
		timer.cancel();
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
