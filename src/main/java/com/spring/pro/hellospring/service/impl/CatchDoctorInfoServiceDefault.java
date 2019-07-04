package com.spring.pro.hellospring.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.spring.pro.hellospring.ExcelUtil;
import com.spring.pro.hellospring.entity.DoctorDetailInfo;
import com.spring.pro.hellospring.entity.IPInfo;
import com.spring.pro.hellospring.service.CatchDoctorInfoService;
import com.spring.pro.hellospring.HTTPCommonUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @Author: xiang.zhao
 * @ClassName:
 * @Description:
 * @Date: 2019/6/19 13:12
 */
@Service
public class CatchDoctorInfoServiceDefault implements CatchDoctorInfoService {

    @Override
    public List<String> findHospitalDoctorInfo(String hospitalURL) {
        String homePageURL = "https://www.haodf.com/";
        List<IPInfo> ipList = getIpList();
        Document hospital = collectURL(hospitalURL,homePageURL,ipList);
        //该医院总共多少医生信息
        List<String> doctorURLList = new ArrayList<>();

        final String hospitalName = hospital.select("h1.hospital-name").first().text();
        final Elements keShiList = hospital.select("ul.faculty-list").get(0).select("li.f-l-item");

        if (!CollectionUtils.isEmpty(keShiList)) {
            //循环获取每个科室名称和详情页URL
            System.out.println("共有科室："+keShiList.size()+"大类");
            keShiList.forEach(keShi -> {
                final Elements keShiUrlList = keShi.select("ul.f-l-i-second").select("a.f-l-i-s-i-w-name");
                if (!CollectionUtils.isEmpty(keShiUrlList)) {
                    System.out.println("共有小科室："+keShiUrlList.size()+"个");
                    keShiUrlList.forEach(keShiUrl -> {
                        final String url = keShiUrl.attr("href");
                        final String keShiName = keShiUrl.text();
                        //进入科室详情
                        Document keShiDetail = collectURL("https:"+url,hospitalURL,ipList);
                        //分页总数
                        final Elements pageSize = keShiDetail.select("div.p_bar").select("a[href]");
                        if (!CollectionUtils.isEmpty(pageSize) && pageSize.size() > 1) {
                            for (int i=1;i<=pageSize.size();i++) {
                                StringBuilder sb = new StringBuilder("https:");
                                sb.append(url.substring(0,url.lastIndexOf(".htm")));
                                sb.append("/menzhen_").append(i).append(".htm");
                                getDoctorDetailURLForKeShi(sb.toString(),hospitalName,keShiName,doctorURLList,ipList);
                            }
                        }else {
                            getDoctorDetailURLForKeShi("https:"+url,hospitalName,keShiName,doctorURLList,ipList);
                        }
                    });
                }
            });
        }

        return doctorURLList;
    }

    @Override
    public List<DoctorDetailInfo> buildDoctorDetailInfo(List<String> doctorInfoList) {
        List<IPInfo> ipList = getIpList();
        List<DoctorDetailInfo> doctorDetailInfoList = new ArrayList<>();
        //组装用户信息
        doctorInfoList.forEach(url -> {
            doctorDetailInfoList.add(buildDoctorInfo(url,ipList));
        });
        return doctorDetailInfoList;
    }

    @Override
    public void exportDoctorInfo(List<DoctorDetailInfo> doctorDetailInfoList,String hospitalName) {
        exportExcel(doctorDetailInfoList,hospitalName);
    }


