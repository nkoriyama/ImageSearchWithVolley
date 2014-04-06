package org.nkoriyama.imagesearchwithvolley.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Preconditions;

public class PhotoInfoParcelable implements PhotoInfo, Parcelable {
    private final String mImageUrl;
    private final String mThumbnailUrl;
    private final String mTitle;
    private final String mShareSubject;
    private final String mShareText;

    private PhotoInfoParcelable(Parcel source) {
        Preconditions.checkNotNull(source);
        mTitle = source.readString();
        mImageUrl = source.readString();
        mThumbnailUrl = source.readString();
        mShareSubject = source.readString();
        mShareText = source.readString();
    }

    public PhotoInfoParcelable(PhotoInfo photoinfo) {
        Preconditions.checkNotNull(photoinfo);
        mTitle = photoinfo.getTitle();
        mImageUrl = photoinfo.getImageUrl();
        mThumbnailUrl = photoinfo.getThumbnailUrl();
        mShareSubject = photoinfo.getShareSubject();
        mShareText = photoinfo.getShareText();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String getImageUrl() {
        return mImageUrl;
    }

    @Override
    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getShareSubject() {
        return mShareSubject;
    }

    @Override
    public String getShareText() {
        return mShareText;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeString(mTitle);
        destination.writeString(mImageUrl);
        destination.writeString(mThumbnailUrl);
        destination.writeString(mShareSubject);
        destination.writeString(mShareText);
    }

    public static final Parcelable.Creator<PhotoInfoParcelable> CREATOR =
            new Parcelable.Creator<PhotoInfoParcelable>() {
                @Override
                public PhotoInfoParcelable createFromParcel(Parcel source) {
                    return new PhotoInfoParcelable(source);
                }

                @Override
                public PhotoInfoParcelable[] newArray(int size) {
                    return new PhotoInfoParcelable[size];
                }
            };
}
