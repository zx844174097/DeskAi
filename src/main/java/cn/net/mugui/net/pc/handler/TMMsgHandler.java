package cn.net.mugui.net.pc.handler;


import cn.hutool.json.JSONObject;
import cn.net.mugui.net.douyu.danmu.domain.Msg;
import cn.net.mugui.net.douyu.danmu.domain.User;
import cn.net.mugui.net.douyu.danmu.handler.IMsgHandler;
import cn.net.mugui.net.pc.bean.MessageBean;
import cn.net.mugui.net.pc.panel.ViewTMChatPanel;
import cn.net.mugui.net.pc.task.DesktopAiTask;
import cn.net.mugui.net.pc.util.ChatGptUtil;
import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;

@Component
public class TMMsgHandler implements IMsgHandler {

	@Autowired
	private ViewTMChatPanel viewTMChatPanel;

	@Autowired
	private DesktopAiTask desktopAiTask;

	@Autowired
	private ChatGptUtil chatGptUtil;

	/**
	 * 返回true代表继续执行该任务链
	 */
	@Override
	public boolean handle(Msg msg, User user) {
		System.out.println("user->"+user);
		System.out.println("msg->"+msg);
		viewTMChatPanel.add(msg);

		JSONObject object = new JSONObject();
		object.put("直播间用户", user.getName());
		object.put("该用户消息", msg.getContent());

		String threadId = chatGptUtil.createThread("master");
		MessageBean messageBean1 = MessageBean.newUser(threadId, "master", "");
		messageBean1.setStatus(MessageBean.Status.SUCCESS.getValue());
		messageBean1.setRole(MessageBean.ROLE_LIVE_USER);
		messageBean1.setContent(msg.getContent());
		MessageBean.update(messageBean1);

		desktopAiTask.add(messageBean1);

		return true;
	}

	@Override
	public String source() {
		return "douyu";
	}

}
