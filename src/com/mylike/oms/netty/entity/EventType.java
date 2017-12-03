package com.mylike.oms.netty.entity;
/**
 * 事件类型--上海讯时OM 50
 * @author loemkie
 *
 */
public enum EventType {
	ANSWERED ("ANSWERED"),//接通
	TRANSIENT ("TRANSIENT"),//转接中
	ALERT ("ALERT"),//振铃
	RING ("RING"),//振铃
	BYE ("BYE");//挂断
	
	// 定义私有变量
    private final String value ;

    // 构造函数，枚举类型只能为私有
    private EventType( String value) {
        this. value = value;
    }

	public String getValue() {
		return value;
	}
}
