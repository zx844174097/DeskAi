package cn.net.mugui.net.douyu.danmu.core;


import cn.net.mugui.net.douyu.danmu.handler.IMsgHandler;

import java.util.List;

public interface IDanmuku {
	
	// 请实现静态实例化create方法
	//public static IDanmuku create(long roomId) 
	
	public boolean start();
	
	public void stop();
	
	public int status();
	
	public List<IMsgHandler> addMsgHandler(IMsgHandler handler);
}
