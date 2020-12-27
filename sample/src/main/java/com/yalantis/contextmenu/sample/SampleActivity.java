package com.yalantis.contextmenu.sample;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuGravity;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.OnMenuClickListener;

import java.util.ArrayList;
import java.util.List;

public class SampleActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvToolbarTitle;

    private ContextMenuDialogFragment contextMenuDialogFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        toolbar = findViewById(R.id.toolbar);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);

        initToolbar();
        initMenuFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null) {
            switch (item.getItemId()) {
                case R.id.context_menu:
                    showContextMenuDialogFragment();
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (contextMenuDialogFragment != null && contextMenuDialogFragment.isAdded()) {
            contextMenuDialogFragment.dismiss();
        } else {
            finish();
        }
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvToolbarTitle.setText("Samantha");
    }

    /**
     * If you want to change the side you need to add 'gravity' parameter,
     * by default it is MenuGravity.END.
     * <p>
     * For example:
     * <p>
     * MenuParams(
     * actionBarSize = resources.getDimension(R.dimen.tool_bar_height).toInt(),
     * menuObjects = getMenuObjects(),
     * isClosableOutside = false,
     * gravity = MenuGravity.START
     * )
     */
    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.actionBarSize = (int) getResources().getDimension(R.dimen.tool_bar_height);
        menuParams.menuObjects = getMenuObjects();
        menuParams.isClosableOutside = false;
        menuParams.gravity = MenuGravity.START;

        contextMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        contextMenuDialogFragment.menuItemClickListener = new OnMenuClickListener() {
            @Override
            public void onClick(View view, int position) {
                String msg = String.format("Clicked on position: %s", position);
                Toast.makeText(SampleActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        };
        contextMenuDialogFragment.menuItemLongClickListener = new OnMenuClickListener() {
            @Override
            public void onClick(View view, int position) {
                String msg = String.format("Long clicked on position: %s", position);
                Toast.makeText(SampleActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        };
    }

    /**
     * You can use any (drawable, resource, bitmap, color) as image:
     * menuObject.drawable = ...
     * menuObject.setResourceValue(...)
     * menuObject.setBitmapValue(...)
     * menuObject.setColorValue(...)
     * <p>
     * You can set image ScaleType:
     * menuObject.scaleType = ScaleType.FIT_XY
     * <p>
     * You can use any [resource, drawable, color] as background:
     * menuObject.setBgResourceValue(...)
     * menuObject.setBgDrawable(...)
     * menuObject.setBgColorValue(...)
     * <p>
     * You can use any (color) as text color:
     * menuObject.textColor = ...
     * <p>
     * You can set any (color) as divider color:
     * menuObject.dividerColor = ...
     */
    private List<MenuObject> getMenuObjects() {
        List<MenuObject> mos = new ArrayList<>();

        final MenuObject close = new MenuObject();
        close.setResourceValue(R.drawable.icn_close);

        final MenuObject send = new MenuObject("Send message");
        send.setResourceValue(R.drawable.icn_1);

        final MenuObject like = new MenuObject("Like profile");
        like.setBitmapValue(BitmapFactory.decodeResource(getResources(), R.drawable.icn_2));

        final MenuObject addFriend = new MenuObject("Add to friends");
        addFriend.drawable = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.icn_3));

        final MenuObject addFavorite = new MenuObject("Add to favorites");
        addFavorite.setResourceValue(R.drawable.icn_4);

        final MenuObject block = new MenuObject("Block user");
        block.setResourceValue(R.drawable.icn_5);

        mos.add(close);
        mos.add(send);
        mos.add(like);
        mos.add(addFriend);
        mos.add(addFavorite);
        mos.add(block);

        return mos;
    }

    private void showContextMenuDialogFragment() {
        if (getSupportFragmentManager().findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
            contextMenuDialogFragment.show(getSupportFragmentManager(), ContextMenuDialogFragment.TAG);
        }
    }
}
