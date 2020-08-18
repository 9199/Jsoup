package com.spring.pro.hellospring;

import com.spring.pro.hellospring.entity.DoctorDetailInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: xiang.zhao
 * @ClassName:
 * @Description:
 * @Date: 2019/6/19 19:17
 */
@RestController
@RequestMapping(value = "/view")
public class CatchDoctorInfoController {

    @Autowired
    private CatchDoctorInfoThread catchDoctorInfoThread;

    @GetMapping("/catch")
    public void catchInfo( @RequestParam("url") String url,
                           @RequestParam("name") String name) {
        catchDoctorInfoThread.startGetDoctorInfo(url,name);
    }

    @GetMapping(value = "/hello")
    public String SayHello() {
        return "catch";
    }


    @Transactional(rollbackFor = Exception.class)
    public void getDoctor() {
        DoctorDetailInfo doctorDetailInfo = new DoctorDetailInfo();
        doctorDetailInfo.setSex("男");
        doctorDetailInfo.setKeShi("内科");
        doctorDetailInfo.setName("杨爱民");
        doctorDetailInfo.setYiYuan("朝阳医院");
        doctorDetailInfo.setZhiWei("主任");
        doctorDetailInfo.setLvLi("1");
        System.out.println(doctorDetailInfo.toString());
    }
}
