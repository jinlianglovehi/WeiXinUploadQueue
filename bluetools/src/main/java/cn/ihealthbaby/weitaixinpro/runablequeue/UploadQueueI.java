package cn.ihealthbaby.weitaixinpro.runablequeue;

/**
 * Created by jinliang on 15/11/11.
 * 文件上传的接口
 */
public interface UploadQueueI {

    /**
     * 队列的开启
     */
    void start();

    /**
     * 队列的结束
     */
    void stop();

    /**
     * 添加队列
     * @param task
     */
    void addTask(Runnable task);

    /**
     * 移除队列
     *
     * @param task
     */
    void removeTask(Runnable task);

    /**
     * 更具标签移除队列
     *
     * @param tag
     */
    void removeTaskByTag(Object tag);


    /**
     * 开始一个任务停止其他的任务
     * @param task
     */
    void startTaskStopOther(Runnable task) ;

    /**
     * 打开其他的任务
     */
    void openOtherTask();


}
