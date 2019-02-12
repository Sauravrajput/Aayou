package com.scriptmall.cabuser;

/**
 * Created by SCRIPTSMALL on 4/10/2017.
 */
public class Config {
//    public static final String doamin_name_old ="http://appdroidsolutions.com/ambulance_service/admin/restapi/user/";
   /// public static final String doamin_name ="http://myambulensi.com/amb/admin/restapi/user/";
///    https://eyesnears.co/my_ambulancy
    //public static final String doamin_name ="http://myambulensi.com/amb/admin/restapi/user/";
    public static final String doamin_name ="https://eyesnears.co/my_ambulancy/admin/restapi/user/";
    public static final String USERREG_URL = doamin_name+"register.php";
    public static final String COUNTRY_URL = doamin_name+"countrylist.php";
    public static final String REGISTER2_URL = doamin_name+"otp.php";
    public static final String DRIVER_LOCATION_URL = doamin_name+"get_driver_location.php";
    public static final String CABTYPE_URL = doamin_name+"cab_type.php";

    public static final String CABCAT_URL = doamin_name+"cab_cat.php";


    public static final String MY_RIDES_URL = doamin_name+"myrides.php";
    public static final String POST_REVIEW_URL = doamin_name+"reviews.php";
    public static final String CANCEL_BOOKING_URL = doamin_name+"cancel_ride.php";

    public static final String FAQ_URL = doamin_name+"faq.php";
    public static final String FAQ_RATE_URL = doamin_name+"faq_helpful.php";

    public static final String PASSWORDUPDATE_URL = doamin_name+"changepassword.php";
    public static final String EDITPROFILE_URL = doamin_name+"editprofile.php";
    public static final String PROFILE_URL = doamin_name+"viewprofile.php";
    public static final String EDIT_NUMBER_URL = doamin_name+"editrelative.php";

    public static final String RATECARD_URL = doamin_name+"cab_list.php";

    public static final String TRACKING_URL = doamin_name+"pickup.php";

    public static final String BOOKING_URL = doamin_name+"booking.php";

    public static final String SOS_ALERT_URL = doamin_name+"sos.php";

    public static final String TRACKER_NUMBER_URL = doamin_name+"trackerslist.php";

    public static final String TRACKERS_RIDES_URL = doamin_name+"trackers_ridelist.php";

    public static final String TRACK_RIDE_DETALS_URL = doamin_name+"tracking_details.php";
    public static final String AMBULANCE_LISTING=doamin_name+ "ambulance_listing.php";
    public static final String DELETE_RIDE_URL = "students_login.php";




    public static final String CAB_TYPE = "cabtype";
    public static final String CAB_IMG = "logo_url";
    public static final String PTPAMT = "pointtopoint_fare";
    public static final String RENTAMT = "rental_fare";
    public static final String OUTAMT = "outstation_oneway";
    public static final String OUTROUNDAMT = "outstation_roundtrip";
    public static final String OUTWAITING = "outstation_waiting";
    public static final String DRIVERAMT = "outstation_allowance";
    public static final String SEAT = "seats";


    public static final String UID   ="userid";
    public static final String ULNAME   ="lname";
    public static final String UFNAME   ="fname";
    public static final String UPHONENO="phone";
    public static final String UMAIL="email";
    public static final String UADDR = "address";
    public static final String CITY = "city";
    public static final String COUNTRY = "country";
    public static final String STATE = "state";
    public static final String ZIP = "zip";
    public static final String UPWD="password";
    public static final String FCM_TOKEN="fcm_token";
    public static final String OTP="otp";

    public static final String PRIVACY = "privacy";

    public static final String ONE_WAY = "oneway";
    public static final String ROUND = "roundtrip";
    public static final String WAITING = "waiting_charge";
    public static final String RENTAL = "rental";

    public static final String RELATIVE_1="relative1";
    public static final String RELATIVE_2="relative2";
    public static final String RELATIVE_3="relative3";
    public static final String RELATIVE_4="relative4";
    public static final String RELATIVE_NAME_1="relative_name1";
    public static final String RELATIVE_NAME_2="relative_name2";
    public static final String RELATIVE_NAME_3="relative_name3";
    public static final String RELATIVE_NAME_4="relative_name4";
    public static final String TRACK_STATUS="tracking_status";
    public static final String TRACK_ID = "tracker_id";
    public static final String TRACK_NAME = "tracker_name";
    public static final String TRACK_PHONE = "tracker_phone";




    public static final String LOGIN_URL = "students_login.php";
    public static final String FORGOTPWD_URL = "students_login.php";

    public static final String DRIVER_STATUS_URL = "students_login.php";

    public static final String DRIVER_STATUS_CHANGE_URL = "students_login.php";

    public static final String NEW_RAID_URL = "students_login.php";

    public static final String RAID_HISTRYLIST_URL = "students_login.php";

    public static final String RAID_FINISH_URL = "students_login.php";

    public static final String DRIVER_LATLONG_URL = "students_login.php";

    public static final String RIDE_BOOK_LIST_URL = "students_login.php";


    public static final String KEY_EMAIL="mail";
    public static final String KEY_PWD="password";


    public static final String DID   ="did";
    public static final String DSTATUS  ="dstatus";
    public static final String DNAME   ="drivername";
    public static final String DPHONENO="drievrphone";
    public static final String DMAIL="drivermail";
    public static final String DADDR="driveraddress";

    public static final String DRI_IMG="profile_url";
    public static final String DRI_NAME="driver_name";

    public static final String RDATE="ride_date";
    public static final String RETDATE="return_date";
    public static final String RPICKUP="pickup_location";
    public static final String RDROP="drop_location";
    public static final String PIC_LAT_LANG="pickup_lat_long";
    public static final String DROP_LATLANG="drop_lat_long";
    public static final String RAMT="ride_amount";
    public static final String RTYPE="ride_type";
    public static final String PIC_LAT="pickup_lat";
    public static final String PIC_LONG="pickup_long";
    public static final String DROP_LAT="drop_lat";
    public static final String DROP_LONG="drop_long";
    public static final String RIDE_STATUS="ride_status";
    public static final String RID="ride_id";

    public static final String RATING = "rating";
    public static final String REVIEW = "comment";
    public static final String NUMBER = "number";



    public static final String JSON_ARRAY = "result";

    public static final String LOGIN_SUCCESS = "success";

    public static final String SHARED_PREF_NAME = "cabbook";

    //This would be used to store the email of current logged in user
    public static final String MAILID_SHARED_PREF = "mailid";
    public static final String DID_SHARED_PREF = "did";
    public static final String UID_SHARED_PREF = "uid";

    public static final String ONEWAY_SHARED_PREF = "oneway";
    public static final String ROUND_SHARED_PREF = "roundtrip";
    public static final String WAITING_SHARED_PREF = "waiting";
    public static final String RENTAL_SHARED_PREF = "rental";
    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";



}
