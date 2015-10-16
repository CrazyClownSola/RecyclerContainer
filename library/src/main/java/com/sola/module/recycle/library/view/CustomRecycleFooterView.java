package com.sola.module.recycle.library.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sola.module.recycle.library.R;
import com.sola.module.recycle.library.RecycleContainerBase;
import com.sola.module.recycle.library.interfaces.IRecycleExtraItem;
import com.sola.module.recycle.library.interfaces.RecycleLoadMoreUIHandler;


/**
 * Description:
 * <p/>
 * author: Sola
 * 2015/10/13
 */
public class CustomRecycleFooterView implements IRecycleExtraItem, RecycleLoadMoreUIHandler {

    // ===========================================================
    // Constants
    // ===========================================================

    private final static int FOOT_TYPE_ID = 0x0301;

    // ===========================================================
    // Fields
    // ===========================================================

    ViewHolder mHolder;

    Context mContext;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Override
    public View getView(Context context, ViewGroup parent) {
        if (mContext == null)
            mContext = context;
        View v = LayoutInflater.from(context).inflate(R.layout.recycle_footer_view,
                parent, false);
        return v;
    }

    @Override
    public RecyclerView.ViewHolder getHolder(Context context, ViewGroup parent) {
        mHolder = new ViewHolder(getView(context, parent));
        return mHolder;
    }

    @Override
    public void refreshView(Context context, RecyclerView.ViewHolder holder) {
    }

    @Override
    public int getViewType() {
        return FOOT_TYPE_ID;
    }


    @Override
    public void onLoading(RecycleContainerBase container) {
        mHolder.id_footer_text.setVisibility(View.VISIBLE);
        mHolder.id_footer_text.setText(getString(R.string.I_A_001));
        mHolder.id_footer_progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadFinish(RecycleContainerBase container, boolean empty, boolean hasMore) {
        if (!hasMore) {
            mHolder.id_footer_text.setText(getString(R.string.I_A_002));
        } else
            mHolder.id_footer_text.setVisibility(View.INVISIBLE);
        mHolder.id_footer_progress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onWaitToLoadMore(RecycleContainerBase container) {

    }

    @Override
    public void onLoadError(RecycleContainerBase container, int errorCode, String errorMessage) {
        mHolder.id_footer_text.setText(getString(R.string.I_A_003));
        mHolder.id_footer_progress.setVisibility(View.INVISIBLE);
    }
    // ===========================================================
    // Methods
    // ===========================================================

    private String getString(int resID) {
        return mContext == null ? "" : mContext.getString(resID);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView id_footer_text;
        ProgressBar id_footer_progress;

        public ViewHolder(
                View v) {
            super(v);
            id_footer_text = (TextView) v.findViewById(R.id.id_footer_text);
            id_footer_progress = (ProgressBar) v.findViewById(R.id.id_footer_progress);
        }
    }
}
