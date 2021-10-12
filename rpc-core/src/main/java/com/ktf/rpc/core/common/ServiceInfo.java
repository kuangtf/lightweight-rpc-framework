package com.ktf.rpc.core.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class ServiceInfo implements Serializable {

	/**
     *  应用名称
	 */
	private String appName;

    /**
     *  服务名称
	 */
	private String serviceName;

	/**
	 *  版本
	 */
	private String version;

	/**
     *  地址
	 */
	private String address;

    /**
     *  端口
	 */
	private Integer port;
}
