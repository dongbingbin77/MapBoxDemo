package com.dongbingbin.app.mapboxdemo.mapbox;

import android.content.Context;
import android.util.Log;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;

import org.json.JSONObject;

import static com.mapbox.mapboxsdk.offline.OfflineRegion.STATE_INACTIVE;


public class MapBoxInitProxy {
    private final static String TAG="MapBoxInitProxy";

    public static void init(Context context){
        Mapbox.getInstance(context, "");
    }


    public static OfflineRegion offlineRegion1;

    static DownloadCallback downloadCallback;

    public static void setDownloadCallback(DownloadCallback downloadCallback) {
        MapBoxInitProxy.downloadCallback = downloadCallback;
    }

    public static void pauseDownload(){
        if(offlineRegion1!=null){
            offlineRegion1.setDownloadState(STATE_INACTIVE);
        }
    }

    public static void restartDownLoad(Context context){
        if(offlineRegion1!=null){
            offlineRegion1.setDownloadState(OfflineRegion.STATE_ACTIVE);
        }else{
            downloadOffline(context);
        }
    }

    public static void downloadOffline(Context context){
        // Define region of map tiles
// Set up the OfflineManager
        OfflineManager offlineManager = OfflineManager.getInstance(context);

        offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
            @Override
            public void onList(OfflineRegion[] offlineRegions) {
                System.out.println(TAG+":已缓存的离线地图总数："+offlineRegions.length);
                for(final OfflineRegion region:offlineRegions){
                    //offlineRegion1 = region;
                    System.out.println(TAG+":已缓存的离线地图："+new String(region.getMetadata()));
                    region.getStatus(new OfflineRegion.OfflineRegionStatusCallback() {
                        @Override
                        public void onStatus(OfflineRegionStatus status) {
                            if(!status.isComplete()&&status.getDownloadState()!=OfflineRegion.STATE_ACTIVE){
                                region.setDownloadState(OfflineRegion.STATE_ACTIVE);
                                region.setObserver(new OfflineRegion.OfflineRegionObserver() {
                                    @Override
                                    public void onStatusChanged(OfflineRegionStatus status) {
                                        double percentage = status.getRequiredResourceCount() >= 0
                                                ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                                                0.0;

                                        if (status.isComplete()) {
// Download complete
                                            region.setDownloadState(STATE_INACTIVE);
                                            if(downloadCallback!=null){
                                                downloadCallback.callBack(100,true);
                                            }
                                            Log.d(TAG, "offline redownload Region downloaded successfully.");
                                        } else if (status.isRequiredResourceCountPrecise()) {
                                            Log.d(TAG, "offline redownload "+percentage+"");
                                            if(downloadCallback!=null){
                                                downloadCallback.callBack(percentage,false);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(OfflineRegionError error) {

                                    }

                                    @Override
                                    public void mapboxTileCountLimitExceeded(long limit) {

                                    }
                                });
                            }
                        }
                        @Override
                        public void onError(String error) {

                        }
                    });
                }

            }

            @Override
            public void onError(String error) {

            }
        });

        if(offlineRegion1!=null){
            return;
        }

        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(new com.mapbox.mapboxsdk.geometry.LatLng(31.809425, 122.102298)) // Northeast
                .include(new com.mapbox.mapboxsdk.geometry.LatLng(30.729557, 120.893802)) // Southwest
                .build();

        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                "mapbox://styles/mapbox/streets-v11",
                latLngBounds,
                0,
                12,
                context.getResources().getDisplayMetrics().density
        );
// Implementation that uses JSON to store Yosemite National Park as the offline region name.
        byte[] metadata;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("JSON_FIELD_REGION_NAME", "shanghai");
            String json = jsonObject.toString();
            metadata = json.getBytes(java.nio.charset.Charset.defaultCharset());
        } catch (Exception exception) {
            Log.e(TAG, "Failed to encode metadata: " + exception.getMessage());
            metadata = null;
        }
        // Create the region asynchronously
        offlineManager.createOfflineRegion(definition, metadata,
                new OfflineManager.CreateOfflineRegionCallback() {
                    @Override
                    public void onCreate(final OfflineRegion offlineRegion) {
                        offlineRegion1 = offlineRegion;
                        offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);

// Monitor the download progress using setObserver
                        offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
                            @Override
                            public void onStatusChanged(OfflineRegionStatus status) {

// Calculate the download percentage
                                double percentage = status.getRequiredResourceCount() >= 0
                                        ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                                        0.0;

                                if (status.isComplete()) {
// Download complete
                                    offlineRegion.setDownloadState(STATE_INACTIVE);
                                    if(downloadCallback!=null){
                                        downloadCallback.callBack(100,true);
                                    }
                                    System.out.println("Region downloaded successfully.");
                                } else if (status.isRequiredResourceCountPrecise()) {
                                    System.out.println("Region downloaded "+percentage+"");
                                    if(downloadCallback!=null){
                                        downloadCallback.callBack(percentage,false);
                                    }
                                }
                            }

                            @Override
                            public void onError(OfflineRegionError error) {
// If an error occurs, print to logcat
                                Log.e(TAG, "onError reason: " + error.getReason());
                                Log.e(TAG, "onError message: " + error.getMessage());
                            }

                            @Override
                            public void mapboxTileCountLimitExceeded(long limit) {
// Notify if offline region exceeds maximum tile count
                                Log.e(TAG, "Mapbox tile count limit exceeded: " + limit);
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error: " + error);
                    }
                });
    }


    public static void deleteAll(Context context){
        OfflineManager offlineManager = OfflineManager.getInstance(context);
        offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
            @Override
            public void onList(OfflineRegion[] offlineRegions) {
                for(OfflineRegion region : offlineRegions){
                    region.delete(new OfflineRegion.OfflineRegionDeleteCallback() {
                        @Override
                        public void onDelete() {

                        }

                        @Override
                        public void onError(String error) {

                        }
                    });
                }
            }

            @Override
            public void onError(String error) {

            }
        });

        offlineRegion1 = null;
    }
}
