package org.nkoriyama.imagesearchwithvolley;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import org.nkoriyama.imagesearchwithvolley.model.PhotoInfo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private final List<PhotoInfo> mPhotoInfoList;
    public boolean mIsInUse;

    public PhotoAdapter() {
        mPhotoInfoList = new ArrayList<>();
        mIsInUse = false;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final PhotoInfo photoInfo = mPhotoInfoList.get(i);
        final PhotoAdapter adapter = this;
        final int position = i;
        viewHolder.title.setText(photoInfo.getTitle());
        viewHolder.image.setImageUrl(photoInfo.getThumbnailUrl(), ImageLoaderHelper.getImageLoader());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsInUse) {
                    ((OnPhotoListItemSelectedListener) MainActivity.getContext()).onPhotoListItemSelected(adapter, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPhotoInfoList.size();
    }

    public PhotoInfo getItem(int position) {
        return mPhotoInfoList.get(position);
    }

    /*
    画像のＵＲＬがあるもののみをコピーする。
    todo サムネイルのURLチェックもするか。
    todo バリデーションチェックもするか。
    todo 存在確認チェックするか。
     */
    public boolean addAll(Collection<? extends PhotoInfo> photoInfoList) {
        if (photoInfoList == null || photoInfoList.size() == 0) {
            return false;
        }
        mPhotoInfoList.addAll(ImmutableList.copyOf(Iterables.filter(
                photoInfoList, new Predicate<PhotoInfo>() {
                    @Override
                    public boolean apply(PhotoInfo input) {
                        return CheckUrl(input);
                    }
                    //@Override
                    //public boolean test(PhotoInfo input) {
                    //return true;
                    //}
                }
        )));
        notifyItemRangeInserted(mPhotoInfoList.size(), photoInfoList.size());
        return true;
    }

    private boolean CheckUrl(PhotoInfo input) {
        boolean ret = true;
        try {
            URI imageURI = new URI(input.getImageUrl());
            URI thumbnailURI = new URI(input.getThumbnailUrl());
        } catch (URISyntaxException e) {
            ret = false;
        } catch (NullPointerException e) {
            ret = false;
        }

        return ret;
    }

    public interface OnPhotoListItemSelectedListener {
        void onPhotoListItemSelected(PhotoAdapter adapter, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_item_title)
        TextView title;
        @BindView(R.id.list_item_image)
        NetworkImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
