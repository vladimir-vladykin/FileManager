package net.vladykin.filemanager;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import net.vladykin.filemanager.fragment.FileListFragment;


public class MainActivity extends AppCompatActivity {
    private static final String LIST_FRAGMENT_TAG = "list_fragment_tag";

    private Toolbar mToolbar;
    private OnBackPressedListener mBackPressedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if (savedInstanceState == null) {
            FileListFragment fragment = new FileListFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment, LIST_FRAGMENT_TAG);
            transaction.commit();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.menu_main, menu);
//
//        return true;
//    }


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

    @Override
    public void onBackPressed() {
        if (mBackPressedListener != null) {
            // if listener successful handled event
            if (mBackPressedListener.onBackPressed()) {
                // don't call super.OnBackPressed() and go out
                return;
            }
        }

        super.onBackPressed();
    }

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        mBackPressedListener = listener;
    }

    public interface OnBackPressedListener {
        boolean onBackPressed();
    }
}
