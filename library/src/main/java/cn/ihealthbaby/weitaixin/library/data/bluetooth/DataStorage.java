package cn.ihealthbaby.weitaixin.library.data.bluetooth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.ihealthbaby.weitaixin.library.data.bluetooth.data.FHRPackage;

/**
 * Created by liuhongjian on 15/8/12 14:29.
 */
public class DataStorage {
	public static final List<FHRPackage> fhrPackages = Collections.synchronizedList(new ArrayList<FHRPackage>());
}
