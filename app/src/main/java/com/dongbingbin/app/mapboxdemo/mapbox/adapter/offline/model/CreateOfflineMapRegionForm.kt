package com.hotelvp.jjzx.activity.controller.member.map.proxy.offline.model

import com.mapbox.mapboxsdk.geometry.LatLng

class CreateOfflineMapRegionForm {
    var name:String

    var northEast: LatLng

    var southWest:LatLng

    var minZoom:Double = 0.0
    var maxZoom:Double = 12.0

    constructor(name: String, northEast: LatLng, southWest: LatLng) {
        this.name = name
        this.northEast = northEast
        this.southWest = southWest
    }
}