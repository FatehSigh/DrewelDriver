package com.octalsoftware.drewel.firebase

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.octalsoftware.drewel.AppDelegate
import com.octalsoftware.drewel.utils.Prefs


class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token
        AppDelegate.LogGC(TAG + "Refreshed token: " + refreshedToken!!)
        Prefs(this).fcMtoken = (refreshedToken)
        Prefs(this).fcMtokeninTemp = (refreshedToken)
//        executeSignInAsync(this, refreshedToken)
    }

    companion object {
        private val TAG = "MyFireBaseIIDService"
    }
}