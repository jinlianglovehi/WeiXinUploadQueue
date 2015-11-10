package cn.ihealthbaby.weitaixinpro.filequeue;

import android.content.Context;
import android.text.TextUtils;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.UploadModel;
import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;
import cn.ihealthbaby.weitaixin.library.data.database.dao.RecordBusinessDao;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.FileUtil;
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
    private int threadSum = 3;
    private static boolean startQueue = false;// 是否开启队列

    private static ThreadPoolExecutor executor;//线程池
    private BlockingQueue<ExecuteUploadThread> queue;//队列
    private Context context;
    //private SynchronousQueue<FileModel> synchronousQueue = new SynchronousQueue<>();

    private static volatile int taskSuccessSum = 0;
    private static volatile int taskFailSum = 0;

    private BlockingQueue<Runnable> workQueue;
    ThreadPoolExecutor threadPoolExecutor;

    private Map<String, Runnable> list;

    public UploadFileUtils(Context context) {
        this.context = context;
        workQueue = new PriorityBlockingQueue<>();
        threadPoolExecutor = new ThreadPoolExecutor(threadSum, 10,
                30L, TimeUnit.MILLISECONDS, workQueue);
        list = new Hashtable<>();

    }


    @Override
    public void add(FileModel model) {
        ExecuteUploadThread thread = new ExecuteUploadThread(model.getUserId(), model.getLocalRecordId());
        thread.setTag("" + model.getLocalRecordId());
        //设置可执行操作
        thread.setExecuteState(true);
        workQueue.offer(new ExecuteUploadThread(model.getUserId(), model.getLocalRecordId()));

    }



    @Override
    public void remove(FileModel model) {
        // 设置标签的对象取消的标签对象
        // workQueue.take()
        for(ExecuteUploadThread currentThread: queue){
            //获取实体对象：
            if(currentThread.getTag().equalsIgnoreCase(model.getLocalRecordId())){
                workQueue.remove(currentThread);
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            if (!workQueue.isEmpty()) {
                ExecuteUploadThread executeThread = (ExecuteUploadThread) workQueue.poll();
                if (executeThread.isExecuteState()) {
                    executor.execute(executeThread);
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
    public void setTag(boolean isExecutor,String tag) {

        for(ExecuteUploadThread currentThread: queue){
           if(currentThread.getTag().equalsIgnoreCase(tag)){
               currentThread.setExecuteState(isExecutor);
               break;
           }
        }

    }

    @Override
    public void setCancel(String localRecordId) {
        for(ExecuteUploadThread currentThread: queue){
            if(currentThread.getTag().equalsIgnoreCase(localRecordId)){
                currentThread.setExecuteState(false);
                break;
            }
        }
    }

    /**
     * 单任务的执行
     */
    private class ExecuteUploadThread extends Thread {
        private long userId;
        private String localRecordId;

        private boolean executeState; //false停止执行， true可以执行

        private String tag;// 设置唯一的标签

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public boolean isExecuteState() {
            return executeState;
        }

        public void setExecuteState(boolean executeState) {
            this.executeState = executeState;
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


        public ExecuteUploadThread(long userId, String localRecordId) {
            this.userId = userId;
            this.tag = localRecordId;
            this.localRecordId = localRecordId;
            initHandler(localRecordId);
        }

        @Override
        public void run() {
            super.run();
            File voiceFile = FileUtil.getVoiceFile(context, localRecordId);
            upload(context, userId, voiceFile);
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
                        }
                        //TODO 七牛上传文件成功
                        taskSuccessSum = taskSuccessSum + 1;
                    } else {
                        //TODO 七牛上传文件失败
                        taskFailSum = taskFailSum + 1;
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
            apiManager.hClientAccountApi.getUploadToken(userId, 1, new DefaultCallback<UploadModel>(context, new AbstractBusiness<UploadModel>() {
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
            }), TAG);
        }

    }


}
