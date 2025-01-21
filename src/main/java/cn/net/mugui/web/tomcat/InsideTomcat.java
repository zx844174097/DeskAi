package cn.net.mugui.web.tomcat;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mugui.Mugui;
import com.mugui.base.client.net.classutil.DataSave;
import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.CompressionConfig;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.coyote.http2.Http2Protocol;

import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;

import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.util.http.LegacyCookieProcessor;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InsideTomcat {

    // tomcat的端口号
    @Setter
    private Integer port;
    // tomcat的字符编码集
    private String code;
    // 拦截请求路径
    private String hinderURL;
    // 请求转发路径
    private String shiftURL;
    // tomcat对象
    @Getter
    private Context context;
    @Getter
    private Tomcat tomcat;


    @Setter
    private Object redisSessionConifg = null;


    @Autowired
    private WebHandle webHandle;


    @Setter
    private boolean secure = false;

    public InsideTomcat() {

    }

    // 启动这个内嵌tomcat容器
    public void run() throws Exception {
        init();
        tomcat.init();

        // 启动tomcat
        tomcat.start();
        // 保持tomcat的启动状态
        tomcat.getServer().await();

    }

    private void customize(Http2Protocol protocol) {
        CompressionConfig compression = this.compression;
        protocol.setCompression(compression.getCompression());
        protocol.setCompressibleMimeType(compression.getCompression());
        protocol.setCompressionMinSize(compression.getCompressionMinSize());
        if (compression.getNoCompressionUserAgents() != null) {
            protocol.setNoCompressionUserAgents(compression.getNoCompressionUserAgents());
        }
    }

    public void setCompression(String on_or_off, Integer min_size, String mime_type) {
        compression = new CompressionConfig();
        compression.setCompression(on_or_off);
        if (min_size != null) {
            compression.setCompressionMinSize(min_size);
        }
        if (mime_type != null) {
            compression.setCompressibleMimeType(mime_type);
        }
    }

    CompressionConfig compression = null;

    // 初始化tomcat容器
    public void init() throws LifecycleException, ServletException {
        // 执行init方法时加载默认属性值
        // 为了演示这里写了死值进去
        if (port == null)
            port = 8080;
        code = "UTF-8";
        hinderURL = "/";
        shiftURL = "/";
        setCompression("on", 64, null);

        tomcat = new Tomcat();
        ProtocolHandler protocolHandler = tomcat.getConnector().getProtocolHandler();
        if (protocolHandler instanceof AbstractHttp11Protocol) {
            ((AbstractHttp11Protocol<?>) protocolHandler).setNoCompressionUserAgents("");
        }
        for (UpgradeProtocol upgradeProtocol : tomcat.getConnector().findUpgradeProtocols()) {
            if (upgradeProtocol instanceof Http2Protocol) {
                customize((Http2Protocol) upgradeProtocol);
            }
        }

        // 创建连接器
        Connector conn = tomcat.getConnector();
        conn.setAsyncTimeout(60000);
        conn.setPort(port);
        conn.setURIEncoding(code);
        conn.setMaxPostSize(128 * 1024);
        conn.setMaxSavePostSize(128 * 1024);
        // 设置Host
        Host host = tomcat.getHost();
        host.setAppBase("webapps");

        // 获取目录绝对路径
        String classPath = System.getProperty("user.dir");

        // 配置tomcat上下文
        context = tomcat.addContext(host, hinderURL, classPath);
        // Configure session cookie before initializing the context
// 添加LifecycleListener，在 CONFIGURE_START 阶段设置SessionCookieConfig
        context.addLifecycleListener(event -> {
            if (Lifecycle.CONFIGURE_START_EVENT.equals(event.getType())) {
                System.out.println("Lifecycle.CONFIGURE_START_EVENT");
                int twentyYearsInSeconds = 30 * 24 * 60 * 60;
                context.getServletContext().getSessionCookieConfig().setMaxAge(twentyYearsInSeconds);
                if (secure)
                    context.getServletContext().getSessionCookieConfig().setSecure(true);
            }
        });
//        if (redisSessionConifg != null) {
//            try {
//                RedissonSessionManager redissonSessionManager = new RedissonSessionManager();
//                redissonSessionManager.setKeyPrefix("session:");
//                redissonSessionManager.setConfig(redisSessionConifg);
//                // 创建 RedissonSessionManager
//                context.setManager(redissonSessionManager);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        context.setSessionTimeout(60000);
        context.setSessionCookieName("SESSION");
        context.setSessionCookiePath("/");


        LegacyCookieProcessor legacyCookieProcessor = new LegacyCookieProcessor();
        if (secure)
            legacyCookieProcessor.setSameSiteCookies("None");
        context.setCookieProcessor(legacyCookieProcessor);

        Wrapper wrapper1 = tomcat.addServlet(shiftURL, "other", new HttpServlet() {
            @Override
            protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                //得到请求路径
                String requestURI = req.getRequestURI();
                System.out.println(requestURI);
                //排除掉other
                if (requestURI.startsWith("/other")) {
                    requestURI = requestURI.substring(6);
                }
                //得到第一层请求路径
                String substring = requestURI.substring(0, requestURI.indexOf("/", 1));
                //得到请求路径对应的servlet
                Mugui httpServlet = hashMap.get(substring);
                if (httpServlet != null) {
                    //排除第一层请求路径
                    requestURI = requestURI.substring(substring.length() + 1);
                    try {
                        httpServlet.invokeFunction(requestURI, req, resp);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                resp.getWriter().write("ok");
            }
        });
        wrapper1.init();
        wrapper1.addMapping("/other");
        wrapper1.addMapping("/other*");
        wrapper1.addMapping("/other/*");
        wrapper1.setAsyncSupported(true);

        // 配置请求拦截转发
        Wrapper wrapper = tomcat.addServlet(shiftURL, "Servlet", webHandle);

        //配置支持表单接收
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"),
                1024 * 1024 * 10, 1024 * 1024 * 20, 1024 * 1024 * 8);
        wrapper.setMultipartConfigElement(multipartConfigElement);

        wrapper.init();
        wrapper.setAsyncSupported(true);
        wrapper.addMapping(shiftURL);

    }

    ConcurrentHashMap<String, Mugui> hashMap = new ConcurrentHashMap<>();

    public void otherInit() {
        DataSave.context.getHashMap().forEach((k, v) -> {
            if (v instanceof Mugui) {
                Mugui httpServlet = (Mugui) v;
                //得到路径注解
                WebServlet annotation = httpServlet.getClass().getAnnotation(WebServlet.class);
                if (annotation != null) {
                    //得到路径
                    String[] value = annotation.urlPatterns();
                    for (String s : value) {
                        System.out.println("reg url:" + s);
                        hashMap.put(s, httpServlet);
                    }
                }

            }
        });
    }


    public String getWebappsPath() {
        String file = getClass().getClassLoader().getResource(".").getFile();
        return file.substring(0, file.indexOf("target")) + "src/main/webapp";
    }
}