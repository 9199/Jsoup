package com.spring.pro.hellospring;


import com.spring.pro.hellospring.entity.DoctorDetailInfo;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * @Author: xiang.zhao
 * @ClassName:
 * @Description:
 * @Date: 2019/6/19 16:22
 */
public abstract class CatchDoctorInfoCallable implements Callable<List<DoctorDetailInfo>> {
    @Override
    public abstract List<DoctorDetailInfo> call() throws Exception ;
}
