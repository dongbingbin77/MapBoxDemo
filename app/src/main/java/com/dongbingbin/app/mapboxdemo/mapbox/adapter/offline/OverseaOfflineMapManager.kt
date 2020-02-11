package com.hotelvp.jjzx.activity.controller.member.map.proxy.offline

import android.content.Context
import android.util.Log
import com.dongbingbin.app.mapboxdemo.StringUtils
import com.dongbingbin.app.mapboxdemo.mapbox.JsonUtils

import com.hotelvp.jjzx.activity.controller.member.map.proxy.offline.model.CreateOfflineMapRegionForm

import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.offline.*
import com.mapbox.mapboxsdk.offline.OfflineManager.ListOfflineRegionsCallback
import java.nio.charset.Charset

class OverseaOfflineMapManager {

    companion object{
        const val TAG="OverseaOfflineMap"
        fun createOfflineMapRegion(context: Context?,form: CreateOfflineMapRegionForm,createCallback:OfflineManager.CreateOfflineRegionCallback) {
            val offlineManager = OfflineManager.getInstance(context!!)
            offlineManager.listOfflineRegions(object : ListOfflineRegionsCallback {

                override fun onList(offlineRegions: Array<OfflineRegion>) {
                    var isExist=false
                    offlineRegions.forEach {
                        val metadataString = String(it.metadata)
                        if(StringUtils.isValid(metadataString)){
                            val metaForm = JsonUtils.fromJsonString(metadataString,CreateOfflineMapRegionForm::class.java)
                            if(metaForm!=null&&metaForm.name==form.name){
                                //已经在队列中可以直接返回

                                isExist=true
                                createCallback?.onCreate(it)
                            }
                        }
                    }
                    if(!isExist|| offlineRegions.isEmpty()){
                        createOffline(context,form,createCallback)
                    }
                }
                override fun onError(error: String) {
                    createOffline(context,form,createCallback)
                }
            })
        }

        fun createOffline(context: Context?,form: CreateOfflineMapRegionForm,createCallback:OfflineManager.CreateOfflineRegionCallback){
            val offlineManager = OfflineManager.getInstance(context!!)
            val latLngBounds = LatLngBounds.Builder()
                    .include(form.northEast) // Northeast
                    .include(form.southWest) // Southwest
                    .build()

            val definition = OfflineTilePyramidRegionDefinition(
                    "mapbox://styles/mapbox/streets-v11",
                    latLngBounds,
                    form.minZoom,
                    form.maxZoom,
                    context!!.resources.displayMetrics.density
            )
            // Implementation that uses JSON to store Yosemite National Park as the offline region name.
            val metadata: ByteArray?
            metadata = try {

                val json = JsonUtils.toJsonString(form)
                json.toByteArray(Charset.defaultCharset())
            } catch (exception: Exception) {
                Log.e(TAG, "Failed to encode metadata: " + exception.message)
                null
            }
            // Create the region asynchronously
            offlineManager.createOfflineRegion(definition, metadata!!, createCallback)
        }


        fun listAllOfflineMapRegion(context:Context?){
            val offlineManager = OfflineManager.getInstance(context!!)
            offlineManager.listOfflineRegions(object : ListOfflineRegionsCallback {
                override fun onList(offlineRegions: Array<out OfflineRegion>?) {

                }

                override fun onError(error: String?) {

                }
            })
        }

    }

}