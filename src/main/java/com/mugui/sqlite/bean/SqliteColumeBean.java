package com.mugui.sqlite.bean;

import com.mugui.bean.JsonBean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SqliteColumeBean extends JsonBean{
	
	private Integer cid;
	private String name;
	
	private String type;
	
	private Integer notnull;
	
	private String dfit_value;
	
	private Integer pk;
}
