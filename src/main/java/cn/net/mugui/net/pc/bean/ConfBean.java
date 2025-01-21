package cn.net.mugui.net.pc.bean;

import java.util.Date;

import com.mugui.bean.JsonBean;
import com.mugui.sql.SQLDB;
import com.mugui.sql.SQLField;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * DG配置Bean
 * 
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
@Getter
@Setter
@Accessors(chain = true)
@SQLDB(TABLE = "conf", KEY = "conf_id")
public class ConfBean extends JsonBean {
	@SQLField(PRIMARY_KEY = true, AUTOINCREMENT = true, AUTOINCREMENT_value = "AUTOINCREMENT")
	private Integer conf_id;

	@SQLField()
	private String key;

	@SQLField()
	private String value;

	@SQLField(DATA_TYPE = "varchar(128)")
	private String detail;

	@SQLField(NULL = false, DEFAULT = true, DEFAULT_text = "CURRENT_TIMESTAMP")
	private Date create_time;

}
