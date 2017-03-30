package me.cchiang.lookforthings;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class GalleryImageAdapter extends BaseAdapter
{
    private Context mContext;
    public ArrayList<String> mImageIds;



    public GalleryImageAdapter(Context context)
    {
        mContext = context;
        mImageIds = new ArrayList<>();
    }

    public int getCount() {
        return mImageIds.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public Context getContext() {
        return this.mContext;
    }


    // Override this method according to your need
    public View getView(int index, View view, ViewGroup viewGroup)
    {
        // TODO Auto-generated method stub
        ImageView i = new ImageView(mContext);

//        i.setImageResource(mImageIds.get(index));

        if(mImageIds.size() > 0 ){
            Picasso.with(mContext).load(mImageIds.get(index)).into(i);
        }


        i.setLayoutParams(new Gallery.LayoutParams(200, 200));

        i.setScaleType(ImageView.ScaleType.FIT_XY);


        return i;
    }


}