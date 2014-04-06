package org.nkoriyama.imagesearchwithvolley;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.nkoriyama.imagesearchwithvolley.model.PhotoInfo;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PhotoAdapter extends ArrayAdapter<PhotoInfo> {
    private final ImageLoader mImageLoader;
    private final LayoutInflater mLayoutInflater;

    public PhotoAdapter(Context context, int resource, List<PhotoInfo> objects) {
        super(context, resource, objects);

        mImageLoader = ((ImageSearchWithVolley)((MainActivity)context)
                .getApplication()).getImageLoader();

        mLayoutInflater = LayoutInflater.from(context);
    }

    static class ViewHolder {
        @InjectView(R.id.list_item_title)
        TextView title;
        @InjectView(R.id.list_item_image)
        NetworkImageView image;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item, parent, false);
            assert convertView != null;
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final PhotoInfo photoInfo = getItem(position);
        viewHolder.title.setText(photoInfo.getTitle());
        viewHolder.image.setImageUrl(photoInfo.getThumbnailUrl(), mImageLoader);

        return convertView;
    }
}
