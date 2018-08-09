package com.octalsoftware.drewel.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.octalsoftware.drewel.AppDelegate
import com.octalsoftware.drewel.constant.Tags
import com.octalsoftware.drewel.model.UserDataModel

class Prefs {
    /**
     * SEREN_SharedPreferences Class is used to maintain sharedpreferences
     */
    /*
     * SEREN_SharedPreferences Members Declarations
	 */
    private var mContext: Context? = null
    private var mSharedPreferences: SharedPreferences? = null
    private var mSharedPreferencesTemp: SharedPreferences? = null
    private var mEditor: Editor? = null

    private val str_PrefName = "DrawelDriver"
    private val str_PrefName1 = "DrawelDriverTemp"

    constructor(context: Context,
                mOnSharedPreferenceChangeListener: OnSharedPreferenceChangeListener?) {
        this.mContext = context
        if (mContext != null) {
            mSharedPreferences = mContext!!.getSharedPreferences(
                    str_PrefName, Context.MODE_PRIVATE)
            mSharedPreferencesTemp = mContext!!.getSharedPreferences(
                    str_PrefName1, Context.MODE_PRIVATE)
            if (mOnSharedPreferenceChangeListener != null) {
                mSharedPreferences!!
                        .registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener)
                mSharedPreferencesTemp!!
                        .registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener)
            }
        } else {
            AppDelegate.LogE("mContext is null at Prefs")
        }
    }

    constructor(context: Context) {
        this.mContext = context
        mSharedPreferences = mContext!!.getSharedPreferences(
                str_PrefName, Context.MODE_PRIVATE)
        mSharedPreferencesTemp = mContext!!.getSharedPreferences(
                str_PrefName1, Context.MODE_PRIVATE)
    }

    /**
     * This method is used to store String value in SharedPreferences
     */

    fun putStringValue(editorkey: String, editorvalue: String) {
        mEditor = mSharedPreferences!!.edit()
        mEditor!!.putString(editorkey, editorvalue)
        mEditor!!.commit()
        AppDelegate.LogP("putStringValue => editorkey = $editorkey, editorvalue = $editorvalue")
    }

    fun putStringValueinTemp(editorkey: String, editorvalue: String) {
        mEditor = mSharedPreferencesTemp!!.edit()
        mEditor!!.putString(editorkey, editorvalue)
        mEditor!!.commit()
        AppDelegate.LogP("putStringValue => editorkey = $editorkey, editorvalue = $editorvalue")
    }

    fun getStringValuefromTemp(editorkey: String, defValue: String): String {
        val PrefValue = mSharedPreferencesTemp!!.getString(editorkey, defValue)
        AppDelegate.LogP("getStringValue => editorkey = $editorkey, editorvalue = $PrefValue")
        return PrefValue

    }

    fun setDefaultLanguage(editorvalue: String) {
        mEditor = mSharedPreferences!!.edit()
        mEditor!!.putString(Tags.LANGUAGE, editorvalue)
        mEditor!!.commit()
        AppDelegate.LogP("putStringValue => editorkey = $Tags.LANGUAGE, editorvalue = $editorvalue")
    }

    fun getDefaultLanguage(): String {
        val PrefValue = mSharedPreferences!!.getString(Tags.LANGUAGE, Tags.LANGUAGE_ENGLISH)
        AppDelegate.LogP("getStringValue => editorkey = $Tags.LANGUAGE, editorvalue = $PrefValue")
        return PrefValue

    }

    /**
     * This method is used to store int value in SharedPreferences
     */

    fun putIntValue(editorkey: String, editorvalue: Int) {
        mEditor = mSharedPreferences!!.edit()
        mEditor!!.putInt(editorkey, editorvalue)
        mEditor!!.commit()
    }

    /**
     * This method is used to store boolean value in SharedPreferences
     */
    fun putBooleanValue(editorkey: String, editorvalue: Boolean) {
        mEditor = mSharedPreferences!!.edit()
        mEditor!!.putBoolean(editorkey, editorvalue)
        AppDelegate.LogP("putBooleanValue => editorkey = $editorkey, editorvalue = $editorvalue")
        mEditor!!.commit()
    }

    /**
     * This method is used to get String value from SharedPreferences
     *
     * @return String PrefValue
     */
    fun getStringValue(editorkey: String, defValue: String): String {
        val PrefValue = mSharedPreferences!!.getString(editorkey, defValue)
        AppDelegate.LogP("getStringValue => editorkey = $editorkey, editorvalue = $PrefValue")
        return PrefValue

    }

    /**
     * This method is used to get int value from SharedPreferences
     *
     * @return int PrefValue
     */
    fun getIntValue(editorkey: String, defValue: Int): Int {
        return mSharedPreferences!!.getInt(editorkey, defValue)
    }

    /**
     * This method is used to get boolean value from SharedPreferences
     *
     * @return boolean PrefValue
     */
    fun getBooleanValue(editorkey: String, defValue: Boolean): Boolean {
        val PrefValue = mSharedPreferences!!.getBoolean(editorkey, defValue)
        AppDelegate.LogP("getBooleanValue => editorkey = $editorkey, editorvalue = $PrefValue")
        return PrefValue
    }

    fun putCategoryValue(editorkey: String, editorvalue: String) {
        mEditor = mSharedPreferences!!.edit()
        mEditor!!.putString(editorkey, editorvalue)
        mEditor!!.commit()
    }

    fun putAuthKey(str_AuthKey: String) {
        mEditor = mSharedPreferences!!.edit()
        mEditor!!.putString(Tags.auth_key, str_AuthKey)
        mEditor!!.commit()
        AppDelegate.LogP("putAuthKey = " + str_AuthKey)
    }

    //      String str_AuthKey = mSharedPreferences.getString(Tags.auth_key, "[B@2ea1911d");
    val authKey: String
        get() {
            val str_AuthKey = mSharedPreferences!!.getString(Tags.auth_key, "")
            AppDelegate.LogP("getAuthKey = " + str_AuthKey!!)
            return str_AuthKey
        }

    //String str_auth_token = mSharedPreferences.getString(Tags.auth_token, "[B@23f0d27c");
    //      String str_auth_token = mSharedPreferences.getString(Tags.auth_token, "[B@27e2b34");
    //        if (str_auth_token.length() == 0)
    //            str_auth_token = "[B@23f0d27c";
    val authToken: String
        get() {
            val str_auth_token = mSharedPreferences!!.getString(Tags.auth_token, "")
            AppDelegate.LogP("getAuthToken = " + str_auth_token!!)
            return str_auth_token
        }

    fun putAuthToken(str_auth_token: String) {
        mEditor = mSharedPreferences!!.edit()
        mEditor!!.putString(Tags.auth_token, str_auth_token)
        mEditor!!.commit()
        AppDelegate.LogP("putAuthToken = " + str_auth_token)
    }


    val primaryAddress: String
        get() {
            val str_PrimaryAddress = mSharedPreferences!!.getString(Tags.primary_address, "")
            AppDelegate.LogP("getPrimaryAddress = " + str_PrimaryAddress!!)
            return str_PrimaryAddress
        }

    fun putPrimaryAddress(str_PrimaryAddress: String) {
        mEditor = mSharedPreferences!!.edit()
        mEditor!!.putString(Tags.primary_address, str_PrimaryAddress)
        mEditor!!.commit()
        AppDelegate.LogP("putPrimaryAddress = " + str_PrimaryAddress)
    }

    /**
     *
     */
    val userId: String
        get() {
            val user_id = mSharedPreferences!!.getString(Tags.user_id, "")
            AppDelegate.LogP("getUserId = " + user_id!!)
            return user_id
        }

    fun putUserId(user_id: String) {
        mEditor = mSharedPreferences!!.edit()
        mEditor!!.putString(user_id, user_id)
        mEditor!!.commit()
        AppDelegate.LogP("putUserId = " + user_id)
    }

    var userdata: UserDataModel?
        get() {
            val gson = Gson()
            val json = mSharedPreferences!!.getString(Tags.USER_DATA, "")
            var obj: UserDataModel? = gson.fromJson<UserDataModel>(json, UserDataModel::class.java)
            AppDelegate.LogP("getUserdata = " + json!!)
            return obj!!
        }
        set(userdata) {
            val edit = mSharedPreferences!!.edit()
            val gson = Gson()
            val json = gson.toJson(userdata)
            edit.putString(Tags.USER_DATA, json)
            edit.commit()
            AppDelegate.LogP("setUserData = " + json)
        }


    //getting user name
    var userCurrentLocation: LatLng
        get() {
            val latLng = LatLng(java.lang.Double.parseDouble(mSharedPreferences!!.getString(Tags.LAT, "0.0")), java.lang.Double.parseDouble(mSharedPreferences!!.getString(Tags.LNG, "0.0")))
            AppDelegate.LogP("getUserCurrentLocation = " + latLng.latitude + "," + latLng.longitude)
            return latLng
        }
        set(userCurrentLocation) {
            mEditor = mSharedPreferences!!.edit()
            mEditor!!.putString(Tags.LAT, userCurrentLocation.latitude.toString() + "")
            mEditor!!.putString(Tags.LNG, userCurrentLocation.longitude.toString() + "")
            mEditor!!.commit()
            AppDelegate.LogP("setUserCurrentLocation => " + userCurrentLocation.latitude + "," + userCurrentLocation.longitude)
        }

    //getting user name
    //provider name is unecessary
    //your coords of course
    var userCurrentLocationObject: Location
        get() {
            val targetLocation = Location("")
            targetLocation.latitude = java.lang.Double.parseDouble(mSharedPreferences!!.getString(Tags.LAT, "0.0"))
            targetLocation.longitude = java.lang.Double.parseDouble(mSharedPreferences!!.getString(Tags.LNG, "0.0"))
            targetLocation.time = mSharedPreferences!!.getLong(Tags.ESTIMATE, 0)
            AppDelegate.LogP("getUserCurrentLocationObject = " + mSharedPreferences!!.getString(Tags.LAT, "0.0") + "," + mSharedPreferences!!.getString(Tags.LNG, "0.0"))
            return targetLocation
        }
        set(location) {
            mEditor = mSharedPreferences!!.edit()
            mEditor!!.putString(Tags.LAT, location.latitude.toString() + "")
            mEditor!!.putString(Tags.LNG, location.latitude.toString() + "")
            mEditor!!.putLong(Tags.ESTIMATE, location.time)
            mEditor!!.commit()
            AppDelegate.LogP("setUserCurrentLocationObject => " + location.latitude + "," + location.longitude + ", " + location.time)
        }

    fun setGCMtokeninTemp(token: String) {
        mEditor = mSharedPreferencesTemp!!.edit()
        mEditor!!.putString(Tags.GCMtoken, token)
        mEditor!!.commit()
        AppDelegate.LogP("setGCMtoken = " + token)
    }

    //getting user name
    //your coords of course
    val gcMtokenfromTemp: String
        get() {
            val token = mSharedPreferencesTemp!!.getString(Tags.GCMtoken, "")
            AppDelegate.LogP("getGCMtokenfromTemp = " + token!!)
            return token
        }

    //your coords of course
    var gcMtoken: String
        get() {
            val token = mSharedPreferences!!.getString(Tags.GCMtoken, "")
            AppDelegate.LogP("getGCMtoken = " + token!!)
            return token
        }
        set(token) {
            mEditor = mSharedPreferences!!.edit()
            mEditor!!.putString(Tags.GCMtoken, token)
            mEditor!!.commit()
            AppDelegate.LogP("setGCMtoken = " + token)
        }

    var fcMtoken: String
        get() {
            val token = mSharedPreferences!!.getString(Tags.FCMtoken, "")
            AppDelegate.LogP("getFCMtoken = " + token!!)
            return token
        }
        set(token) {
            mEditor = mSharedPreferences!!.edit()
            mEditor!!.putString(Tags.FCMtoken, token)
            mEditor!!.commit()
            AppDelegate.LogP("setFCMtoken = " + token)
        }
    var fcMtokeninTemp: String
        get() {
            val token = mSharedPreferencesTemp!!.getString(Tags.FCMtoken, "")
            AppDelegate.LogP("getFCMtoken = " + token!!)
            return token
        }
        set(token) {
            mEditor = mSharedPreferencesTemp!!.edit()
            mEditor!!.putString(Tags.FCMtoken, token)
            mEditor!!.commit()
            AppDelegate.LogP("setFCMtoken = " + token)
        }

    fun clearTempPrefs() {
        try {
            mEditor = mSharedPreferencesTemp!!.edit()
            mEditor!!.clear()
            mEditor!!.commit()
            AppDelegate.LogP("clearTempPrefs")
        } catch (e: Exception) {
            AppDelegate.LogE(e)
        }

    }

    fun clearSharedPreference() {
        try {
            mEditor = mSharedPreferences!!.edit()
            mEditor!!.clear()
            mEditor!!.commit()
            AppDelegate.LogP("clearSharedPreference")
        } catch (e: Exception) {
            AppDelegate.LogE(e)
        }

    }

    /*Temp data saved and cleaned after uses*/
