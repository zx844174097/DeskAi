package cn.net.mugui.net.pc.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.net.mugui.net.music.domain.Music;
import cn.net.mugui.net.music.task.Mp3Task;
import com.alibaba.fastjson.JSONObject;
import com.mugui.base.base.Component;
import com.mugui.base.client.net.bagsend.BagSend;
import com.mugui.base.client.net.bagsend.NetCall;
import com.mugui.base.client.net.bean.Message;
import com.mugui.base.client.net.classutil.DataSave;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

@Component
public class SystemUtil {

    public static JSONObject now() {
        JSONObject object = new JSONObject();
        object.put("当前时间", DateUtil.now());
        JSONObject currentWindow = getCurrentWindow();
        object.put("当前窗口", currentWindow);
        object.put("窗口内容", getCurrentWindowText(currentWindow));
        object.put("其他信息", otherInfo());
        return object;
    }

    private static Object otherInfo() {
        JSONObject object = new JSONObject();
        //判断这个进程是否存在 obs64.exe
        int[] ints = new int[4096];
        boolean b = Psapi.INSTANCE.EnumProcesses(ints, ints.length, new IntByReference());
        if (b) {
            for (int i : ints) {
                if (i == 0) {
                    continue;
                }
                Kernel32 kernel32 = Kernel32.INSTANCE;
                WinNT.HANDLE handle = kernel32.OpenProcess(Kernel32.PROCESS_QUERY_INFORMATION | Kernel32.PROCESS_VM_READ, false, i);
                char[] chars = new char[512];
                Psapi.INSTANCE.GetModuleBaseNameW(handle, null, chars, chars.length);
                String string = Native.toString(chars);
                if (StrUtil.equals(string, "obs64.exe")) {
                    object.put("直播中", "使用obs64.exe");
                    break;
                }
            }
        }
        Mp3Task bean = DataSave.context.getBean(Mp3Task.class);
            Vector<Music> list = bean.list();
            LinkedList<String> linkedList = new LinkedList<>();
            for (int i = 0; i < list.size(); i++) {
                Music music = list.get(i);
                if (i == bean.getNowIndex()) {
                    linkedList.add("正在播放:" + music.getName() + "(歌唱:" + music.getSinger() + ")");
                } else {
                    linkedList.add(music.getName() + "(歌唱:" + music.getSinger() + ")");
                }
            }
            object.put("音乐播放中", linkedList);


        return object;
    }

    private static Object getCurrentWindowText(JSONObject currentWindow) {
        String string = currentWindow.getString("进程名");
        if (StrUtil.startWith(string, "idea")) {
            //idea信息
            return getIdeaInfo(currentWindow.getString("窗口标题"));
        } else if (StrUtil.startWith(string, "java")) {
            //java信息
            return getJavaInfo(currentWindow);
        }
        return "暂时不支持的窗口内容";
    }

    private static Object getJavaInfo(JSONObject javaWindow) {
        String string = javaWindow.getString("窗口标题");
        if (StrUtil.equals(string, "桌面AI精灵")) {
            return "此窗口进程为“桌面AI精灵”本体进程";
        }
        return "暂时不支持的窗口内容";

    }

    private static BagSend ideaBagSend;

    /**
     * 得到idea的信息
     *
     * @return
     */
    private static Object getIdeaInfo(String 窗口标题) {
        JSONObject object = new JSONObject();
        object.put("project", 窗口标题);
        if (ideaBagSend == null)
            ideaBagSend = DataSave.context.initBean(new BagSend());
        synchronized (ideaBagSend) {
            ideaBagSend.sendData("web.method.info", object).main(new NetCall.Call() {
                @Override
                public Message ok(Message data) {
                    Object date = data.getDate();
                    object.put("info", date);
                    synchronized (ideaBagSend) {
                        ideaBagSend.notify();
                    }
                    return null;
                }

                @Override
                public Message err(Message data) {
                    synchronized (ideaBagSend) {
                        ideaBagSend.notify();
                    }
                    return null;
                }
            });
            try {
                ideaBagSend.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return object.get("info");
    }

    /**
     * 得到当前用户操作的窗口信息和内容
     *
     * @return
     */
    private static JSONObject getCurrentWindow() {

        JSONObject object = new JSONObject();

        JNAUtil.enbalePrivileges();
        WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();

        //得到窗口所在进程
        IntByReference processId = new IntByReference();
        User32.INSTANCE.GetWindowThreadProcessId(hwnd, processId);
        Kernel32 kernel32 = Kernel32.INSTANCE;
        WinNT.HANDLE handle = kernel32.OpenProcess(Kernel32.PROCESS_QUERY_INFORMATION | Kernel32.PROCESS_VM_READ, false, processId.getValue());
        object.put("进程ID", processId.getValue());
        char[] chars = new char[512];
        Psapi.INSTANCE.GetModuleBaseNameW(handle, null, chars, chars.length);
        object.put("进程名", Native.toString(chars));
        kernel32.CloseHandle(handle);


        int length = User32.INSTANCE.GetWindowTextLength(hwnd) + 1;
        char[] windowText = new char[length];
        int i = User32.INSTANCE.GetWindowText(hwnd, windowText, 512);
        String string = Native.toString(windowText);
        object.put("窗口标题", string);

        //得到窗口内的主体内容
        return object;
    }


    public static List<WinDef.HWND> getChildWindows(WinDef.HWND parent) {
        final List<WinDef.HWND> childWindows = new ArrayList<>();
        User32.INSTANCE.EnumChildWindows(parent, new WinUser.WNDENUMPROC() {
            @Override
            public boolean callback(WinDef.HWND hwnd, Pointer arg1) {
                childWindows.add(hwnd);
                return true;
            }
        }, null);
        return childWindows;
    }
}
