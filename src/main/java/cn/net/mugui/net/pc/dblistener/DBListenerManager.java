package cn.net.mugui.net.pc.dblistener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.mugui.base.base.ApplicationContext;
import com.mugui.base.base.Component;
import com.mugui.base.client.net.auto.AutoManager;
import com.mugui.base.client.net.base.ManagerInterface;
import com.mugui.base.client.net.classutil.DataSave;
import com.mugui.sql.SqlServer;
import com.mugui.sql.SqlServer.SelectListenerImpl;


@Component
@AutoManager
public class DBListenerManager implements ManagerInterface<String, SelectListenerImpl> {
	private static HashMap<String, SelectListenerImpl> map = null;

	public void init() {
		clear();
		map = new HashMap<>();
	}

	public Set<Entry<String, SelectListenerImpl>> entrySet() {
		return map.entrySet();
	}

	private ApplicationContext applicationContext = null;

	@Override
	public boolean init(Object name) {
		init();
		applicationContext = DataSave.context;
		for (Class<?> class_name : DataSave.initClassList((Class<?>) name)) {
			if (class_name.isAnnotationPresent(DBListener.class)) {
				DBListener filter = class_name.getAnnotation(DBListener.class);
				SelectListenerImpl temp = null;
				map.put(filter.hashCode() + "", temp = (SelectListenerImpl) applicationContext.getBean(class_name));
				SqlServer.addSelectListener(temp);
			}
		}
		return true;
	}

	@Override
	public boolean clear() {
		if (map != null) {
			Iterator<SelectListenerImpl> iterator = map.values().iterator();
			while (iterator.hasNext()) {
				SqlServer.removeSelectListener(iterator.next());
			}
			map.clear();
		}
		return true;
	}

	@Override
	public boolean is(String name) {
		if (map == null) {
			throw new NullPointerException("please run init");
		}
		return !map.isEmpty() && map.get(name) != null;
	}

	@Override
	public SelectListenerImpl del(String name) {
		if (map == null) {
			throw new NullPointerException("please run init");
		}
		SelectListenerImpl selectListenerImpl = null;
		SqlServer.removeSelectListener(selectListenerImpl = map.remove(name));
		return selectListenerImpl;
	}

	@Override
	public SelectListenerImpl get(String name) {
		if (map == null) {
			throw new NullPointerException("please run init");
		}
		return map.get(name);
	}

	@Override
	public boolean add(String name, SelectListenerImpl object) {
		if (map == null) {
			throw new NullPointerException("please run init");
		}
		return true;
	}

	@Override
	public boolean isInit() {
		// TODO Auto-generated method stub
		return false;
	}

}
