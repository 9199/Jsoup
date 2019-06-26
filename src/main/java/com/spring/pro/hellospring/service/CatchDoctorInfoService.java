package com.spring.pro.hellospring.service;

import com.spring.pro.hellospring.entity.DoctorDetailInfo;

import java.util.List;

/**
 * @Author: xiang.zhao
 * @ClassName:
 * @Description:
 * @Date: 2019/6/19 13:11
 */
public interface CatchDoctorInfoService {

    List<String> findHospitalDoctorInfo(String url);

    List<DoctorDetailInfo> buildDoctorDetailInfo(List<String> doctorInfoList);

    void exportDoctorInfo(List<DoctorDetailInfo> doctorDetailInfoList, String hospitalName);

}
