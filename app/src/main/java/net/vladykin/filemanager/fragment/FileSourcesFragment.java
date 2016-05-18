package net.vladykin.filemanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.vladykin.filemanager.R;
import net.vladykin.filemanager.adapter.FileSourcesAdapter;
import net.vladykin.filemanager.entity.FileSourceItem;
import net.vladykin.filemanager.presenter.FilesSourcesPresenter;
import net.vladykin.filemanager.util.FileModule;
import net.vladykin.filemanager.view.FilesSourcesView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Fragment for let user to choose
 * file source.
 *
 * @author Vladimir Vladykin
 */
public final class FileSourcesFragment extends BaseFragment
        implements FilesSourcesView, FileSourcesAdapter.FileSourceClickListener {

    @Inject FilesSourcesPresenter presenter;
    @Bind(R.id.recycler_view) RecyclerView recyclerView;

    private FileSourcesAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appComponent()
                .addPresenterSubComponent(new FileModule())
                .inject(this);

        adapter = new FileSourcesAdapter();
        adapter.setClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_sources, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setupRecyclerView();
        presenter.bindView(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.unbindView(this);
    }

    @Override
    public void setFilesSources(List<FileSourceItem> sources) {
        adapter.setItems(sources);
    }

    @Override
    public void displayFilesHierarchy(FileSourceItem sourceItem) {
        getRouter().onFileSourceChosen(sourceItem.getSource());
    }

    @Override
    public void onFileSourceClick(FileSourceItem source) {
        presenter.onFileSourceClick(source);
    }

    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }
}
