package cn.ihealthbaby.weitaixin.library.event;

import java.io.File;

/**
 * Created by liuhongjian on 15/10/12 23:06.
 */
public class StartMonitorEvent {
	private File file;

	public StartMonitorEvent() {
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
}
