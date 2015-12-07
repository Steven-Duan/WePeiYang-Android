package com.rex.wepeiyang.ui.news.associationsnews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rex.wepeiyang.R;
import com.rex.wepeiyang.bean.NewsItem;
import com.rex.wepeiyang.interactor.AssociationInteractorImpl;
import com.rex.wepeiyang.ui.BaseFragment;
import com.rex.wepeiyang.ui.common.OnRcvScrollListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sunjuntao on 15/11/19.
 */
public class AssociationFragment extends BaseFragment implements AssociationView {

    @InjectView(R.id.rv_association)
    RecyclerView rvAssociation;
    @InjectView(R.id.srl_association)
    SwipeRefreshLayout srlAssociation;
    private AssociationPresenter presenter;
    private AssociationAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_association, container, false);
        ButterKnife.inject(this, view);
        presenter = new AssociationPresenterImpl(this, new AssociationInteractorImpl());
        rvAssociation.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new AssociationAdapter(getActivity());
        rvAssociation.setAdapter(adapter);
        rvAssociation.setOnScrollListener(new OnRcvScrollListener() {
            @Override
            public void onBottom() {
                super.onBottom();
                presenter.loadMoreItems();
            }
        });
        srlAssociation.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.refreshItems();
            }
        });
        presenter.refreshItems();
        return view;
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }



    @Override
    public void showRefreshing() {
        srlAssociation.setRefreshing(true);
    }

    @Override
    public void hideRefreshing() {
        srlAssociation.setRefreshing(false);
    }

    @Override
    public void loadMoreItems(List<NewsItem> items) {
        adapter.addItems(items);
    }

    @Override
    public void refreshItems(List<NewsItem> items) {
        adapter.refreshItems(items);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
