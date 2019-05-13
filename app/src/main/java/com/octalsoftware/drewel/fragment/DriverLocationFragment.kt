package com.octalsoftware.drewel.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.octalsoftware.drewel.AppDelegate
import com.octalsoftware.drewel.R
import com.octalsoftware.drewel.constant.Tags
import com.octalsoftware.drewel.interfaces.AsyncGetDirection
import com.octalsoftware.drewel.retrofitService.APIInterface
import com.octalsoftware.drewel.retrofitService.RestClient
import com.octalsoftware.drewel.utils.GetdirectionAsync
import com.octalsoftware.drewel.utils.Prefs
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.layout_track_order.*
import java.util.*
import java.util.concurrent.TimeUnit


class DriverLocationFragment : Fragment(), OnMapReadyCallback, AsyncGetDirection {
    override fun OnDirectionCompleted(listLatLng: ArrayList<LatLng>?, state: String?) {
    }

    override fun OnDirectionPathCompleted(listLatLng: ArrayList<LatLng>?, state: String?) {
        if (listLatLng!!.size > 0) {
            val rectLine = PolylineOptions().width(10f).color(Color.RED)
            // Adding route on the map
            rectLine.addAll(listLatLng)

            googleMap.addPolyline(rectLine)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.layout_track_order, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBar.visibility=View.GONE
        trackOrderMapView.onCreate(savedInstanceState)
        trackOrderMapView.onResume() // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(activity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        trackOrderMapView.getMapAsync(this)
    }
    private lateinit var googleMap: GoogleMap
    private var destinationLatitude = ""
    private var destinationLongitude = ""
    private var deliveryBoyId = ""
    private lateinit var driverCurrentLocationLatLng: LatLng
    private lateinit var deliveryLocationLatLng: LatLng
    private lateinit var driverCurrentMarker: Marker
    private var locationUpdateDisposable: Disposable? = null





    override fun onMapReady(mMap: GoogleMap) {
        googleMap = mMap
        googleMap.uiSettings.isMyLocationButtonEnabled = false

        checkRequestPermission()
        setMarkers()
    }

    /* set marker for destination and driver current location*/
    private fun setMarkers() {
        var latitude = Prefs(activity!!).getStringValue(Tags.LAT, "")
        var longitude = Prefs(activity!!).getStringValue(Tags.LNG, "")
//
        if(!latitude.isEmpty() && !longitude.isEmpty()) {
            driverCurrentLocationLatLng = LatLng(latitude.toDouble(), longitude.toDouble())
//        driverCurrentMarker.position = driverCurrentLocationLatLng
//        driverCurrentLocationLatLng = LatLng(destinationLatitude.toDouble(), destinationLongitude.toDouble())
            driverCurrentMarker = googleMap.addMarker(MarkerOptions().position(driverCurrentLocationLatLng).anchor(0.5f, 0.5f))
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(driverCurrentLocationLatLng, 14f)
//        deliveryLocationLatLng = LatLng(destinationLatitude.toDouble(), destinationLongitude.toDouble())
//        googleMap.addMarker(MarkerOptions().position(deliveryLocationLatLng).anchor(0.5f, 0.5f))
            googleMap.moveCamera(cameraUpdate)


        }
        getDriverLocation()
    }

    /* get driver location in each 30 seconds */
    private fun getDriverLocation() {
        locationUpdateDisposable = Observable.interval(0, 30, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            var latitude = Prefs(activity!!).getStringValue(Tags.LAT, "")
                            var longitude = Prefs(activity!!).getStringValue(Tags.LNG, "")
//                            var latitude = "25.262645"
//                            var longitude = "75.5156"
                            if(!latitude.isEmpty() && !longitude.isEmpty()) {
                                driverCurrentLocationLatLng = LatLng(latitude.toDouble(), longitude.toDouble())
                                driverCurrentMarker.position = driverCurrentLocationLatLng
                                googleMap.clear()
                                driverCurrentMarker = googleMap.addMarker(MarkerOptions().position(driverCurrentLocationLatLng).anchor(0.5f, 0.5f))

                                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(driverCurrentLocationLatLng, 14f)
                                googleMap.moveCamera(cameraUpdate)
                            }
//                            GetdirectionAsync(activity, this,
//                                    driverCurrentLocationLatLng.latitude, driverCurrentLocationLatLng.longitude,deliveryLocationLatLng.latitude, deliveryLocationLatLng.longitude, "drive").execute()

//                            callGetDirectionApi()
                        },
                        { error -> Log.e("TAG", "{$error.message}") },
                        { Log.d("TAG", "completed") })
    }

    private fun checkRequestPermission() {
        if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        } else {
            googleMap.isMyLocationEnabled = true
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return
            googleMap.isMyLocationEnabled = true
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /* get distance and direction between destination and driver current location*/
    private fun callGetDirectionApi() {
        val directionUrl = AppDelegate.getDirectionsUrl(driverCurrentLocationLatLng, deliveryLocationLatLng)
        val getDirectionObservable = RestClient.createService(APIInterface::class.java).getGoogleDirection(directionUrl)
        getDirectionObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ directionResults ->
                    AppDelegate.LogT("Route==>"+directionResults)
                    val routeList = AppDelegate.getStepsToDrawPolyline(directionResults)
                    if (routeList.size > 0) {
                        val rectLine = PolylineOptions().width(10f).color(Color.RED)
                        // Adding route on the map
                        rectLine.addAll(routeList)

                        googleMap.addPolyline(rectLine)
                    }
                }, { error ->
                    AppDelegate.showToast(activity, error.message!!)
                    Log.e("TAG", "{$error.message}")
                }
                )
    }


    override fun onStop() {
        super.onStop()

        locationUpdateDisposable?.dispose()
    }
}