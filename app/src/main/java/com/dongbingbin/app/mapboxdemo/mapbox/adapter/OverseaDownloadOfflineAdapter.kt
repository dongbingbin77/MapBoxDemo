package com.hotelvp.jjzx.activity.controller.member.map.adapter

import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.dongbingbin.app.mapboxdemo.AppApplication
import com.dongbingbin.app.mapboxdemo.R
import com.dongbingbin.app.mapboxdemo.StringUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

import com.hotelvp.jjzx.activity.controller.member.map.OverseaDownloadOfflineMapListActivity
import com.hotelvp.jjzx.activity.controller.member.map.adapter.model.OverseaDownloadOfflineItem

import com.hotelvp.jjzx.activity.controller.member.map.proxy.offline.OverseaOfflineMapManager

import com.mapbox.mapboxsdk.offline.OfflineManager
import com.mapbox.mapboxsdk.offline.OfflineRegion
import com.mapbox.mapboxsdk.offline.OfflineRegionError
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus
import io.paperdb.Paper

class OverseaDownloadOfflineAdapter : BaseQuickAdapter<OverseaDownloadOfflineItem, BaseViewHolder>(R.layout.oversea_download_offline_item,ArrayList<OverseaDownloadOfflineItem>()){

    val TAGs="OfflineAdapter"

    var onCompleteClickLister:(()->Unit)?=null


    var selectItem: OverseaDownloadOfflineItem?=null

    override fun convert(helper: BaseViewHolder, item: OverseaDownloadOfflineItem?) {

        //if("en" == Constant.getLanguage()){
            helper.getView<TextView>(R.id.city_name).text = item?.nameTextEN
//        }else{
//            helper.getView<TextView>(R.id.city_name).text = item?.nameTextCN
//        }



        if(item!=null&&item.offlineRegion==null) {
            if(item?.isCurrentCity){
                helper.getView<TextView>(R.id.isCurrentCity).visibility = View.VISIBLE
            }else{
                helper.getView<TextView>(R.id.isCurrentCity).visibility = View.GONE
            }
            //初始化上海区域离线地图
            OverseaOfflineMapManager.createOfflineMapRegion(AppApplication.getAppApplication(), item?.createForm, object : OfflineManager.CreateOfflineRegionCallback {
                override fun onCreate(offlineRegion: OfflineRegion?) {
                    item.offlineRegion = offlineRegion
                    getOfflineRegion(helper,item)
                }

                override fun onError(error: String?) {

                }
            })
        }else {
            getOfflineRegion(helper,item)
        }
    }

