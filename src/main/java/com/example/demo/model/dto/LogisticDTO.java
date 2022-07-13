package com.example.demo.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author zqh
 * @date 2022年07月13 19:13
 * @desc
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("物流-查询参数")
public class LogisticDTO {
    @ApiModelProperty(value = "快递单号 【顺丰请输入运单号 : 收件人或寄件人手机号后四位。例如：SF1359081140583:4649】", required = true, example = "780098068058")
    private String no;

    @ApiModelProperty(value = "快递公司代码: 可不填自动识别，填了查询更快【代码见附表】", example = "zto")
    private String type;

    @ApiModelProperty(value = "appCode", required = true, example = "0bf47b30e9a54d549a2b00d64ba53f12")
    private String appCode;
}