package com.renygit.recycleview;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by reny on 2016/7/13.
 */

public class LoadMoreView extends RelativeLayout {

    public static final int STATUS_HIDE          = 0x00;
    public static final int STATUS_LOADING       = 0x01;
    public static final int STATUS_ERROR         = 0x02;
    public static final int STATUS_THEEND        = 0x03;
    
    private View mLoadingView;
    private View mErrorView;
    private View mTheEndView;

    private RStyleConfig config;

    private int mViewStatus;

    private OnClickListener mOnRetryClickListener;

    public LoadMoreView(Context context) {
        this(context, null);
    }

    public LoadMoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(context, R.layout.view_load_more, this);
        setOnClickListener(null);

        config = new RStyleConfig.Build().build();

        showViewByStatus(STATUS_HIDE);//初始为隐藏状态
    }

    public int getViewStatus() {
        return mViewStatus;
    }


    public void setConfig(RStyleConfig config) {
        this.config = config;
    }

    public void setmLoadingView(View mLoadingView) {
        this.mLoadingView = mLoadingView;
    }

    public void setmErrorView(View mErrorView) {
        this.mErrorView = mErrorView;
        if(null != mOnRetryClickListener){
            this.mErrorView.setOnClickListener(mOnRetryClickListener);
        }
    }

    public void setmTheEndView(View mTheEndView) {
        this.mTheEndView = mTheEndView;
    }

    public void showViewByStatus(int viewStatus) {
        if(mViewStatus == viewStatus){
            return;
        }
        mViewStatus = viewStatus;

        if(viewStatus == STATUS_HIDE){
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);

        if(null != config) {
            this.setBackgroundColor(ContextCompat.getColor(getContext(), config.getBgColor()));
        }

        switch (viewStatus){
            case STATUS_LOADING:
                if(null == mLoadingView){
                    ViewStub viewStub = (ViewStub) findViewById(R.id.loading_viewstub);
                    mLoadingView = viewStub.inflate();

                    if(null != config) {
                        try {
                            TextView tv_loading = (TextView) mLoadingView.findViewById(R.id.tv_loading);
                            AVLoadingIndicatorView indicatorView = (AVLoadingIndicatorView) mLoadingView.findViewById(R.id.pb_loading);

                            tv_loading.setTextColor(ContextCompat.getColor(getContext(), config.getTextColor()));
                            tv_loading.setText(config.getTipLoading());
                            indicatorView.setIndicatorColor(ContextCompat.getColor(getContext(), config.getIndicatorColor()));
                            indicatorView.setIndicator(config.getIndicatorName());
                        }catch (Exception e){e.printStackTrace();}
                    }

                }
                break;
            case STATUS_ERROR:
                if(null == mErrorView){
                    ViewStub viewStub = (ViewStub) findViewById(R.id.error_viewstub);
                    mErrorView = viewStub.inflate();

                    if(null != config) {
                        try {
                            TextView tv_error = (TextView) mErrorView.findViewById(R.id.tv_error);

                            tv_error.setTextColor(ContextCompat.getColor(getContext(), config.getTextColor()));
                            tv_error.setText(config.getTipError());
                        }catch (Exception e){e.printStackTrace();}
                    }

                    if(null != mOnRetryClickListener){
                        mErrorView.setOnClickListener(mOnRetryClickListener);
                    }
                }
                break;
            case STATUS_THEEND:
                if(null == mTheEndView){
                    ViewStub viewStub = (ViewStub) findViewById(R.id.end_viewstub);
                    mTheEndView = viewStub.inflate();

                    if(null != config) {
                        try {
                            TextView tv_end = (TextView) mTheEndView.findViewById(R.id.tv_end);

                            tv_end.setTextColor(ContextCompat.getColor(getContext(), config.getTextColor()));
                            tv_end.setText(config.getTipEnd());
                        }catch (Exception e){e.printStackTrace();}
                    }
                }
                break;
        }

        if(null != mLoadingView){
            mLoadingView.setVisibility(viewStatus == STATUS_LOADING ? View.VISIBLE : View.GONE);
        }
        if(null != mErrorView){
            mErrorView.setVisibility(viewStatus == STATUS_ERROR ? View.VISIBLE : View.GONE);
        }
        if(null != mTheEndView){
            mTheEndView.setVisibility(viewStatus == STATUS_THEEND ? View.VISIBLE : View.GONE);
        }
    }

    public void setOnRetry(OnClickListener listener){
        this.mOnRetryClickListener = listener;
    }
}
