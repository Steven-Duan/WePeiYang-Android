package com.twt.service.job.search

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import com.twt.service.job.service.*
import com.twt.service.job.R

class JobSearchActivity : AppCompatActivity() {

    private lateinit var back: ImageView
    private lateinit var searchEditText: EditText
    private lateinit var searchImageView: ImageView
    val manager = supportFragmentManager
    var ft = manager.beginTransaction()
    private var historyFragment: Fragment = JobSearchHistoryFragment()
    private var resultFragment: Fragment = JobSearchResultFragment()
    lateinit var keyword: String private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.job_activity_search)
        window.statusBarColor = resources.getColor(R.color.job_green)
        bindid()
        historyFragment = JobSearchHistoryFragment()
        ft.add(R.id.job_search_fl_fragment, historyFragment).commit()
        back.setOnClickListener { onBackPressed() }
        searchImageView.setOnClickListener {
            if (searchEditText.text.trim().toString() != "") {
                search(searchEditText.text.trim().toString())
            }
        }
        searchEditText.setOnEditorActionListener { _, actionId, event ->
            var flag = true
            if (actionId == EditorInfo.IME_ACTION_SEND || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                if (searchEditText.text.trim().toString() != "") {
                    search(searchEditText.text.trim().toString())
                }
            } else {
                flag = false
            }
            flag
        }
    }

    private fun bindid() {
        back = findViewById(R.id.job_search_iv_back)
        searchEditText = findViewById(R.id.job_search_et_input)
        searchImageView = findViewById(R.id.job_search_iv_icon)
    }

    @SuppressLint("CommitTransaction")
    fun search(keyword: String) {
        this.keyword = keyword
        searchHistory.add(keyword)
        if (searchHistory.size > 10) searchHistory.remove(searchHistory.first())
        if (searchEditText.text.trim().toString()!=keyword) searchEditText.setText(keyword.toCharArray(),0,keyword.length)
        ft = manager.beginTransaction()
        ft.remove(historyFragment)
        resultFragment = JobSearchResultFragment()
        ft.replace(R.id.job_search_fl_fragment, resultFragment).commit()
        searchEditText.setOnClickListener { showHistory() }
    }

    @SuppressLint("CommitTransaction")
    private fun showHistory() {
        ft=manager.beginTransaction()
        ft.remove(resultFragment)
        historyFragment = JobSearchHistoryFragment()
        ft.replace(R.id.job_search_fl_fragment, historyFragment)
        ft.commit()
    }
}
