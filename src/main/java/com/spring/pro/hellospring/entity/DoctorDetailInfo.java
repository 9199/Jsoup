package com.spring.pro.hellospring.entity;

/**
 * @Author: xiang.zhao
 * @ClassName:
 * @Description:
 * @Date: 2019/6/17 20:14
 */
public class DoctorDetailInfo {
    String yiYuan;
    String keShi;
    String name;
    String zhiWei;
    String shChang;
    String dateList;
    String lvLi;
    String sex;

    public String getYiYuan() {
        return yiYuan;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setYiYuan(String yiYuan) {
        this.yiYuan = yiYuan;
    }

    public String getKeShi() {
        return keShi;
    }

    public void setKeShi(String keShi) {
        this.keShi = keShi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZhiWei() {
        return zhiWei;
    }

    public void setZhiWei(String zhiWei) {
        this.zhiWei = zhiWei;
    }

    public String getShChang() {
        return shChang;
    }

    public void setShChang(String shChang) {
        this.shChang = shChang;
    }

    public String getDateList() {
        return dateList;
    }

    public void setDateList(String dateList) {
        this.dateList = dateList;
    }

    public String getLvLi() {
        return lvLi;
    }

    public void setLvLi(String lvLi) {
        this.lvLi = lvLi;
    }
}
