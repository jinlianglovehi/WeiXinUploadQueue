package cn.ihealthbaby.weitaixinpro.filequeue;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;

import java.io.File;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.UploadModel;
import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;
import cn.ihealthbaby.weitaixin.library.data.database.dao.RecordBusinessDao;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.UploadUtil;

/**
 * Created by jinliang on 15/11/9.
 */
public class UploadFileUtils implements MulFileUploadInterface {

    private static final String TAG = UploadFileUtils.class.getSimpleName();
    /**
     * 一次任务执行所需的参数
     * userId 和 localRecordId
     */
    private int THREAD_SUM = 1;
    private int THREAD_TOTAL = 10;

    private long KEEP_ALIVE_TIME = 30L;

    private Context context;

    private static volatile int taskSuccessSum = 0;
    private static volatile int taskFailSum = 0;

    //private BlockingQueue<Runnable> workQueue;
    private PriorityBlockingQueue<Runnable> workQueue;
    private ThreadPoolExecutor threadPoolExecutor;


    public UploadFileUtils(Context context) {
        this.context = context;
        workQueue = new PriorityBlockingQueue<>(10,new Comparators());//优先级队列
        //workQueue = new LinkedBlockingQueue<>();
        threadPoolExecutor = new ThreadPoolExecutor(THREAD_SUM, THREAD_TOTAL, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, workQueue);
    }

    public void add(FileModel model,int priority) {

        Log.i(TAG, "--队列");
        ExecuteUploadThread thread = new ExecuteUploadThread(model.getUserId(), model.getLocalRecordId(),1);
        thread.setTag("" + model.getLocalRecordId());
        //设置可执行操作
        thread.setExecuteState(true);
        workQueue.add(thread);
    }


    public void remove(FileModel model) {
        // 设置标签的对象取消的标签对象
        // workQueue.take()
        for (Runnable currentRun : workQueue) {
            //获取实体对象：
            ExecuteUploadThread currentThread = (ExecuteUploadThread) currentRun;
            if (currentThread.getTag() == model.getTag()) {
                workQueue.remove(currentThread);
            }
        }
    }

    @Override
    public void add(Runnable runnable) {
        workQueue.add(runnable);
    }

    @Override
    public void remove(Runnable runnable) {
        workQueue.remove(runnable);
    }

    @Override
    public void run() {
        while (true) {
            //Log.i(TAG,"--run-isEmpty---"+ workQueue.isEmpty());
            if (!workQueue.isEmpty()) {
               // Log.i(TAG, "--run----" + workQueue.isEmpty());
               //   ExecuteUploadThread executeThread = (ExecuteUploadThread) workQueue.poll();
                ExecuteUploadThread runnable = (ExecuteUploadThread) workQueue.poll();
//                if (executeThread != null) {
//                    threadPoolExecutor.execute(executeThread);
//                }
                if(runnable!=null){
                    threadPoolExecutor.execute(runnable);
                }
            }
        }
    }

    @Override
    public void stop() {
        //startQueue = false;
        threadPoolExecutor.shutdown();
    }


    @Override
    public void setCancel(String localRecordId, Object tag) {
        for (Runnable currentRun : workQueue) {
            ExecuteUploadThread currentThread = (ExecuteUploadThread) currentRun;
            if (currentThread.getTag() == tag) {
                currentThread.setExecuteState(false);
                break;
            }
        }
    }

    /**
     * 单任务的执行
     */


    private class ExecuteUploadThread implements Runnable {
        private long userId;
        private String localRecordId;

        private boolean executeState; //false停止执行， true可以执行

        private Object tag;// 设置唯一的标签

        public Object getTag() {
            return tag;
        }

        public void setTag(Object tag) {
            this.tag = tag;
        }

        public boolean isExecuteState() {
            return executeState;
        }

        public void setExecuteState(boolean executeState) {
            this.executeState = executeState;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        /**
         * 七牛上传的参数
         *
         * @param userId
         * @param localRecordId
         */
        private ApiManager apiManager;
        private UpProgressHandler upProgressHandler;
        private String token;
        private String key;
        // public String localRecordId;
        //  public long userId;
        private UploadManager uploadManager;
        private UpCompletionHandler upCompletionHandler;
        private UploadOptions options;
        private int priority; // 设置的优先级

        public ExecuteUploadThread(long userId, String localRecordId, int priority) {
            this.userId = userId;
            this.localRecordId = localRecordId;
            this.priority =priority;
            initHandler(localRecordId);

        }



        @Override
        public void run() {
            Log.i(TAG, "---线程执行----优先级:"+this.priority);
        }


        private void initHandler(final String localRecordId) {
            // 七牛上传的回调函数
            upCompletionHandler = new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    if (info.statusCode == 200) {
                        String path = info.path;
                        if (!TextUtils.isEmpty(path)) {
                            Record record = null;
                            RecordBusinessDao recordBusinessDao = RecordBusinessDao.getInstance(context);
                            try {
                                record = recordBusinessDao.queryByLocalRecordId(localRecordId);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            record.setSoundUrl(path);
                            try {
                                recordBusinessDao.update(record);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //TODO 上传本地  通过业务通知用户

                        }

                    } else {

                    }
                }
            };
            upProgressHandler = new UpProgressHandler() {
                @Override
                public void progress(String key, double percent) {

                }
            };
            uploadManager = new UploadManager(UploadUtil.config());
            UpCancellationSignal UpCancellationSignal = new UpCancellationSignal() {
                @Override
                public boolean isCancelled() {
                    return false;
                }
            };
            options = new UploadOptions(null, Constants.MIME_TYPE_WAV, true, upProgressHandler, UpCancellationSignal);
        }

        /**
         * 请求上传key 和 上传token
         */
        private void upload(Context context, Long userId, final File file) {
            apiManager.hClientAccountApi.getUploadToken(
                    userId, 1, new DefaultCallback<UploadModel>(
                            context, new AbstractBusiness<UploadModel>() {
                        @Override
                        public void handleData(UploadModel data) {
                            LogUtil.d(TAG, data.toString());
                            key = data.getKey();
                            token = data.getToken();
                            uploadManager.put(file, key, token, upCompletionHandler, options);
                        }

                        @Override
                        public void handleAllFailure(Context context) {
                            super.handleAllFailure(context);
                            //TODO 获取七牛上传的Token 失败
                            taskFailSum = taskFailSum + 1;

                        }
                    }
                    ), TAG
            );
        }
//
//        @Override
//        public int compareTo(ExecuteUploadThread another) {
//            return this.priority > another.priority ? 1
//                    : this.priority < another.priority ? -1 : 0;
//        }
    }


    @SuppressWarnings("unchecked")
    class Comparators implements Comparator {
        public int compare(Object arg0, Object arg1) {
            int val1 = ((ExecuteUploadThread)arg0).getPriority();
            int val2 = ((ExecuteUploadThread)arg1).getPriority();
            return val1 < val2 ? 0 : 1;
        }
    }

    /**
     *    static final AtomicLong seq = new AtomicLong(0);
     *   final long seqNum;
     *   final E entry;
     *   public FIFOEntry(E entry) {
     *     seqNum = seq.getAndIncrement();
     *     this.entry = entry;
     *   }
     *   public E getEntry() { return entry; }
     *   public int compareTo(FIFOEntry<E> other) {
     *     int res = entry.compareTo(other.entry);
     *     if (res == 0 && other.entry != this.entry)
     *       res = (seqNum < other.seqNum ? -1 : 1);
     *     return res;
     *   }
     */
}