//    val tempUserdata: UserDataModel
//        get() {
//            val gson = Gson()
//            val json = mSharedPreferencesTemp!!.getString(Tags.TEMP_USER_DATA, "")
//            val obj = gson.fromJson<UserDataModel>(json, UserDataModel::class.java!!)
//            AppDelegate.LogP("getTempUserdata = " + json!!)
//            return obj
//        }
//
//    fun setTempUserData(result: UserDataModel) {
//        val edit = mSharedPreferencesTemp!!.edit()
//        val gson = Gson()
//        val json = gson.toJson(result)
//        edit.putString(Tags.TEMP_USER_DATA, json)
//        edit.commit()
//        AppDelegate.LogP("setTempUserData = " + json)
//    }

    //    public TaxiTypeModel getTaxiServices() {
    //        Gson gson = new Gson();
    //        String json = mSharedPreferences.getString(Tags.TAXI_DATA, "");
    //        TaxiTypeModel obj = gson.fromJson(json, TaxiTypeModel.class);
    //        AppDelegate.LogP("getTempTaxidata = " + json);
    //        return obj;
    //    }
    //
    //    public void setTaxiServices(TaxiTypeModel result) {
    //        Editor edit = mSharedPreferences.edit();
    //        Gson gson = new Gson();
    //        String json = gson.toJson(result);
    //        edit.putString(Tags.TAXI_DATA, json);
    //        edit.commit();
    //        AppDelegate.LogP("setTempTaxiData = " + json);
    //    }

