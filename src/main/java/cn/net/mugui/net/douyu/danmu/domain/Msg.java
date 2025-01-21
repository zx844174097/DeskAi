package cn.net.mugui.net.douyu.danmu.domain;

import com.mugui.bean.JsonBean;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Msg extends JsonBean {

	// 弹幕时间
	public long time;
	// 弹幕类型
	public String type;
	// 弹幕内容
	public String content;
	// 备注 
	public String remark;
	// 发言用户
	public User srcUser;
}
