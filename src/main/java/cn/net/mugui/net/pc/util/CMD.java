//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cn.net.mugui.net.pc.util;

import com.mugui.base.util.Other;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class CMD {
    private static Process rt;
    private boolean isTrue;
    private String info = "";
    private PrintWriter pw = null;
    private static int winmode = -1;
    private static String CHARSET = "";
    public static final int LIUNX = 1;
    public static final int WINDOWS = 2;

    static {
        String os = System.getProperties().getProperty("os.name");
        if (!os.startsWith("win") && !os.startsWith("Win")) {
            winmode = 1;
            CHARSET = "UTF-8";
        } else {
            winmode = 2;
            CHARSET = "GBK";
        }

    }

    public CMD() {
    }

    public boolean isColose() {
        return this.isTrue;
    }

    public String getInfo() {
        return this.info;
    }

    public void reInfo() {
        this.info = "";
    }

    public static int getDeviceType() {
        return winmode;
    }

    public void start() {
        this.isTrue = true;

        try {
            String url = null;
            if (winmode == 2) {
                url = "cmd.exe /k ";
            } else if (winmode == 1) {
                url = "/bin/sh -c ls";
            }

            rt = Runtime.getRuntime().exec(url);
            this.getRtInput();
            this.getRtInEur();
            Other.sleep(50);
            this.pw = new PrintWriter(new OutputStreamWriter(rt.getOutputStream(), CHARSET));
            this.reInfo();
        } catch (IOException var2) {
            var2.printStackTrace();
            this.isTrue = false;
            rt.destroy();
        }

    }

    public void getRtInput() {
        (new Thread(new Runnable() {
            InputStream is = null;
            BufferedReader br = null;

            public void run() {
                try {
                    this.is = CMD.rt.getInputStream();
                    this.br = new BufferedReader(new InputStreamReader(this.is, CMD.CHARSET));
                    String s = null;

                    while((s = this.br.readLine()) != null) {
                        System.out.println(s);
                    }
                } catch (Exception var10) {
                    var10.printStackTrace();
                } finally {
                    try {
                        CMD.this.isTrue = false;
                        if (this.br != null) {
                            this.br.close();
                        }

                        if (this.is != null) {
                            this.is.close();
                        }

                        CMD.rt.destroy();
                    } catch (Exception var9) {
                    }

                }

            }
        })).start();
    }

    public void getRtInEur() {
        (new Thread(new Runnable() {
            BufferedReader br = null;
            InputStream is = null;

            public void run() {
                try {
                    this.is = CMD.rt.getErrorStream();
                    this.br = new BufferedReader(new InputStreamReader(this.is, CMD.CHARSET));
                    String s = null;

                    while((s = this.br.readLine()) != null) {
                        System.out.println(s);
                    }
                } catch (Exception var10) {
                    var10.printStackTrace();
                } finally {
                    try {
                        CMD.this.isTrue = false;
                        if (this.br != null) {
                            this.br.close();
                        }

                        if (this.is != null) {
                            this.is.close();
                        }

                        CMD.rt.destroy();
                    } catch (Exception var9) {
                    }

                }

            }
        })).start();
    }

    public void send(String intfo) {
        if (intfo.equals("exit")) {
            Other.sleep(200);
            rt.destroy();
            this.isTrue = false;
        } else {
            this.pw.println(intfo);
            if (intfo.trim().indexOf("cd") == 0) {
                if (winmode == 2) {
                    this.pw.println("dir");
                } else if (winmode == 1) {
                    this.pw.println("ls");
                }
            }

            this.pw.flush();
        }
    }
}
