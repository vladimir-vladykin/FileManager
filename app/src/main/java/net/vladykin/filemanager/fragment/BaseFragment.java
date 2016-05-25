package net.vladykin.filemanager.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import net.vladykin.filemanager.ApplicationComponent;
import net.vladykin.filemanager.FileManagerApp;
import net.vladykin.filemanager.FloatingButtonController;
import net.vladykin.filemanager.MainActivity;
import net.vladykin.filemanager.R;
import net.vladykin.filemanager.ToolbarController;
import net.vladykin.filemanager.navigation.MainRouter;

public abstract class BaseFragment extends Fragment
        implements MainActivity.OnBackPressedListener {

    protected MainActivity mActivity;
    private MaterialDialog mMessageDialog;
    private ToolbarController mToolbarController;
    private FloatingButtonController mFloatingButtonController;

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        // todo change to onAttach(Context)
//
//    }

    @Override
    public void onAttach(Context context) {
        // todo test it on low sdk devices
        super.onAttach(context);
        mActivity = (MainActivity) getActivity();
        mActivity.setOnBackPressedListener(this);

        mToolbarController = (ToolbarController) getActivity();
        mFloatingButtonController = (FloatingButtonController) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // prepare message dialog
        mMessageDialog = new MaterialDialog.Builder(mActivity)
                .positiveText(R.string.ok)
                .positiveColorRes(R.color.primary)
                .build();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFloatingButtonController.hideActionButton();
    }

    protected MainRouter getRouter() {
        return mActivity.getRouter();
    }

    public FloatingButtonController getFloatingButtonController() {
        return mFloatingButtonController;
    }

    protected void showBackButton() {
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void hideBackButton() {
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    protected void setToolbarTitle(@StringRes int titleId) {
        setToolbarTitle(getResources().getString(titleId));
    }

    protected void setToolbarTitle(@Nullable CharSequence title) {
        mToolbarController.setToolbarTitle(title);
    }

    // todo probably delete
    protected void showMessage(int titleId, int contentId) {
        showMessage(titleId, getString(contentId));
    }

    protected void showMessage(int titleId, String content) {
        mMessageDialog.setTitle(titleId);
        mMessageDialog.setContent(content);
        mMessageDialog.show();
    }

    // todo this should be showMessage instead of methods with dialogs!!!
    protected void showSnackbarMessage(@StringRes int messageId) {
        View rootView = getView();
        if (rootView == null) {
            Log.e(getTag(), "Cannot show message, because fragment's root view is null");
            return;
        }
        Snackbar.make(rootView, messageId, Snackbar.LENGTH_LONG)
                .show();
    }

    protected void showMessageWithAction(CharSequence message,
                                         CharSequence actionText,
                                         Runnable action) {
        View rootView = getView();
        if (rootView == null) {
            Log.e(getTag(), "Cannot show message, because fragment's root view is null");
            return;
        }
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
                .setAction(actionText, v -> action.run())
                .show();
    }

    protected void showInputDialog(int titleId, String prefillText, int hintId, MaterialDialog.InputCallback callback) {
        MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
                .title(titleId)
                .positiveText(R.string.ok)
                .input(getString(hintId), prefillText, callback)
                .contentColorRes(R.color.primary)
                .positiveColorRes(R.color.primary)
                .build();
        dialog.show();
    }

    protected ApplicationComponent appComponent() {
        return FileManagerApp.component();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
