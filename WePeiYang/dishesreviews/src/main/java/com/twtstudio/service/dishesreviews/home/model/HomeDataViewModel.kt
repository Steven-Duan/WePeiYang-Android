package com.twtstudio.service.dishesreviews.home.model

import android.arch.lifecycle.ViewModel
import com.twt.wepeiyang.commons.experimental.cache.*
import com.twt.wepeiyang.commons.experimental.network.CommonBody
import com.twtstudio.service.dishesreviews.model.DishesHomeBean
import com.twtstudio.service.dishesreviews.model.DishesService

class HomeDataViewModel : ViewModel() {
    private val homeBeanLocalData = Cache.hawk<DishesHomeBean>("DishesHome")
    private val homeBeanRemote = Cache.from(DishesService.Companion::getHomeInfo).map(CommonBody<DishesHomeBean>::data)
    val homeBeanLiveData = RefreshableLiveData.use(homeBeanLocalData, homeBeanRemote)
}