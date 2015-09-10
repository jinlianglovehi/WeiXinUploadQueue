package cn.ihealthbaby.weitaixin.library.event;

/**
 * Created by liuhongjian on 15/9/10 10:44.
 */
public class PlaySoundEvent {
	private boolean play;

	public PlaySoundEvent(boolean play) {
		this.play = play;
	}

	public boolean isPlay() {
		return play;
	}

	public void setPlay(boolean play) {
		this.play = play;
	}
}
