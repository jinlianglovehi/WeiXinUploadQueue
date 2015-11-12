package cn.ihealthbaby.weitaixinpro.runablequeue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * Created by jinliang on 15/11/10.
 */
public class FileQueue {
    private static final String TAG = FileQueue.class.getSimpleName();
    private static final int CORE_POOL_SIZE = 2;//核心的执行线程
    private static final int MAX_MUM_POOL_SIZE = 5;
    private static final long KEEP_ALIVE_TIME = 3000L;
    private ThreadPoolExecutor executor;
    public FileQueue() {
        BlockingQueue<Runnable> queue = new PriorityBlockingQueue<>();
        executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAX_MUM_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS, queue
        );
    }
    /**
     * 停止队列的任务执行
     */
    public void stop() {
        executor.shutdown();
    }
    /**
     * 添加task 任务
     *
     * @param task
     */
    public void add(Runnable task) {
        executor.execute(task);
    }
//    public void remove(Runnable task) {
//        executor.remove(task);
//    }
}
