package com.example.demo.util;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.demo.model.dto.LogisticDTO;
import com.example.demo.model.vo.LogisticApiResult;
import com.example.demo.model.vo.LogisticVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zqh
 * @date 2022年07月13 19:11
 * @desc
 */
@Slf4j
public class LogisticUtil {

    /**
     * 查询物流信息
     *
     * @param params 提交参数
     * @return 物流信息
     * @author zhengqingya
     * @date 2021/10/23 10:48 下午
     */
    public static LogisticVO getLogisticInfo(LogisticDTO params) {
        String no = params.getNo();
        String type = params.getType();
        String appCode = params.getAppCode();

        // 请求地址
        String requestUrl = String.format("https://wuliu.market.alicloudapi.com/kdi?no=%s&type=%s",
                no, StringUtils.isBlank(type) ? "" : type);
        // 发起请求
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", String.format("APPCODE %s", appCode));
        String resultJson = HttpUtil.getUrl(requestUrl, headerMap);
        System.out.println(resultJson);
        LogisticApiResult logisticApiResult = JSON.parseObject(resultJson, LogisticApiResult.class);
        Assert.notNull(logisticApiResult, "参数异常");
        Assert.isTrue(logisticApiResult.getStatus() == 0, logisticApiResult.getMsg());
        return logisticApiResult.getResult();
    }
}