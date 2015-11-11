package cn.ihealthbaby.weitaixinpro.runablequeue;

/**
 * Created by jinliang on 15/11/11.
 */
public abstract class SimpleTask implements Runnable {
    /**
     * 优先级
     */
    protected int priority=0;
    /**
     * 运行状态的定义
     */
    public static final int TASK_WAIT_ING = 0X01;
    public static final int TASK_RUNNING = 0x02;
    public static final int TASK_STOP = 0X03;

    /**
     * 任务运行状态
     */
    protected int runState=TASK_WAIT_ING;// 运行的状态

    /**
     * 任务标签
     */
    protected Object tag;

    public SimpleTask() {
    }

    @Override
    public void run() {
        runState = TASK_RUNNING;
        runTask();
    }

    /**
     * 添加执行的任务
     */
    protected abstract void runTask();

    /**
     * 返回的结果
     * @param progressBar
     * @param resultCode
     * @param result
     */
    protected abstract void getResult(int progressBar, int resultCode, Object result);

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public SimpleTask(Object tag) {
        this.tag = tag;
    }
}
