package com.forte.demo.bean;

/**
 *
 "title": "算法训练 简单加法(基本型)",
 "description": "<br />　　首先给出简单加法算式的定义：<br /> 　　如果有一个算式(i)+(i+1)+(i+2),(i&gt;=0)，在计算的过程中，没有任何一个数位出现了进位，则称其为简单的加法算式。<br /> 　　例如：i=3时，3+4+5=12，有一个进位，因此3+4+5不是一个简单的加法算式；又如i=112时，112+113+114=339，没有在任意数位上产生进位，故112+113+114是一个简单的加法算式。<br /> <br /> 　　问题：给定一个正整数n，问当i大于等于0且小于n时,有多少个算式(i)+(i+1)+(i+2)是简单加法算式。其中n&lt;10000。",
 "input_description": "输入描述:<br />　　一个整数，表示n <br />输入样例: <br />",
 "output_description": "<br />输出描述: <br />　　一个整数,表示简单加法算式的个数<br /> 输出样例: <br />",
 "samples": "[{\"input\": \"参考上文 \", \"output\": \"参考上文\"}]",
 "test_case_id": "1382253515589",
 "hint": "HINT:时间限制：1.0s  内存限制：512.0MB<br />",
 "create_time": "2018-02-28T03:15:44.118Z",
 "last_update_time": null,
 "created_by": 1,
 "time_limit": 1000,
 "memory_limit": 512,
 "spj": false,
 "spj_language": null,
 "spj_code": null,
 "spj_version": null,
 "visible": true,
 "total_submit_number": 0,
 "total_accepted_number": 0,
 "difficulty": 2,
 "source": "蓝桥杯练习系统 ID: 138 原题链接: http://lx.lanqiao.cn/problem.page?gpid=T138",
 "tags": [
 4
 ]
 }
 }
 */
public class LqbQuestion {
    private String model;
    private String pk;
    private Fields fields;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public Fields getFields() {
        return fields;
    }

    public void setFields(Fields fields) {
        this.fields = fields;
    }
}
