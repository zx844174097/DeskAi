package cn.net.mugui.net.pc.util;

import cn.hutool.core.io.resource.ResourceUtil;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APIOptions;

import java.io.File;

public interface JNIUtil extends Library {

    public static JNIUtil INSTANCE = Native.load(new File(ResourceUtil.getResource("jInectDll.dll").getFile()).getAbsolutePath(), JNIUtil.class, W32APIOptions.DEFAULT_OPTIONS);
//    public static JNIUtil INSTANCE = Native.load("jInectDll", JNIUtil.class, W32APIOptions.DEFAULT_OPTIONS);

    WinDef.DWORD GetPIDForProcess(WString process);

    WinDef.HMODULE GetDLLHandle(WString wDllName, WinDef.DWORD dPid);

    int InjectDll(WString szPName, WString szDllPath);

    int UnInjectDll(WString szPName, WString szDName);

}
