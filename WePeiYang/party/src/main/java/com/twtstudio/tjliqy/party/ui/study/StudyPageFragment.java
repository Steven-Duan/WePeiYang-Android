package com.twtstudio.tjliqy.party.ui.study;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.twtstudio.tjliqy.party.R;
import com.twtstudio.tjliqy.party.R2;
import com.twtstudio.tjliqy.party.bean.CourseInfo;
import com.twtstudio.tjliqy.party.bean.TextInfo;
import com.twtstudio.tjliqy.party.interactor.StudyInteractorImpl;
import com.twtstudio.tjliqy.party.support.ResourceHelper;
import com.twtstudio.tjliqy.party.ui.BaseFragment;

import java.util.List;

import butterknife.BindView;

/**
 * Created by dell on 2016/7/19.
 */
public class StudyPageFragment extends BaseFragment implements StudyView{

    public static  final  String ARG_PAGE = "ARG_PAGE";

    private StudyPresenter presenter;

    private StudyCourseAdapter courseAdapter;

    private StudyTextAdapter textAdapter;

    private static final String TAG = "StudyPageFragment";

    @BindView(R2.id.rv_study)
    RecyclerView recyclerView;
    @BindView(R2.id.tv_loading)
    TextView tvLoading;

    public static StudyPageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        StudyPageFragment pageFragment = new StudyPageFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_party_study_page;
    }

    @Override
    public void preInitView() {

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        switch (getArguments().getInt(ARG_PAGE)){
            case 1:
                courseAdapter = new StudyCourseAdapter(getContext());
                recyclerView.setAdapter(courseAdapter);
                break;
            case 2:
                textAdapter = new StudyTextAdapter(getContext());
                recyclerView.setAdapter(textAdapter);
        }

    }

    @Override
    public void initView() {
        presenter = new StudyPresenterImpl(this,new StudyInteractorImpl());
        switch (getArguments().getInt(ARG_PAGE)){
            case 1:
                presenter.getCourse();
                break;
            case 2:
                presenter.getText();
                break;
        }
    }

    @Override
    public void afterInitView() {

    }

    @Override
    public void create() {
    }

    @Override
    public void bindCourseData(List<CourseInfo> courseInfos) {
        tvLoading.setVisibility(View.GONE);
        courseAdapter.addCourse(courseInfos);
    }

    @Override
    public void bindTextData(List<TextInfo> textInfos) {
        tvLoading.setVisibility(View.GONE);
        textAdapter.addText(textInfos);
    }

    @Override
    public void onFailure() {
        tvLoading.setText(ResourceHelper.getString(R.string.toast_network_failed));
    }

    @Override
    public void toastMsg(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}