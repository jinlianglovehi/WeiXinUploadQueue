package cn.ihealthbaby.weitaixinpro.ui.widget;

/**
 * Created by liuhongjian on 15/10/8 20:53.
 */
public class SoundUploadedEvent {
	private int position;

	SoundUploadedEvent(int position) {
		this.position = position;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
