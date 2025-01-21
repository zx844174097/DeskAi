package cn.net.mugui.net.pc.bean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mugui.bean.JsonBean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BagBaseBean extends JsonBean{

	private String model="gpt-3.5-turbo";
	
	
	private JSONArray messages=new JSONArray();

	@Override
	public String toString() {
		JSONObject object=new JSONObject();
		object.put("model", model);

		JSONArray mes=new JSONArray();
		messages.forEach((msg)->{
			JSONObject m=new JSONObject();
			MessageBean messageBean=new MessageBean();
			MessageBean message= (MessageBean) msg;
			messageBean.setContent(message.getContent());
			messageBean.setRole(message.getRole());
			mes.add(messageBean);
		});

		object.put("messages", mes);
		return object.toString();
	}
}
