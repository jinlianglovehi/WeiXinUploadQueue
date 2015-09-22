package cn.ihealthbaby.weitaixin.library.util;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by liuhongjian on 15/9/22 15:23.
 */
public abstract class FixedRateCountDownTimer {
	private static final int END = 1;
	public final Timer timer;
	public TimerTask timerTask;
	private final long interval;
	public long start;
	public long resume;
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

	public FixedRateCountDownTimer(long duration, long interval) {
		this.duration = duration;
		this.interval = interval;
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
		return SystemClock.elapsedRealtime() - start - getPausedTime();
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
				long l = stop - SystemClock.elapsedRealtime();
				handler.sendEmptyMessage(0);
			}
		};
		onStart(start);
		timer.scheduleAtFixedRate(timerTask, 0, interval / 100);
//		timer.schedule(timerTask, 0, interval);
	}

	public abstract void onStart(long startTime);

	public abstract void onRestart();

	public abstract void onTick(long millisUntilFinished);

	public abstract void onFinish();

	public void cancel() {
		cancled = true;
		timer.cancel();
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
