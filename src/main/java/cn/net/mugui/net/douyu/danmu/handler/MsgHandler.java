package cn.net.mugui.net.douyu.danmu.handler;


import com.mugui.base.base.Component;
import com.mugui.base.client.net.auto.AutoManager;
import com.mugui.base.client.net.base.Manager;
import com.mugui.base.client.net.classutil.DataSave;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AutoManager
public class MsgHandler extends Manager<String, List<IMsgHandler>> {

	@Override
	public boolean init(Object object) {
		 super.init(object);
		ConcurrentHashMap<String, Object> hashMap = DataSave.context.getHashMap();
		hashMap.forEach((k, v) -> {
			if (v instanceof IMsgHandler) {
				IMsgHandler handler = (IMsgHandler) v;
				List<IMsgHandler> handlers = get(handler.source());
				if(handlers == null) {
					handlers = new ArrayList<>();
					add(handler.source(), handlers);
				}
				handlers.add(handler);
			}
		});
		return true;
	}
}
