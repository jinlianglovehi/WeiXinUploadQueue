package cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.listener;


import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;

/**
 * Created by liuhongjian on 15/7/900:41.
 */
public class SimpleCallback implements HttpClientAdapter.Callback {
    @Override
    public void call(Result result) {
        int status = result.getStatus();
        Object data = result.getData();
        String msg = result.getMsg();
        boolean success = result.isSuccess();
    }
}
