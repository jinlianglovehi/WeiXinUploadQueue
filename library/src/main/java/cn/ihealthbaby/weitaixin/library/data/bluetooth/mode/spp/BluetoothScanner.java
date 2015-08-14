package cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp;

/**
 * 蓝牙搜索接口
 */
public interface BluetoothScanner {
	/**
	 * 开启蓝牙
	 */
	public void enable();

	/**
	 * 查询蓝牙开启状态
	 */
	public boolean isEnable();

	/**
	 * 搜索设备
	 */
	public void discovery();

	/**
	 * 停止搜索
	 */
	public void cancleDiscovery();

	/**
	 * 关闭蓝牙
	 */
	public void disable();

	/**
	 * 是否正在搜索
	 *
	 * @return
	 */
	public boolean isDiscovering();
}
