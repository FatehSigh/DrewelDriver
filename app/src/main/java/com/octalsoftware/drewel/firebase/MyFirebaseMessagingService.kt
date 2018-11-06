package com.octalsoftware.drewel.firebase

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.octalsoftware.drewel.AppDelegate
import com.octalsoftware.drewel.R
import com.octalsoftware.drewel.activity.AcceptedOrderDetailActivity
import com.octalsoftware.drewel.activity.DeliveredOrderDetailActivity
import com.octalsoftware.drewel.activity.LoginActivity
import com.octalsoftware.drewel.constant.Tags
import com.octalsoftware.drewel.model.PNModel
import com.octalsoftware.drewel.utils.Prefs
import org.json.JSONObject
import java.util.*
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.octalsoftware.drewel.HomeActivity
import com.octalsoftware.drewel.activity.NotificationActivity
import com.octalsoftware.drewel.utils.NotificationRxJavaBus
import com.os.offerlee.rxbus.SampleRxJavaBus


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance()
    private val options = DisplayImageOptions.Builder().cacheInMemory(true)
            .cacheOnDisc(true).resetViewBeforeLoading(true).build()

    object NotificationType {
        const val deliveryStatusChange = "deliveryStatusChange"
        const val deliveryBoyAssigned = "deliveryBoyAssigned"
        const val general = "general"
    }

    /*AAAAMN3mbZE:APA91bHoKD-ijoI0Qra3OHKvC_cAA1dkuHCcNKYnznfxJ6uV_-KBHHn-mTir2waw6xjYitWKlOPrU3iBBYSWaUQmNsfgdzIV1wPAsQoCKzewijzvfdx0PJpaLX_Gv-kMhG7PQlikjv_H*/
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

    private fun showdeliveryStatusChangeNotif(notificationModel: PNModel) {
        var intent: Intent? = null
        try {
            if (Prefs(this).userdata == null) {
                intent = Intent(this, LoginActivity::class.java)
            } else {
                intent = (Intent(this, DeliveredOrderDetailActivity::class.java))

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
        try {
            if (Prefs(this).userdata == null) {
            } else {
//                val intent2 = Intent()
//                intent2.action = "UPDATE_DELIVER"
//                sendBroadcast(intent2)
            }
        } catch (e: Exception) {
        }


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
        try {
            if (Prefs(this).userdata == null) {
            } else {
                val intent2 = Intent()
                intent2.action = "UPDATE_ACCEPTED"
                sendBroadcast(intent2)
            }
        } catch (e: Exception) {
        }

//
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        AppDelegate.LogT("remoteMessage==>" + remoteMessage!!.data)
        var data = sendNotif(remoteMessage.data)
//        if (data.notification_type.equals(NotificationType.deliveryBoyAssigned)) showAssignDeliveryBoyNotif(data)
//        else if (data.notification_type.equals(NotificationType.deliveryStatusChange)) showdeliveryStatusChangeNotif(data)
//        else if (data.notification_type.equals(NotificationType.general)) showGeneralNotif(data)
        Prefs(this).putIntValue(Tags.QUANTITY, Prefs(this).getIntValue(Tags.QUANTITY,0)+1)
        val intent2 = Intent()
        intent2.action = "UPDATE_COUNT"
        sendBroadcast(intent2)
        generateNotification(data)
    }


    private fun sendNotif(data: Map<String, String>): PNModel {
        var notificationsModel = PNModel()
        notificationsModel.notification_type = data.get(Tags.notification_type)
        notificationsModel.notification_id = data.get(Tags.notification_id)
        AppDelegate.LogT("data.get(Tags.payload)=" + data.get(Tags.payload))
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

    private fun generateNotification(data: PNModel) {

        val largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_foreground)
        val notificationManager = getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))

                .setContentTitle(getString(R.string.app_name))
                .setContentText(data.message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setLargeIcon(largeIcon)
                .setStyle(NotificationCompat.BigTextStyle().bigText(data.message))
                .setContentIntent(setNotificationIntent(data))
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher)
            mBuilder.color = ContextCompat.getColor(this, R.color.colorPrimary)
        } else
            mBuilder.setSmallIcon(R.mipmap.ic_launcher)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = getString(R.string.default_notification_channel_name)
            val description = getString(R.string.default_notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(getString(R.string.default_notification_channel_id), name, importance)
            mChannel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(mChannel)
        }
//        val id = (System.currentTimeMillis() * (Math.random() * 100).toInt()).toInt()
        notificationManager.notify(getRandomNumer(), mBuilder.build())
        when {
            data.notification_type.equals(NotificationType.deliveryBoyAssigned) -> try {
                NotificationRxJavaBus.getInstance().notificationPublishSubject.onNext("UPDATE_ACCEPTED")
                val intent2 = Intent()
                intent2.action = "UPDATE_ACCEPTED"
                sendBroadcast(intent2)

            } catch (e: Exception) {
            }
            data.notification_type.equals(NotificationType.deliveryStatusChange) -> try {
                NotificationRxJavaBus.getInstance().notificationPublishSubject.onNext("UPDATE_DELIVER")
                NotificationRxJavaBus.getInstance().notificationPublishSubject.onNext("UPDATE_COMPLETED")
            } catch (e: Exception) {
            }
        }
    }


    private fun setNotificationIntent(data: PNModel): PendingIntent {
        var notificationIntent: Intent? = null
        if (data.notification_type.equals(NotificationType.deliveryBoyAssigned)) {
            notificationIntent = Intent(this, HomeActivity::class.java)
        } else if (data.notification_type.equals(NotificationType.deliveryStatusChange)) {
            notificationIntent = Intent(this, DeliveredOrderDetailActivity::class.java)
        } else if (data.notification_type.equals(NotificationType.general)) {
            notificationIntent = Intent(this, NotificationActivity::class.java)
        }
//      val notificationIntent = Intent(this, ProductDetailActivity::class.java)
        notificationIntent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val parentIntent = Intent(this, HomeActivity::class.java)
        parentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        /* create stack builder if you want to open parent activty on notification click*/
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(HomeActivity::class.java)
//        notificationIntent.putExtra(AppIntentExtraKeys.ORDER_ID, data.item_id)
        notificationIntent!!.putExtra(Tags.FROM, 1)
        notificationIntent.putExtra(Tags.DATA, data)
        /* add all notification to stack*/
        stackBuilder.addNextIntent(parentIntent)
        stackBuilder.addNextIntent(notificationIntent)
        /* get pending intent from stack builder*/
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT)
    }

}

