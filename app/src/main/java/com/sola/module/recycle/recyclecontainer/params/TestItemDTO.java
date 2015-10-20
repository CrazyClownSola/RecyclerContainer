package com.sola.module.recycle.recyclecontainer.params;

import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sola.module.recycle.recyclecontainer.interfaces.IRecycleAnimatorListItem;
import com.sola.module.recycle.recyclecontainer.R;
import com.sola.module.recycle.recyclecontainer.RecycleDetailActivity_;

import java.io.ByteArrayOutputStream;

/**
 * Description:
 * <p/>
 * author: Sola
 * 2015/9/28
 */
public class TestItemDTO implements IRecycleAnimatorListItem {


    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    String test;

    ViewHolder mHolder;

    // ===========================================================
    // Constructors
    // ===========================================================

    public TestItemDTO(String test) {
        this.test = test;
    }


    // ===========================================================
    // Getter & Setter
    // ===========================================================


    public ImageView getId_image_item_shown() {
        return mHolder.id_image_item_shown;
    }
//    public ViewHolder getHolder() {
//        return mHolder;
//    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @TargetApi(21)
    @Override
    public View getView(final Context context, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_test_item_dto,
                parent, false);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, RecycleDetailActivity_.class);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            id_ptr_frame.setTransitionGroup(false);?
//            ((ViewGroup) (e.getId_image_item_shown().getParent()).getParent()).setTransitionGroup(false);
                ((BitmapDrawable) getId_image_item_shown().getDrawable()).getBitmap().
                        compress(Bitmap.CompressFormat.PNG, 100, stream);
//            e.getId_image_item_shown().getParent().tr
                intent.putExtra("image", stream.toByteArray());
                ActivityOptions options =
                        ActivityOptions.makeSceneTransitionAnimation((Activity) context,
                                getId_image_item_shown(),
                                "image_transition"
                        );
//            options.
                context.startActivity(intent, options.toBundle());
            }
        });
        return v;
//        return View.inflate(context,R.layout.list_item_test_item_dto,null);
    }

    @Override
    public RecyclerView.ViewHolder getHolder(final Context context, ViewGroup parent) {
        mHolder = new ViewHolder(getView(context, parent));
        return mHolder;
    }


    @Override
    public void refreshView(Context context, RecyclerView.ViewHolder holder) {
//        holder
//        TestItemView view = (TestItemView) holder.itemView;
        ((ViewHolder) holder).id_text_test.setText(test);
//        mHolder.id_image_item_shown.setImageDrawable(
//                context.getResources().getDrawable(R.drawable.item_translation));
    }


    @Override
    public AnimatorSet getAnimatorSet(View view) {
        return null;
//        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0, 1f);
//        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0, 1f);
//        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0, 1f);
//        AnimatorSet set = new AnimatorSet();
//        set.play(alpha).with(scaleX).with(scaleY);
//        set.setDuration(600);
//        set.setInterpolator(new LinearInterpolator());
//        return set;
    }
    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView id_text_test;
        ImageView id_image_item_shown;

        public ViewHolder(
                View v) {
            super(v);
            id_text_test = (TextView) v.findViewById(R.id.id_text_test);
            id_image_item_shown = (ImageView) v.findViewById(R.id.id_image_item_shown);
        }

    }
}
