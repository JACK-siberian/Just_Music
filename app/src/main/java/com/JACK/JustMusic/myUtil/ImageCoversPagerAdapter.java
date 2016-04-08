package com.JACK.JustMusicWW.myUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.JACK.JustMusicWW.R;

import java.util.ArrayList;

public class ImageCoversPagerAdapter extends PagerAdapter {

    private ArrayList<Uri> imageCovers;
    private Context context;

    public ImageCoversPagerAdapter(Context context, ArrayList<Uri> images){
        this.context = context;
        this.imageCovers = images;
    }

    @Override
    public int getCount() {
        if ( imageCovers != null)
            return imageCovers.size();
        else
            return 0;
    }
    @Override
    public int getItemPosition(Object object) {
        /*View v = (View) object;
        long viewTag = (long)v.getTag();
        for ( Image image : images)
            if ( viewTag == image.getId())
                return POSITION_UNCHANGED;*/

        return POSITION_NONE;
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if ( imageCovers != null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View itemGallery = inflater.inflate(R.layout.pager_image_covers_item, null);


            ImageView imageView = (ImageView) itemGallery.findViewById(R.id.imageCover);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            Uri curImageUri = imageCovers.get(position);
            Bitmap bitmap = MyUtil.getCoverArt(context, curImageUri);
            if (bitmap != null)
                imageView.setImageBitmap(bitmap);
            else
                //noinspection ResourceType
                imageView.setImageResource(R.raw.no_cover);

            container.addView(itemGallery);
            return itemGallery;
        }
        else
            return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}