    /**
     * 链接动态IP库，获取动态IP
     * @return
     */
    private static List<IPInfo> getIpList() {
        List<IPInfo> ipInfoSet = new ArrayList<>();
        final String GET_IP_URL = "http://piping.mogumiao.com/proxy/api/get_ip_al?appKey=85e143121fca4921ad7ccac7b8b15db9&count=5&expiryDate=0&format=1&newLine=2";

        Boolean flag = true;
        Integer count = 1;
        while (flag) {
            count ++;
            for (int i=0 ;i<1;i++) {
                Document doc = null;
                try {
                    Thread.sleep(5000);
                    doc = Jsoup.connect(GET_IP_URL).get();
                    System.out.println(doc.text());
                    JSONObject jsonObject = JSONObject.parseObject(doc.text());
                    List<Map<String,Object>> list = (List<Map<String,Object>>) jsonObject.get("msg");

                    for (Map<String,Object> map : list ) {
                        IPInfo ipInfo = new IPInfo();
                        String ip = (String)map.get("ip");
                        String port = (String)map.get("port") ;
                        ipInfo.setIp(ip);
                        ipInfo.setPort(Integer.valueOf(port));
                        ipInfoSet.add(ipInfo);
                    }
                    flag = false;
                } catch (Exception e) {
                    continue;
                }
            }
            if (count == 10) {
                flag = false;
            }
            System.out.println("count :" + count);
        }
        return ipInfoSet;
    }


