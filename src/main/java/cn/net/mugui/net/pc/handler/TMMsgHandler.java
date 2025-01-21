package cn.net.mugui.net.pc.handler;


import cn.net.mugui.net.douyu.danmu.domain.Msg;
import cn.net.mugui.net.douyu.danmu.domain.User;
import cn.net.mugui.net.douyu.danmu.handler.IMsgHandler;
import cn.net.mugui.net.pc.panel.ViewTMChatPanel;
import cn.net.mugui.net.pc.task.DesktopAiTask;
import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;

@Component
public class TMMsgHandler implements IMsgHandler {

	@Autowired
	private ViewTMChatPanel viewTMChatPanel;

	@Autowired
	private DesktopAiTask desktopAiTask;

	/**
	 * 返回true代表继续执行该任务链
	 */
	@Override
	public boolean handle(Msg msg, User user) {
		System.out.println("user->"+user);
		System.out.println("msg->"+msg);
		viewTMChatPanel.add(msg);

		DesktopAiTask.Data data = new DesktopAiTask.Data();
		data.setType(DesktopAiTask.Data.Type.douyu_live);
		data.setText(msg.content);
		data.setUser(user.toString());
		desktopAiTask.add(data);

		return true;
	}

	@Override
	public String source() {
		return "douyu";
	}

}
