package cn.ihealthbaby.weitaixin.library.codec;

/**
 * Created by liuhongjian on 15/8/13 16:23.
 */
public class Adpcm0 {
	private static int[] index_adjust = {-1, -1, -1, -1, 2, 4, 6, 8, -1, -1, -1, -1, 2, 4, 6, 8};
	private static int[] step_table = {7, 8, 9, 10, 11, 12, 13, 14, 16, 17, 19, 21, 23, 25, 28, 31, 34, 37, 41, 45, 50, 55, 60, 66, 73, 80, 88, 97, 107, 118, 130, 143, 157, 173, 190, 209, 230, 253, 279, 307, 337, 371, 408, 449, 494, 544, 598, 658, 724, 796, 876, 963, 1060, 1166, 1282, 1411, 1552, 1707, 1878, 2066, 2272, 2499, 2749, 3024, 3327, 3660, 4026, 4428, 4871, 5358, 5894, 6484, 7132, 7845, 8630, 9493, 10442, 11487, 12635, 13899, 15289, 16818, 18500, 20350, 22385, 24623, 27086, 29794, 32767};
	private static int adpcm_index;
	private static int adpcm_value;
	private static int DTA_PER_PACKAGE = 100;

	public static short[] anylyseData(byte[] in8_buf, double ratio) {
		int step;
		int vpdiff;
		int codeData;
		short[] out10_buf = new short[200];
		int i, j;
		int sign, delta;
		int index = 0;
		short dat = 0;
		step = step_table[adpcm_index];
		for (i = 0; i < DTA_PER_PACKAGE; i++)//每个包中有100个字节的声音数据,解压缩之后有200个
		{
			codeData = (int) in8_buf[i];           // 得到下一个数据
			for (j = 0; j < 2; j++)//将一个字节的数据中的高4位和低4位分别转换成两个字节数据
			{
				if (j == 0)
					delta = (codeData >> 4) & 0xf;
				else
					delta = (codeData >> 0) & 0xf;
				adpcm_index += index_adjust[delta];
				if (adpcm_index < 0) adpcm_index = 0;
				if (adpcm_index > 88) adpcm_index = 88;
				sign = delta & 8;
				delta = delta & 7;
				vpdiff = step >> 3;
				if ((delta & 4) != 0) vpdiff += step;
				if ((delta & 2) != 0) vpdiff += step >> 1;
				if ((delta & 1) != 0) vpdiff += step >> 2;
				if (sign != 0) {
					adpcm_value -= vpdiff;
					if (adpcm_value < -32768)
						adpcm_value = -32768;
				} else {
					adpcm_value += vpdiff;
					if (adpcm_value > 32767)
						adpcm_value = 32767;
				}
				step = step_table[adpcm_index];
				dat = (short) (adpcm_value + 0x200);
				if (dat > 0x3ff)
					dat = 0x3ff;
				if (dat < 0)
					dat = 0;
				dat = (short) (dat >> 2);
				dat *= ratio;
				out10_buf[index++] = dat;
			}
		}
		return out10_buf;
	}
}
