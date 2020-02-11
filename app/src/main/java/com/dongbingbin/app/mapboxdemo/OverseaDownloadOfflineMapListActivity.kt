package com.hotelvp.jjzx.activity.controller.member.map

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dongbingbin.app.mapboxdemo.R
import com.dongbingbin.app.mapboxdemo.StringUtils
import com.hotelvp.jjzx.activity.controller.member.map.adapter.OverseaDownloadOfflineAdapter
import com.hotelvp.jjzx.activity.controller.member.map.adapter.model.OverseaDownloadOfflineItem
import com.hotelvp.jjzx.activity.controller.member.map.proxy.offline.model.CreateOfflineMapRegionForm

import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.offline.OfflineRegion
import io.paperdb.Paper
import kotlinx.android.synthetic.main.bgsh_oversea_download_offlin_map_list_activity.*

class OverseaDownloadOfflineMapListActivity : AppCompatActivity(){

    companion object{

        var book_paper_name="offline_map_paper_book"

        const val shanghai_offline_map="shanghai"

        fun entryActivityContext(context: Context){

            val intent = Intent(context, OverseaDownloadOfflineMapListActivity::class.java)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bgsh_oversea_download_offlin_map_list_activity)
        initUI()
        initData()
        initUIData()
    }


    private val offlineMapRegions = ArrayList<OfflineRegion?>()

    var adapter: OverseaDownloadOfflineAdapter = OverseaDownloadOfflineAdapter()



    fun initUI(){





        dialog_view_back.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View?) {
                //隐藏弹框
                dialog_view.visibility = View.GONE
            }
        })
        dialog_view_cancel.setOnClickListener {
            //隐藏弹框
            dialog_view.visibility = View.GONE
        }

        dialog_view_delete.setOnClickListener {
            //删除选中地图
           // WHServiceRouterImpl.getInstance().router(this, "botaoota://BTPublicPlugin/openLoadingV")
            adapter.deleteSelectItem {
             //   WHServiceRouterImpl.getInstance().router(this, "botaoota://BTPublicPlugin/closeLoadingV")
            }
            dialog_view.visibility= View.GONE
        }

        dialog_view_view_map.setOnClickListener {
            //跳转离线地图
//            adapter.selectItem

            try{
                //OverseaOfflineMapDetailActivity.entryActivityContext(this@OverseaDownloadOfflineMapListActivity,adapter.selectItem?.name!!)
            }catch (ex1:Exception ){
                ex1.printStackTrace()
            }

        }

        try{
            //var lp = jj_webc_actyvity_navbar.getLayoutParams() as RelativeLayout.LayoutParams;
//            var top2 = DeviceUtil.getStatusBarHeight(applicationContext)
//            lp.topMargin = top2
//            jj_webc_actyvity_navbar.layoutParams = lp
        }catch ( ex1:Exception){
            ex1.printStackTrace();
        }

    }

    fun initData(){

        var itemShanghai = Paper.book(book_paper_name).read<OverseaDownloadOfflineItem>(shanghai_offline_map)

        if(itemShanghai==null|| StringUtils.isEmpty(itemShanghai.name)) {
            itemShanghai = OverseaDownloadOfflineItem(shanghai_offline_map, "上海", "Shanghai", 0, true,
                    CreateOfflineMapRegionForm(shanghai_offline_map, LatLng(31.809425, 122.102298)
                            , LatLng(30.729557, 120.893802)), null)

            Paper.book(book_paper_name).write(shanghai_offline_map,itemShanghai)
        }
//        if(appApplication!=null&&appApplication.locationBean!=null){
//            if(appApplication.locationBean.city.replace("市","").equals(itemShanghai.nameTextCN)
//                    ||appApplication.locationBean.city.equals(itemShanghai.nameTextEN,true)){
                itemShanghai.isCurrentCity=true
//            }
//        }

        adapter.addData(itemShanghai)


//        var itemNanjing = Paper.book(book_paper_name).read<OverseaDownloadOfflineItem>("nanjing")
//
//        if(itemNanjing==null||StringUtils.isEmpty(itemNanjing.name)) {
//            itemNanjing = OverseaDownloadOfflineItem("nanjing", "南京", "Nanjing", 0, true,
//                    CreateOfflineMapRegionForm("nanjing", LatLng(32.156134, 119.000919)
//                            , LatLng(31.946621, 118.498294)), null)
//
//            Paper.book(book_paper_name).write("nanjing",itemNanjing)
//        }
//        adapter.addData(itemNanjing)

        adapter.onCompleteClickLister={
            dialog_view.visibility= View.VISIBLE
        }



        //初始化上海区域离线地图
//        OverseaOfflineMapManager.createOfflineMapRegion(this, CreateOfflineMapRegionForm("shanghai", LatLng(31.809425, 122.102298)
//        , LatLng(30.729557, 120.893802)),object:OfflineManager.CreateOfflineRegionCallback{
//            override fun onCreate(offlineRegion: OfflineRegion?) {
//                if(!isExistList(offlineRegion)){
//                    offlineMapRegions.add(offlineRegion)
//                    adapter.addData(offlineMapRegions)
//                }
//            }
//            override fun onError(error: String?) {
//
//            }
//        })
    }

    private fun initUIData(){
        oversea_offline_map_list.adapter = adapter
        oversea_offline_map_list.layoutManager = LinearLayoutManager(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        try{
            adapter.data.forEach {
                if (it!=null){
                    it.offlineRegion?.setObserver(null)
                }
            }
        }catch (ex1:Exception ){
            ex1.printStackTrace();
        }

    }

    override fun onDestroy() {
        try {
            
        }catch (exp:Exception){

        }
        super.onDestroy()
    }

    //    fun isExistList(offlineRegion: OfflineRegion?):Boolean {
//        var result:Boolean=false;
//        var form2:CreateOfflineMapRegionForm? = null;
//        form2 = JsonUtils.fromJsonString(String(offlineRegion?.metadata!!),CreateOfflineMapRegionForm::class.java)
//
//        if(offlineMapRegions.isEmpty()){
//            return false
//        }else{
//            offlineMapRegions.forEach {
//
//                var form1:CreateOfflineMapRegionForm? = null;
//                form1 = JsonUtils.fromJsonString(String(it?.metadata!!),CreateOfflineMapRegionForm::class.java)
//                if(form1!=null&&form2!=null&& form1.name ==form2.name){
//                    result=true
//                    return@forEach
//                }
//            }
//        }
//        return result
//    }

}