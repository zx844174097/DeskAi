package cn.net.mugui.net.pc.manager;

import com.mugui.base.base.ApplicationContext;
import com.mugui.base.base.Component;
import com.mugui.base.client.net.auto.AutoManager;
import com.mugui.base.client.net.base.Manager;
import com.mugui.base.client.net.classutil.DataSave;

@AutoManager
@Component
public class FunctionUIManager extends Manager<String, FunctionUI> {
	@Override
	public boolean init(Object object) {
		Class<?> name = (Class<?>) object;
		super.init(object);

		for (Class<?> class_name : DataSave.initClassList(name)) {
			if (FunctionUI.class.isAssignableFrom(class_name)) {
				System.out.println("FunctionUI：" + class_name.getName());
				map.put(class_name.getName(), (FunctionUI) DataSave.context.getBean(class_name));
			}
		}
		return false;
	}
}
