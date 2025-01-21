package cn.net.mugui.net.web.util;


import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;

import cn.net.mugui.net.pc.dao.Sql;
import cn.net.mugui.net.pc.bean.ConfBean;



/**
 * 配置数学表
 * @author mugui
 *
 */
@Component
public class SysConf  {

	public Integer system_user_id() {
		return Integer.parseInt(getValue("system_user_id"));
	}

	@Autowired
	private Sql systemDao;

	public void init() { 
		systemDao.createTable(ConfBean.class);
	}

	public String getValue(String index) {
		ConfBean system = get(index);
		if (system == null)
			return null;
		return system.getValue();
	}

	public ConfBean get(String index) {
		ConfBean system = new ConfBean();
		system.setKey(index);
		return systemDao.select(system);
	}

	public void setValue(String key, String value) {
		ConfBean system = get(key);
		if (system == null) {
			system = new ConfBean();
			system.setKey(key);
			system.setValue(value);
			system = systemDao.save(system);
		} else {
			system.setValue(value);
			systemDao.updata(system);
		}

	}

	public void save(ConfBean systemConf) {
		ConfBean temp = get(systemConf.getKey());
		if (temp == null) {
			systemDao.save(systemConf);
		} else {
			systemConf.setConf_id(temp.getConf_id());
			systemDao.updata(systemConf);
		}
	}

	public void save(String key, Object value, String extra) {
		ConfBean systemConf = new ConfBean();
		systemConf.setDetail(extra);
		systemConf.setKey(key);
		systemConf.setValue(value.toString());
		save(systemConf);
	}

	public void remove(String s) {
		ConfBean confBean = get(s);
		if(confBean!=null){
			systemDao.remove(confBean);
		}
	}
}
