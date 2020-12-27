package com.yalantis.contextmenu.lib;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.support.annotation.ColorRes;
import android.view.View;

final class ViewUtil {
    static final float ROTATION_ZERO_DEGREES = 0f;
    static final float ROTATION_NINETY_DEGREES = 90f;
    static final float ALPHA_INVISIBLE = 0f;
    static final float ALPHA_VISIBLE = 1f;
    static final float TRANSLATION_ZERO_VALUE = 0f;

    private static final String ROTATION_Y_PROPERTY = "rotationY";
    private static final String ROTATION_X_PROPERTY = "rotationX";
    private static final String ALPHA_PROPERTY = "alpha";
    private static final String TRANSLATION_X_PROPERTY = "translationX";
    private static final String BACKGROUND_COLOR_PROPERTY = "backgroundColor";

    static ObjectAnimator rotationCloseHorizontal(View view, MenuGravity gravity) {
        final float from = ROTATION_ZERO_DEGREES;
        float to;
        switch (gravity) {
            case END:
                to = -ROTATION_NINETY_DEGREES;
                break;
            case START:
            default:
                to = ROTATION_NINETY_DEGREES;
                break;
        }


        if (ContextUtil.isLayoutDirectionRtl(view.getContext())) {
            to *= -1f;
        }

        return ObjectAnimator.ofFloat(view, ROTATION_Y_PROPERTY, from, to);
    }

    static ObjectAnimator rotationOpenHorizontal(View view, MenuGravity gravity) {
        float from;
        switch (gravity) {
            case END:
                from = -ROTATION_NINETY_DEGREES;
                break;
            case START:
            default:
                from = ROTATION_NINETY_DEGREES;
                break;
        }
        final float to = ROTATION_ZERO_DEGREES;

        if (ContextUtil.isLayoutDirectionRtl(view.getContext())) {
            from *= -1f;
        }

        return ObjectAnimator.ofFloat(view, ROTATION_Y_PROPERTY, from, to);
    }

    static ObjectAnimator rotationCloseVertical(View view) {
        return ObjectAnimator.ofFloat(view, ROTATION_X_PROPERTY, ROTATION_ZERO_DEGREES, -ROTATION_NINETY_DEGREES);
    }

    static ObjectAnimator rotationOpenVertical(View view) {
        return ObjectAnimator.ofFloat(view, ROTATION_X_PROPERTY, -ROTATION_NINETY_DEGREES, ROTATION_ZERO_DEGREES);
    }

    static ObjectAnimator alphaDisappear(View view) {
        return ObjectAnimator.ofFloat(view, ALPHA_PROPERTY, ALPHA_VISIBLE, ALPHA_INVISIBLE);
    }

    static ObjectAnimator alphaAppear(View view) {
        return ObjectAnimator.ofFloat(view, ALPHA_PROPERTY, ALPHA_INVISIBLE, ALPHA_VISIBLE);
    }

    static ObjectAnimator translationEnd(View view, float x) {
        float from = TRANSLATION_ZERO_VALUE;
        float to = x;

        if (ContextUtil.isLayoutDirectionRtl(view.getContext())) {
            from = x;
            to = TRANSLATION_ZERO_VALUE;
        }

        return ObjectAnimator.ofFloat(view, TRANSLATION_X_PROPERTY, from, to);
    }

    static ObjectAnimator translationStart(View view, float x) {
        float from = x;
        float to = TRANSLATION_ZERO_VALUE;

        if (ContextUtil.isLayoutDirectionRtl(view.getContext())) {
            from = TRANSLATION_ZERO_VALUE;
            to = x;
        }

        return ObjectAnimator.ofFloat(view, TRANSLATION_X_PROPERTY, from, to);
    }

    static AnimatorSet fadeOutSet(View view, float x, MenuGravity gravity) {
        AnimatorSet set = new AnimatorSet();
        final ObjectAnimator translation;
        switch (gravity) {
            case END:
                translation = translationEnd(view, x);
                break;
            case START:
            default:
                translation = translationStart(view, x);
                break;
        }
        set.playTogether(alphaDisappear(view), translation);
        return set;
    }

    static ObjectAnimator colorAnimation(View view, long duration,
                                         @ColorRes int startColorResId,
                                         @ColorRes int endColorResId) {
        ObjectAnimator animator = ObjectAnimator.ofObject(view, BACKGROUND_COLOR_PROPERTY,
                new ArgbEvaluator(), ContextUtil.getColorCompat(view.getContext(), startColorResId),
                ContextUtil.getColorCompat(view.getContext(), endColorResId));
        animator.setDuration(duration);
        return animator;
    }

    static void backgroundColorAppear(View view, long duration) {
        ObjectAnimator animator = colorAnimation(view, duration, android.R.color.transparent, R.color.yctm_menu_fragment_background);
        animator.start();
    }

    static void backgroundColorDisappear(View view, long duration, Runnable onAnimationEnd) {
        ObjectAnimator animator = colorAnimation(view, duration, R.color.yctm_menu_fragment_background,
                android.R.color.transparent);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                onAnimationEnd.run();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }
}


