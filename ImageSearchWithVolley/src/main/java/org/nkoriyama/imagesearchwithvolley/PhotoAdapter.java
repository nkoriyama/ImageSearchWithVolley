package org.nkoriyama.imagesearchwithvolley;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.nkoriyama.imagesearchwithvolley.model.PhotoInfo;

import java.util.Collection;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private List<PhotoInfo> mPhotoInfoList;
    private int mResource;
    private final ImageLoader mImageLoader;

    public PhotoAdapter(Context context, int resource, List<PhotoInfo> photoInfoList) {
        mImageLoader = ((ImageSearchWithVolley) ((MainActivity) context)
                .getApplication()).getImageLoader();
        mResource = resource;
        mPhotoInfoList = photoInfoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(mResource, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final PhotoInfo photoInfo = mPhotoInfoList.get(i);
        viewHolder.title.setText(photoInfo.getTitle());
        viewHolder.image.setImageUrl(photoInfo.getThumbnailUrl(), mImageLoader);
    }

    @Override
    public int getItemCount() {
        return mPhotoInfoList.size();
    }


    public PhotoInfo getItem(int position) {
        return mPhotoInfoList.get(position);
    }

    public void addAll(Collection<? extends PhotoInfo> photoInfoList) {
        final int positionStart = mPhotoInfoList.size();
        final int itemCount = photoInfoList.size();
        mPhotoInfoList.addAll(photoInfoList);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.list_item_title)
        TextView title;
        @InjectView(R.id.list_item_image)
        NetworkImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
