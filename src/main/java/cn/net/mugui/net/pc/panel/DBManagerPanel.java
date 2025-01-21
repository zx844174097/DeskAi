package cn.net.mugui.net.pc.panel;

import java.awt.BorderLayout;

import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;
import com.mugui.sqlite.ui.SqliteManagerUI;

import cn.net.mugui.net.pc.manager.FunctionUI;

/**
 * 数据库管理
 * 
 * @author 木鬼
 *
 */
@Component
public class DBManagerPanel extends FunctionUI {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7060674933539600888L;

	public DBManagerPanel() {
		setTitle("数据库管理");
		setMenu_name("数据库管理");
		setLayout(new BorderLayout(0, 0));
	}

	@Autowired
	SqliteManagerUI sqliteManagerUI = null;

	@Override
	public void init() {
		sqliteManagerUI.init();
		add(sqliteManagerUI, BorderLayout.CENTER);
	}

	@Override
	public void quit() {
		sqliteManagerUI.quit();
	}

	@Override
	public void dataInit() {
		sqliteManagerUI.dataInit();
	}

	@Override
	public void dataSave() {
		sqliteManagerUI.dataSave();
	}

}
