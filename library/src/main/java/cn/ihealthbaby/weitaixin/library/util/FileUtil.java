package cn.ihealthbaby.weitaixin.library.util;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by liuhongjian on 15/9/10 10:28.
 */
public class FileUtil {
	/**
	 * @param context
	 * @return
	 */
	public static File getVoiceDir(Context context) {
		return context.getCacheDir();
	}

	/**
	 * 生成文件并支持追加数据。
	 *
	 * @param path
	 * @param name
	 * @return
	 */
	public static boolean generateFile(String path, String name, byte[] bytes) {
		boolean result = true;
		File file = new File(path, name);
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file, true);
			fileOutputStream.write(bytes);
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			result = false;
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	/**
	 * int数组转成byte数组。
	 *
	 * @param ints
	 * @return
	 */
	private static byte[] intForByte(int[] ints) {
		int size = ints.length;
		byte[] shorts = new byte[size];
		for (int i = 0; i < size; i++) {
			shorts[i] = (byte) ints[i];
		}
		return shorts;
	}

	/**
	 * 添加文件头。
	 *
	 * @param path
	 * @param name
	 * @return
	 */
	public static boolean addFileHead(File file) {
		int[] ints = null;
		try {
			ints = ByteUtil.WriteWaveFileHeader(file.length(), 4000, 1, 8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return skipByte(0, intForByte(ints), file);
	}

	/**
	 * 跳过字节并插入字节。
	 *
	 * @param skip 跳过多少过字节进行插入数据
	 * @param str  要插入的字符串
	 * @param file 文件
	 */
	private static boolean skipByte(long skip, byte[] bytes, File file) {
		boolean result = true;
		try {
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			if (skip < 0 || skip > raf.length()) {
//				System.out.println("跳过字节数无效");
				result = false;
			}
			// raf.setLength(raf.length() + bytes.length);
			// for (long i = raf.length() - 1; i > bytes.length + skip - 1; i--)
			// {
			// raf.seek(i - bytes.length);
			// byte temp = raf.readByte();
			// raf.seek(i);
			// raf.writeByte(temp);
			// }
			raf.seek(skip);
			raf.write(bytes);
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}
}
