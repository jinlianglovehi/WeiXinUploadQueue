package cn.ihealthbaby.weitaixinpro.filequeue;

import java.util.concurrent.ExecutionException;

/**
 * Created by jinliang on 15/11/9.
 * 多文件上传的接口
 */
public interface MulFileUploadInterface {



    /**
     * 文件的操作
     * @param model
     */
   // void add(FileModel model);

    /**
     * 移除一个Task任务
     * @param model
     */
   // void remove(FileModel model);
    void add(Runnable runnable);

    void remove(Runnable runnable);
    /**
     * 执行任务
     * @param model
     */
    //void execute(FileModel model) throws ExecutionException, InterruptedException;

    /**
     *  开始执行队列
     */
    void run() throws ExecutionException, InterruptedException;

    /**
     * 结束执行队列
     */
    void stop();

   // void setTag(boolean isExecutor,String tag);

    void setCancel(String localRecordId,Object tag);

}

