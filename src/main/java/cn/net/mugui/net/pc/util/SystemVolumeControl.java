package cn.net.mugui.net.pc.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.net.mugui.net.web.util.SysConf;
import com.mugui.base.client.net.classutil.DataSave;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.StandardCopyOption;

public class SystemVolumeControl {


    /**
     * 静音
     */
    public static void mute() {
        String init = init();
        RuntimeUtil.exec("cmd /c " + init+" /Mute \""+systemAudioName+"\"");
        System.out.println("cmd /c " + init+" /Mute \""+systemAudioName+"\"");
    }

    /**
     * 设置音量
     * @param volume 音量
     */
    public static void setVolume(int volume) {
        String init = init();
        RuntimeUtil.exec("cmd /c " + init + " /SetVolume "+  " \""+systemAudioName+"\""+ " " + volume);
    }

    /**
     * 获取音量
     * @return 音量
     */
    public static int getVolume() {
        String init = init();
        String s = RuntimeUtil.execForStr("cmd /c " + init + " /Stdout /GetPercent " + "\""+systemAudioName+"\"");
        return new BigDecimal(s.trim()).intValue();
    }

    /**
     * 取消静音
     * @return
     */
    public static void unMute() {
        String init = init();
        RuntimeUtil.exec("cmd /c " + init + " /Unmute \""+systemAudioName+"\"");
        System.out.println("cmd /c " + init + " /Unmute \""+systemAudioName+"\"");
    }
    private static  String systemAudioName;
    private static String init() {
        URL resource = ResourceUtil.getResource("svcl.exe");
        //判断项目主路径下是否有svcl.exe文件
        File file = new File(DataSave.APP_PATH);
        File file1 = FileUtil.file(file, "svcl.exe");
        if (!file1.exists()) {
            //如果没有则将项目主路径下的svcl.exe文件复制到项目主路径下
            FileUtil.copyFile(FileUtil.file(resource), file1.getAbsoluteFile(), StandardCopyOption.REPLACE_EXISTING);
        }
        systemAudioName=DataSave.context.getBean(SysConf.class).getValue("system.audio.name");
        if(StrUtil.isBlank(systemAudioName)){
            systemAudioName="Digital Audio (S/PDIF)";
        }
        return file1.getAbsolutePath();
    }


}

