package com.yalantis.contextmenu.lib;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuAdapter {
    private static final int FIRST_CHILD_INDEX = 0;

    private final Context context;
    private final LinearLayout menuWrapper;
    private final LinearLayout textWrapper;
    private final List<MenuObject> menuObjects;
    private final int actionBarSize;
    private final MenuGravity gravity;

    public MenuAdapter(Context context, LinearLayout menuWrapper, LinearLayout textWrapper, List<MenuObject> menuObjects, int actionBarSize, MenuGravity gravity) {
        this.context = context;
        this.menuWrapper = menuWrapper;
        this.textWrapper = textWrapper;
        this.menuObjects = menuObjects;
        this.actionBarSize = actionBarSize;
        this.gravity = gravity;

        setViews();
    }

    public View.OnClickListener onCloseOutsideClickListener;
    public View.OnClickListener onItemClickListener;
    public View.OnClickListener onItemLongClickListener;

    private View.OnClickListener onItemClickListenerCalled;
    private View.OnClickListener onItemLongClickListenerCalled;

    private View clickedView = null;

    private AnimatorSet hideMenuAnimatorSet;
    private AnimatorSet showMenuAnimatorSet;

    private boolean isMenuOpen = false;
    private boolean isAnimationRun = false;

    private long animationDuration = MenuParams.ANIMATION_DURATION;

    private final View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onItemClickListenerCalled = onItemClickListener;
            viewClicked(view);
        }
    };

    private final View.OnLongClickListener itemLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            onItemLongClickListenerCalled = onItemLongClickListener;
            viewClicked(view);
            return true;
        }
    };

    public void setAnimationDuration(long duration) {
        animationDuration = duration;

        if (hideMenuAnimatorSet == null) {
            hideMenuAnimatorSet = setOpenCloseAnimation(true);
        }
        if (showMenuAnimatorSet == null) {
            showMenuAnimatorSet = setOpenCloseAnimation(false);
        }
        showMenuAnimatorSet.setDuration(animationDuration);
        hideMenuAnimatorSet.setDuration(animationDuration);
    }

    void menuToggle() {
        if (!isAnimationRun) {
            resetAnimations();
            isAnimationRun = true;

            if (isMenuOpen) {
                hideMenuAnimatorSet.start();
            } else {
                showMenuAnimatorSet.start();
            }

            toggleIsMenuOpen();
        }
    }

    void closeOutside() {
        onItemClickListenerCalled = onCloseOutsideClickListener;
        View child = menuWrapper.getChildAt(FIRST_CHILD_INDEX);
        if (child != null) {
            viewClicked(child);
        }
    }

    int getItemCount() {
        return menuObjects.size();
    }

    private int getLastItemPosition() {
        return getItemCount() - 1;
    }

    /**
     * Creating views and filling to wrappers
     */
    private void setViews() {
        for (int index=0;index<menuObjects.size();index++) {
            MenuObject menuObject = menuObjects.get(index);
            TextView tv = ContextUtil.getItemTextView(context, menuObject, actionBarSize,
                    itemClickListener, itemLongClickListener);
            textWrapper.addView(tv);
            RelativeLayout layout = ContextUtil.getImageWrapper(context, menuObject, actionBarSize,
                    itemClickListener, itemLongClickListener, index != getLastItemPosition());
            menuWrapper.addView(layout);
        }
    }

    /**
     * Set starting params to vertical animations
     */
    private void resetVerticalAnimation(View view, boolean toTop) {
        if (!isMenuOpen) {
            view.setRotation(ViewUtil.ROTATION_ZERO_DEGREES);
            view.setRotationY(ViewUtil.ROTATION_ZERO_DEGREES);
            view.setRotationX(-ViewUtil.ROTATION_NINETY_DEGREES);
        }
        view.setPivotX((actionBarSize / 2.0f));
        view.setPivotY(!toTop ? 0 : actionBarSize);
    }

    /**
     * Set starting params to side animations
     */
    private void resetSideAnimation(View view) {
        if (!isMenuOpen) {
            view.setRotation(ViewUtil.ROTATION_ZERO_DEGREES);
            view.setRotationY(getRotationY());
            view.setRotationX(ViewUtil.ROTATION_ZERO_DEGREES);
        }
        view.setPivotX(getPivotX());
        view.setPivotY(actionBarSize / 2.0f);
    }

    private float getRotationY() {
        switch (gravity) {
            case END:
                if (ContextUtil.isLayoutDirectionRtl(context)) {
                    return ViewUtil.ROTATION_NINETY_DEGREES;
                } else {
                    return -ViewUtil.ROTATION_NINETY_DEGREES;
                }
            case START:
            default:
                if (ContextUtil.isLayoutDirectionRtl(context)) {
                    return -ViewUtil.ROTATION_NINETY_DEGREES;
                } else {
                    return ViewUtil.ROTATION_NINETY_DEGREES;
                }
        }
    }


    private float getPivotX() {
        switch (gravity) {
            case END:
                if (ContextUtil.isLayoutDirectionRtl(context)) {
                    return ViewUtil.ROTATION_ZERO_DEGREES;
                } else {
                    return actionBarSize;
                }
            case START:
            default:
                if (ContextUtil.isLayoutDirectionRtl(context)) {
                    return actionBarSize;
                } else {
                    return ViewUtil.ROTATION_ZERO_DEGREES;
                }
        }
    }


    /**
     * Set starting params to text animations
     */
    private void resetTextAnimation(View view) {
        view.setAlpha(!isMenuOpen ? ViewUtil.ALPHA_INVISIBLE : ViewUtil.ALPHA_VISIBLE);
        view.setTranslationX(!isMenuOpen ? actionBarSize : ViewUtil.TRANSLATION_ZERO_VALUE);
    }

    /**
     * Set starting params to all animations
     */
    private void resetAnimations() {
        for (int i = 0; i< getItemCount(); i++) {
            resetTextAnimation(textWrapper.getChildAt(i));

            if (i == 0) {
                resetSideAnimation(menuWrapper.getChildAt(i));
            } else {
                resetVerticalAnimation(menuWrapper.getChildAt(i), false);
            }
        }
    }

    /**
     * Creates open/close AnimatorSet
     */
    private AnimatorSet setOpenCloseAnimation(boolean isCloseAnimation)  {
        final ArrayList<Animator> textAnimations = new ArrayList<>();
        final ArrayList<Animator> imageAnimations = new ArrayList<>();

        if (isCloseAnimation) {
            for (int i = getLastItemPosition(); i>=0; i--) {
                fillOpenClosingAnimations(true, textAnimations, imageAnimations, i);
            }
        } else {
            for (int i = 0; i< getItemCount(); i ++) {
                fillOpenClosingAnimations(false, textAnimations, imageAnimations, i);
            }
        }

        AnimatorSet set = new AnimatorSet();
        set.setDuration(animationDuration);
        set.setStartDelay(0);
        set.setInterpolator(new HesitateInterpolator());
        AnimatorSet tset = new AnimatorSet();
        tset.playSequentially(textAnimations);
        AnimatorSet iset = new AnimatorSet();
        iset.playSequentially(imageAnimations);
        set.playTogether(tset, iset);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                toggleIsAnimationRun();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return set;
    }

    /**
     * Filling arrays of animations to build Set of Closing / Opening animations
     */
    private void fillOpenClosingAnimations(boolean isCloseAnimation,
                                          ArrayList<Animator> textAnimations,
                                          ArrayList<Animator> imageAnimations,
                                          int wrapperPosition) {
        View twchild = textWrapper.getChildAt(wrapperPosition);
        AnimatorSet taset = new AnimatorSet();
        final ObjectAnimator textAppearance = isCloseAnimation ? ViewUtil.alphaDisappear(twchild) : ViewUtil.alphaAppear(twchild);
        final ObjectAnimator textTranslation;
        if (isCloseAnimation) {
            switch (gravity) {
                case END:
                    textTranslation = ViewUtil.translationEnd(twchild, getTextEndTranslation());
                    break;
                case START:
                default:
                    textTranslation = ViewUtil.translationStart(twchild, getTextEndTranslation());
                    break;
            }
        } else {
            switch (gravity) {
                case END:
                    textTranslation = ViewUtil.translationStart(twchild, getTextEndTranslation());
                    break;
                case START:
                default:
                    textTranslation = ViewUtil.translationEnd(twchild, getTextEndTranslation());
                    break;
            }
        }
        taset.playTogether(textAppearance, textTranslation);
        textAnimations.add(taset);

        View mwchild = menuWrapper.getChildAt(wrapperPosition);
        if (isCloseAnimation) {
            if (wrapperPosition == 0) {
                imageAnimations.add(ViewUtil.rotationCloseHorizontal(mwchild, gravity));
            } else {
                imageAnimations.add(ViewUtil.rotationCloseVertical(mwchild));
            }
        } else {
            if (wrapperPosition == 0) {
                imageAnimations.add(ViewUtil.rotationOpenHorizontal(mwchild, gravity));
            } else {
                imageAnimations.add(ViewUtil.rotationOpenVertical(mwchild));
            }
        }
    }

    private void viewClicked(View view) {
        if (isMenuOpen && !isAnimationRun) {
            clickedView = view;

            final int childIndex = ((ViewGroup) view.getParent()).indexOfChild(view);
            if (childIndex == -1) {
                return;
            }

            toggleIsAnimationRun();
            buildChosenAnimation(childIndex);
            toggleIsMenuOpen();
        }
    }

    private void buildChosenAnimation(int childIndex) {
        final ArrayList<Animator> fadeOutTextTopAnimatorList = new ArrayList<>();
        final ArrayList<Animator> closeToBottomImageAnimatorList = new ArrayList<>();
        final ArrayList<Animator> fadeOutTextBottomAnimatorList = new ArrayList<>();
        final ArrayList<Animator> closeToTopImageAnimatorList = new ArrayList<>();

        fillAnimatorLists(childIndex, fadeOutTextTopAnimatorList, closeToBottomImageAnimatorList,
                fadeOutTextBottomAnimatorList, closeToTopImageAnimatorList);

        resetSideAnimation(menuWrapper.getChildAt(childIndex));

        final Pair<AnimatorSet, AnimatorSet> fullAnimatorSetPair = getFullAnimatorSetPair(childIndex,
                fadeOutTextTopAnimatorList, closeToBottomImageAnimatorList,
                fadeOutTextBottomAnimatorList, closeToTopImageAnimatorList);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(animationDuration);
        set.setInterpolator(new HesitateInterpolator());
        set.playTogether(fullAnimatorSetPair.first, fullAnimatorSetPair.second);
        set.start();
    }

    private void fillAnimatorLists(int childIndex, ArrayList<Animator> fadeOutTextTopAnimatorList,
                                  ArrayList<Animator> closeToBottomImageAnimatorList,
                                  ArrayList<Animator> fadeOutTextBottomAnimatorList,
                                  ArrayList<Animator> closeToTopImageAnimatorList) {
        for (int i = 0 ; i<= getLastItemPosition(); i++) {
            final View menuWrapperChild = menuWrapper.getChildAt(i);
            final ObjectAnimator menuWrapperChildRotation = ViewUtil.rotationCloseVertical(menuWrapperChild);
            final AnimatorSet textWrapperChildFadeOut = ViewUtil.fadeOutSet(textWrapper.getChildAt(i), getTextEndTranslation(), gravity);

            if (i >=0 && i< childIndex) {
                resetVerticalAnimation(menuWrapperChild, true);
                closeToBottomImageAnimatorList.add(menuWrapperChildRotation);
                fadeOutTextTopAnimatorList.add(textWrapperChildFadeOut);
            } else if (i >= childIndex + 1 && i<= getLastItemPosition()) {
                resetVerticalAnimation(menuWrapperChild, false);
                closeToTopImageAnimatorList.add(menuWrapperChildRotation);
                fadeOutTextBottomAnimatorList.add(textWrapperChildFadeOut);
            }
        }

        Collections.reverse(closeToTopImageAnimatorList);
        Collections.reverse(fadeOutTextBottomAnimatorList);
    }

    private Pair<AnimatorSet, AnimatorSet> getFullAnimatorSetPair(int childIndex,
                                                                  ArrayList<Animator> fadeOutTextTopAnimatorList,
                                                                  ArrayList<Animator> closeToBottomImageAnimatorList,
                                                                  ArrayList<Animator> fadeOutTextBottomAnimatorList,
                                                                  ArrayList<Animator> closeToTopImageAnimatorList)  {
        final AnimatorSet closeToBottom = new AnimatorSet();
        closeToBottom.playSequentially(closeToBottomImageAnimatorList);
        final AnimatorSet fadeOutTop = new AnimatorSet();
        fadeOutTop.playSequentially(fadeOutTextTopAnimatorList);

        final AnimatorSet closeToTop = new AnimatorSet();
        closeToTop.playSequentially(closeToTopImageAnimatorList);
        final AnimatorSet fadeOutBottom = new AnimatorSet();
        fadeOutBottom.playSequentially(fadeOutTextBottomAnimatorList);

        final ObjectAnimator closeToEnd = ViewUtil.rotationCloseHorizontal(menuWrapper.getChildAt(childIndex), gravity);
        closeToEnd.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                toggleIsAnimationRun();

                if (clickedView != null) {
                    if (onItemClickListenerCalled != null) {
                        onItemClickListenerCalled.onClick(clickedView);
                    }
                    if (onItemLongClickListenerCalled != null) {
                        onItemLongClickListenerCalled.onClick(clickedView);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        final AnimatorSet fadeOutChosenText = ViewUtil.fadeOutSet(textWrapper.getChildAt(childIndex), getTextEndTranslation(), gravity);

        final AnimatorSet imageFullAnimatorSet = new AnimatorSet();
        imageFullAnimatorSet.play(closeToBottom).with(closeToTop);
        final AnimatorSet textFullAnimatorSet = new AnimatorSet();
        textFullAnimatorSet.play(fadeOutTop).with(fadeOutBottom);

        if (closeToBottomImageAnimatorList.size() >= closeToTopImageAnimatorList.size()) {
            imageFullAnimatorSet.play(closeToBottom).before(closeToEnd);
            textFullAnimatorSet.play(fadeOutTop).before(fadeOutChosenText);
        } else {
            imageFullAnimatorSet.play(closeToTop).before(closeToEnd);
            textFullAnimatorSet.play(fadeOutBottom).before(fadeOutChosenText);
        }

        return new Pair(imageFullAnimatorSet, textFullAnimatorSet);
    }

    private float getTextEndTranslation() {
        return context.getResources().getDimension(R.dimen.yctm_text_translation);
    }

    private void toggleIsAnimationRun() {
        isAnimationRun = !isAnimationRun;
    }

    private void toggleIsMenuOpen() {
        isMenuOpen = !isMenuOpen;
    }
}