package com.example.demo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zqh
 * @date 2022年07月13 19:22
 * @desc
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("物流-api响应结果")
public class LogisticApiResult {

    @ApiModelProperty("状态码")
    private Integer status;

    @ApiModelProperty("提示信息")
    private String msg;

    @ApiModelProperty("结果集")
    private LogisticVO result;

}