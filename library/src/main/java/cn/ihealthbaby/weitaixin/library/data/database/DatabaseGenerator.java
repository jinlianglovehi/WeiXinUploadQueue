package cn.ihealthbaby.weitaixin.library.data.database;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * Created by liuhongjian on 15/9/16 09:44.
 */
public class DatabaseGenerator {
	private static final String PACKAGE_NAME = "cn.ihealthbaby.weitaixin.library";
	private static final String RELATIVE_PATH = ".data.database.dao";
	private static final String PATH = PACKAGE_NAME + RELATIVE_PATH;
	private static final String PACKAGENAME_TEST = "cn.ihealthbaby.weitaixin.library.androidTest.data.test";
	private static final String PACKAGENAME_DAO = "cn.ihealthbaby.weitaixin.library.data.database.dao";

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Schema schema = new Schema(1, PATH);
//		schema.setDefaultJavaPackageTest(PACKAGENAME_TEST);
//		schema.setDefaultJavaPackageDao(PACKAGENAME_DAO);
		schema.enableKeepSectionsByDefault();
		schema.enableActiveEntitiesByDefault();
		Entity record = schema.addEntity("Record");
		record.addIdProperty().codeBeforeField("/**\n" + "\t * 自增id\n" + "\t */");
		record.addStringProperty("localRecordId").unique().notNull().codeBeforeField("/**\n" + "\t * 本地记录id,对应AdviceItem的jianceId\n" + "\t */");
		record.addLongProperty("userId").notNull().codeBeforeField("/**\n" + "\t * 用户id\n" + "\t */");
		record.addStringProperty("userName").notNull().codeBeforeField("/**\n" + "\t * 用户名\n" + "\t */");
		record.addStringProperty("serialNumber").notNull().codeBeforeField("/**\n" + "\t * 对应AdviceItem的serialNum\n" + "\t */");
		record.addIntProperty("uploadState").notNull().codeBeforeField("/**\n" + "\t * 上传状态\n" + "\t */");
		record.addDateProperty("recordStartTime").codeBeforeField("/**\n" + "\t * 监测开始时间,对应AdviceItem的testTime\n" + "\t */");
		record.addLongProperty("duration").codeBeforeField("/**\n" + "\t * 监测时长,对应AdviceItem的testTimeLong\n" + "\t */");
		record.addStringProperty("recordData").codeBeforeField("/**\n" + "\t * 监测记录的数据结构,JSON格式\n" + "\t */");
		record.addStringProperty("soundPath").codeBeforeField("/**\n" + "\t * 本地音频文件路径\n" + "\t */");
		record.addIntProperty("feelingId").codeBeforeField("/**\n" + "\t * 监护心情,对应AdviceItem的feelingId\n" + "\t */");
		record.addStringProperty("feelingString").codeBeforeField("/**\n" + "\t * 监护心情,对应AdviceItem的feeling\n" + "\t */");
		record.addIntProperty("purposeId").codeBeforeField("/**\n" + "\t * 监护目的,对应AdviceItem的puposeId\n" + "\t */");
		record.addStringProperty("purposeString").codeBeforeField("/**\n" + "\t * 监护目的,对应AdviceItem的pupose\n" + "\t */");
//		record.addLongProperty("cloudRecordId").codeBeforeField("/**\n" + "\t * 云端id,对应AdviceItem的id\n" + "\t */");
//		record.addStringProperty("soundUrl").codeBeforeField("/**\n" + "\t * 服务端音频文件路径对应AdviceItem的path\n" + "\t */");
//		record.addIntProperty("serviceStatus").codeBeforeField("/**\n" + "\t * 对应AdviceItem的status\n" + "\t */");
		DaoGenerator daoGenerator = new DaoGenerator();
		daoGenerator.generateAll(schema, "./library/src/main/java/");
	}
}
