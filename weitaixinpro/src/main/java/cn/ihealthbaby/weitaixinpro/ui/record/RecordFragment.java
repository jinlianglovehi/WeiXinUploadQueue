package cn.ihealthbaby.weitaixinpro.ui.record;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.base.BaseFragment;
import cn.ihealthbaby.weitaixinpro.ui.adapter.RecordAdapter;

public class RecordFragment extends BaseFragment {

    static RecordFragment instance;
    @Bind(R.id.back)
    RelativeLayout mBack;
    @Bind(R.id.title_text)
    TextView mTitleText;
    @Bind(R.id.function)
    TextView mFunction;
    @Bind(R.id.pullToRefresh)
    PullToRefreshListView mPullToRefresh;
    private RecordAdapter mRecordAdapter;

    private boolean isMove = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mPullToRefresh.onRefreshComplete();
        }
    };


    public static RecordFragment getInstance() {
        if (instance == null) {
            instance = new RecordFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.record_fragment, null);
        ButterKnife.bind(this, view);
        mTitleText.setText("检测记录");
        mBack.setVisibility(View.INVISIBLE);
        initView();
        return view;
    }

    private void initView() {
        mRecordAdapter = new RecordAdapter(getActivity());
        //TODO  取出本地数据

        mPullToRefresh.setAdapter(mRecordAdapter);
        mPullToRefresh.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //TODO  取出本地数据
                mHandler.sendEmptyMessage(1);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                //TODO  取出本地数据 分页
//                mRecordAdapter.addData(data);
//                mHandler.sendEmptyMessage(1);
            }
        });


        mPullToRefresh.getRefreshableView().setOnTouchListener(new View.OnTouchListener() {
            private View selectedView;
            private View tvAdviceStatused;
            private float oldXDis;
            private float oldX;
            private float oldY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mRecordAdapter.getSelectedView() == null) {
                    return false;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mRecordAdapter.selectedViewOld != null) {
                            mRecordAdapter.cancel(mRecordAdapter.selectedViewOld);
                        }
                        selectedView = mRecordAdapter.getSelectedView();
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
                                if (Math.abs(event.getX() - oldXDis) >= mRecordAdapter.recordDelete.getWidth() && selectedView != null) {
                                    selectedView.setX(-mRecordAdapter.recordDelete.getWidth());
                                } else {
                                    if (selectedView != null) {
                                        selectedView.setX(selectedView.getX() + distanceX);
                                    }
                                }
                            }
                        } else {
                            mRecordAdapter.cancel();
                        }
                        oldX = event.getX();
                        oldY = event.getY();
                        break;

                    case MotionEvent.ACTION_UP:
                        float distanceX2 = event.getX() - oldXDis;
                        if (distanceX2 < 0) {
                            if (Math.abs(distanceX2) >= mRecordAdapter.recordDelete.getWidth() / 2 && selectedView != null) {
                                selectedView.setX(-mRecordAdapter.recordDelete.getWidth());
                            } else {
                                if (selectedView != null) {
                                    selectedView.setX(0);
                                }
                            }
                        } else {
                            mRecordAdapter.cancel();
                        }
                        mRecordAdapter.selectedViewOld = selectedView;
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, 1, Menu.NONE, "删除");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                if (false) {
                    //TODO 上传代码

                } else {
                    ToastUtil.show(getActivity(), "请先上传，才能删除~~~");
                }
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}