    fun getOfflineRegion(helper: BaseViewHolder, item: OverseaDownloadOfflineItem?){
        item?.offlineRegion?.getStatus(object : OfflineRegion.OfflineRegionStatusCallback {
            override fun onStatus(status: OfflineRegionStatus?) {
                try {

                    if (status != null && !status.isComplete && item.currentStatus==1) {
                        //如果没有完成，并且没有在下载，则启动
                        item?.offlineRegion?.setDownloadState(OfflineRegion.STATE_ACTIVE)
                            item?.offlineRegion?.setObserver(object : OfflineRegion.OfflineRegionObserver {
                                override fun onStatusChanged(status: OfflineRegionStatus?) {
                                    val percentage = if (status!!.requiredResourceCount >= 0) 100.0 * status.completedResourceCount / status.requiredResourceCount else 0.0

                                    if (status.isComplete) { // Download complete

                                        try {
                                            //setProgressText(helper, "complete")
                                            item?.offlineRegion?.setDownloadState(OfflineRegion.STATE_INACTIVE)
                                            item?.currentStatus = OverseaDownloadOfflineItem.CURRENT_STATUS_COMPLETE
                                            saveBook(item)

                                            var iv = helper.getView<ImageView>(R.id.item_btn)
                                            Glide.with(AppApplication.getAppApplication()).load(R.drawable.bgsh_offline_download_complete).into(iv)
                                            hideProgressText(helper)
                                        } catch (ex1: Exception) {
                                            ex1.printStackTrace()
                                        }

                                        Log.d(TAGs, "offline redownload Region downloaded successfully.")
                                    } else if (status.isRequiredResourceCountPrecise) {
                                        Log.d(TAGs, "offline redownload $percentage")
                                        try {
                                            dealDownloadingUI(helper, percentage)
                                            //helper.getView<TextView>(R.id.percent_offline).text="$percentage"
                                        } catch (ex1: Exception) {
                                            ex1.printStackTrace()
                                        }
                                        try {

                                            if (item?.currentStatus == OverseaDownloadOfflineItem.CURRENT_STATUS_PAUSE) {
                                                dealPauseUI(helper, percentage)
                                            }
                                        } catch (ex1: Exception) {
                                            ex1.printStackTrace()
                                        }
                                    }
                                }

                                override fun mapboxTileCountLimitExceeded(limit: Long) {
                                }

                                override fun onError(error: OfflineRegionError?) {
                                }
                            })

                    } else if (status != null && status.isComplete) {
                        //下载完成
                        //setProgressText(helper, "complete")
                        Glide.with(AppApplication.getAppApplication()).load(R.drawable.bgsh_offline_download_complete).into(helper.getView<ImageView>(R.id.item_btn))
                        hideProgressText(helper)
                        if(item?.currentStatus != OverseaDownloadOfflineItem.CURRENT_STATUS_COMPLETE) {
                            item?.currentStatus = OverseaDownloadOfflineItem.CURRENT_STATUS_COMPLETE
                            saveBook(item)
                            notifyDataSetChanged()
                        }
                    }else if(item.currentStatus==OverseaDownloadOfflineItem.CURRENT_STATUS_DEFAULT){
                        //初始状态
                        Glide.with(AppApplication.getAppApplication()).load(R.drawable.bgsh_offline_download2).into(helper.getView<ImageView>(R.id.item_btn))
                        hideProgressText(helper)
                    }
                        //暂时去掉暂停状态
                    else if(item.currentStatus==OverseaDownloadOfflineItem.CURRENT_STATUS_PAUSE){
                        //暂停中
                        if(status!=null) {
                            dealPauseUI(helper, 100.0 * status.completedResourceCount / status.requiredResourceCount)
                        }
                        item?.offlineRegion?.setDownloadState(OfflineRegion.STATE_INACTIVE)
                        //item?.offlineRegion?.setObserver(null)
                    }
                    else if(item.currentStatus==OverseaDownloadOfflineItem.CURRENT_STATUS_COMPLETE){
                        //下载完毕
                        Glide.with(AppApplication.getAppApplication()).load(R.drawable.bgsh_offline_download_complete).into(helper.getView<ImageView>(R.id.item_btn))
                        hideProgressText(helper)
                    }
                    helper.getView<FrameLayout>(R.id.image_layout).setOnClickListener {
                        if(!item.isDeleting) {
                            //只有不在删除中的才能点击

                            if (item?.currentStatus == OverseaDownloadOfflineItem.CURRENT_STATUS_DEFAULT) {
                                //初始状态到下载状态
                                item?.currentStatus = OverseaDownloadOfflineItem.CURRENT_STATUS_DOWNLOADING
                            }
                            //暂时去掉暂停状态
                            else if (item?.currentStatus == OverseaDownloadOfflineItem.CURRENT_STATUS_DOWNLOADING) {
                                //下载状态转换到暂停状态
                                item?.currentStatus = OverseaDownloadOfflineItem.CURRENT_STATUS_PAUSE
                            }
                            else if (item?.currentStatus == OverseaDownloadOfflineItem.CURRENT_STATUS_PAUSE) {
                                //暂停状态转到下载
                                item?.currentStatus = OverseaDownloadOfflineItem.CURRENT_STATUS_DOWNLOADING
                            } else if (item?.currentStatus == OverseaDownloadOfflineItem.CURRENT_STATUS_COMPLETE) {
                                //下载完成后
                                onCompleteClickLister?.let {
                                    it()
                                }
                                selectItem = item
                            }

                            notifyDataSetChanged()
                            saveBook(item)
                        }else{
                            println("dongbingbin 删除中。。。无法操作")
                        }
                    }
                } catch (ex1: Exception) {
                    ex1.printStackTrace()
                }
            }

            override fun onError(error: String?) {

            }
        })
    }

