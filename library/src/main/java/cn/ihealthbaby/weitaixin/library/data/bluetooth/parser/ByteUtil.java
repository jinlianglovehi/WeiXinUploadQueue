package cn.ihealthbaby.weitaixin.library.data.bluetooth.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ByteUtil {
	/**
	 * 往sd卡里写入wav文件。
	 * 
	 * @param bytes
	 */	
	private static int[] index_adjust = {-1, -1, -1, -1, 2, 4, 6, 8,-1, -1, -1, -1, 2, 4, 6, 8};
	private static int[] step_table = {7, 8, 9, 10, 11, 12, 13, 14, 16, 17, 19, 21, 23, 25, 28, 31, 34, 37, 41, 45, 50, 55, 60, 66, 73, 80, 88, 97, 107, 118, 130, 143, 157, 173, 190, 209, 230, 253, 279, 307, 337, 371, 408, 449, 494, 544, 598, 658, 724, 796, 876, 963, 1060, 1166, 1282, 1411, 1552, 1707, 1878, 2066, 2272, 2499, 2749, 3024, 3327, 3660, 4026, 4428, 4871, 5358, 5894, 6484, 7132, 7845, 8630, 9493, 10442, 11487, 12635, 13899, 15289, 16818, 18500, 20350, 22385, 24623, 27086, 29794, 32767 };
	private static int adpcm_index;
	private static int adpcm_value;

	private static int DTA_PER_PACKAGE = 100;
	
	public static int whiteWAV(int[] bytes, String path) {
		try {
			File file = new File(path);
			FileOutputStream fileOutputStream = new FileOutputStream(file, true);
			for (int i : bytes) {
				fileOutputStream.write(i);
			}
			fileOutputStream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bytes.length;
	}

	/**
	 * 把原始的五个字节数组以为生成新的数组。
	 * 
	 * @param b
	 * @return
	 */
	public static int[] analyseByte(int[] b) {
		int[] buf = new int[4];
		int index = 0;
		for (int i = 0; i < 4; i++) {
			int dat = b[4];
			dat = (dat >> ((3 - i) << 1)) & 0x03;
			dat = dat << 8;
			dat += b[i];
			buf[index++] = dat >> 2;
		}
		return buf;
	}

	/**
	 * 对一个音频包进行解析。
	 * 
	 * @param b
	 */
	public static int[] analysePackage(int[] b) {
		int[] bs = new int[5];
		int[] ys = new int[256];
		for (int i = 0; i < 64; i++) {
			int index = i * 5;
			bs[0] = b[index];
			bs[1] = b[index + 1];
			bs[2] = b[index + 2];
			bs[3] = b[index + 3];
			bs[4] = b[index + 4];
			System.arraycopy(analyseByte(bs), 0, ys, i * 4, 4);
		}
		return ys;
	}
	
	public static int[] anylyseData(int[] in8_buf, double ratio)
	{
		int step;
	    int vpdiff;
	    int codeData;
	    int[] p = in8_buf;//从第3个字节开始
	    int[] out10_buf = new int[200];
	    int i,j;
	    int sign,delta;
	    int index  = 0;
	    short dat = 0;
	    
	    step = step_table[adpcm_index];
	    for(i=0;i<DTA_PER_PACKAGE;i++)//每个包中有100个字节的声音数据,解压缩之后有200个
	    {
	        codeData = p[i];           // 得到下一个数据
	        for(j=0;j<2;j++)//将一个字节的数据中的高4位和低4位分别转换成两个字节数据
	        {
	            if(j==0)
	                delta = (codeData >> 4) & 0xf;
	            else
	                delta = (codeData >> 0) & 0xf;
	            
	            adpcm_index += index_adjust[delta];
	            if (adpcm_index<0) adpcm_index=0;
	            if (adpcm_index>88) adpcm_index=88;
	            
	            sign = delta & 8;
	            delta = delta & 7;
	            
	            vpdiff = step >> 3;
	            if ( (delta & 4) != 0 ) vpdiff += step;
	            if ( (delta & 2) != 0 ) vpdiff += step>>1;
	            if ( (delta & 1) != 0 ) vpdiff += step>>2;
	            
	            if (sign != 0)
	            {
	                adpcm_value -= vpdiff;
	                if ( adpcm_value < -32768 )
	                    adpcm_value = -32768;
	            }
	            else
	            {
	                adpcm_value += vpdiff;
	                if ( adpcm_value > 32767 )
	                    adpcm_value = 32767;
	            }
	            step = step_table[adpcm_index];
	            
	            dat = (short) (adpcm_value + 0x200);
	            if(dat > 0x3ff)
	                dat = 0x3ff;
	            if(dat < 0)
	                dat = 0;
	            dat = (short) (dat>>2);
	            dat *= ratio;
	            out10_buf[index++] = dat;
	        }
	    }

	    return out10_buf;
	}


	/**
	 * 对声音包进行校验。 
	 * 
	 * @param is
	 * @return
	 */
	public static boolean soundFormatCheck(int[] is) {
		int size = is.length;
		long count = 0;
		for (int i = 0; i < size - 1; i++) {
			count = count + is[i];
		}
		boolean result = count % 256 == is[size - 1] ? true : false;
		return result;
	}

	/**
	 * 生成wav文件头。
	 * 
	 * @param out
	 *            输出流
	 * @param totalAudioLen
	 *            音频文件的长度
	 * @param totalDataLen
	 *            文件的总字节数
	 * @param longSampleRate
	 *            采样率
	 * @param channels
	 *            声道
	 * @param byteRate
	 *            字节速率
	 * @throws IOException
	 */
	public static int[] WriteWaveFileHeader(long totalDataLen,
			long longSampleRate, int channels, long byteRate)
			throws IOException {
		int[] header = new int[44];
		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) ((totalDataLen + 36) & 0xff);
		header[5] = (byte) (((totalDataLen + 36) >> 8) & 0xff);
		header[6] = (byte) (((totalDataLen + 36) >> 16) & 0xff);
		header[7] = (byte) (((totalDataLen + 36) >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		// header[24] = (byte) (longSampleRate & 0xff);
		// header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		// header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		// header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		// header[28] = (byte) (byteRate & 0xff);
		// header[29] = (byte) ((byteRate >> 8) & 0xff);
		// header[30] = (byte) ((byteRate >> 16) & 0xff);
		// header[31] = (byte) ((byteRate >> 24) & 0xff);

		header[24] = 0xa0;
		header[25] = 0x0f;
		header[26] = 0;
		header[27] = 0;
		header[28] = 0xa0;
		header[29] = 0x0f;
		header[30] = 0;
		header[31] = 0;
		// header[32] = (byte) (2 * 16 / 8); // block align
		header[32] = 1; // block align
		header[33] = 0;
		header[34] = 8; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		// header[40] = (byte) (totalAudioLen & 0xff);
		// header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		// header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		// header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

		header[40] = (int) (totalDataLen & 0xff);
		header[41] = (int) ((totalDataLen >> 8) & 0xff);
		header[42] = (int) ((totalDataLen >> 16) & 0xff);
		header[43] = (int) ((totalDataLen >> 24) & 0xff);
		return header;
	}
}
