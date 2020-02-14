package com.dongbingbin.app.mapboxdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dongbingbin.app.mapboxdemo.mapbox.DownloadCallback
import com.dongbingbin.app.mapboxdemo.mapbox.MapBoxInitProxy
import com.hotelvp.jjzx.activity.controller.member.map.OverseaDownloadOfflineMapListActivity
import com.mapbox.mapboxsdk.offline.OfflineRegion
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
        MapBoxInitProxy.init(this)
        MapBoxInitProxy.setDownloadCallback(DownloadCallback { progress, isComplete ->
            if(isComplete){
                progress_txt.text = "100%"
            }else {
                progress_txt.text = "${progress}%"
            }
            progress_txt.setTextColor(application.resources.getColor(R.color.color_green))
        })

    }

    fun initUI(){
        start_btn.setOnClickListener {
            MapBoxInitProxy.restartDownLoad(this@MainActivity)
        }

        pause_btn.setOnClickListener {
            MapBoxInitProxy.pauseDownload()
            progress_txt.setTextColor(application.resources.getColor(R.color.color_red))
        }

        delete_btn.setOnClickListener {
//            if(MapBoxInitProxy.offlineRegion1!=null) {
//                start_btn.isEnabled = false
//                pause_btn.isEnabled = false
//                MapBoxInitProxy.offlineRegion1?.delete(object :
//                    OfflineRegion.OfflineRegionDeleteCallback {
//                    override fun onDelete() {
//                        start_btn.isEnabled = true
//                        pause_btn.isEnabled = true
//                        MapBoxInitProxy.offlineRegion1 = null
//                    }
//
//                    override fun onError(error: String?) {
//                    }
//                })
//            }
            MapBoxInitProxy.deleteAll(this@MainActivity,object:MapBoxInitProxy.OnDeleteCallback{
                override fun onStart() {
                    start_btn.isEnabled = false
                    pause_btn.isEnabled = false
                }

                override fun onEnd() {
                    start_btn.isEnabled = true
                    pause_btn.isEnabled = true
                }
            })
        }

        go_to_offline_map.setOnClickListener {
//            throw Exception("2292929")
            OverseaDownloadOfflineMapListActivity.entryActivityContext(this@MainActivity)
        }
    }

}
