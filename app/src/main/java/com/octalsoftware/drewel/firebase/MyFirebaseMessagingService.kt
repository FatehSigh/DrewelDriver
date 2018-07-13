package com.octalsoftware.drewel.firebase

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.octalsoftware.drewel.AppDelegate
import com.octalsoftware.drewel.HomeActivity
import com.octalsoftware.drewel.R
import com.octalsoftware.drewel.activity.AcceptedOrderDetailActivity
import com.octalsoftware.drewel.activity.DeliveredOrderDetailActivity
import com.octalsoftware.drewel.activity.LoginActivity
import com.octalsoftware.drewel.activity.NotificationActivity
import com.octalsoftware.drewel.constant.Tags
import com.octalsoftware.drewel.model.PNModel
import com.octalsoftware.drewel.utils.Prefs
import org.json.JSONObject
import java.util.*
import android.app.NotificationChannel


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance()
    private val options = DisplayImageOptions.Builder().cacheInMemory(true)
            .cacheOnDisc(true).resetViewBeforeLoading(true).build()

    object NotificationType {
        const val deliveryStatusChange = "deliveryStatusChange"
        const val deliveryBoyAssigned = "deliveryBoyAssigned"
        const val general = "general"
    }

    /*{notification_type=deliveryStatusChange, payload={"second_user_id":"","image":"","profile_image":"","amount":"52.000","user_id":"62","item_id":"143","last_name":"octal","title":"","first_name":"tester one"}, badge=0, message=Order #s status changed to under packaging.}*/
    /*{notification_type=deliveryBoyAssigned, payload={"second_user_id":"","image":"","profile_image":"","amount":"45.000","user_id":"62","item_id":"142","last_name":"octal","title":"","first_name":"tester one"}, badge=0, message=Order #142 assigned to you to deliver.}*/
/*{notification_type=general, payload={"second_user_id":"","image":"","profile_image":"","amount":"","user_id":"62","item_id":"","last_name":"octal","title":"","first_name":"tester one"}, badge=0, message=test}*/
/*{
    "message":{
    "token":"bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...",
    ,
    "data" :{notification_type=deliveryStatusChange, payload={"second_user_id":"","image":"","profile_image":"","amount":"52.000","user_id":"62","item_id":"143","last_name":"octal","title":"","first_name":"tester one"}, badge=0, message=Order #s status changed to under packaging.}
}
}*/
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        AppDelegate.LogT("remoteMessage==>" + remoteMessage!!.data)
        var data = sendNotif(remoteMessage.data)
        if (data.notification_type.equals(NotificationType.deliveryBoyAssigned)) showAssignDeliveryBoyNotif(data)
        else if (data.notification_type.equals(NotificationType.deliveryStatusChange)) showdeliveryStatusChangeNotif(data)
        else if (data.notification_type.equals(NotificationType.general)) showGeneralNotif(data)

        val intent2 = Intent()
        intent2.action = "UPDATE_COUNT"
        sendBroadcast(intent2)
    }

    private fun showdeliveryStatusChangeNotif(notificationModel: PNModel) {
        var intent: Intent? = null
        try {
            if (Prefs(this).userdata == null) {
                intent = Intent(this, LoginActivity::class.java)
            } else {
                intent = (Intent(this, DeliveredOrderDetailActivity::class.java))
                val intent2 = Intent()
                intent2.action = "UPDATE_DELIVER"
                sendBroadcast(intent2)
            }
        } catch (e: Exception) {
            intent = (Intent(this, LoginActivity::class.java))
        }
        intent!!.putExtra(Tags.FROM, 1)
        intent.putExtra(Tags.DATA, notificationModel)
        val CHANNEL_ID = getString(R.string.app_name)// The id of the channel.
        val name = getString(R.string.app_name)// The user-visible name of the channel.
        val importance = NotificationManager.IMPORTANCE_HIGH
        val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this)
                    .setSmallIcon(R.mipmap.logo)
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setContentTitle(notificationModel.title)
                    .setStyle(Notification.BigTextStyle().bigText(notificationModel.title))
                    .setContentText(notificationModel.message)
                    .setChannelId(CHANNEL_ID)
        } else {
            Notification.Builder(this)
                    .setSmallIcon(R.mipmap.logo)
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setContentTitle(notificationModel.title)
                    .setStyle(Notification.BigTextStyle().bigText(notificationModel.title))
                    .setContentText(notificationModel.message)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder.setContentIntent(fullScreenPendingIntent)
        var notification: Notification? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setCategory(Notification.CATEGORY_MESSAGE).setFullScreenIntent(fullScreenPendingIntent, true)
            notification = notificationBuilder.build()
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            notification = Notification(R.mipmap.logo,
                    getString(R.string.app_name), System.currentTimeMillis())
        } else {
            notification = notificationBuilder.build()
        }
        notification!!.defaults = Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE
        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
