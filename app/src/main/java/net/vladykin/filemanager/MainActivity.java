package net.vladykin.filemanager;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import net.vladykin.filemanager.navigation.MainRouter;


public class MainActivity extends AppCompatActivity
        implements ToolbarController, FloatingButtonController {

    private Toolbar mToolbar;
    private FloatingActionButton mFloatingButton;
    private OnBackPressedListener mBackPressedListener;
    private MainRouter router;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mFloatingButton = (FloatingActionButton) findViewById(R.id.floating_action_button);
        setFloatingButtonBehaviorActive(false);

        router = new MainRouter(getSupportFragmentManager(), R.id.container);

        if (savedInstanceState == null) {
            router.onAppStarted();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public MainRouter getRouter() {
        return router;
    }

    @Override
    public void setToolbarTitle(@Nullable CharSequence title) {
        if (mToolbar != null) {
            mToolbar.setTitle(title);
        }
    }

    @Override
    public void setToolbarElevation(int elevation) {
        if (mToolbar != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setElevation(elevation);
        }
    }

    @Override
    public void showActionButton(View.OnClickListener clickListener) {
        mFloatingButton.setOnClickListener(clickListener);
        mFloatingButton.setVisibility(View.VISIBLE);
        setFloatingButtonBehaviorActive(true);
    }

    @Override
    public void hideActionButton() {
        mFloatingButton.setOnClickListener(null);
        mFloatingButton.setVisibility(View.GONE);
        setFloatingButtonBehaviorActive(false);
    }

    @Override
    public void onBackPressed() {
        if (mBackPressedListener != null) {
            // if listener successful handled event
            if (mBackPressedListener.onBackPressed()) {
                // don't call super.OnBackPressed() and go out
                return;
            }
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            super.onBackPressed();
            return;
        }

        // the last fragment, so finish activity
        finish();
    }

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        mBackPressedListener = listener;
    }

    public interface OnBackPressedListener {
        boolean onBackPressed();
    }

    private void setFloatingButtonBehaviorActive(boolean active) {
        // todo behavior temporary disabled, I want it be always visible now for stability
//        if (mFloatingButton == null) {
//            return;
//        }
//
//        CoordinatorLayout.LayoutParams params =
//                (CoordinatorLayout.LayoutParams) mFloatingButton.getLayoutParams();
//        ScrollAwareFloatingBehavior behavior = (ScrollAwareFloatingBehavior) params.getBehavior();
//        behavior.setBehaviorActive(active);
    }
}
