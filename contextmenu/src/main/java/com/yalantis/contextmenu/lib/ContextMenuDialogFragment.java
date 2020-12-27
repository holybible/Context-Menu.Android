package com.yalantis.contextmenu.lib;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class ContextMenuDialogFragment extends DialogFragment {
    public static final String TAG = "ContextMenuDialogFragment";
    public static final String ARGS_MENU_PARAMS = "menuParams";

    public static ContextMenuDialogFragment newInstance(MenuParams menuParams) {
        ContextMenuDialogFragment fragment = new ContextMenuDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_MENU_PARAMS, menuParams);
        fragment.setArguments(bundle);
        return fragment;
    }

    public OnMenuClickListener menuItemClickListener;
    public OnMenuClickListener menuItemLongClickListener;

    private WrapperView wrapperView;

    private MenuParams menuParams;
    private MenuAdapter dropDownMenuAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.YCTMMenuFragmentStyle);

        Bundle args = getArguments();
        if (args != null) {
            Parcelable p = args.getParcelable(ARGS_MENU_PARAMS);
            if (p instanceof MenuParams) {
                menuParams = (MenuParams) p;
            } else {
                menuParams = new MenuParams();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.yctm_fragment_menu, container, false);
        wrapperView = view.findViewById(R.id.wrapperView);
        view.setFitsSystemWindows(menuParams.isFitsSystemWindow);
        ((ViewGroup) view).setClipToPadding(menuParams.isClipToPadding);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDropDownMenuAdapter();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dropDownMenuAdapter.menuToggle();
            }
        }, menuParams.animationDelay);

        ViewUtil.backgroundColorAppear(wrapperView, menuParams.backgroundColorAnimationDuration);
        wrapperView.show(menuParams.gravity);

        if (menuParams.isClosableOutside) {
            wrapperView.rootRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isAdded()) {
                        dropDownMenuAdapter.closeOutside();
                    }
                }
            });
        }
    }

    private void initDropDownMenuAdapter() {
        if (getActivity() != null) {
            dropDownMenuAdapter = new MenuAdapter(getActivity(),
                    wrapperView.wrapperButtons,
                    wrapperView.wrapperText,
                    menuParams.menuObjects,
                    menuParams.actionBarSize,
                    menuParams.gravity);
            dropDownMenuAdapter.setAnimationDuration(menuParams.animationDuration);
            dropDownMenuAdapter.onCloseOutsideClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    close();
                }
            };
            dropDownMenuAdapter.onItemClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (menuItemClickListener != null) {
                        int position = ((ViewGroup) view.getParent()).indexOfChild(view);
                        menuItemClickListener.onClick(view, position);
                    }
                    close();
                }
            };
            dropDownMenuAdapter.onItemLongClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (menuItemLongClickListener != null) {
                        int position = ((ViewGroup) view.getParent()).indexOfChild(view);
                        menuItemLongClickListener.onClick(view, position);
                    }
                    close();
                }
            };
        }
    }

    private void close() {
        ViewUtil.backgroundColorDisappear(wrapperView, menuParams.backgroundColorAnimationDuration, new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismissAllowingStateLoss();
                    }
                }, menuParams.animationDelay);
            }
        });
    }
}