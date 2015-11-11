package cn.ihealthbaby.weitaixinpro.runablequeue;

import android.util.Log;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by jinliang on 15/11/10.
 */
public class FileQueue implements UploadQueueI {

    private static final String TAG = FileQueue.class.getSimpleName();
    private PriorityBlockingQueue<Runnable> queue;
    private ThreadPoolExecutor executor;

    private int CORE_POOL_SIZE = 1;//核心的执行线程
    private int MAX_MUM_POOL_SIZE = 10;
    private long KEEP_ALIVE_TIME = 30L;

    /**
     * false 任务不循环， true 任务循环
     */

    protected boolean OPEN_STATUS = false;


    public FileQueue() {
        /**
         * 有序队列需要实现比较器
         */
        //queue = new PriorityBlockingQueue<>();
         queue = new PriorityBlockingQueue<>(10, com);
        //queue = new PriorityBlockingQueue<>();

        executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAX_MUM_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS, queue
        );
    }

    /**
     * @param CORE_POOL_SIZE 执行的核心线程数
     */
    public FileQueue(int CORE_POOL_SIZE) {
        queue = new PriorityBlockingQueue<>();
        //queue = new PriorityBlockingQueue<>(10, new Comparators());
        this.CORE_POOL_SIZE = CORE_POOL_SIZE;
        executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAX_MUM_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS, queue
        );

    }

    /**
     * 比较器
     */

    private Comparator com  = new Comparator<SimpleTask>() {
        @Override
        public int compare(SimpleTask arg0, SimpleTask arg1) {
           // return 0;

            int val1 =  arg0.getPriority();
            int val2 = arg1.getPriority();
            return val1 < val2 ? 0 : 1;
        }
    };
    @SuppressWarnings("unchecked")
 static    class Comparators implements Comparator<SimpleTask> {
        public int compare(SimpleTask arg0, SimpleTask arg1) {


            int val1 = ((SimpleTask) arg0).getPriority();
            int val2 = ((SimpleTask) arg1).getPriority();
            return val1 < val2 ? 0 : 1;

        }

    }

    /**
     * 开启队列的执行任务
     */
    @Override
    public void start() {
        Log.i(TAG, "开启队列");
        OPEN_STATUS = true;
        while (OPEN_STATUS) {
            if (!queue.isEmpty()) {
                Runnable runnable = queue.poll();
                if (runnable != null) {


                    executor.submit(runnable);


//                    try {
//                       // executor.submit(runnable).get();
//                    } catch (InterruptedException e) {
//                        Log.i(TAG, "终止的任务:" + ((SimpleTask) runnable).getTag());
//                        //e.printStackTrace();
//                    } catch (ExecutionException e) {
//                        //e.printStackTrace();
//                        Log.i(TAG, "终止的任务:" + ((SimpleTask) runnable).getTag());
//                    }
                }
            }
        }
    }

    /**
     * 停止队列的任务执行
     */
    @Override
    public void stop() {
        OPEN_STATUS = false;
        queue.clear();
        executor.shutdown();
    }

    /**
     * 添加task 任务
     *
     * @param task
     */
    @Override
    public void addTask(Runnable task) {
        queue.offer(task);
    }

    /**
     * 移除task 任务
     */
    @Override
    public void removeTask(Runnable task) {
        queue.remove(task);
    }

    @Override
    public void removeTaskByTag(Object tag) {
        for (Runnable task : queue) {
            if (((SimpleTask) task).getTag() == tag) {
                queue.remove(task);
            }
        }
    }

    /**
     * 首先启动当前的任务
     * 停止其他的任务
     *
     * @param task
     */
    @Override
    public void startTaskStopOther(Runnable task) {
        OPEN_STATUS = false;
        executor.submit(task);
    }

    /**
     * 开启其他任务
     */
    @Override
    public void openOtherTask() {
        OPEN_STATUS = true;
    }

}
