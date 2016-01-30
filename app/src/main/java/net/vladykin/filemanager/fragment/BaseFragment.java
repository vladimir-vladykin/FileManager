package net.vladykin.filemanager.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;

import net.vladykin.filemanager.MainActivity;
import net.vladykin.filemanager.R;

/**
 * Created by Владимир on 22.02.2015.
 */
public abstract class BaseFragment extends Fragment implements MainActivity.OnBackPressedListener, MainActivity.ToolbarAnimationListener {
    protected MainActivity mActivity;

    private MaterialDialog mMessageDialog;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
        mActivity.setOnBackPressedListener(this);
        mActivity.setToolbarAnimationListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // prepare message dialog
        mMessageDialog = new MaterialDialog.Builder(mActivity)
                .positiveText(R.string.ok)
                .positiveColor(R.color.primary)
                .build();
    }

    protected void showBackButton() {
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void hideBackButton() {
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    protected void showMessage(int titleId, int contentId) {
        showMessage(titleId, getString(contentId));
    }

    protected void showMessage(int titleId, String content) {
        mMessageDialog.setTitle(titleId);
        mMessageDialog.setContent(content);
        mMessageDialog.show();
    }

    protected void showInputDialog(int titleId, String prefillText, int hintId, MaterialDialog.InputCallback callback) {
        MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
                .title(titleId)
                .positiveText(R.string.ok)
                .input(getString(hintId), prefillText, callback)
                .contentColorRes(R.color.primary)
                .positiveColor(R.color.primary)
                .build();
        dialog.show();
    }

    @Override
    public abstract boolean onBackPressed();

    @Override
    public abstract void onAnimationEnd();
}