    fun deleteSelectItem(deleteCallback:()->Unit){
        try{
            if(selectItem!=null&&selectItem!!.offlineRegion!=null){
                selectItem?.isDeleting = true
                selectItem?.offlineRegion?.delete(object: OfflineRegion.OfflineRegionDeleteCallback {
                    override fun onDelete() {
                        selectItem?.currentStatus = OverseaDownloadOfflineItem.CURRENT_STATUS_DEFAULT
                        selectItem?.offlineRegion=null
                        saveBook(selectItem)
                        selectItem?.isDeleting = false
                        notifyDataSetChanged()
                        deleteCallback?.let {
                            it()
                        }
                    }
                    override fun onError(error: String?) {
                        deleteCallback?.let {
                            it()
                        }
                    }
                })
            }
        }catch (ex1:Exception){
            ex1.printStackTrace();
        }
    }

    fun dealDownloadingUI(helper: BaseViewHolder,progress:Double){
        try{
            Glide.with(AppApplication.getAppApplication()).load(R.drawable.bgsh_offline_pause).into(helper.getView<ImageView>(R.id.item_btn))
            setProgressText(helper, mContext?.getString(R.string.bgsh_app_offline_map_downloading)+"${progress.toInt()}%")
            helper.getView<TextView>(R.id.percent_offline).setTextColor(AppApplication.getAppApplication().resources.getColor(R.color.wh_base_ui_theme_normal_red))
        }catch ( ex1:Exception){
            ex1.printStackTrace();
        }

    }

    fun dealPauseUI(helper: BaseViewHolder,percentage:Double){
        try{
            if(mContext==null){
                return
            }
            Glide.with(AppApplication.getAppApplication()).load(R.drawable.bgsh_offline_pause2).into(helper.getView<ImageView>(R.id.item_btn))
            //hideProgressText(helper)
            showProgressText(helper)
            if(StringUtils.isValid(helper.getView<TextView>(R.id.percent_offline).text.toString())) {
                helper.getView<TextView>(R.id.percent_offline).text = helper.getView<TextView>(R.id.percent_offline).text.toString().replace(mContext.getString(R.string.bgsh_app_offline_map_downloading), mContext.getString(R.string.bgsh_app_offline_map_paused))
            }else{
                helper.getView<TextView>(R.id.percent_offline).text = mContext?.getString(R.string.bgsh_app_offline_map_paused)+"${percentage.toInt()}%"
            }

            //println("dongbingbin "+AppApplication.getAppApplication().getString(R.string.bgsh_app_offline_map_paused))
            helper.getView<TextView>(R.id.percent_offline).setTextColor(AppApplication.getAppApplication().resources.getColor(R.color.wh_base_ui_theme_normal_red))
        }catch (ex1:Exception ){
            ex1.printStackTrace();
        }

    }

    fun saveBook(item: OverseaDownloadOfflineItem?){
        var temp = OverseaDownloadOfflineItem(item?.name!!,item?.nameTextCN,item?.nameTextEN,item?.packageSize,item?.isCurrentCity,item?.createForm,null)
        temp.currentStatus = item?.currentStatus
        Paper.book(OverseaDownloadOfflineMapListActivity.book_paper_name).write(item?.name,temp)
    }

    fun hideProgressText(helper: BaseViewHolder){
        helper.getView<TextView>(R.id.percent_offline).visibility = View.GONE
    }


    fun showProgressText(helper: BaseViewHolder){
        helper.getView<TextView>(R.id.percent_offline).visibility = View.VISIBLE
    }

    fun setProgressText(helper: BaseViewHolder,text:String){
        try{
            helper.getView<TextView>(R.id.percent_offline).text=text
            helper.getView<TextView>(R.id.percent_offline).visibility = View.VISIBLE
        }catch (ex1:Exception){
            ex1.printStackTrace()
        }
    }

}