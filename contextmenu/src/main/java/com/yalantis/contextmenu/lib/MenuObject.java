package com.yalantis.contextmenu.lib;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.ImageView;

public class MenuObject implements Parcelable {

    public String title = "";

    public Drawable bgDrawable = null;
    public int bgColor = 0;
    public int bgResource = 0;
    public Drawable drawable = null;
    public int color = 0;
    public Bitmap bitmap = null;
    public int resource = 0;

    public ImageView.ScaleType scaleType  = ImageView.ScaleType.CENTER_INSIDE;
    public int textColor = 0;
    public int dividerColor = Integer.MAX_VALUE;
    public int menuTextAppearanceStyle = 0;

    public MenuObject() {
    }

    public MenuObject(String title) {
        this.title = title;
    }

    protected MenuObject(Parcel in) {
        String str = in.readString();
        title = TextUtils.isEmpty(str) ? "" : str;
        Bitmap bitmapBgDrawable = in.readParcelable(Bitmap.class.getClassLoader());
        if (bitmapBgDrawable == null) {
            bgDrawable = new ColorDrawable(in.readInt());
        } else {
            // TODO create BitmapDrawable with resources
            bgDrawable = new BitmapDrawable(bitmapBgDrawable);
        }
        bgColor = in.readInt();
        bgResource = in.readInt();

        Bitmap bitmapDrawable = in.readParcelable(Bitmap.class.getClassLoader());
        if (bitmapDrawable != null) {
            // TODO create BitmapDrawable with resources
            drawable = new BitmapDrawable(bitmapDrawable);
        }

        color = in.readInt();
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        resource = in.readInt();
        scaleType = ImageView.ScaleType.values()[in.readInt()];
        textColor = in.readInt();
        dividerColor = in.readInt();
        menuTextAppearanceStyle = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        if (bgDrawable == null) {
            dest.writeParcelable(null, flags);
        } else if (bgDrawable instanceof BitmapDrawable) {
            dest.writeParcelable(((BitmapDrawable)bgDrawable).getBitmap(), flags);
        } else if (bgDrawable instanceof ColorDrawable) {
            dest.writeInt(((ColorDrawable) bgDrawable).getColor());
        }
        dest.writeInt(bgColor);
        dest.writeInt(bgResource);
        if (drawable == null) {
            dest.writeParcelable(null, flags);
        } else if (drawable instanceof BitmapDrawable) {
            dest.writeParcelable(((BitmapDrawable) drawable).getBitmap(), flags);
        }
        dest.writeInt(color);
        dest.writeParcelable(bitmap, flags);
        dest.writeInt(resource);
        dest.writeInt(scaleType.ordinal());
        dest.writeInt(textColor);
        dest.writeInt(dividerColor);
        dest.writeInt(menuTextAppearanceStyle);
    }

    public static final Creator<MenuObject> CREATOR = new Creator<MenuObject>() {
        @Override
        public MenuObject createFromParcel(Parcel in) {
            return new MenuObject(in);
        }

        @Override
        public MenuObject[] newArray(int size) {
            return new MenuObject[size];
        }
    };

    public void setBgDrawable(ColorDrawable drawable) {
        setBgDrawableInternal(drawable);
    }

    public void setBgDrawable(BitmapDrawable drawable) {
        setBgDrawableInternal(drawable);
    }

    public void setBgColorValue(int value) {
        bgColor = value;
        bgDrawable = null;
        bgResource = 0;
    }

    public void setBgResourceValue(int value) {
        bgResource = value;
        bgDrawable = null;
        bgColor = 0;
    }

    public void setColorValue(int value) {
        color = value;
        drawable = null;
        bitmap = null;
        resource = 0;
    }

    public void setBitmapValue(Bitmap value) {
        bitmap = value;
        drawable = null;
        color = 0;
        resource = 0;
    }

    public void setResourceValue(int value) {
        resource = value;
        drawable = null;
        color = 0;
        bitmap = null;
    }

    private void setBgDrawableInternal(Drawable drawable) {
        bgDrawable = drawable;
        bgColor = 0;
        bgResource = 0;
    }
}