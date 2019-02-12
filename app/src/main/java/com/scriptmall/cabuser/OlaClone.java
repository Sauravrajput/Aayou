package com.scriptmall.cabuser;

import java.io.Serializable;

/**
 * Created by scriptmall on 11/20/2017.
 */

public class OlaClone implements Serializable {

    String cabid,cabname,cabtime;
    String cabprice,cabimg;

    String rdate, rpickup,rdrop,ramount,rtime,ridestatus,driver_img,driver_name,rid;

    String catid,catname;
    String subcatid,subcatname;

    String pic_lat,pic_lng,drop_lat,drop_lng,ride_type;

    String faq_id,ques,ans;

    String tracker_name,tracker_phone,tracker_id;

    String cabtype,seats,ptpamt,rentamt,outamt,outroundamt,outwaitingamt,driveramt;

    public OlaClone(){}

    public  OlaClone(String cabname,String cabtime){
        this.cabname=cabname;
        this.cabtime=cabtime;

        this.tracker_name=cabname;
        this.tracker_phone=cabtime;
    }



    public  OlaClone(String rdate,String rpickup,String rdrop,String rtime,String ridestatus,String rid,String cabname){
        this.rdate=rdate;
        this.rpickup=rpickup;
        this.rdrop=rdrop;
        this.rtime=rtime;
        this.ridestatus=ridestatus;
        this.rid=rid;
        this.cabname=cabname;
    }

    public OlaClone(String catname){
        this.catname=catname;

        this.subcatname=catname;
    }

    public String getCabid() {  return cabid;  }
    public void setCabid(String cabid) {  this.cabid = cabid;  }

    public String getCabname() {  return cabname;  }
    public void setCabname(String cabname) {  this.cabname = cabname;  }

    public String getCabtime() {  return cabtime;  }
    public void setCabtime(String cabtime) {  this.cabtime = cabtime;  }

    public String getCabprice() {  return cabprice;  }
    public void setCabprice(String cabprice) {  this.cabprice = cabprice;  }


    public String getCabimg() {  return cabimg;  }
    public void setCabimg(String cabimg) {  this.cabimg = cabimg;  }

    public String getRdate() {  return rdate;  }
    public void setRdate(String rdate) {  this.rdate = rdate;  }

    public String getRpickup() {  return rpickup;  }
    public void setRpickup(String rpickup) {  this.rpickup = rpickup;  }

    public String getRdrop() {  return rdrop;  }
    public void setRdrop(String rdrop) {  this.rdrop = rdrop;  }

    public String getRamount() {  return ramount;  }
    public void setRamount(String ramount) {  this.ramount = ramount;  }

    public String getRtime() {  return rtime;  }
    public void setRtime(String rtime) {  this.rtime = rtime;  }

    public String getRidestatus() {  return ridestatus;  }
    public void setRidestatus(String ridestatus) {  this.ridestatus = ridestatus;  }

    public String getDriver_img() {  return driver_img;  }
    public void setDriver_img(String driver_img) {  this.driver_img = driver_img;  }

    public String getRid() {  return rid;  }
    public void setRid(String rid) {  this.rid = rid;  }

    public String getCatid() {  return catid;  }
    public void setCatid(String catid) {  this.catid = catid;  }

    public String getCatname() {  return catname;  }
    public void setCatname(String catname) {  this.catname = catname;  }

    public String getSubcatid() {  return subcatid;  }
    public void setSubcatid(String subcatid) {  this.subcatid = subcatid;  }

    public String getSubcatname() {  return subcatname;  }
    public void setSubcatname(String subcatname) {  this.subcatname = subcatname;  }

    public String getPic_lat() {  return pic_lat;  }
    public void setPic_lat(String pic_lat) {  this.pic_lat = pic_lat;  }

    public String getPic_lng() {  return pic_lng;  }
    public void setPic_lng(String pic_lng) {  this.pic_lng = pic_lng;  }

    public String getDrop_lat() {  return drop_lat;  }
    public void setDrop_lat(String drop_lat) {  this.drop_lat = drop_lat;  }

    public String getDrop_lng() {  return drop_lng;  }
    public void setDrop_lng(String drop_lng) {  this.drop_lng = drop_lng;  }

    public String getRide_type() {  return ride_type;  }
    public void setRide_type(String ride_type) {  this.ride_type = ride_type;  }

    public String getDriver_name() {  return driver_name;  }
    public void setDriver_name(String driver_name) {  this.driver_name = driver_name;  }

    public String getFaq_id() {  return faq_id;  }
    public void setFaq_id(String faq_id) {  this.faq_id = faq_id;  }

    public String getQues() {  return ques;  }
    public void setQues(String ques) {  this.ques = ques;  }

    public String getAns() {  return ans;  }
    public void setAns(String ans) {  this.ans = ans;  }

    public String getTracker_name() {  return tracker_name;  }
    public void setTracker_name(String tracker_name) {  this.tracker_name = tracker_name;  }

    public String getTracker_phone() {  return tracker_phone;  }
    public void setTracker_phone(String tracker_phone) {  this.tracker_phone = tracker_phone;  }

    public String getTracker_id() {  return tracker_id;  }
    public void setTracker_id(String tracker_id) {  this.tracker_id = tracker_id;  }

    public String getCabtype() {  return cabtype;  }
    public void setCabtype(String cabtype) {  this.cabtype = cabtype;  }

    public String getSeats() {  return seats;  }
    public void setSeats(String seats) {  this.seats = seats;  }

    public String getPtpamt() {  return ptpamt;  }
    public void setPtpamt(String ptpamt) {  this.ptpamt = ptpamt;  }

    public String getRentamt() {  return rentamt;  }
    public void setRentamt(String rentamt) {  this.rentamt = rentamt;  }

    public String getOutamt() {  return outamt;  }
    public void setOutamt(String outamt) {  this.outamt = outamt;  }

    public String getOutroundamt() {  return outroundamt;  }
    public void setOutroundamt(String outroundamt) {  this.outroundamt = outroundamt;  }

    public String getOutwaitingamt() {  return outwaitingamt;  }
    public void setOutwaitingamt(String outwaitingamt) {  this.outwaitingamt = outwaitingamt;  }

    public String getDriveramt() {  return driveramt;  }
    public void setDriveramt(String driveramt) {  this.driveramt = driveramt;  }


}
