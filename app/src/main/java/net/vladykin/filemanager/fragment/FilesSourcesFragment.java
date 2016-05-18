package net.vladykin.filemanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.vladykin.filemanager.entity.FileSourceItem;
import net.vladykin.filemanager.presenter.FilesSourcesPresenter;
import net.vladykin.filemanager.view.FilesSourcesView;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * Fragment for let user to choose
 * file source.
 *
 * @author Vladimir Vladykin
 */
public final class FilesSourcesFragment extends BaseFragment implements FilesSourcesView {

    @Inject FilesSourcesPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        component().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(0/*todo layout*/, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        presenter.bindView(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.unbindView(this);
    }

    @Override
    public void setFilesSources(List<FileSourceItem> sources) {
        // todo show files list
    }

    @Override
    public void showFilesHierarchy(FileSourceItem source) {
        // todo start FileListFragment
    }
}
