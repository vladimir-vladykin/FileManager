package net.vladykin.filemanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.Resources;
import android.nfc.Tag;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ViewAnimator;

import net.vladykin.filemanager.fragment.FileListFragment;


public class MainActivity extends AppCompatActivity {
    private static final String LIST_FRAGMENT_TAG = "list_fragment_tag";
    private static final int TOOLBAR_ANIMATION_DURATION = 300;

    private Toolbar mToolbar;
    private OnBackPressedListener mBackPressedListener;
    private boolean isPendingIntroAnimation;
    private ToolbarAnimationListener mToolbarAnimationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            isPendingIntroAnimation = true;
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //start file list fragment
        FileListFragment fragment;

        if (savedInstanceState != null) {
            fragment = (FileListFragment) getSupportFragmentManager().findFragmentByTag(LIST_FRAGMENT_TAG);
        } else {
            fragment = new FileListFragment();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, LIST_FRAGMENT_TAG);
        transaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (isPendingIntroAnimation) {
            isPendingIntroAnimation = false;
            startIntroAnimation();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void startIntroAnimation() {
        final int actionBarSize = dpToPixel(56);
        mToolbar.setTranslationY(-actionBarSize);
        mToolbar.animate()
                .translationY(0)
                .setDuration(TOOLBAR_ANIMATION_DURATION)
                .setStartDelay(300)
                .start();

        int delay = 400;
        final int delayStep = 200;

        //go through all children of toolbar and animate it
        for (int i = 0; i < mToolbar.getChildCount(); i++, delay += delayStep) {
            View child = mToolbar.getChildAt(i);
            child.setTranslationY(-actionBarSize);
            ViewPropertyAnimator childAnimator = child.animate();

            //if it is last child
            if (i == mToolbar.getChildCount() - 1) {
                //set animation listener
                childAnimator.setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (mToolbarAnimationListener != null) {
                            mToolbarAnimationListener.onAnimationEnd();
                        }
                    }
                });
            }

            childAnimator
                    .translationY(0)
                    .setDuration(TOOLBAR_ANIMATION_DURATION)
                    .setStartDelay(delay)
                    .start();
        }
    }

    private int dpToPixel(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public void onBackPressed() {
        if (mBackPressedListener != null) {
            //if listener successful handled event
            if (mBackPressedListener.onBackPressed()) {
                //don't call super.OnBackPressed() and go out
                return;
            }
        }

        super.onBackPressed();
    }

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        mBackPressedListener = listener;
    }

    public void setToolbarAnimationListener(ToolbarAnimationListener listener) {
        mToolbarAnimationListener = listener;
    }

    public interface OnBackPressedListener {
        boolean onBackPressed();
    }

    public interface ToolbarAnimationListener {
        void onAnimationEnd();
    }
}
