package cn.ihealthbaby.weitaixin.ui.record;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.model.AdviceItem;
import cn.ihealthbaby.client.model.PageData;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.adapter.MyAdviceItemAdapter;
import cn.ihealthbaby.weitaixin.base.BaseFragment;
import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;
import cn.ihealthbaby.weitaixin.library.data.database.dao.RecordBusinessDao;
import cn.ihealthbaby.weitaixin.library.data.model.MyAdviceItem;
import cn.ihealthbaby.weitaixin.library.data.net.AbstractBusiness;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.ui.MeMainFragmentActivity;
import cn.ihealthbaby.weitaixin.ui.monitor.GuardianStateActivity;
import cn.ihealthbaby.weitaixin.ui.widget.RoundImageView;


public class RecordFragment extends BaseFragment {

    private final static String TAG = "RecordFragment";

    @Nullable
    @Bind(R.id.back) RelativeLayout back;
    @Nullable
    @Bind(R.id.title_text) TextView title_text;
    @Nullable
    @Bind(R.id.function) TextView function;

    @Nullable
    @Bind(R.id.pullToRefresh) PullToRefreshListView pullToRefresh;
    @Nullable
    @Bind(R.id.ivWoHeadIcon) RoundImageView ivWoHeadIcon;
    @Nullable
    @Bind(R.id.tvWoHeadName) TextView tvWoHeadName;
    @Nullable
    @Bind(R.id.tvWoHeadDeliveryTime) TextView tvWoHeadDeliveryTime;
    @Nullable
    @Bind(R.id.tvUsedCount) TextView tvUsedCount;
    @Nullable
    @Bind(R.id.tvHospitalName) TextView tvHospitalName;
    @Bind(R.id.tv_min) TextView mTvMin;


    private MyAdviceItemAdapter adapter;
    private MeMainFragmentActivity context;

    private int pageIndex = 1, pageSize = 10;
    private View view;
    private RecordBusinessDao recordBusinessDao;
    private ArrayList<AdviceItem> mAdviceItems = new ArrayList<AdviceItem>();
    private String[] strStateFlag = new String[]{"问医生", "等待回复", "已回复", "需上传"};
    private boolean isMove = false;


    private int countNumber = 0;
    private static RecordFragment instance;

    public static RecordFragment getInstance() {
        if (instance == null) {
            instance = new RecordFragment();
        }
        return instance;
    }


    @OnClick(R.id.ivWoHeadIcon)
    public void WoHeadIcon() {
        Intent intent = new Intent(getActivity().getApplicationContext(), GuardianStateActivity.class);
        startActivity(intent);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_record, null);
        ButterKnife.bind(this, view);
        back.setVisibility(View.INVISIBLE);
        title_text.setText("记录");
        function.setText("编辑");

//      dataDao = DataDao.getInstance(getActivity().getApplicationContext());


        recordBusinessDao = RecordBusinessDao.getInstance(getActivity().getApplicationContext());
//        savelocal();


