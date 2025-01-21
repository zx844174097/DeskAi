package cn.net.mugui.net.douyu.danmu.handler.impl;


import cn.net.mugui.net.douyu.danmu.domain.Msg;
import cn.net.mugui.net.douyu.danmu.domain.User;
import cn.net.mugui.net.douyu.danmu.handler.IMsgHandler;
import cn.net.mugui.net.pc.panel.ViewTMChatPanel;
import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;

@Component
public class DouyuMsgHandler implements IMsgHandler {

	/**
	 * 返回true代表继续执行该任务链
	 */
	@Override
	public boolean handle(Msg msg, User user) {
		System.out.println("user->"+user);
		System.out.println("msg->"+msg);
		return true;
	}

	@Override
	public String source() {
		return "douyu";
	}

}
