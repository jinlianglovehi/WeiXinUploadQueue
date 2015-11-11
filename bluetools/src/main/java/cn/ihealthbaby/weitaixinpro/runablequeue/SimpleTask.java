package cn.ihealthbaby.weitaixinpro.runablequeue;

import android.util.Log;

/**
 * Created by jinliang on 15/11/11.
 */
public abstract class SimpleTask implements Runnable, SimpleTaskI, Comparable<SimpleTask> {
    private static final String TAG = SimpleTask.class.getSimpleName();

    /**
     * 优先级
     */
    protected int priority = 0;
    /**
     * 运行状态的定义
     */
    public static final int TASK_WAIT_ING = 0X01;
    public static final int TASK_RUNNING = 0x02;
    public static final int TASK_STOP = 0X03;

    /**
     * 任务运行状态
     */
    protected int runState = TASK_WAIT_ING;// 运行的状态

    /**
     * 任务标签
     */
    protected Object tag;

    /**
     * 不含标签任务
     */
    public SimpleTask() {
    }

    public SimpleTask(int priority,Object tag) {
        this.priority = priority;
        this.tag = tag;
    }

    @Override
    public void run() {
        Log.i(TAG, "执行任务tag:" + tag.toString()+"    priority:"+this.priority);
        start();
    }




    public int getPriority() {
        return priority;
    }

    /**
     *
     * @param priority
     */
    public void setPriority(int priority) {
        this.priority = priority;


    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }


    @Override
    public int compareTo(SimpleTask another) {
        int left = this.getPriority();
        int right = another.getPriority();
        return left -right;
        // High-priority requests are "lesser" so they are sorted to the front.
        // Equal priorities are sorted by sequence number to provide FIFO ordering.
       // return left < right ? 0 : 1;

    }
    @Override
    public abstract void start();

//    @Override
//    public  abstract void stop() ;


}
