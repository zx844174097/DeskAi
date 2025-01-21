package cn.net.mugui.net.douyu.danmu.handler;


import cn.net.mugui.net.douyu.danmu.domain.Msg;
import cn.net.mugui.net.douyu.danmu.domain.User;

public interface IMsgHandler {

	public boolean handle(Msg msg, User user);

	String source();
}
