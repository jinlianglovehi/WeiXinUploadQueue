package cn.ihealthbaby.weitaixin.library.codec;

public class Adpcm {
	static {
		System.loadLibrary("adpcm");
	}

	public static native void decode(byte[] input, short[] output, int len, AdpcmState state);

	class AdpcmState {
		int valprev;
		int index;
	}
}
