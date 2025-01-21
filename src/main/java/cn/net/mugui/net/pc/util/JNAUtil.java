package cn.net.mugui.net.pc.util;

import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;

import static com.sun.jna.platform.win32.WinBase.INFINITE;
import static com.sun.jna.platform.win32.WinNT.*;

public class JNAUtil {

    public static void enbalePrivileges() {
        while(Kernel32.INSTANCE.GetLastError()!=0){

        }
        HANDLE handle = Kernel32.INSTANCE.GetCurrentProcess();
        GetLastError();
        HANDLEByReference handleByReference=new HANDLEByReference();
        boolean b = Advapi32.INSTANCE.OpenProcessToken(handle, TOKEN_ADJUST_PRIVILEGES,handleByReference);
        GetLastError();
        if(!b){
            throw new RuntimeException("提升权限失败");
        }
        LUID luid=new LUID();
        boolean bool = Advapi32.INSTANCE.LookupPrivilegeValue(null, "SeDebugPrivilege",luid);
        System.out.println(bool);
        GetLastError();
        TOKEN_PRIVILEGES tokenPrivileges = new TOKEN_PRIVILEGES(1);
        for (int i = 0; i < 1; i++) {
            tokenPrivileges.Privileges[i] = new LUID_AND_ATTRIBUTES(luid, new DWORD(WinNT.SE_PRIVILEGE_ENABLED));
        }
        IntByReference uintByReference=new IntByReference();
        boolean b1 = Advapi32.INSTANCE.AdjustTokenPrivileges(handleByReference.getValue(), false, tokenPrivileges, tokenPrivileges.size(), null, uintByReference);

        int i = GetLastError();
        if(!b1||i!=0){
            throw new RuntimeException("提升权限失败");
        }
        System.out.println("权限提升成功");
    }
    private static int GetLastError(){
        int i = Kernel32.INSTANCE.GetLastError();
        if(i!=0){
            StackTraceElement stackTraceElement = new Exception().getStackTrace()[1];
            System.out.println( stackTraceElement.getMethodName()+":"+stackTraceElement.getLineNumber()+"  GetLastError:"+i);
        }
        return i;
    }

    /**
     *注入模块
     * @param processName
     * @param modulePath
     * @return
     */
    public static boolean injectDll(String processName,String modulePath){
        enbalePrivileges();
        //得到系统快照
        HANDLE toolhelp = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, null);

        Tlhelp32.PROCESSENTRY32 processentry32 = findProcess(toolhelp,processName);
        GetLastError();
        //得到进程权限
        int vali = processentry32.th32ProcessID.intValue();
        //dumpbin.exe -exports C:\Windows\System32\kernel32.dll |findstr OpenProcess
        //       1061  424 00019CF0 OpenProcess
        //       1062  425          OpenProcessToken (forwarded to api-ms-win-core-processthreads-l1-1-0.OpenProcessToken)
       // WinNT.HANDLE hProcess = (HANDLE) invokeMethod(HANDLE.class, "kernel32", 1061, PROCESS_ALL_ACCESS, false, vali);

        HANDLE hProcess = Kernel32.INSTANCE.OpenProcess(PROCESS_ALL_ACCESS, false, vali);
        GetLastError();
        boolean b = injectDll(hProcess, modulePath);
        if(b){

            System.out.println("向目标："+processName+" 注入："+modulePath+" 成功");
        }

