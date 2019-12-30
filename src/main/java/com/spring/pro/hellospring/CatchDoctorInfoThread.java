package com.spring.pro.hellospring;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.spring.pro.hellospring.entity.DoctorDetailInfo;
import com.spring.pro.hellospring.service.CatchDoctorInfoService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: xiang.zhao
 * @ClassName:
 * @Description:
 * @Date: 2019/6/19 16:48
 */
@Repository
public class CatchDoctorInfoThread {
    @Autowired
    private CatchDoctorInfoService catchDoctorInfoService;
    private final AtomicInteger syncECTaskCounter = new AtomicInteger(0);
    ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("thread-call-runner-%d").build();
    int size = 8;
    ExecutorService executorService = new ThreadPoolExecutor(size,size,0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(),namedThreadFactory);
    public void startGetDoctorInfo(String url,String name) {
        List<List<String>> hospitalDoctorInfoList = new ArrayList<>();

        final Long startTime = System.currentTimeMillis();
        final List<String> hospitalDoctorInfo = catchDoctorInfoService.findHospitalDoctorInfo(url);
        final Long firstTime = System.currentTimeMillis();
        System.out.println("Find "+hospitalDoctorInfo.size()+" Doctor url cost time: " + (firstTime - startTime));

        Integer size = hospitalDoctorInfo.size();
        Integer listSize = 50;
        for (int i=0 ; i<size ; i+=listSize) {
            if (i+listSize > size) {
                listSize = size - i;
            }
            List<String> doctorInfo = hospitalDoctorInfo.subList(i, i + listSize);
            hospitalDoctorInfoList.add(doctorInfo);
        }

        System.out.println(listSize+"个医生信息为一组，总共分组：" + hospitalDoctorInfoList.size());

        int threadNum = 0;
        List<Future<List<DoctorDetailInfo>>> futureList = new ArrayList<>();
        for (List<String> docUrls : hospitalDoctorInfoList) {
            Future<List<DoctorDetailInfo>> future = runnerOperation(docUrls);
            futureList.add(future);
            threadNum ++;
        }
        System.out.println("总线程数："+threadNum);
        List<DoctorDetailInfo> docInfoList = new ArrayList<>();
        for (Future<List<DoctorDetailInfo>> future : futureList) {
            try {
                List<DoctorDetailInfo> doctorDetailInfos = future.get();
                docInfoList.addAll(doctorDetailInfos);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        final Long secondTime = System.currentTimeMillis();
        System.out.println("Build All Doctor info cost time: " + (secondTime - firstTime));

        if (CollectionUtils.isNotEmpty(docInfoList)) {
            System.out.println("总共获取"+docInfoList.size()+"条数据");
            catchDoctorInfoService.exportDoctorInfo(docInfoList,name);
        }

        final Long thirdTime = System.currentTimeMillis();
        System.out.println("Export All Doctor info cost time: " + (thirdTime - secondTime));
    }

    private Future<List<DoctorDetailInfo>> runnerOperation(List<String> docUrls) {

        CatchDoctorInfoCallable infoCallable = new CatchDoctorInfoCallable() {
            @Override
            public List<DoctorDetailInfo> call() throws Exception {
                syncECTaskCounter.incrementAndGet();
                try {
                    return catchDoctorInfoService.buildDoctorDetailInfo(docUrls);
                }catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }finally {
                    syncECTaskCounter.decrementAndGet();
                }
            }
        };
        while (true) {
            if (syncECTaskCounter.get() < 8) {
                System.out.println("活跃线程数："+syncECTaskCounter.get());
                return executorService.submit(infoCallable);
            }
        }
    }
}
