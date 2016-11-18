package com.bignerdranch.android.photogallery;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adedayo on 11/11/2016.
 */

public class PhotoGalleryFragment extends VisibleFragment{

    private RecyclerView mPhotoRecyclerView;
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    private static final String TAG = "PhotoGalleryFragment";
    private List<GalleryItem> mItems = new ArrayList<>();


    public static Fragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems();
//        Intent i = PollService.newIntent(getActivity());
//        getActivity().startService(i);
//        PollService.setServiceAlarm(getActivity(), true);

//        Handler responseHandler = new Handler();
//        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
//        mThumbnailDownloader.setThumbnailDownloaderListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
//            @Override
//            public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail) {
//                Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
////                target.bindDrawable(drawable);
//            }
//        });
//        mThumbnailDownloader.start();
//        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_photo_gallery, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
        if (PollService.isServiceAlarmOn(getActivity())){
            toggleItem.setTitle(R.string.stop_polling);
        } else {
            toggleItem.setTitle(R.string.start_polling);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.d(TAG, "QueryTextSubmit: " + query);
                QueryPreferences.setStoredQuery(getActivity(),query);
                updateItems();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "QueryTextSubmit: " + newText);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = QueryPreferences.getStoredQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d(TAG, "Clear clicked 2");
                if (QueryPreferences.getStoredQuery(getActivity()) != null) {
                    QueryPreferences.setStoredQuery(getActivity(), null);
//                    ...
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Log.d(TAG, "Clear clicked");
        switch (item.getItemId()){
            case R.id.menu_item_clear:
                Log.d(TAG, "Clear clicked 3");
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateItems();
                return true;
            case R.id.menu_item_toggle_polling:
                Log.d(TAG, "Clear clicked 4");
                boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(), shouldStartAlarm);
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void updateItems(){
        String query = QueryPreferences.getStoredQuery(getActivity());
        new FetchItemsTask(query).execute();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
//        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
//        mThumbnailDownloader.clearQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        setupAdapter();
        return v;
    }

    private void setupAdapter(){
        if (isAdded()){
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

//        private TextView mTitleTextView;
        private ImageView mItemImageView;
        private GalleryItem mGalleryItem;

        public PhotoHolder(View itemView) {
            super(itemView);
//            mTitleTextView = (TextView) itemView;
            mItemImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
            itemView.setOnClickListener(this);
        }

        public void bindGalleryItem(GalleryItem item){
//            mTitleTextView.setText(item.toString());
            mGalleryItem = item;
            Picasso.with(getActivity()).load(item.getUrl()).placeholder(R.drawable.bill_up_close).into(mItemImageView);
        }

        @Override
        public void onClick(View view) {
//            Intent intent = new Intent(Intent.ACTION_VIEW, mGalleryItem.getPhotPageUri());
//            startActivity(intent);
            Intent intent = PhotoPageActivity.newIntent(getActivity(), mGalleryItem.getPhotPageUri());
            startActivity(intent);

        }
//        public void bindDrawable(Drawable drawable){
//            mItemImageView.setImageDrawable(drawable);
//
//        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{

        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems){
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            TextView textView = new TextView(getActivity());
//            return new PhotoHolder(textView);
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            holder.bindGalleryItem(galleryItem);
//            Drawable placeholder = getResources().getDrawable(R.drawable.bill_up_close);
//            holder.bindDrawable(placeholder);
//            mThumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl());


        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {

        private String mQuery;

        public FetchItemsTask(String query){
            mQuery = query;
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {
//            String query = "robot";

            if (mQuery == null){
                return new FlickrFetchr().fetchRecentPhotos();

            }else {
                return new FlickrFetchr().searchPhotos(mQuery);
            }
//            return new FlickrFetchr().fetchItems();
//            return null;
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items){
            mItems = items;
            setupAdapter();
        }
    }




}
