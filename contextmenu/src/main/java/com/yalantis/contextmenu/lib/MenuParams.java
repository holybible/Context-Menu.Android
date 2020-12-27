package com.yalantis.contextmenu.lib;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * property [animationDelay]
 * Delay after opening and before closing.
 * @see ContextMenuDialogFragment
 *
 * property [isClosableOutside]
 * If option menu can be closed on touch to non-button area.
 *
 * property [gravity]
 * You can change the side. By default - MenuGravity.END
 * @see MenuGravity
 */
public class MenuParams implements Parcelable {
    public static final long ANIMATION_DURATION = 100L;
    public static final long BACKGROUND_COLOR_ANIMATION_DURATION = 200L;

    public int actionBarSize = 0;
    public List<MenuObject> menuObjects = new ArrayList<>();
    public long animationDelay = 0L;
    public long animationDuration = ANIMATION_DURATION;
    public long backgroundColorAnimationDuration = BACKGROUND_COLOR_ANIMATION_DURATION;
    public boolean isFitsSystemWindow = false;
    public boolean isClipToPadding = true;
    public boolean isClosableOutside = false;
    public MenuGravity gravity = MenuGravity.END;

    public MenuParams() {
    }

    protected MenuParams(Parcel in) {
        actionBarSize = in.readInt();
        menuObjects = in.createTypedArrayList(MenuObject.CREATOR);
        animationDelay = in.readLong();
        animationDuration = in.readLong();
        backgroundColorAnimationDuration = in.readLong();
        isFitsSystemWindow = in.readByte() != 0;
        isClipToPadding = in.readByte() != 0;
        isClosableOutside = in.readByte() != 0;
        MenuGravity mg = (MenuGravity) in.readSerializable();
        gravity = mg != null ? mg : MenuGravity.END;
    }

    public static final Creator<MenuParams> CREATOR = new Creator<MenuParams>() {
        @Override
        public MenuParams createFromParcel(Parcel in) {
            return new MenuParams(in);
        }

        @Override
        public MenuParams[] newArray(int size) {
            return new MenuParams[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(actionBarSize);
        dest.writeTypedList(menuObjects);
        dest.writeLong(animationDelay);
        dest.writeLong(animationDuration);
        dest.writeLong(backgroundColorAnimationDuration);
        dest.writeByte((byte) (isFitsSystemWindow ? 1 : 0));
        dest.writeByte((byte) (isClipToPadding ? 1 : 0));
        dest.writeByte((byte) (isClosableOutside ? 1 : 0));
        dest.writeSerializable(gravity);
    }

}