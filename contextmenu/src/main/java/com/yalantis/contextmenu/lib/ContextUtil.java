package com.yalantis.contextmenu.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

final class ContextUtil {
    static int getDefaultActionBarSize(Context context) {
        final TypedArray styledAttrs = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        final int actionBarSize = (int) styledAttrs.getDimension(0, 0f);
        styledAttrs.recycle();
        return actionBarSize;
    }

    static int getColorCompat(Context context, @ColorRes int color) {
        return ContextCompat.getColor(context, color);
    }

    static int getDimension(Context context, @DimenRes int dimen) {
        return (int) context.getResources().getDimension(dimen);
    }

    static boolean isLayoutDirectionRtl(Context context) {
        return context.getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    static TextView getItemTextView(Context context, MenuObject menuItem, int menuItemSize,
                                    View.OnClickListener onCLick,
                                    View.OnLongClickListener onLongClick) {
        TextView tv = new TextView(context);

        final int textColor;
        if (menuItem.textColor == 0) {
            textColor = android.R.color.white;
        } else {
            textColor = menuItem.textColor;
        }

        final int styleResId;
        if (menuItem.menuTextAppearanceStyle > 0) {
            styleResId = menuItem.menuTextAppearanceStyle;
        } else {
            styleResId = R.style.YCTMTextView_DefaultStyle;
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, menuItemSize);
        tv.setLayoutParams(params);

        tv.setText(menuItem.title);
        tv.setGravity(Gravity.CENTER_VERTICAL);

        tv.setOnClickListener(onCLick);
        tv.setOnLongClickListener(onLongClick);
        tv.setPadding(
                getDimension(context, R.dimen.yctm_text_start_end_padding_medium), 0,
                getDimension(context, R.dimen.yctm_text_start_end_padding_small), 0
        );

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            tv.setTextAppearance(context, styleResId);
        } else {
            tv.setTextAppearance(styleResId);
        }

        tv.setTextColor(getColorCompat(context, textColor));

        return tv;
    }

    static ImageView getItemImageButton(Context context, MenuObject menuItem) {
        ImageButton image = new ImageButton(context);

        final int paddingValue = getDimension(context, R.dimen.yctm_menu_item_padding);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        image.setLayoutParams(params);
        image.setClickable(false);
        image.setFocusable(false);
        image.setScaleType(menuItem.scaleType);

        image.setPadding(paddingValue, paddingValue, paddingValue, paddingValue);
        image.setBackgroundColor(Color.TRANSPARENT);

        if (menuItem.color != 0) {
            image.setImageDrawable(new ColorDrawable(menuItem.color));
        } else if (menuItem.resource != 0) {
            image.setImageResource(menuItem.resource);
        } else if (menuItem.bitmap != null) {
            image.setImageBitmap(menuItem.bitmap);
        } else if (menuItem.drawable != null) {
            image.setImageDrawable(menuItem.drawable);
        }
        return image;
    }

    static View getDivider(Context context, MenuObject menuItem) {
        View view = new View(context);

        final int dividerColor;
        if (menuItem.dividerColor == Integer.MAX_VALUE) {
            dividerColor = R.color.yctm_divider_color;
        } else {
            dividerColor = menuItem.dividerColor;
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getDimension(context, R.dimen.yctm_divider_height));
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        view.setLayoutParams(params);
        view.setClickable(true);

        view.setBackgroundColor(getColorCompat(context, dividerColor));

        return view;
    }

    static RelativeLayout getImageWrapper(Context context, MenuObject menuItem, int menuItemSize,
                                          View.OnClickListener onCLick,
                                          View.OnLongClickListener onLongClick,
                                          boolean showDivider) {
        RelativeLayout layout = new RelativeLayout(context);

        layout.setLayoutParams(new LinearLayout.LayoutParams(menuItemSize, menuItemSize));

        layout.setOnClickListener(onCLick);
        layout.setOnLongClickListener(onLongClick);
        layout.addView(getItemImageButton(context, menuItem));
        if (showDivider) {
            layout.addView(getDivider(context, menuItem));
        }

        if (menuItem.bgColor != 0) {
            layout.setBackgroundColor(menuItem.bgColor);
        } else if (menuItem.bgDrawable != null) {
            layout.setBackground(menuItem.bgDrawable);
        } else if (menuItem.bgResource != 0) {
            layout.setBackgroundResource(menuItem.bgResource);
        } else {
            layout.setBackgroundColor(getColorCompat(context, R.color.yctm_menu_item_background));
        }

        return layout;
    }
}