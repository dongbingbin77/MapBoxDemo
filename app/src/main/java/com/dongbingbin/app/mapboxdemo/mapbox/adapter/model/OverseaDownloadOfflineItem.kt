package com.hotelvp.jjzx.activity.controller.member.map.adapter.model

import com.google.gson.annotations.Expose
import com.hotelvp.jjzx.activity.controller.member.map.proxy.offline.model.CreateOfflineMapRegionForm

import com.mapbox.mapboxsdk.offline.OfflineRegion

class OverseaDownloadOfflineItem {

    companion object{
        const val CURRENT_STATUS_DEFAULT = 0
        const val CURRENT_STATUS_DOWNLOADING = 1
        const val CURRENT_STATUS_PAUSE = 2
        const val CURRENT_STATUS_COMPLETE = 3
    }

    var isDeleting:Boolean=false

    var name:String
    var nameTextCN:String
    var nameTextEN:String
    var packageSize:Long
    var isCurrentCity:Boolean
    var createForm: CreateOfflineMapRegionForm
    @Expose
    var hasSetObservable:Boolean=false
    @Expose
    var offlineRegion: OfflineRegion?

    var currentStatus:Int=0//0:什么都不做，1:下载中，2：暂停中 ，3：下载完毕


    constructor(name: String, nameTextCN: String, nameTextEN: String, packageSize: Long, isCurrentCity: Boolean, createForm: CreateOfflineMapRegionForm, offlineRegion: OfflineRegion?) {
        this.name = name
        this.nameTextCN = nameTextCN
        this.nameTextEN = nameTextEN
        this.packageSize = packageSize
        this.isCurrentCity = isCurrentCity
        this.createForm = createForm
        this.offlineRegion = offlineRegion
    }

    fun getSaveItem():OverseaDownloadOfflineItem{
        var temp = OverseaDownloadOfflineItem(this.name!!,this.nameTextCN,this.nameTextEN,this.packageSize,this.isCurrentCity,this.createForm,null)
        temp.currentStatus = this.currentStatus
        return temp
    }
}