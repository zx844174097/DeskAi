package cn.net.mugui.net.douyu.danmu.domain;

import com.mugui.bean.JsonBean;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class User extends JsonBean {

	public int level;
	public String id;
	public String name;
}
