package com.octalsoftware.drewel.retrofitService;

/**
 * Created by heena on 26/9/17.
 */

public interface ApiConstant {
    String delivery_boy_notification_status = "notification_status";
    String login = "delivery_boy_login";
    String countries_list = "countries_list";
    String updateLocation = "delivery_boy_location_change";
    String delivery_boy_tasks_list = "delivery_boy_tasks_list";/*FLAG 1 = pending,2=out for delivery,3=complete*/
    String forgotpassword = "forgot_password";
    String changePasswordUser = "change_password";
    String edit_delivery_boy_profile = "edit_delivery_boy_profile";
    String driverStatus = "delivery_boy_status_change";/*status : 1=available,2=not available*/
    String delivery_boy_update_order_status = "delivery_boy_update_order_status";/*status  = 2(out for delivery), status= 3(delivered)*/
    String get_order_detail_for_delivery_boy = "get_order_detail_for_delivery_boy";
    String logout = "logout";
    String earningList = "earningList";
    String get_notifications = "delivery_boy_get_notifications";
    String read_notification = "read_notification";
    /*header*/
    String timestamp = "timestamp";
    String appkey = "appkey";
    String appkey_value = "skeuomo@5632832";
    String token = "token";
    String session = "session";

    int POST = 01;
    int GET = 02;
    public static int CAMERA_REQUEST = 2;
    public static int GALARY_REQUEST = 3;
    public static int GPS_REQUEST_CODE = 1000;
}