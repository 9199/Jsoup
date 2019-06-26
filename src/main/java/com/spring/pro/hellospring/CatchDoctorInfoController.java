package com.spring.pro.hellospring;

import org.springframework.beans.factory.annotation.Autowired;
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
}
