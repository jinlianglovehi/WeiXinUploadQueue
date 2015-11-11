package cn.ihealthbaby.weitaixinpro.runablequeue;

import android.content.Context;
import android.util.Log;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by jinliang on 15/11/10.
 */
public class FileQueue extends Thread {

    private PriorityBlockingQueue<Runnable> queue;
    private ThreadPoolExecutor executor;

    private int CORE_POOL_SIZE = 3;//核心的执行线程
    private int MAX_MUM_POOL_SIZE = 10;
    private long KEEP_ALIVE_TIME = 30L;


    public FileQueue(Context contex) {
        queue = new PriorityBlockingQueue<>(10,new Comparators());
        executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAX_MUM_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS, queue
        );
    }



    /**
     * 比较器
     */

    @SuppressWarnings("unchecked")
    class Comparators implements Comparator {
        public int compare(Object arg0, Object arg1) {
            int val1 = ((SimpleTask)arg0).getPriority();
            int val2 = ((SimpleTask)arg1).getPriority();
            return val1 < val2 ? 0 : 1;
        }
    }
    @Override
    public void run() {
        super.run();
        Log.i("--队列运行中--","--队列运行中--");
        while (true) {
            if (!queue.isEmpty()) {
                Runnable runnable = queue.poll();
                if (runnable != null) {
                    executor.submit(runnable);
                }
            }
        }
    }

    /**
     * 添加task 任务
     *
     * @param task
     */
    public void addTask(Runnable task) {
        Log.i("addTask","---addTask");
        queue.offer(task);
    }

    /**
     * 移除task 任务
     */
    public void removeTask(Runnable task) {
        queue.remove(task);
    }


}
