package org.nkoriyama.imagesearchwithvolley;

import android.app.Fragment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PhotoListPagerItem {
    private final Class<? extends PhotoListFragment> mFragmentClass;
    private final int mIndicatorColor;
    private final int mDividerColor;


    PhotoListPagerItem(Class<? extends PhotoListFragment> fragmentClass,
                       int indicatorColor, int dividerColor) {
        mFragmentClass = fragmentClass;
        mIndicatorColor = indicatorColor;
        mDividerColor = dividerColor;
    }

    Fragment createFragment(String query) {
        try {
            Method method = mFragmentClass.getMethod("newInstance", String.class);
            return (Fragment)method.invoke(null, query);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    CharSequence getTitle() {
        final String fragmentName =  mFragmentClass.getSimpleName();
        return fragmentName.substring(0, fragmentName.indexOf("PhotoListFragment"));
    }

    int getIndicatorColor() {
        return mIndicatorColor;
    }

    int getDividerColor() {
        return mDividerColor;
    }
}
