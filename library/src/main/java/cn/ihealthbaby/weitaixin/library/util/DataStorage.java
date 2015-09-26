package cn.ihealthbaby.weitaixin.library.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.ihealthbaby.weitaixin.library.data.bluetooth.data.FHRPackage;

/**
 * Created by liuhongjian on 15/8/12 14:29.
 */
public class DataStorage {
	public static final List<Integer> fhrs = Collections.synchronizedList(new ArrayList<Integer>());
	public static final FHRPackage fhrPackagePool = new FHRPackage();
	public static final   FHRPackage fhrPackage = new FHRPackage();
	public static final List<Integer> fms = Collections.synchronizedList(new ArrayList<Integer>());
	public static final List<Integer> doctors = Collections.synchronizedList(new ArrayList<Integer>());
}
