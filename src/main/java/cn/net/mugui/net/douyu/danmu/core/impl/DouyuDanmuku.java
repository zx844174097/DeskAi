package cn.net.mugui.net.douyu.danmu.core.impl;


import cn.net.mugui.net.douyu.danmu.common.util.HttpRequestUtil;
import cn.net.mugui.net.douyu.danmu.common.util.TrustAllCertSSLUtil;
import cn.net.mugui.net.douyu.danmu.core.IDanmuku;
import cn.net.mugui.net.douyu.danmu.handler.IMsgHandler;
import cn.net.mugui.net.douyu.danmu.handler.MsgHandler;
import cn.net.mugui.net.web.util.SysConf;
import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;
import com.mugui.base.client.net.classutil.DataSave;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class DouyuDanmuku implements IDanmuku, Runnable {

    List<IMsgHandler> handlers=null;
    long roomid = -1;
    long realRoomId;
    DouyuWebsocket douyuSocket;
    DouyuLoginWebsocket douyuLoginSocket;
    Thread hearbeatThread;
    Thread msgHandlerThread;
    volatile boolean running;

    @Autowired
    private SysConf sysConf;


    @Autowired
    private MsgHandler msgHandler;


    @Override
    public boolean start() {

        if (roomid == -1) {
            roomid = Long.parseLong(sysConf.getValue("douyu.roomId"));
        }
        if(handlers==null){
            handlers =msgHandler.get("douyu");
        }


        try {
            HttpRequestUtil util = new HttpRequestUtil();
            String basicInfoUrl = String.format("https://www.douyu.com/%s", roomid);
            String html = util.getContent(basicInfoUrl, new HashMap<>());
            // System.out.println(html);
            Pattern pRoomId = Pattern.compile("\\$ROOM.room_id ?= ?([0-9]+);");
            Matcher matcher = pRoomId.matcher(html);
            matcher.find();
            realRoomId = Long.parseLong(matcher.group(1));

            URI url = new URI("wss://danmuproxy.douyu.com:8502");
            douyuSocket = new DouyuWebsocket(url, roomid, realRoomId, handlers, this);
            douyuSocket.setSocketFactory(TrustAllCertSSLUtil.getFactory());
            douyuSocket.connectBlocking();

            running = true;
            hearbeatThread = new Thread(this);
            // 如果接收线程异常退出，心跳线程需要最多一个周期才能反应过来。
            hearbeatThread.setDaemon(true);
            hearbeatThread.start();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            running = false;
            return false;
        }
    }

    @Override
    public void stop() {
        hearbeatThread.interrupt();
        douyuSocket.close();
    }

    @Override
    public int status() {
        if (running)
            return 1;
        else
            return 0;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(45000);
                douyuSocket.heartBeat();
                //douyuLoginSocket.heartBeat();
                //System.out.println(roomid + " - douyu发送心跳包成功");
            } catch (Exception e) {
                //e.printStackTrace();
                running = false;
            }
        }
        System.out.println(roomid + " - douyu心跳线程结束");
//		try {
//			douyuLoginSocket.closeBlocking();
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
        try {
            douyuSocket.closeBlocking();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<IMsgHandler> addMsgHandler(IMsgHandler handler) {
        handlers.add(handler);
        return handlers;
    }

    public DouyuWebsocket getDouyuSocket() {
        return douyuSocket;
    }

    public DouyuLoginWebsocket getDouyuLoginSocket() {
        return douyuLoginSocket;
    }

    public Thread getHearbeatThread() {
        return hearbeatThread;
    }

}
