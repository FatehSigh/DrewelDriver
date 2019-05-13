package com.octalsoftware.drewel

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.octalsoftware.drewel.constant.Tags
import java.util.*
import java.util.regex.Pattern
import android.widget.TextView
import com.google.android.gms.maps.model.LatLng
import com.octalsoftware.drewel.utils.DrewelProgressDialog
import com.octalsoftware.drewel.utils.RouteDecode
import com.os.drewel.apicall.responsemodel.googledirectionresultmodel.DirectionResults
import com.os.drewel.apicall.responsemodel.googledirectionresultmodel.Location
import com.os.drewel.apicall.responsemodel.googledirectionresultmodel.Steps
import java.text.DecimalFormat
import java.text.NumberFormat

class AppDelegate {

    companion object {

        var mInstance: AppDelegate? = null
        fun showSnackBar(v: View, msg: String) {
            val snackbar = Snackbar.make(v, msg, Snackbar.LENGTH_LONG)
            val tv = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text) as TextView
            tv.setTextAppearance(v.context, R.style.EditTextStyle2)
            snackbar.show()
//            showToast(getInstance(),msg)
        }

        val storeLat = 23.5402489
        val storeLng = 58.380247300000065

        fun isValidPassword(pass2: String): Boolean {
            val PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-zA-Z]).{6,20})"
            val pattern = Pattern.compile(PASSWORD_PATTERN)
            val matcher = pattern.matcher(pass2)
            return matcher.matches()
        }

        fun isValidEmail(target: String): Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }

        fun setLocale(lang: String?, mContext: Context) {
            var lang = lang
            if (lang == null) {
                lang = Tags.LANGUAGE_ENGLISH
            }
            val myLocale = Locale(lang)
            val res = mContext.resources
            val dm = res.displayMetrics
            val conf = res.configuration
            conf.locale = myLocale
            if (Build.VERSION.SDK_INT >= 17) {
                conf.setLayoutDirection(myLocale)
            }
            res.updateConfiguration(conf, dm)
        }


        @JvmOverloads
        fun haveNetworkConnection(mContext: Context?, showAlert: Boolean = true): Boolean {
            var isConnected: Boolean
            var haveConnectedWifi = false
            var haveConnectedMobile = false

            if (mContext == null) {
                return false
            } else {
                val cm = mContext
                        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val netInfo = cm.allNetworkInfo
                for (ni in netInfo) {
                    if (ni.typeName.equals("WIFI", ignoreCase = true))
                        if (ni.isConnected)
                            haveConnectedWifi = true
                    if (ni.typeName.equals("MOBILE", ignoreCase = true))
                        if (ni.isConnected)
                            haveConnectedMobile = true
                }
                isConnected = haveConnectedWifi || haveConnectedMobile
                if (isConnected) {
                    return isConnected
                } else {
                    if (showAlert) {
                        showToast(mContext, mContext.resources.getString(R.string.active_internet_connection))
                    }
                }
                return isConnected
            }
        }

        fun showToast(mContext: Context?, Message: String) {
            try {
                if (mContext != null)
                    Toast.makeText(mContext, Message, Toast.LENGTH_SHORT).show()
                else
                    Log.e("tag", "context is null at showing toast.")
            } catch (e: Exception) {
                Log.e("tag", "context is null at showing toast.", e)
            }
        }

        fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {

            // Origin of route
            val strOrigin = "origin=" + origin.latitude + "," + origin.longitude

            // Destination of route
            val strDest = "destination=" + dest.latitude + "," + dest.longitude

            // Sensor enabled
            val sensor = "sensor=false"

            // Building the parameters to the web service
            val parameters = "$strOrigin&$strDest&$sensor"

            // Output format
            val output = "json"
            var mUrl = ("https://maps.googleapis.com/maps/api/directions/json?"
                    + "origin=" + origin.latitude + "," + origin.longitude
                    + "&destination=" + dest.latitude + "," + dest.longitude
                    + "&sensor=false&units=metric&mode=driving" + "&key=" + "AIzaSyD7f-U96w7RxPTkMW9UYcPEE0odwdcpGsw")
            // Building the url to the web service
//            return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
            return mUrl
        }

        fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): String {

            val theta = lon1 - lon2
            var dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta))
            dist = Math.acos(dist)
            dist = rad2deg(dist)
            dist = dist * 60.0 * 1.1515
             val dfInt = DecimalFormat("0.##")

            return dfInt.format(dist).toString()
        }

        fun deg2rad(deg: Double): Double {
            return deg * Math.PI / 180.0
        }

        fun rad2deg(rad: Double): Double {
            return rad * 180.0 / Math.PI
        }

        fun getStepsToDrawPolyline(directionResults: DirectionResults): ArrayList<LatLng> {
            val routeList = ArrayList<LatLng>()
            if (directionResults.routes!!.isNotEmpty()) {
                var decodeList: ArrayList<LatLng>
                val routeA = directionResults.routes[0]
                if (routeA.legs!!.isNotEmpty()) {
                    val steps = routeA.legs[0].steps
                    var step: Steps
                    var location: Location?
                    var polyline: String?
                    if (steps != null) {
                        for (i in steps.indices) {
                            step = steps.get(i)
                            location = step.start_location
                            routeList.add(LatLng(location!!.lat, location.lng))
                            Log.i("zacharia", "Start Location :" + location.lat + ", " + location.lng)
                            polyline = step.polyline!!.points
                            decodeList = RouteDecode.decodePoly(polyline!!)
                            routeList.addAll(decodeList)
                            location = step.end_location
                            routeList.add(LatLng(location!!.lat, location.lng))
                        }
                    }
                }
            }
            return routeList
        }

        private var mProgressDialog: ProgressDialog? = null

        @JvmOverloads
        fun showProgressDialog(mContext: Activity, mTitle: String = "",
                               mMessage: String = "Loading...") {
            hideKeyBoard(mContext)
            try {
                DrewelProgressDialog.getProgressDialog(mContext).showDialog1()
            } catch (e: Exception) {

            }
//            try {
//                if (mContext != null) {
//                    if (mProgressDialog != null && mProgressDialog!!.isShowing) {
//                        return
//                    }
//                    mProgressDialog = ProgressDialog(mContext)
//                    mProgressDialog!!.setCancelable(false)
//                    mProgressDialog!!.setTitle(mTitle)
//                    mProgressDialog!!.setMessage(mMessage)
//                    if (mProgressDialog != null && mProgressDialog!!.isShowing) {
//                        mProgressDialog!!.dismiss()
//                        mProgressDialog!!.show()
//                    } else {
//                        mProgressDialog!!.show()
//                    }
//                } else {
//                    Log.d("tag", "showProgressDialog instance is null")
//                }
//            } catch (e: Exception) {
//                LogE(e)
//            }
        }

        fun hideProgressDialog(mContext: Context?) {
            try {
                DrewelProgressDialog.getProgressDialog(mContext).dismissDialog1()
            } catch (e: Exception) {
                AppDelegate.LogE(e)
            }
//            try {
//                if (mContext != null) {
//                    if (mProgressDialog != null && mProgressDialog!!.isShowing) {
//                        mProgressDialog!!.dismiss()
//                    } else {
//                        mProgressDialog = ProgressDialog(mContext)
//                        mProgressDialog!!.dismiss()
//                    }
//                } else {
//                    Log.d("tag", " hide instense is null")
//                }
//            } catch (e: Exception) {
//                LogE(e)
//            }
        }


        fun Log(TAG: String, Message: String, LogType: Int) {
            when (LogType) {
            // Case 1- To Show Message as Debug
                1 -> Log.d(TAG, Message)
            // Case 2- To Show Message as Error
                2 -> Log.e(TAG, Message)
            // Case 3- To Show Message as Information
                3 -> Log.i(TAG, Message)
            // Case 4- To Show Message as Verbose
                4 -> Log.v(TAG, Message)
            // Case 5- To Show Message as Assert
                5 -> Log.w(TAG, Message)
            // Case Default- To Show Message as System Print
                else -> println(Message)
            }
        }

        /* Function to show log for error message */
        fun LogE(e: Exception?) {
            if (e != null) {
                LogE(e.message!!)
                e.printStackTrace()
            } else {
                Log(Tags.ERROR, "exception object is also null.", 2)
            }
        }

        /* Function to show log for error message */
        fun LogE(e: OutOfMemoryError?) {
            if (e != null) {
                LogE(e.message!!)
                e.printStackTrace()
            } else {
                Log(Tags.ERROR, "exception object is also null.", 2)
            }
        }

        /* Function to show log for error message */
        fun LogE(message: String, exception: Exception?) {
            if (exception != null) {
                LogE("from = " + message + " => "
                        + exception.message)
                exception.printStackTrace()
            } else {
                Log(Tags.ERROR, "exception object is also null. at " + message, 2)
            }
        }

        fun LogE(Message: String) {
            Log(Tags.ERROR, "" + Message, 2)
        }

        fun LogT(Message: String) {
            Log(Tags.TEST, "" + Message, 1)
        }

        fun LogT(Message: String, type: Int) {
            Log(Tags.TEST, "" + Message, type)
        }

        fun LogP(Message: String) {
            Log(Tags.PREF, "" + Message, 1)
        }

        fun LogGC(Message: String) {
            Log(Tags.GCM, "" + Message, 1)
        }


        fun hideKeyBoard(mActivity: Activity?) {
            if (mActivity == null)
                return
            else {
                val inputManager = mActivity
                        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                // check if no view has focus:
                val v = mActivity.currentFocus ?: return

                LogT("hideKeyBoard viewNot null")
                inputManager.hideSoftInputFromWindow(v.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }

        @Synchronized
        fun getInstance(mContext: Context): AppDelegate {
            if (mInstance == null) {
                mInstance = mContext.applicationContext as AppDelegate
            }
            return mInstance as AppDelegate
        }

        fun isValidString(string: String?): Boolean {
            return string != null && !string.equals("null", ignoreCase = true) && string.length > 0
        }

        fun openURL(mActivity: Activity, str_url: String) {
            var str_url = str_url
            try {
                if (!isValidString(str_url)) {
                    showToast(mActivity, "Website url is null.")
                    return
                }
                if (!str_url.startsWith("http://") && !str_url.startsWith("https://"))
                    str_url = "http://" + str_url
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(str_url))
                mActivity.startActivity(browserIntent)
            } catch (e: Exception) {
                LogE(e)
            }
        }

        fun call(context: Context, number: String) {
            var uri = "tel:" + number.trim()
            var intent = Intent(Intent.ACTION_DIAL)
            intent.setData(Uri.parse(uri))
            context.startActivity(intent)
        }
    }
}