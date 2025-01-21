package cn.net.mugui.net.pc;

import cn.hutool.core.util.StrUtil;
import cn.net.mugui.log.DPrintStream;
import cn.net.mugui.net.pc.util.QQUtil;
import cn.net.mugui.net.web.util.SysConf;
import cn.net.mugui.web.tomcat.InsideTomcat;
import com.mugui.base.base.ApplicationContext;
import com.mugui.base.base.Autowired;
import com.mugui.base.client.net.baghandle.NetBagModuleManager;
import com.mugui.base.client.net.classutil.DataSave;
import com.mugui.sql.DBConf;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.fusesource.jansi.AnsiConsole;


/**
 * Hello world!
 *
 */
public class PcApp {
	static {
//		System.setProperty("http.proxyHost","127.0.0.1");
//		System.setProperty("http.proxyPort","12083");
//		System.setProperty("proxyHost","127.0.0.1");
//		System.setProperty("proxyPort","12083");
	}

	public static void main(String[] args) {
		LogInit();
		DataSave.APP_PATH(PcApp.class);
		DBConf.getDefaultDBConf().readConf("org.sqlite.JDBC", "jdbc:sqlite:" + DataSave.APP_PATH + "//e.dll", "root",
				"mugui123");


		DataSave.context = new ApplicationContext();
		DataSave.context.init("cn.net.mugui", "com.mugui");
		DataSave.context.getBean(NetBagModuleManager.class).init(PcApp.class);
		System.out.println(DataSave.APP_PATH);
		try {

			SysConf sysConf=DataSave.context.getBean(SysConf.class);
			sysConf.init();

			String value = sysConf.getValue("chatGpt.model");
			if(StrUtil.isBlank(value)){
				sysConf.setValue("qq.user","qq号");
				sysConf.setValue("chatGpt.organization","模型organization");
				sysConf.setValue("chatGpt.model","调用模型");
				sysConf.setValue("chatGpt.key","模型私钥");
			}


			AppFrame bean = DataSave.context.getBean(AppFrame.class);
			bean.init();
			bean.setVisible(true);

			InsideTomcat insideTomcat=DataSave.context.getBean(InsideTomcat.class);
			insideTomcat.setPort(8000);

			insideTomcat.init();
			Tomcat tomcat = insideTomcat.getTomcat();
			//添加请求拦截器
			Context context = insideTomcat.getContext();


			tomcat.init();
			// 启动tomcat
			tomcat.start();
			// 保持tomcat的启动状态
			tomcat.getServer().await();


		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * 日志系统初始化
	 *
	 */
	private static void LogInit() {
		//判断是否为idea环境，如果是idea环境则使用idea的日志系统，否则使用AnsiConsole日志系统
		if (System.getProperty("idea") != null) {
			AnsiConsole.systemInstall();
		}
		System.setOut(new DPrintStream(System.out));
		System.setErr(new DPrintStream(System.err));
	}
}
