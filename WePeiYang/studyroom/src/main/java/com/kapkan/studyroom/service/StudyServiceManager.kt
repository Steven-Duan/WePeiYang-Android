package com.kapkan.studyroom.service

import com.twt.wepeiyang.commons.experimental.preference.CommonPreferences

object StudyServiceManager{

    fun getDate() = SelfStudyApi.getClassTable()

    fun star(id:String) = SelfStudyApi.starClassroom(id)

    fun unStar(id:String) = SelfStudyApi.unStarClassroom(id)

    fun getBuildingList() = SelfStudyApi.getBuildingList()

    fun getAvailableRoom(week:Int,day:Int) = SelfStudyApi.getAvaliableRoom(week,day)

    fun getAvailableRoombyCourse(week:Int,day:Int,course:Int) = SelfStudyApi.getAvaliableRoombyClass(week,day,course)

    fun getCollection() = SelfStudyApi.getCollectionList()

    fun login() = SelfStudyApi.login(getToken())

    fun getToken(): String {
        return "Bearer{${CommonPreferences.token}}"
    }
}