//    var taxiServices: ArrayList<TaxiTypeModel>?
//        get() {
//            val gson = Gson()
//            val json = mSharedPreferencesTemp!!.getString(Tags.TAXI_DATA, null)
//            val type = object : TypeToken<ArrayList<TaxiTypeModel>>() {
//
//            }.type
//            val arrayList = gson.fromJson<ArrayList<TaxiTypeModel>>(json, type)
//            AppDelegate.LogP("getTaxiArryList => " + json!!)
//            return arrayList
//        }
//        set(arrayCategoryList) {
//            mEditor = mSharedPreferencesTemp!!.edit()
//            val gson = Gson()
//            val json = gson.toJson(arrayCategoryList)
//            mEditor!!.putString(Tags.TAXI_DATA, json)
//            mEditor!!.commit()
//            AppDelegate.LogP("setTaxiArryList = " + arrayCategoryList)
//        }

    //    public CityModel getCitydata() {
    //        Gson gson = new Gson();
    //        String json = mSharedPreferencesTemp.getString(Tags.CITY_DATA, "");
    //        CityModel obj = gson.fromJson(json, CityModel.class);
    //        AppDelegate.LogP("getCitydata = " + json);
    //        return obj;
    //    }
    //
    //    public void setCityData(CityModel result) {
    //        Editor edit = mSharedPreferencesTemp.edit();
    //        Gson gson = new Gson();
    //        String json = gson.toJson(result);
    //        edit.putString(Tags.CITY_DATA, json);
    //        edit.commit();
    //        AppDelegate.LogP("setCityData = " + json);
    //    }

//    val savedStateData: BookOrderModel
//        get() {
//            val gson = Gson()
//            val json = mSharedPreferences!!.getString(Tags.SAVE, "")
//            val obj = gson.fromJson<BookOrderModel>(json, BookOrderModel::class.java!!)
//            AppDelegate.LogP("saveStateData = " + json!!)
//            return obj ?: BookOrderModel()
//        }
//
//    fun saveStateData(result: BookOrderModel) {
//        val edit = mSharedPreferences!!.edit()
//        val gson = Gson()
//        val json = gson.toJson(result)
//        edit.putString(Tags.SAVE, json)
//        edit.commit()
//        AppDelegate.LogP("getSavedStateData= " + json)
//    }
//
//    var className: String
//        get() {
//            val user_id = mSharedPreferences!!.getString(Tags.class_name, "")
//            AppDelegate.LogP("getClassName = " + user_id!!)
//            return user_id
//        }
//        set(className) {
//            mEditor = mSharedPreferences!!.edit()
//            mEditor!!.putString(Tags.class_name, className)
//            mEditor!!.commit()
//            AppDelegate.LogP("setClassName = " + className)
//        }

}
