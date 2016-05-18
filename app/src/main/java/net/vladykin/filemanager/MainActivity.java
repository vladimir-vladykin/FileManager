package net.vladykin.filemanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import net.vladykin.filemanager.navigation.MainRouter;


public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private OnBackPressedListener mBackPressedListener;
    private MainRouter router;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

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
}