//        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(getRandomNumer(), notification)
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel: NotificationChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mNotificationManager.createNotificationChannel(mChannel)
        }
        mNotificationManager.notify(getRandomNumer(), notification)


    }

    private fun showGeneralNotif(notificationModel: PNModel) {
        var intent: Intent? = null
        try {
            if (Prefs(this).userdata == null) {
                intent = Intent(this, LoginActivity::class.java)
            } else {
                intent = (Intent(this, NotificationActivity::class.java))
            }
        } catch (e: Exception) {
            intent = (Intent(this, LoginActivity::class.java))
        }

//        val intent = Intent(this, NotificationActivity::class.java)
        intent!!.putExtra(Tags.FROM, 1)
        intent.putExtra(Tags.DATA, notificationModel)

        val CHANNEL_ID = getString(R.string.app_name)// The id of the channel.
        val name = getString(R.string.app_name)// The user-visible name of the channel.
        val importance = NotificationManager.IMPORTANCE_HIGH
        val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this)
                    .setSmallIcon(R.mipmap.logo)
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setContentTitle(notificationModel.title)
                    .setStyle(Notification.BigTextStyle().bigText(notificationModel.title))
                    .setContentText(notificationModel.message)
                    .setChannelId(CHANNEL_ID)
        } else {
            Notification.Builder(this)
                    .setSmallIcon(R.mipmap.logo)
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setContentTitle(notificationModel.title)
                    .setStyle(Notification.BigTextStyle().bigText(notificationModel.title))
                    .setContentText(notificationModel.message)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder.setContentIntent(fullScreenPendingIntent)
        var notification: Notification? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setCategory(Notification.CATEGORY_MESSAGE).setFullScreenIntent(fullScreenPendingIntent, true)
            notification = notificationBuilder.build()
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            notification = Notification(R.mipmap.logo,
                    getString(R.string.app_name), System.currentTimeMillis())
        } else {
            notification = notificationBuilder.build()
        }
        notification!!.defaults = Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE
        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
//        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(getRandomNumer(), notification)
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel: NotificationChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mNotificationManager.createNotificationChannel(mChannel)
        }
        mNotificationManager.notify(getRandomNumer(), notification)
    }

    private fun showAssignDeliveryBoyNotif(notificationModel: PNModel) {
        var intent: Intent? = null
        try {
            if (Prefs(this).userdata == null) {
                intent = Intent(this, LoginActivity::class.java)
            } else {
                intent = (Intent(this, AcceptedOrderDetailActivity::class.java))
                val intent2 = Intent()
                intent2.action = "UPDATE_ACCEPTED"
                sendBroadcast(intent2)
            }
        } catch (e: Exception) {
            intent = (Intent(this, LoginActivity::class.java))
        }
//      val intent = Intent(this, AcceptedOrderDetailActivity::class.java)
        intent!!.putExtra(Tags.FROM, 1)
        intent.putExtra(Tags.DATA, notificationModel)

        val CHANNEL_ID = getString(R.string.app_name)// The id of the channel.
        val name = getString(R.string.app_name)// The user-visible name of the channel.
        val importance = NotificationManager.IMPORTANCE_HIGH

        val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this)
                    .setSmallIcon(R.mipmap.logo)
//                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setContentTitle(notificationModel.title)
                    .setStyle(Notification.BigTextStyle().bigText(notificationModel.title))
                    .setContentText(notificationModel.message)
                    .setChannelId(CHANNEL_ID)
        } else {
            Notification.Builder(this)
                    .setSmallIcon(R.mipmap.logo)
//                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setContentTitle(notificationModel.title)
                    .setStyle(Notification.BigTextStyle().bigText(notificationModel.title))
                    .setContentText(notificationModel.message)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder.setContentIntent(fullScreenPendingIntent)
        var notification: Notification? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setCategory(Notification.CATEGORY_MESSAGE).setFullScreenIntent(fullScreenPendingIntent, true)
            notification = notificationBuilder.build()
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            notification = Notification(R.mipmap.logo,
                    getString(R.string.app_name), System.currentTimeMillis())
        } else {
            notification = notificationBuilder.build()
        }
        notification!!.defaults = Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE
        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
//        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(getRandomNumer(), notification).createNotificationChannel(mChannel)
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel: NotificationChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mNotificationManager.createNotificationChannel(mChannel)
        }
        mNotificationManager.notify(getRandomNumer(), notification)
//
    }

    private fun sendNotif(data: Map<String, String>): PNModel {
        var notificationsModel = PNModel()
        notificationsModel.notification_type = data.get(Tags.notification_type)
        notificationsModel.notification_id = data.get(Tags.notification_id)
        var jsonObject = JSONObject(data.get(Tags.payload))
        notificationsModel.second_user_id = jsonObject.getString(Tags.second_user_id)
        notificationsModel.image = jsonObject.getString(Tags.image)
        notificationsModel.profile_image = jsonObject.getString(Tags.profile_image)
        notificationsModel.amount = jsonObject.getString(Tags.amount)
        notificationsModel.user_id = jsonObject.getString(Tags.user_id)
        notificationsModel.item_id = jsonObject.getString(Tags.item_id)
        notificationsModel.last_name = jsonObject.getString(Tags.last_name)
        notificationsModel.title = jsonObject.getString(Tags.title)
        notificationsModel.first_name = jsonObject.getString(Tags.first_name)
        notificationsModel.badge = Integer.parseInt(data.get(Tags.badge))
        notificationsModel.message = data.get(Tags.message)
        AppDelegate.LogT("Notification==>" + notificationsModel)
        return notificationsModel
    }

    fun getRandomNumer(): Int {
        val r = Random()
        return r.nextInt(80 - 65) + 65
    }
}

