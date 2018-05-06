package com.twtstudio.service.dishesreviews.base

import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

abstract class BaseMutableListAdapter(
        val list: MutableList<Any>,
        val context: Context,
        internal val owner: LifecycleOwner
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val inflater: LayoutInflater = LayoutInflater.from(context)
    override abstract fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder

    override abstract fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int)

    override fun getItemCount(): Int {
        return list.size
    }
}