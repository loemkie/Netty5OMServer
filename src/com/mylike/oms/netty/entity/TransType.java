package com.mylike.oms.netty.entity;
/**
 * 事件类型--上海讯时OM 50
 * @author loemkie
 *
 */
public enum TransType {
	VISITOR  ("visitor"),//客户打给客服
	OUTER ("outer");//转接中
	// 定义私有变量
    private final String value ;

    // 构造函数，枚举类型只能为私有
    private TransType( String value) {
        this. value = value;
    }
	public String getValue() {
		return value;
	}
}
