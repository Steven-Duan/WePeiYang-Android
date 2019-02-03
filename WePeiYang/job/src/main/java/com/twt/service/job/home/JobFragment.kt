package com.twt.service.job.home

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.twt.service.job.R
import com.twt.service.job.service.*

class JobFragment : Fragment(),JobHomeContract.JobHomeView {

    private lateinit var kind: String// 记录是四种类型中的哪一种
    private var type: Int = 0
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val homePresenterImp : JobHomePresenterImp = JobHomePresenterImp(this)

    companion object {
        fun newInstance(kind: String): JobFragment {
            val args = Bundle()
            args.putString(ARG_KIND, kind)
            val jobFragment = JobFragment()
            jobFragment.arguments = args
            return jobFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            kind = getString(ARG_KIND)
            type = funs.getType(kind)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.job_fragment_home, container, false)
        initView(view)
        initLoad()
        return view
    }

    private fun initView(view : View) {
        recyclerView = view.findViewById(R.id.job_rv_homepage)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.job_sr_refresh).apply {
            setColorSchemeColors(Color.parseColor("#64a388"))
        }
    }

    private fun initLoad(){
        homePresenterImp.getGeneral(kind,1)
    }

    private fun initRefresh(){

    }

    private fun initLoadMore(){

    }

    override fun showHomeFair(commonBean: List<CommonL>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showHomeMessage(importantBean: List<ImportantL>?, commonBean: List<CommonL>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showHomeRight(importantBean: List<ImportantR>?, commonBean: List<CommonR>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(msg: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNull() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}