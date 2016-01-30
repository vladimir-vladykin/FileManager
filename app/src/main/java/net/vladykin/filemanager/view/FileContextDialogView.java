package net.vladykin.filemanager.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.vladykin.filemanager.R;

import java.lang.ref.WeakReference;

/**
 * Created by Vladimir on 05.04.2015.
 *
 * View for show context dialog.
 */
public class FileContextDialogView extends LinearLayout implements View.OnClickListener {

    private int mPosition;
    private WeakReference<ContextItemClickListener> mListenerReference;

    public FileContextDialogView(Context context) {
        super(context);
        mPosition = -1;
        mListenerReference = new WeakReference<>(null);
        init();
    }

    public void setContextItemClickListener(ContextItemClickListener listener) {
        mListenerReference = new WeakReference<>(listener);
    }

    public void bindToItem(int position) {
        mPosition = position;
    }

    private void init() {
        inflate(getContext(), R.layout.dialog_file, this);
        findViewById(R.id.context_button_open).setOnClickListener(this);
        findViewById(R.id.context_button_copy).setOnClickListener(this);
        findViewById(R.id.context_button_move).setOnClickListener(this);
        findViewById(R.id.context_button_rename).setOnClickListener(this);
        findViewById(R.id.context_button_remove).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        ContextItemClickListener listener = mListenerReference.get();
        if(listener != null) {
            switch (v.getId()) {
                case R.id.context_button_open:
                    listener.onOpenClick(mPosition);
                    break;
                case R.id.context_button_copy:
                    listener.onCopyClick(mPosition);
                    break;
                case R.id.context_button_move:
                    listener.onMoveClick(mPosition);
                    break;
                case R.id.context_button_rename:
                    listener.onRenameClick(mPosition);
                    break;
                case R.id.context_button_remove:
                    listener.onRemoveClick(mPosition);
                    break;
            }
        }
    }

    public interface ContextItemClickListener {
        void onOpenClick(int position);
        void onCopyClick(int position);
        void onMoveClick(int position);
        void onRenameClick(int position);
        void onRemoveClick(int position);

    }
}
