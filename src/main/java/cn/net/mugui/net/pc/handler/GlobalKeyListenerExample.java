package cn.net.mugui.net.pc.handler;


import cn.net.mugui.net.music.task.Mp3Task;
import cn.net.mugui.net.pc.task.MicrosoftSpeechRecognizer;
import cn.net.mugui.net.pc.util.SystemVolumeControl;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;
import com.mugui.util.Other;

@Component
public class GlobalKeyListenerExample implements NativeKeyListener {

    private boolean altPressed = false;

    private  boolean isListener = false;

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        // 检测 Ctrl 和 Shift 是否按下
        if (e.getKeyCode() == NativeKeyEvent.VC_ALT) {
            altPressed = true;
        }
        if(altPressed){
            // 检测 S 键是否按下，并检查组合键
            if (e.getKeyCode() == NativeKeyEvent.VC_Z  ) {
                isListener = true;
                return;
            }

            //按下键盘上+号
            if(e.getKeyCode() == 0xe4e){
                mp3Task.increaseVolume();
                return;
            }
            //按下键盘上-号
            if(e.getKeyCode() == 0xe4a) {
                mp3Task.lowerVolume();
                return;
            }
//            System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
        }
    }


    @Autowired
    private Mp3Task mp3Task;


    Thread thread=new Thread(new Runnable() {
        @Override
        public void run() {
            while(true){
                if(isListener){
                    microsoftSpeechRecognizer.start();
                }else {
                    Other.sleep(20);
                    continue;
                }
                //缩小系统音量
                SystemVolumeControl.mute();

                while(isListener){
                    Other.sleep(400);
                }
                SystemVolumeControl.unMute();
                microsoftSpeechRecognizer.stop();
            }
        }
    });
    {
        thread.start();
    }


    @Autowired
    private MicrosoftSpeechRecognizer microsoftSpeechRecognizer;

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        // 释放 Ctrl 和 Shift 时重置状态
        if (e.getKeyCode() == NativeKeyEvent.VC_ALT) {
            altPressed = false;
            isListener = false;
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // 可根据需求处理其他事件
    }
}
