package cn.net.mugui.net.pc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mugui.Dui.AutomaticChangeColor;
import com.mugui.Dui.DButton;
import com.mugui.Dui.DFrame;
import com.mugui.Dui.DPanel;
import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;
import com.mugui.base.client.net.classutil.DataSave;
import com.mugui.bean.JsonBean;
import com.mugui.util.Other;

import cn.net.mugui.net.pc.manager.FunctionUI;
import cn.net.mugui.net.pc.manager.FunctionUIManager;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@SuppressWarnings("serial")
@Component
public class AppFrame extends DFrame {
	public AppFrame() {
		super(1000, 800);
		setIconImage(DimgFile.getImgFile(DataSave.APP_PATH + "/data/MainIcon.bmp").bufferedImage);
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		label = new JLabel("桌面AI精灵");
		label.addMouseListener(l);
		label.addMouseMotionListener(l);
		label.setFont(new Font("宋体", Font.BOLD, 25));
		panel.add(label);

		DButton button_1 = new DButton((String) null, (Color) null);
		button_1.setText("最小化");
		panel.add(button_1);
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setExtendedState(JFrame.ICONIFIED);
				setVisible(false);
				miniTray();
			}
		});
		DButton button = new DButton((String) null, (Color) null);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				dispatchEvent(new WindowEvent(AppFrame.this, WindowEvent.WINDOW_CLOSING));
			}
		});
		setMiniTrayIcon(getIconImage());
		PopupMenu popupMenu = new PopupMenu();
		popupMenu.add(new MenuItem("退出"));
		popupMenu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("退出")) {
					clearTray();
					System.exit(0);
				}
			}
		});
		setPopupMenu(popupMenu);
		button.setText("关闭");
		panel.add(button);
		AutomaticChangeColor autoTable = new AutomaticChangeColor(panel, 0);
		autoTable.start();

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.CENTER);
		body = new JPanel();

		MenuList = new JPanel();
		// automaticChangeColor.addNew(body);
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(gl_panel_1.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING,
				gl_panel_1.createSequentialGroup().addContainerGap()
						.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
								.addComponent(MenuList, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 980,
										Short.MAX_VALUE)
								.addComponent(body, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))
						.addContainerGap()));
		gl_panel_1.setVerticalGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup().addContainerGap()
						.addComponent(MenuList, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addGap(2).addComponent(body, GroupLayout.DEFAULT_SIZE, 702, Short.MAX_VALUE)
						.addContainerGap()));
		MenuList.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		body.setLayout(new BorderLayout(0, 0));
		panel_1.setLayout(gl_panel_1);
		// automaticChangeColor.start();
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 6665580296838483902L;

	@Getter
	@Setter
	@Accessors(chain = true)
	public static class TextBean extends JsonBean {
		private String md5;
		private String pc_name;
		private long time;
		private String dpid;
		private String hash;
	}

	public static String getPC_NAME() {
		return System.getenv("COMPUTERNAME");
	}

	public static String getPC_sequence() {
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(new String[] { "wmic", "cpu", "get", "ProcessorId" });
			process.getOutputStream().close();
			Scanner sc = new Scanner(process.getInputStream());
			String property = sc.next();
			if (!property.trim().equalsIgnoreCase("ProcessorId")) {
				sc.close();
				return null;
			}
			String serial = sc.next();
			sc.close();
			return serial;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (process != null)
				process.destroy();
		}
		return "FFFFFFFFFFFFFFFF";
	}

	public static String getDPID() {
		return getPC_NAME() + ":" + getPC_sequence();
	}

	public static String getPCUser_NAME() {
		return System.getProperty("user.name");
	}

	private MouseAdapter l = new MouseAdapter() {

		Point origin = new Point();

		@Override
		public void mousePressed(MouseEvent e) {
			// 鼠标按下
			origin.x = e.getX();
			origin.y = e.getY();

		}

		@Override
		public void mouseExited(MouseEvent e) {
			AppFrame.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			AppFrame.this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			Point p = AppFrame.this.getLocation();
			AppFrame.this.setLocation(p.x + (e.getX() - origin.x), p.y + (e.getY() - origin.y));
			Other.sleep(20);
		}

	};
	private JPanel body = null;

	public java.awt.Component nowJPanel = null;

	public void updateUI(java.awt.Component component) {
		if (nowJPanel != null) {
			if (nowJPanel instanceof DPanel) {
				((DPanel) nowJPanel).quit();
			}
			if (nowJPanel != component)
				body.removeAll();
		}
		body.add(nowJPanel = component);
		if (component instanceof FunctionUI) {
			FunctionUI functionUI = (FunctionUI) component;
			String title2 = functionUI.getTitle();
			updateTitle(title2);
		}
		if (component instanceof DPanel) {
			((DPanel) component).init();
		}
		body.validate();
		body.repaint();
	}

	public java.awt.Component getNowUI() {
		return nowJPanel;
	}

	private JLabel label;

	public void updateTitle(String text) {
		label.setText("桌面AI精灵" + text);
	}

	private JPanel MenuList;
	private Map<String, DButton> menu_buttons = new LinkedHashMap<>();

	
	@Autowired
	private FunctionUIManager manager;
	
	public void init() {
		System.out.println("初始化");
		setTitle("桌面AI精灵");
		Iterator<Entry<String, FunctionUI>> iterator = manager.map.entrySet().iterator();
		FunctionUI ui=null;
		while (iterator.hasNext()) {
			Entry<String, FunctionUI> next = iterator.next();
			FunctionUI value = next.getValue();
			ui=value;
			addFunctionUI(value);
		}
		updateUI(ui);
	}

	public void addFunctionUI(FunctionUI functionUI) {
		DButton dButton = menu_buttons.get(functionUI.getMenu_name());
		if (dButton == null) {
			synchronized (AppFrame.class) {
				dButton = menu_buttons.get(functionUI.getMenu_name());
				if (dButton == null) {
					dButton = new DButton(functionUI.getMenu_name(), null);
					dButton.setFont(new Font("华文行魏", Font.CENTER_BASELINE, 13));
					dButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							updateUI(functionUI);
						}
					});
					menu_buttons.put(functionUI.getMenu_name(), dButton);
					MenuList.add(dButton);
				}
			}
		}
	}

	public void showLogin() {
		menu_buttons.clear();
		MenuList.removeAll();
		setTitle("桌面AI精灵");
	}


}
