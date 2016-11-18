package com.bignerdranch.android.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * Created by adedayo on 18/11/2016.
 */

public class PhotoPageActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return PhotoPageFragment.newInstance(getIntent().getData());
    }


    public static Intent newIntent(Context context, Uri photoPageUri){
        Intent i = new Intent(context, PhotoPageActivity.class);
        i.setData(photoPageUri);
        return i;
    }
}
