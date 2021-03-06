package com.google.core.database.datascope;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 数据权限查询参数
 * @author iris
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DataScope extends HashMap {

	/**
	 * 限制范围的字段名称
	 */
	private String scopeName = "depart_id";

	/**
	 * 具体的数据范围
	 */
	private List<Integer> departIds = new ArrayList<>();
}
