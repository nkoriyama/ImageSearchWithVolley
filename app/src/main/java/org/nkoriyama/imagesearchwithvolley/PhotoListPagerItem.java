package org.nkoriyama.imagesearchwithvolley;

import android.support.v4.app.Fragment;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PhotoListPagerItem {
    private final Class<? extends PhotoListFragment> mPhotoListFragmentClass;
    private final int mIndicatorColor;
    private final int mDividerColor;


    PhotoListPagerItem(Class<? extends PhotoListFragment> fragmentClass,
                       int indicatorColor, int dividerColor) {
        mPhotoListFragmentClass = fragmentClass;
        mIndicatorColor = indicatorColor;
        mDividerColor = dividerColor;
    }

    Fragment createFragment(String query) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(query));
        try {
            Method method = mPhotoListFragmentClass.getMethod("newInstance", String.class);
            return (PhotoListFragment) method.invoke(null, query);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    CharSequence getTitle() {
        final String fragmentName = mPhotoListFragmentClass.getSimpleName();
        return fragmentName.substring(0, fragmentName.indexOf("PhotoListFragment"));
    }

    int getIndicatorColor() {
        return mIndicatorColor;
    }

    int getDividerColor() {
        return mDividerColor;
    }
}