        context = (MeMainFragmentActivity) getActivity();
        initView();
        pullHeadDatas();
        pullDatas();

//      registerForContextMenu(pullToRefresh.getRefreshableView());
        pullToRefresh.getRefreshableView().setOnTouchListener(new View.OnTouchListener() {
            private View selectedView;
            private View tvAdviceStatused;
            private float oldXDis;
            private float oldX;
            private float oldY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (adapter.getSelectedView() == null) {
                    return false;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (adapter.selectedViewOld != null) {
                            adapter.cancel(adapter.selectedViewOld);
                        }
                        selectedView = adapter.getSelectedView();
//                        tvAdviceStatused = adapter.getAdviceStatused();
                        oldXDis = event.getX();
                        oldX = event.getX();
                        oldY = event.getY();
                        isMove = false;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        isMove = true;
                        float distanceX = event.getX() - oldX;
                        if (distanceX < 0) {
                            float distanceY = event.getY() - oldY;
                            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                                if (Math.abs(event.getX() - oldXDis) >= adapter.recordDelete.getWidth() && selectedView != null) {
                                    selectedView.setX(-adapter.recordDelete.getWidth());
                                } else {
                                    if (selectedView != null) {
                                        selectedView.setX(selectedView.getX() + distanceX);
                                    }
                                }
                            }
                        } else {
                            adapter.cancel();
                        }
                        oldX = event.getX();
                        oldY = event.getY();
                        break;

                    case MotionEvent.ACTION_UP:
                        float distanceX2 = event.getX() - oldXDis;
                        if (distanceX2 < 0) {
                            if (Math.abs(distanceX2) >= adapter.recordDelete.getWidth() / 2 && selectedView != null) {
                                selectedView.setX(-adapter.recordDelete.getWidth());
                            } else {
                                if (selectedView != null) {
                                    selectedView.setX(0);
                                }
                            }
                        } else {
                            adapter.cancel();
                        }
                        adapter.selectedViewOld = selectedView;
//                        adapter.tvAdviceStatusedOld = tvAdviceStatused;
                        break;
                }
                return false;
            }
        });

        return view;
    }

    private void savelocal() {

        for (int i = 0; i <5 ; i++) {
            Record record=new Record();
    //      record.setGestationalWeeks(Long.parseLong(1003333+""));
            record.setRecordStartTime(new Date());
            record.setDuration(Long.parseLong(i+1003333 + ""));
            record.setUploadState(Record.UPLOAD_STATE_LOCAL);
            record.setLocalRecordId("112313231"+i);
            record.setPurposeString("目的好啊");
            record.setFeelingString("心情好");

            record.setUserId(SPUtil.getUserID(getActivity().getApplicationContext()));
            record.setSerialNumber("DSDDSFSFSF");
            record.setUserName("13798988787"+i);

            try {
                long index = recordBusinessDao.insert(record);
                LogUtil.d("recordBusinessDao", "recordBusinessDao==> " + index);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.d("recordBusinessDao", "recordBusinessDao==> " + e.toString());
            }
        }

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, 1, Menu.NONE, "删除");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case 1:
                if (mAdviceItems.size() > 0) {
                    AdviceItem adviceItem = mAdviceItems.get((int) menuInfo.id);
                    if (adviceItem.getStatus() != 3) {
                        final CustomDialog customDialog = new CustomDialog();
                        Dialog dialog = customDialog.createDialog1(context, "正在删除...");
                        dialog.show();
                        ApiManager.getInstance().adviceApi.delete(adviceItem.getId(), new HttpClientAdapter.Callback<Void>() {
                            @Override
                            public void call(Result<Void> t) {
                                if (t.isSuccess()) {
                                    ToastUtil.show(context, "删除成功");
                                    adapter.datas.remove((int) menuInfo.id);
                                    adapter.notifyDataSetChanged();
                                    if (tvUsedCount != null) {
                                        tvUsedCount.setText((--countNumber) + "");
                                    }
                                } else {
                                    ToastUtil.show(context, t.getMsgMap() + "");
                                }
                                customDialog.dismiss();
                            }
                        }, context);
                    } else {
                        ToastUtil.show(context, "请先上传，才能删除");
                    }
                }
                break;
        }
        return super.onContextItemSelected(item);
    }


    private void pullHeadDatas() {
        User user = SPUtil.getUser(getActivity().getApplicationContext());
        if (SPUtil.isLogin(getActivity().getApplicationContext()) && user != null) {
            ImageLoader.getInstance().displayImage(user.getHeadPic(), ivWoHeadIcon, setDisplayImageOptions());
            tvWoHeadName.setText(user.getName());
            tvWoHeadDeliveryTime.setText(DateTimeTool.getGestationalWeeks(user.getDeliveryTime()));
            if (user.getServiceInfo() != null) {
                tvHospitalName.setText("建档: " + user.getServiceInfo().getHospitalName());
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void initView() {
        adapter = new MyAdviceItemAdapter(context, null, tvUsedCount);
        pullToRefresh.setAdapter(adapter);
        pullToRefresh.setMode(PullToRefreshBase.Mode.BOTH);
//      pullToRefresh.setScrollingWhileRefreshingEnabled(false);
        init();

        pullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) { //下拉刷新
                pageIndex = 1;
                pullFirstData(null);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) { //上拉加载更多
                ApiManager.getInstance().adviceApi.getAdviceItems((++pageIndex), pageSize, new HttpClientAdapter.Callback<PageData<AdviceItem>>() {
                    @Override
                    public void call(Result<PageData<AdviceItem>> t) {
                        if (t.isSuccess()) {
                            PageData<AdviceItem> data = t.getData();
                            ArrayList<AdviceItem> dataList = (ArrayList<AdviceItem>) data.getValue();
                            LogUtil.d("PageData", "ArrayList: " + dataList.size());

                            if (dataList.size() > 0) {
                                countNumber = data.getCount();
                                tvUsedCount.setText(countNumber + "");
                                adapter.addDatas(dataList);
                                adapter.notifyDataSetChanged();
                                mAdviceItems = adapter.datas;
                            } else {
                                ToastUtil.show(context, "没有更多数据了~~~");
                                pageIndex--;
                            }
                        } else {
                            ToastUtil.show(context, t.getMsgMap() + "");
                            pageIndex--;
                        }
                        if (pullToRefresh != null) {
                            pullToRefresh.onRefreshComplete();
                        }
                    }
                }, getRequestTag());
            }
        });

        pullToRefresh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (!isMove) {
                    //1提交但为咨询  2咨询未回复  3咨询已回复  4咨询已删除
                    AdviceItem adviceItem = (AdviceItem) adapter.getItem(position - 1);
                    int status = adviceItem.getStatus();

                    Intent intent = new Intent(getActivity().getApplicationContext(), RecordPlayActivity.class);
                    intent.putExtra("strStateFlag", strStateFlag[status]);
                    startActivity(intent);
                }
            }
        });

    }

    private void pullDatas() {
        CustomDialog customDialog = new CustomDialog();
        Dialog dialog = customDialog.createDialog1(context, "从数据库中加载...");
        dialog.show();

        //从缓存数据库中展示数据列表
        pullFirstData(customDialog);
    }


    public void pullFirstData(final CustomDialog customDialogTwo) {
        ApiManager.getInstance().adviceApi.getAdviceItems(1,pageSize,new DefaultCallback<PageData<AdviceItem>>(getActivity(), new AbstractBusiness<PageData<AdviceItem>>() {
            @Override
            public void handleData(PageData<AdviceItem> data) throws Exception {
                final ArrayList<AdviceItem> dataList = (ArrayList<AdviceItem>) data.getValue();

                ArrayList<Record> records = null;
                try {
                    //获取本地记录
                    records = (ArrayList<Record>) recordBusinessDao.queryUserRecord(SPUtil.getUserID(getActivity().getApplicationContext()), Record.UPLOAD_STATE_LOCAL, Record.UPLOAD_STATE_UPLOADING);
                    LogUtil.d("recordBusinessDao", records.size() + " =recordBusinessDao= " + records);
                    //把本地记录 转换成云端 记录集合类型
                    dataList.addAll(switchList(records));
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d("recordBusinessDao", "recordBusinessDao= " + e.toString());
                }


                if (dataList != null && dataList.size() > 0) {
                    //
                    adapter.datas.clear();
                    adapter.setDatas(dataList);
                    adapter.notifyDataSetChanged();
                    mAdviceItems = adapter.datas;
                } else {
                    if (records != null && records.size() > 0) {
                        adapter.setDatas(switchList(records));
                        adapter.notifyDataSetChanged();
                        mAdviceItems = adapter.datas;
                        countNumber = adapter.datas.size();
                    } else {
                        ToastUtil.show(getActivity().getApplicationContext(), "没有数据");
                        countNumber = 0;
                    }
                    if (tvUsedCount != null) {
                        tvUsedCount.setText(countNumber + "");
                    }
                }
                if (pullToRefresh != null) {
                    pullToRefresh.onRefreshComplete();
                }
                if (customDialogTwo != null) {
                    customDialogTwo.dismiss();
                }
            }
        }), getRequestTag());

    }


    /**
     * 把本地记录 转换成云端 记录集合类型，然后存储到云端集合中
     */
    public ArrayList<AdviceItem> switchList(ArrayList<Record> records) {
        if (records == null) {
            records = new ArrayList<Record>();
        }
        ArrayList<AdviceItem> adviceItems = new ArrayList<AdviceItem>();
        for (int i = 0; i < records.size(); i++) {
            Record record = records.get(i);
            AdviceItem adviceItem = new AdviceItem();

            adviceItem.setId(record.getId());
            // TODO: 2015/9/18 孕周
            adviceItem.setGestationalWeeks("");
            adviceItem.setTestTime(record.getRecordStartTime());
            adviceItem.setTestTimeLong((int) (record.getDuration() / 1000));
            if (record.getUploadState()== Record.UPLOAD_STATE_LOCAL||record.getUploadState()== Record.UPLOAD_STATE_UPLOADING) {
                adviceItem.setStatus(Record.UPLOAD_STATE_CLOUD);
            }
            adviceItem.setClientId(record.getLocalRecordId());
            adviceItem.setFeeling(record.getFeelingString());
            adviceItem.setAskPurpose(record.getPurposeString());

            adviceItems.add(adviceItem);
        }
        return adviceItems;
    }




    private void init() {
        ILoadingLayout startLabels = pullToRefresh.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在载入...");// 刷新时
        startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = pullToRefresh.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉刷新...");// 刚下拉时，显示的提示
        endLabels.setRefreshingLabel("正在载入...");// 刷新时
        endLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

        // 设置下拉刷新文本
        pullToRefresh.getLoadingLayoutProxy(false, true).setPullLabel("上拉刷新...");
        pullToRefresh.getLoadingLayoutProxy(false, true).setReleaseLabel("放开刷新...");
        pullToRefresh.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
        // 设置上拉刷新文本
        pullToRefresh.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新...");
        pullToRefresh.getLoadingLayoutProxy(true, false).setReleaseLabel("放开刷新...");
        pullToRefresh.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在加载...");
    }

    private final int requestCoded = 100;
    private final int resultCoded = 200;
    private final int STATE = 1;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d("resu++", requestCode + " resu++ " + resultCode);
        if (requestCode == requestCoded) {
            if (resultCode == Activity.RESULT_OK) {
                LogUtil.d("resultCoded", "resultCoded " + resultCode);
                if (data != null) {
                    int positionExtra = data.getIntExtra("positionExtra", -1);
                    if (positionExtra != -1) {
                        adapter.datas.get(positionExtra).setStatus(STATE);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    public DisplayImageOptions setDisplayImageOptions() {
        DisplayImageOptions options = null;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.button_monitor_helper)
                .showImageForEmptyUri(R.drawable.button_monitor_helper)
                .showImageOnFail(R.drawable.button_monitor_helper)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer())
//				.displayer(new RoundedBitmapDisplayer(5))
                .build();
        return options;
    }


}