    public static IPInfo isHostReachable(List<IPInfo> ipInfoList) {
        IPInfo ip = null;
        try {
            if (CollectionUtils.isEmpty(ipInfoList)) {
                return null;
            }
            for (IPInfo ipInfo : ipInfoList) {
                if (Boolean.TRUE.equals(InetAddress.getByName(ipInfo.getIp()).isReachable(3000))) {
                    ip = ipInfo;
                    ipInfoList.remove(ip);
                    return ip;
                }else {
                    ipInfoList.remove(ip);
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ip;
    }

    /**
     * 通过科室获取所有的医生URL
     * @param url
     * @param hospitalName
     * @param keShiName
     */
    private static void getDoctorDetailURLForKeShi(String url,String hospitalName,String keShiName,List<String> doctorURLList,List<IPInfo> ipInfoList) {
        //
        Document ks =collectURL(url,url,ipInfoList);
        //每页有多少医生
        final Elements doctorList = ks.getElementById("doc_list_index").getElementsByTag("tr");
        if (!CollectionUtils.isEmpty(doctorList)) {
            System.out.println("获取医生信息："+doctorList.size()+"条");
            doctorList.forEach(doctor -> {
                StringBuilder sb = new StringBuilder();
                sb.append(hospitalName+",").append(keShiName+",");
                //每个医生的信息详情
                final Element d = doctor.getElementsByTag("td").first().getElementsByTag("li").first().select("a").first();
                final String doctorUrl = d.attr("href");
                final String doctorName = d.text();
                sb.append(doctorName+",");
                final String docZhiWei = doctor.getElementsByTag("td").first().getElementsByTag("li").first().select("p").first().text();
                sb.append(docZhiWei+",");
                doctorURLList.add(sb.append("https:"+doctorUrl).toString());

            });
        }
    }

    private static DoctorDetailInfo buildDoctorInfo(String info,List<IPInfo> ipInfoList) {
        final String[] infoArray = info.split(",");
        String yiYuan = infoArray[0];
        String keShi = infoArray[1];
        String name = infoArray[2];
        String zhiWei = infoArray[3];
        String url = infoArray[4];
        Document doctorDetail = collectURL(url,url,ipInfoList);
        if (doctorDetail == null) {
            System.out.println("获取医生"+name+"信息失败");
            return null;
        }
        final String[] doctorInfo = doctorDetail.head().select("meta[name=description]").first().attr("content").split("，");
        final String[] days = doctorDetail.getElementsByTag("script").get(4).data().toString().split("<img title");
        final String[] doc = doctorDetail.getElementsByTag("script").get(3).data().toString().split("<!--HAODF");

        DoctorDetailInfo docInfo = new DoctorDetailInfo();
        DecimalFormat df = new DecimalFormat("#.00");

        docInfo.setYiYuan(yiYuan);
        docInfo.setKeShi(keShi);
        docInfo.setName(name);
        docInfo.setZhiWei(zhiWei);
        docInfo.setShChang(doctorInfo[3]);
        docInfo.setDateList(df.format((days.length-1)*0.5));
        if (doc[0].contains("\\u7537")) {
            docInfo.setSex("男");
        }else if(doc[0].contains("\\u5973")) {
            docInfo.setSex("女");
        }else {
            docInfo.setSex("网站未维护性别信息");
        }
        return docInfo;
    }

    /**
     * 导出EXCEL
     * @param doctorDetailInfos
     */
    private static void exportExcel(List<DoctorDetailInfo> doctorDetailInfos,String hospitalName) {
        try {
            if (CollectionUtils.isEmpty(doctorDetailInfos)) {
                System.out.println("没有获取到医生信息！");
                return;
            }
            System.out.println("获取到"+doctorDetailInfos.size()+"条医生信息！");
            LinkedHashMap<String, String> head = newExportHead();
            JSONArray exportResults = new JSONArray();
            doctorDetailInfos.forEach(info -> {
                JSONArray jsonArray = newExportJson(info);
                exportResults.addAll(jsonArray);
            });
            ExcelUtil excelUtil = new ExcelUtil();
            excelUtil.exportExcel(head, exportResults, hospitalName+"医生信息");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static LinkedHashMap<String, String> newExportHead() {
        LinkedHashMap<String, String> head = new LinkedHashMap<>();
        head.put("yiYuan","医院");
        head.put("keShi","科室");
        head.put("name","姓名");
        head.put("zhiWei","职位");
        head.put("shChang","擅长");
        head.put("dateList","天数");
        head.put("sex","性别");
        return head;
    }

    /**
     * l
     * @param url
     * @param sourceUrl
     * @return
     */
    private static Document collectURL(String url,String sourceUrl,List<IPInfo> ipList) {
        HTTPCommonUtil.trustEveryone();
        Document document = null;
        Boolean flag = true;
        Integer count = 0;
        Random r = new Random();
        final String[] ua = {
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36 OPR/26.0.1656.60",
            "Opera/8.0 (Windows NT 5.1; U; en)",
            "Mozilla/5.0 (Windows NT 5.1; U; en; rv:1.8.1) Gecko/20061208 Firefox/2.0.0 Opera 9.50",
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; en) Opera 9.50",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0",
            "Mozilla/5.0 (X11; U; Linux x86_64; zh-CN; rv:1.9.2.10) Gecko/20100922 Ubuntu/10.10 (maverick) Firefox/3.6.10",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.57.2 (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11",
            "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.16 (KHTML, like Gecko) Chrome/10.0.648.133 Safari/534.16",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.11 TaoBrowser/2.0 Safari/536.11",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.71 Safari/537.1 LBBROWSER",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; LBBROWSER)",
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 732; .NET4.0C; .NET4.0E; LBBROWSER)",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; QQBrowser/7.0.3698.400)",
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 732; .NET4.0C; .NET4.0E)",
            "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.84 Safari/535.11 SE 2.X MetaSr 1.0",
            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; SV1; QQDownload 732; .NET4.0C; .NET4.0E; SE 2.X MetaSr 1.0)",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Maxthon/4.4.3.4000 Chrome/30.0.1599.101 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 UBrowser/4.0.3214.0 Safari/537.36",
        };
        while (flag) {
            count ++;
            if (CollectionUtils.isEmpty(ipList)) {
                ipList = getIpList();

                System.out.println("IP消耗完重新获取新IP");
            }
            try {
                IPInfo ip = isHostReachable(ipList);
                if (ip != null) {
                    document = Jsoup.connect(url).userAgent(ua[r.nextInt(22)]).timeout(3000).proxy(ip.getIp(), ip.getPort()).header("referer", sourceUrl).get();
                    flag = false;
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            if (count == 5) {
                ipList = getIpList();
            }
            if (count == 10) {
                flag = false;
            }
        }

        return document;
    }

    private static JSONArray newExportJson(DoctorDetailInfo info) {
        JSONArray jsonArray = new JSONArray();
        JSONObject object = new JSONObject();

        object.put("yiYuan",info.getYiYuan());
        object.put("keShi",info.getKeShi());
        object.put("name",info.getName());
        object.put("zhiWei",info.getZhiWei());
        object.put("shChang",info.getShChang());
        object.put("dateList",info.getDateList());
        object.put("sex",info.getSex());

        jsonArray.add(object);
        return jsonArray;
    }
}
