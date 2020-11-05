package com.project.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/*
@author WL
@CreateDate 2020-7-21
@update
@description 公众号实体类
*/
@ApiModel("公众号")
public class PublicAccount {
    @ApiModelProperty("公众号id")
    private Integer publicaccountId;
    @ApiModelProperty("公众号创建者id,如果公众号为企业公众号，则为公司id,否则为用户id")
    private Integer publicaccountOwner;
    @ApiModelProperty("公众号名字，是唯一的")
    private String publicaccountName;
    @ApiModelProperty("公众号类别，分为企业公众号和个人公众号")
    private String publicaccountType;
    @ApiModelProperty("公众号简介")
    private String publicaccountBrief;

    public Integer getPublicaccountId() {
        return publicaccountId;
    }

    public void setPublicaccountId(Integer publicaccountId) {
        this.publicaccountId = publicaccountId;
    }

    public Integer getPublicaccountOwner() {
        return publicaccountOwner;
    }

    public void setPublicaccountOwner(Integer publicaccountOwner) {
        this.publicaccountOwner = publicaccountOwner;
    }

    public String getPublicaccountName() {
        return publicaccountName;
    }

    public void setPublicaccountName(String publicaccountName) {
        this.publicaccountName = publicaccountName == null ? null : publicaccountName.trim();
    }

    public String getPublicaccountType() {
        return publicaccountType;
    }

    public void setPublicaccountType(String publicaccountType) {
        this.publicaccountType = publicaccountType == null ? null : publicaccountType.trim();
    }

    public String getPublicaccountBrief() {
        return publicaccountBrief;
    }

    public void setPublicaccountBrief(String publicaccountBrief) {
        this.publicaccountBrief = publicaccountBrief == null ? null : publicaccountBrief.trim();
    }
}
