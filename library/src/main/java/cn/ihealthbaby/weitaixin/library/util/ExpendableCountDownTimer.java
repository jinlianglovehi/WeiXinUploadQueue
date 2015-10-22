/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ihealthbaby.weitaixin.library.util;

import android.os.Handler;
import android.os.Message;

/**
 * 根据countDownTimer修改来的辅助工具类,增加部分功能,如 总时间延时,暂停,恢复,从某一时间开始
 */
public abstract class ExpendableCountDownTimer {
	private static final int MSG = 1;
	/**
	 * The interval in millis that the user receives callbacks
	 */
	private final long interval;
	/**
	 * 总的计时时间
	 */
	private long duration;
	private long startTime;
	private long stopTime;
	/**
	 * boolean representing if the timer was cancelled
	 */
	private boolean mCancelled = false;
	// handles counting down
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			synchronized (ExpendableCountDownTimer.this) {
				if (mCancelled) {
					return;
				}
				final long millisLeft = stopTime - System.currentTimeMillis();
				if (millisLeft <= 0) {
					onFinish();
				} else if (millisLeft < interval) {
					// no tick, just delay until done
					sendMessageDelayed(obtainMessage(MSG), millisLeft);
				} else {
					long lastTickStart = System.currentTimeMillis();
					onTick(millisLeft);
					// take into account user's onTick taking time to execute
					long delay = lastTickStart + interval - System.currentTimeMillis();
					// special case: user's onTick took more than interval to
					// complete, skip to next interval
					while (delay < 0) delay += interval;
					sendMessageDelayed(obtainMessage(MSG), delay);
				}
			}
		}
	};
	private long offset;

	public ExpendableCountDownTimer(long duration, long interval) {
		this.duration = duration;
		this.interval = interval;
	}

	/**
	 * @param extraTime 延长的总时间,(负值表示缩短时间)
	 */
	public synchronized void extra(long extraTime) {
		//延长总时间
		duration += extraTime;
		//重新设定结束时间
		stopTime = System.currentTimeMillis() + duration;
		onExtra(duration, extraTime, stopTime);
	}

	/**
	 * @param offset 开始的时间点,相对时间
	 */
	public synchronized final void startAt(long offset) {
		if (offset < 0) {
			offset = 0;
		}
		this.offset = offset;
		mCancelled = false;
		if (duration <= offset) {
			onFinish();
			return;
		}
		startTime = System.currentTimeMillis() - offset;
		stopTime = System.currentTimeMillis() + duration - offset;
		mHandler.sendMessage(mHandler.obtainMessage(MSG));
		onStart(startTime);
	}

	public synchronized final void start() {
		startAt(0);
	}

	public synchronized final void cancel() {
		mCancelled = true;
		mHandler.removeMessages(MSG);
	}

	public long getDuration() {
		return duration;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getStopTime() {
		return stopTime;
	}

	public long getInterval() {
		return interval;
	}

	public long getConsumedTime() {
		long consumedTime = 0;
		if (startTime != 0) {
			consumedTime = System.currentTimeMillis() - startTime;
			if (consumedTime < 0) {
				consumedTime = 0;
			}
		}
		return consumedTime;
	}

	public abstract void onStart(long startTime);

	public abstract void onExtra(long duration, long extraTime, long stopTime);

	public abstract void onTick(long millisUntilFinished);

	public abstract void onFinish();

	public abstract void onRestart();

	public void restart() {
		cancel();
		onRestart();
		start();
	}

	public long getOffset() {
		return offset;
	}
}
