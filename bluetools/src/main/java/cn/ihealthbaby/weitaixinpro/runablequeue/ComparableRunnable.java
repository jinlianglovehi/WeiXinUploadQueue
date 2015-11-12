package cn.ihealthbaby.weitaixinpro.runablequeue;
/**
 * Created by jinliang on 15/11/11.
 */
public abstract class ComparableRunnable implements Runnable, Comparable<ComparableRunnable> {
    public static final int DEFAULT_PRIORITY = 0;
    /**
     * 优先级
     */
    protected int priority = DEFAULT_PRIORITY;
    /**
     * 任务标签
     */
    protected Object tag;
    private boolean isCancle;
    /**
     * 不含标签任务
     */
    public ComparableRunnable() {
    }
    public ComparableRunnable(int priority) {
        this.priority = priority;
    }
    public int getPriority() {
        return priority;
    }
    /**
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
    /**
     * 数值越大,优先级越高
     *
     * @param another
     * @return
     */
    @Override
    public int compareTo(ComparableRunnable another) {
        return another.getPriority() - getPriority();
    }
}

