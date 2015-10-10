package cn.ihealthbaby.weitaixin.library.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.ihealthbaby.weitaixin.library.data.bluetooth.data.FHRPackage;

/**
 * Created by liuhongjian on 15/8/12 14:29.
 */
public class DataStorage {
	/**
	 * 胎心数据集合
	 */
	public static final List<Integer> fhrs = Collections.synchronizedList(new ArrayList<Integer>());
	/**
	 * 胎动数据集合
	 */
	public static final List<Integer> fms = Collections.synchronizedList(new ArrayList<Integer>());
	/**
	 * 医生干预的数据,指position
	 */
	public static final List<Integer> doctors = Collections.synchronizedList(new ArrayList<Integer>());
	/**
	 * 存储一个胎心包数据
	 */
	public static FHRPackage fhrPackage = new FHRPackage();
}
