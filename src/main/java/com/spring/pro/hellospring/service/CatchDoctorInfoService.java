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

    /**
     * 获取到医院下所有的医生主页URL
     * @param url
     * @return
     */
    List<String> findHospitalDoctorInfo(String url);

    /**
     * 批量组装需要爬取的医生信息
     * @param doctorInfoList
     * @return
     */
    List<DoctorDetailInfo> buildDoctorDetailInfo(List<String> doctorInfoList);

    /**
     * 医生信息导出成Excel
     * @param doctorDetailInfoList
     * @param hospitalName
     */
    void exportDoctorInfo(List<DoctorDetailInfo> doctorDetailInfoList, String hospitalName);

}
