package com.yalantis.contextmenu.lib;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class WrapperView extends ScrollView {

    RelativeLayout rootRelativeLayout;
    LinearLayout wrapperButtons;
    LinearLayout wrapperText;

    public WrapperView(Context context) {
        super(context);
        init();
    }

    public WrapperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WrapperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setupScrollView();
        setupRootRelativeLayout();
        setupWrappers();
    }

    public void show(MenuGravity menuGravity) {
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlp.addRule(menuGravity == MenuGravity.START ? RelativeLayout.ALIGN_PARENT_START : RelativeLayout.ALIGN_PARENT_END);
        wrapperButtons.setLayoutParams(rlp);

        wrapperText.setGravity(menuGravity == MenuGravity.START ? Gravity.START : Gravity.END);
        RelativeLayout.LayoutParams trlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        trlp.addRule(menuGravity == MenuGravity.START ? RelativeLayout.END_OF : RelativeLayout.START_OF, wrapperButtons.getId());
        trlp.setMargins(0,
                0,
                (int) getContext().getResources().getDimension(R.dimen.yctm_text_start_end_margin),
                0);
        wrapperText.setLayoutParams(trlp);
    }

    private void setupScrollView() {
        setFillViewport(true);
    }

    private void setupRootRelativeLayout() {
        rootRelativeLayout = new RelativeLayout(getContext());
        rootRelativeLayout.setId(ViewCompat.generateViewId());
        rootRelativeLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));
        addView(rootRelativeLayout);
    }

    private void setupWrappers() {
        wrapperButtons = new LinearLayout(getContext());
        wrapperButtons.setId(ViewCompat.generateViewId());
        wrapperButtons.setOrientation(LinearLayout.VERTICAL);
        rootRelativeLayout.addView(wrapperButtons);

        wrapperText = new LinearLayout(getContext());
        wrapperText.setId(ViewCompat.generateViewId());
        wrapperText.setOrientation(LinearLayout.VERTICAL);
        rootRelativeLayout.addView(wrapperText);
    }
}