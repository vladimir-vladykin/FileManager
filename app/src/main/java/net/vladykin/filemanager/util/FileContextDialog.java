package net.vladykin.filemanager.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;

import net.vladykin.filemanager.R;
import net.vladykin.filemanager.view.FileContextDialogView;

/**
 * Created by Vladimir on 05.04.2015.
 *
 * Class for show file context menu.
 */
public class FileContextDialog {
    private Context mContext;
    private Dialog mDialog;
    private FileContextDialogView mView;

    public FileContextDialog(Context context) {
        mContext = context;
        mDialog = create();
    }

    public void show() {
//        setPreDrawListener();
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    public void bindToItem(int position) {
        mView.bindToItem(position);
    }

    public void setContextItemClickListener(FileContextDialogView.ContextItemClickListener listener) {
        mView.setContextItemClickListener(listener);
    }

    private Dialog create() {
        Dialog dialog = new Dialog(mContext, R.style.Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mView = new FileContextDialogView(mContext);
        dialog.setContentView(mView);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        return dialog;
    }

    private void setPreDrawListener() {
        mView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mView.getViewTreeObserver().removeOnPreDrawListener(this);
                startIntroAnimation();
                return true;
            }
        });
    }

    private void startIntroAnimation() {
        mView.setScaleY(0.1f);
        mView.setPivotY(0);

        mView.animate()
                .scaleY(1)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(300)
                .start();
    }

}
