package com.example.demo.controller;

import com.example.demo.model.dto.LogisticDTO;
import com.example.demo.model.vo.LogisticVO;
import com.example.demo.util.LogisticUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zqh
 * @date 2022年07月13 19:16
 * @desc
 */
@Slf4j
@RestController
@RequestMapping("/test")
@Api(tags = "测试api")
public class SelectController {

    @ApiOperation("查询物流信息")
    @GetMapping("getLogistic")
    public LogisticVO getLogistic(@ModelAttribute LogisticDTO params) {
        return LogisticUtil.getLogisticInfo(params);
    }


}
