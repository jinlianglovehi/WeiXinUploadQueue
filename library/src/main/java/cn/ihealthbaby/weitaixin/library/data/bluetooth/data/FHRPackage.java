package cn.ihealthbaby.weitaixin.library.data.bluetooth.data;

/**
 * Created by liuhongjian on 15/7/17 11:38.
 */
public class FHRPackage extends DataPackage {
	/**
	 * 胎心率1
	 */
	private int FHR1;
	/**
	 * 胎心率2,第一版没有
	 */
	private int FHR2;
	/**
	 *
	 */
	private int TOCO;
	/**
	 * 1为true,表示有自动胎动 AutoFatalMovement
	 */
	private boolean AFM;
	/**
	 * 信号强度,1-3(01,10,11),共三个状态
	 */
	private int signalStrength;
	/**
	 * 电池电量,0-4,共五个数值(000,001,010,011,100)
	 */
	private int batteryPower;

	public FHRPackage() {
		super();
	}

	public void setFHRPackage(FHRPackage fhrPackage) {
		this.AFM = fhrPackage.isAFM();
		this.FHR1 = fhrPackage.getFHR1();
		this.FHR2 = fhrPackage.getFHR2();
		this.signalStrength = fhrPackage.getSignalStrength();
		this.batteryPower = fhrPackage.getBatteryPower();
		this.time = fhrPackage.getTime();
		this.version = fhrPackage.getVersion();
	}

	public int getTOCO() {
		return TOCO;
	}

	public void setTOCO(int TOCO) {
		this.TOCO = TOCO;
	}

	public int getFHR1() {
		return FHR1;
	}

	public void setFHR1(int FHR1) {
		this.FHR1 = FHR1;
	}

	public int getFHR2() {
		return FHR2;
	}

	public void setFHR2(int FHR2) {
		this.FHR2 = FHR2;
	}

	public int getBatteryPower() {
		return batteryPower;
	}

	public void setBatteryPower(int batteryPower) {
		this.batteryPower = batteryPower;
	}

	public int getSignalStrength() {
		return signalStrength;
	}

	public void setSignalStrength(int signalStrength) {
		this.signalStrength = signalStrength;
	}

	public boolean isAFM() {
		return AFM;
	}

	public void setAFM(boolean AFM) {
		this.AFM = AFM;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer(super.toString());
		sb.append("FHRPackage{");
		sb.append("AFM=").append(AFM);
		sb.append(", FHR1=").append(FHR1);
		sb.append(", FHR2=").append(FHR2);
		sb.append(", TOCO=").append(TOCO);
		sb.append(", signalStrength=").append(signalStrength);
		sb.append(", batteryPower=").append(batteryPower);
		sb.append('}');
		return sb.toString();
	}

	public void recycle() {
		FHR1 = 0;
		FHR2 = 0;
		AFM = false;
		signalStrength = 0;
		batteryPower = 0;
		time = 0;
		version = "";
	}
}
