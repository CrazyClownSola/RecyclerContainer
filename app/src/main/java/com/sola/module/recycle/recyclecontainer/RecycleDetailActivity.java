package com.sola.module.recycle.recyclecontainer;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.MenuItem;
import android.widget.ImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Description:
 * <p/>
 * author: Sola
 * 2015/10/14
 */
@EActivity(R.layout.activity_recycle_detail)
public class RecycleDetailActivity extends AppCompatActivity {
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    @ViewById
    ImageView id_image_item_shown;

    @ViewById
    Toolbar id_tool_bar;

//    Actor

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @TargetApi(21)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAfterTransition();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    // ===========================================================
    // Methods
    // ===========================================================
    @TargetApi(21)
    @AfterViews
    public void afterViews() {
        setSupportActionBar(id_tool_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setEnterTransition(new Slide());
//        getWindow().setEnterTransition(new Fade());
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        id_image_item_shown.setImageBitmap(bitmap);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