        Kernel32.INSTANCE.CloseHandle(hProcess);
        Kernel32.INSTANCE.CloseHandle(toolhelp);
        return false;
    }

    private static boolean injectDll(HANDLE hProcess, String modulePath) {

        // 写入数据
        int len = (modulePath.length() + 1 ) * Native.WCHAR_SIZE;
        Memory memory=new Memory(len);
        memory.setWideString(0,modulePath.toString());

        Pointer lpBaseAddress = Kernel32.INSTANCE.VirtualAllocEx(hProcess, null, new SIZE_T(memory.size()), MEM_COMMIT, PAGE_READWRITE);
        GetLastError();
        if(lpBaseAddress==null){
            throw new RuntimeException("分配内存失败");
        }

        IntByReference lpNumberOfBytesWritten = new IntByReference();
        boolean b = Kernel32.INSTANCE.WriteProcessMemory(hProcess, lpBaseAddress, memory, (int) memory.size(), lpNumberOfBytesWritten);

        //dumpbin.exe -exports C:\Windows\System32\kernel32.dll |findstr LoadLibrary
        //        987  3DA 0001F500 LoadLibraryA
        //        988  3DB 0001D680 LoadLibraryExA
        //        989  3DC 000193F0 LoadLibraryExW
        //        990  3DD 0001E880 LoadLibraryW
        HMODULE hmodule = Kernel32.INSTANCE.LoadLibraryEx("Kernel32", null, 1);
        Pointer LoadLibraryA = Kernel32.INSTANCE.GetProcAddress(hmodule, 990);
        System.out.println("LoadLibraryA"+LoadLibraryA.toString());


//        Memory memory1 = new Memory(memory.size());
//        boolean b1 = Kernel32.INSTANCE.ReadProcessMemory(hProcess, lpBaseAddress, memory1, (int) memory.size(), lpNumberOfBytesWritten);
//        byte[] byteArray = memory1.getByteArray(0, 120);
//        StringJoiner stringJoiner=new StringJoiner(",");
//        for(int i=0;i<120;i++){
//            stringJoiner.add(Integer.toString(byteArray[i],16));
//        }
//        System.out.println(stringJoiner);
        GetLastError();
        if(!b){
            throw new RuntimeException("写入数据失败");
        }


        HANDLE handle1 = Kernel32.INSTANCE.CreateRemoteThread(hProcess, null, 0, LoadLibraryA, lpBaseAddress, 0, null);

        System.out.println(handle1);
        GetLastError();
        Kernel32.INSTANCE.WaitForSingleObject(handle1, INFINITE);
        // 释放内存空间
        Kernel32.INSTANCE.VirtualFreeEx(hProcess,lpBaseAddress,new SIZE_T(memory.size()),MEM_DECOMMIT | MEM_RELEASE);
        Kernel32.INSTANCE.CloseHandle(handle1);
        if(handle1==null){
            throw new RuntimeException("注入失败");
        }
        //在目标进程中创建线程
//        WinNT.HANDLEByReference hRemoteThread=new WinNT.HANDLEByReference();
//        //dumpbin.exe -exports C:\Windows\System32\ntdll.dll |findstr ZwCreateThread
//        //1928  77F 000A4530 ZwCreateThread
//        //1929  780 000A5400 ZwCreateThreadEx
//        //1930  781 000A5420 ZwCreateThreadStateChange
//        DWORD o = (DWORD) invokeMethod(DWORD.class, "ntdll.dll", 1929, hRemoteThread.getPointer(), PROCESS_ALL_ACCESS, null,hProcess, LoadLibraryA, lpBaseAddress, 0, 0, 0, 0,0, 0);
//        GetLastError();
//        if(o==null){
//            throw new RuntimeException("注入失败");
//        }
//        System.out.println(hRemoteThread.getValue());
//
//        Kernel32.INSTANCE.WaitForSingleObject(hRemoteThread.getValue(), INFINITE);
//        Kernel32.INSTANCE.VirtualFreeEx(hProcess,lpBaseAddress,new SIZE_T(256),MEM_DECOMMIT | MEM_RELEASE);
//        Kernel32.INSTANCE.CloseHandle(hRemoteThread.getValue());
//        int i = GetLastError();
//        if(i!=0){
//            System.out.println("目标进程中创建线程失败！"+i);
//            return false;
//        }
        return true;

    }

    /**
     * 卸载模块
     * @param processName
     * @param moduleName
     * @return
     */
    public static boolean unInjectDll(String processName, String moduleName) {
        try {

            //得到系统快照
            HANDLE toolhelp = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, null);

            Tlhelp32.PROCESSENTRY32 processentry32 = findProcess(toolhelp,processName);

            //得到进程权限
            int vali = processentry32.th32ProcessID.intValue();
            HANDLE handle = Kernel32.INSTANCE.OpenProcess(PROCESS_ALL_ACCESS, false, vali);

            //查找模块
//            Tlhelp32.MODULEENTRY32W module = findModule(processentry32, moduleName);
//            //卸载
//            unInjectDll(handle,module.modBaseAddr);

            //查找模块
            HMODULE hmodule = findModule2(handle, moduleName);

            //卸载
            unInjectDll(handle,hmodule.getPointer());

            Kernel32.INSTANCE.CloseHandle(handle);
            Kernel32.INSTANCE.CloseHandle(toolhelp);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 找到模块
     * @param handle
     * @param moduleName
     * @return
     */
    private static HMODULE findModule2(HANDLE handle, String moduleName) {
        HMODULE hMods[] = new HMODULE[1024];

        //dumpbin.exe -exports C:\Windows\System32\psapi.dll |findstr EnumProcessModules
        //  6    5 000013E0 EnumProcessModulesEx
        DWORDByReference intByReference = new DWORDByReference();
        DWORD dwFilterFlag = new DWORD(0x03);
        invokeMethod("psapi.dll", 6, handle, hMods, hMods.length * Native.getNativeSize(HANDLE.class), intByReference, dwFilterFlag);

        if (intByReference.getValue() != null && intByReference.getValue().intValue() / 8 > 0) {
            for (int j = 0; j < intByReference.getValue().intValue() / 8; j++) {
                char[] chars = new char[10240];
                int i = Psapi.INSTANCE.GetModuleFileNameExW(handle, hMods[j], chars,
                        Native.getNativeSize(chars.getClass(), chars) / Native.getNativeSize(char.class));
                if (new String(chars, 0, i).endsWith(moduleName)) {
                    System.out.println(i + " " + new String(chars, 0, i) + " " + hMods[j].getPointer());
                    return hMods[j];
                }
            }

        }
        throw new RuntimeException("not find module "+moduleName+" by "+handle.toString());
    }

    // 卸载dll
    private static void unInjectDll(HANDLE handle, Pointer module) {

        Kernel32 kernel32 = Kernel32.INSTANCE;
        //>dumpbin.exe -exports C:\Windows\System32\kernel32.dll |findstr FreeLibrary
        //  446  1BD 0001A650 FreeLibrary
        //  447  1BE 0001FC80 FreeLibraryAndExitThread
        HMODULE hmodule = Kernel32.INSTANCE.LoadLibraryEx("kernel32.dll", null, 1);
        Pointer pointer = Kernel32.INSTANCE.GetProcAddress(hmodule, 446);


        DWORDByReference dword = new DWORDByReference();
        HANDLE handle1 = Kernel32.INSTANCE.CreateRemoteThread(handle, null, 0, pointer, module, 0, dword);
        kernel32.WaitForSingleObject(handle1, INFINITE);
        Kernel32.INSTANCE.CloseHandle(handle1);
    }

    private static Tlhelp32.MODULEENTRY32W findModule(Tlhelp32.PROCESSENTRY32 processentry32, String moduleName) {

        Kernel32 kernel32 = Kernel32.INSTANCE;

        {
            HANDLE moduleSnapshot =  kernel32.CreateToolhelp32Snapshot(new DWORD(0x00000010 | 0x00000008), processentry32.th32ProcessID);
            int i = kernel32.GetLastError();
            System.out.println(i);

            Tlhelp32.MODULEENTRY32W me = new Tlhelp32.MODULEENTRY32W();
            boolean b1 = kernel32.Module32FirstW(moduleSnapshot, me);
            if (b1) {
                while (kernel32.Module32NextW(moduleSnapshot, me)) {
                    if (new String(me.szModule).startsWith(moduleName)) {

                        System.out.println(new String(me.szModule) + " " + me.modBaseAddr);
                        return me;
                    }
                }
            }


        }
        throw  new RuntimeException("not find module "+moduleName+" by "+processentry32.th32ModuleID);
    }

    /**
     * 找到进程
     * @param toolhelp
     * @param processName
     * @return
     */
    private static Tlhelp32.PROCESSENTRY32 findProcess(HANDLE toolhelp, String processName) {

        Tlhelp32.PROCESSENTRY32 moduleEntry = new Tlhelp32.PROCESSENTRY32();

        boolean b = Kernel32.INSTANCE.Process32First(toolhelp, moduleEntry);
        System.out.println(b);

        while (Kernel32.INSTANCE.Process32Next(toolhelp, moduleEntry)) {
            if (new String(moduleEntry.szExeFile).startsWith(processName)) {
                return moduleEntry;
            }
        }
        throw new RuntimeException("not find "+processName);
    }

    /**
     * @param functionAddress
     * @param parameter
     * @return
     */
    private static void invokeMethod(String dllName, int functionAddress, Object... parameter) {
        HMODULE hmodule = Kernel32.INSTANCE.LoadLibraryEx(dllName, null, 1);
        Pointer pointer = Kernel32.INSTANCE.GetProcAddress(hmodule, functionAddress);
        if(pointer==null){
            throw  new RuntimeException("NotFindMethod:"+functionAddress+" of "+dllName);
        }
        Function function = Function.getFunction(pointer);
        function.invoke(parameter);
    }
    private static Object invokeMethod(Class retObject,String dllName, int functionAddress, Object... parameter) {
        HMODULE hmodule = Kernel32.INSTANCE.LoadLibraryEx(dllName, null, 1);
        Pointer pointer = Kernel32.INSTANCE.GetProcAddress(hmodule, functionAddress);
        if(pointer==null){
            throw  new RuntimeException("NotFindMethod:"+functionAddress+" of "+dllName);
        }
        Function function = Function.getFunction(pointer);
        Object invoke = function.invoke(retObject, parameter);
        return invoke;
    }

    // 定义 SID_IDENTIFIER_AUTHORITY 类
    public static class SID_IDENTIFIER_AUTHORITY extends Structure {
        public byte[] Value = new byte[6];

        public SID_IDENTIFIER_AUTHORITY(byte[] value) {
            if (value.length != 6) {
                throw new IllegalArgumentException("SID_IDENTIFIER_AUTHORITY must have 6 bytes");
            }
            System.arraycopy(value, 0, this.Value, 0, value.length);
        }

        @Override
        protected java.util.List<String> getFieldOrder() {
            return java.util.Collections.singletonList("Value");
        }
    }


    public static boolean isRunningAsAdmin() {
        HANDLEByReference phToken = new HANDLEByReference();

        if (!Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(), WinNT.TOKEN_QUERY, phToken)) {
            throw new RuntimeException("Failed to get process token. Error code: " + Kernel32.INSTANCE.GetLastError());
        }

        SID_IDENTIFIER_AUTHORITY ntAuthority = new SID_IDENTIFIER_AUTHORITY(new byte[]{0, 0, 0, 0, 0, 5});
        PointerByReference pSid = new PointerByReference();

        // 调用 AllocateAndInitializeSid
        if (!MyAdvapi32.INSTANCE.AllocateAndInitializeSid(ntAuthority, (byte) 2,
                SECURITY_BUILTIN_DOMAIN_RID, DOMAIN_ALIAS_RID_ADMINS,
                0, 0, 0, 0, 0, 0, pSid)) {
            throw new RuntimeException("Failed to allocate and initialize SID. Error code: " + Kernel32.INSTANCE.GetLastError());
        }

        try {
            TOKEN_ELEVATION elevation = new TOKEN_ELEVATION();
            IntByReference returnLength = new IntByReference();

            boolean result = Advapi32.INSTANCE.GetTokenInformation(phToken.getValue(),
                    TOKEN_INFORMATION_CLASS.TokenElevation, elevation,
                    elevation.size(), returnLength);

            if (!result) {
                throw new RuntimeException("Failed to get token elevation. Error code: " + Kernel32.INSTANCE.GetLastError());
            }

            return elevation.TokenIsElevated != 0;
        } finally {
            MyAdvapi32.INSTANCE.FreeSid(pSid.getValue());
        }
    }


    public interface MyAdvapi32 extends Advapi32 {
        MyAdvapi32 INSTANCE = Native.load("Advapi32", MyAdvapi32.class);

        boolean AllocateAndInitializeSid(SID_IDENTIFIER_AUTHORITY pIdentifierAuthority, byte nSubAuthorityCount,
                                         int dwSubAuthority0, int dwSubAuthority1, int dwSubAuthority2, int dwSubAuthority3,
                                         int dwSubAuthority4, int dwSubAuthority5, int dwSubAuthority6, int dwSubAuthority7,
                                         PointerByReference pSid);

        boolean FreeSid(Pointer pSid);
    }
    public static final int SECURITY_BUILTIN_DOMAIN_RID = 0x00000020;
    public static final int DOMAIN_ALIAS_RID_ADMINS = 0x00000220;

    public interface MyKernel32 extends StdCallLibrary {
        MyKernel32 INSTANCE = Native.load("Kernel32", MyKernel32.class);

        Pointer GlobalAlloc(int uFlags, int dwBytes);

        boolean GlobalFree(Pointer hMem);

        int GPTR = 0x0040; // 表示分配零初始化的全局内存块
    }
}
