package cn.net.mugui.net.pc.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.mugui.base.base.Component;
import com.mugui.base.client.net.classutil.DataSave;

import java.net.*;
import java.io.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;


@Component
public class WxListener {

    public void Stop(){
        listening=false;
    }
    boolean listening = true;

     static ConcurrentLinkedDeque<TCPWorker> tcpWorkers=new ConcurrentLinkedDeque<>();

    public void init(int port)  {
        listening=true;
        ThreadUtil.execute(() -> {
            ServerSocket serverSocket = null;


            try {
                serverSocket = new ServerSocket(port); // 监听 4444 端口
            } catch (IOException e) {
                System.err.println("Could not listen on port: 4444");
                return;
            }

            System.out.println("Server started. Listening on port 4444...");

            try {
                while (listening) {
                    TCPWorker tcpWorker = new TCPWorker(serverSocket.accept());
                    tcpWorkers.add(tcpWorker);
                    tcpWorker.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {

                    Iterator<TCPWorker> iterator = tcpWorkers.iterator();
                    while(iterator.hasNext()){
                        iterator.next().Stop();
                    }
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }


class TCPWorker extends Thread {
    private Socket clientSocket = null;


    private WxUtil wxUtil= DataSave.context.getBean(WxUtil.class);

    public TCPWorker(Socket socket) {
        super("TCPWorker");
        clientSocket = socket;
    }

    public void run() {
        try( InputStream inputStream= clientSocket.getInputStream();
            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        ) {

           while(listening){
               byte[] bytes=new byte[10240];
               int read = inputStream.read(bytes);
               outputStream.write(bytes,0,read);
               System.out.println(outputStream.toString());
               String s = outputStream.toString();

               while(listening){
                   if(s.length()<=1){
                       break;
                   }
                   if(s.charAt(0)=='\n'){
                       s=s.substring(1);
                       continue;
                   }
                   int i = s.indexOf('\n');
                   if(i<0){
                       break;
                   }
                   String substring = s.substring(0, i);

                   s=s.substring(i);
                   if(StrUtil.isNotBlank(substring)){
                       WxUtil.WxBean wxBean = WxUtil.WxBean.newBean(WxUtil.WxBean.class, substring);
                      if(System.currentTimeMillis()- DateUtil.parseDateTime(wxBean.getTime()).getTime() <10000){
                          wxUtil.handle(wxBean);
                      }
                   }
               }
           }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.println("停止");
            if(listening){
                Iterator<TCPWorker> iterator = WxListener.tcpWorkers.iterator();
                while(iterator.hasNext()){
                    if (iterator.next()==this) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    public void Stop() {
        try {
            if(clientSocket!=null)
                clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
}