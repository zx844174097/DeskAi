package cn.net.mugui.net.pc.manager;

import com.mugui.Dui.DPanel;

import lombok.Getter;
import lombok.Setter;

public abstract class FunctionUI extends DPanel {
	@Getter
	@Setter
	private String title = null;
	@Getter
	@Setter
	private String menu_name = null;
}
