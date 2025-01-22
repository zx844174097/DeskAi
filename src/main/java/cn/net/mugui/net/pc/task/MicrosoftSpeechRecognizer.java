
package cn.net.mugui.net.pc.task;

import java.io.*;
import java.util.concurrent.ExecutionException;

import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.StrUtil;
import cn.net.mugui.net.pc.bean.MessageBean;
import cn.net.mugui.net.pc.bean.PcConversationalMsgBean;
import cn.net.mugui.net.web.util.SysConf;
import com.alibaba.fastjson.JSONObject;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.AudioStreamFormat;
import com.microsoft.cognitiveservices.speech.audio.PullAudioInputStream;
import com.microsoft.cognitiveservices.speech.audio.PullAudioInputStreamCallback;
import com.microsoft.cognitiveservices.speech.util.EventHandler;
import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;
import com.mugui.base.base.InitMethod;
import com.mugui.base.client.net.auto.AutoTask;
import com.mugui.base.client.net.base.Task;
import com.mugui.base.client.net.task.TaskImpl;
import lombok.SneakyThrows;

import javax.sound.sampled.*;
import java.util.concurrent.locks.ReentrantLock;

@Task
@AutoTask
@Component
public class MicrosoftSpeechRecognizer extends TaskImpl {

    private SpeechRecognizer speechRecognizer = null;
    private SpeechSynthesizer speechSynthesizer = null;

    @InitMethod
    public void init() {
        initSpeechSynthesizer();
    }


    @Autowired
    private SysConf sysConf;

    private void initSpeechSynthesizer() {
        String value = sysConf.getValue("microsoft.key");
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(value, "eastus");
        speechConfig.setSpeechSynthesisVoiceName("zh-CN-XiaoxiaoMultilingualNeural");
        speechConfig.setSpeechSynthesisLanguage("zh-CN");
        speechSynthesizer = new SpeechSynthesizer(speechConfig);
        speechSynthesizer.SynthesisStarted.addEventListener(synthesisStarted);
        speechSynthesizer.SynthesisCompleted.addEventListener(synthesisCompleted);
        speechSynthesizer.SynthesisCanceled.addEventListener(synthesisCanceled);
    }


    private boolean isRecognizing = false;

    TargetDataLine microphone = null;

    AudioFormat format = new AudioFormat(16000, 16, 1, true, false);

    public void initSpeechRecognizer() {
        String value = sysConf.getValue("microsoft.key");
        SpeechConfig config = SpeechConfig.fromSubscription(value, "eastus");
        config.setSpeechRecognitionLanguage("zh-CN");

        try {
            if (microphone == null) {
                // 定义音频格式
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                if (!AudioSystem.isLineSupported(info)) {
                    System.out.println("音频线不支持！");
                }
                microphone = AudioSystem.getTargetDataLine(format);
                microphone.open(format);
                System.out.println("Start speaking...");
                microphone.start();
            }
            PullAudioInputStream pullAudioInputStream = PullAudioInputStream.create(new PullAudioInputStreamCallback() {
                @Override
                public int read(byte[] dataBuffer) {
                    if (!isRecognizing) {
                        for (int i = 0; i < dataBuffer.length; i++) {
                            dataBuffer[i] = 0;
                        }
                        return dataBuffer.length;
                    }
                    int read = microphone.read(dataBuffer, 0, dataBuffer.length);
                    return read;
                }

                @Override
                public void close() {
                    microphone.close();
                }
            }, AudioStreamFormat.getWaveFormatPCM((long) format.getSampleRate(), (short) format.getSampleSizeInBits(), (short) format.getChannels()));
            AudioConfig audioConfig = AudioConfig.fromStreamInput(pullAudioInputStream);

            speechRecognizer = new SpeechRecognizer(config, audioConfig);
            speechRecognizer.recognizing.addEventListener(recognizingEventHandler);
            speechRecognizer.recognized.addEventListener(recognizedEventHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private final EventHandler<SpeechRecognitionEventArgs> recognizingEventHandler = new EventHandler<>() {
        @Override
        public void onEvent(Object sender, SpeechRecognitionEventArgs eventArgs) {
            System.out.println("Recognizing: " + eventArgs.getResult().getText());
        }
    };

    private final EventHandler<SpeechRecognitionEventArgs> recognizedEventHandler = new EventHandler<>() {
        @SneakyThrows
        @Override
        public void onEvent(Object sender, SpeechRecognitionEventArgs eventArgs) {
            if (eventArgs.getResult().getReason() == ResultReason.RecognizedSpeech) {
                SpeechRecognitionResult result = eventArgs.getResult();
                System.out.println("RECOGNIZED: Text=" + result.getText());
                if (StrUtil.isNotBlank(result.getText())) {

                    DesktopAiTask.Data data = new DesktopAiTask.Data();
                    data.setType(DesktopAiTask.Data.Type.Master);
                    data.setText(result.getText());
                    desktopAiTask.add(data);

                    isRecognizing = false;
                }
            }
        }
    };

    @Autowired
    private DesktopAiTask desktopAiTask;


    @Override
    public void run() {
    }

    private final EventHandler<SpeechSynthesisEventArgs> synthesisStarted = new EventHandler<>() {
        @Override
        public void onEvent(Object sender, SpeechSynthesisEventArgs eventArgs) {
            System.out.println("Synthesis started: " + eventArgs.getResult().getReason().toString());
        }
    };

    private final EventHandler<SpeechSynthesisEventArgs> synthesisCompleted = new EventHandler<>() {
        @Override
        public void onEvent(Object sender, SpeechSynthesisEventArgs eventArgs) {
            System.out.println("Synthesis completed: " + eventArgs.getResult().getReason().toString());
        }
    };

    private final EventHandler<SpeechSynthesisEventArgs> synthesisCanceled = new EventHandler<>() {
        @Override
        public void onEvent(Object sender, SpeechSynthesisEventArgs eventArgs) {
            System.out.println("Synthesis canceled: " + eventArgs.getResult().getReason().toString());
        }
    };

    public void sendMsg(String content) {
        if (StrUtil.isBlank(content))
            return;
        try {
            SpeechSynthesisResult result = speechSynthesizer.SpeakTextAsync(content).get();
            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                System.out.println("Synthesis completed for content: " + content);
                isRecognizing = true;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        initSpeechRecognizer();
        try {
            isRecognizing = true;
            speechRecognizer.startContinuousRecognitionAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            speechRecognizer.stopContinuousRecognitionAsync().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static TimedCache<Integer, String> cache = new TimedCache<>(1000 * 60 * 5);

    static {
        cache.schedulePrune(1000 * 60 * 5);
    }

    public void speech(MessageBean messageBean) {
        try {
            String s = viewContent(messageBean.getContent());
            if (StrUtil.isNotBlank(s)) {

                String s1 = cache.get(messageBean.getMessage_id());
                if (StrUtil.isNotBlank(s1)) {
                    s = s.substring(s1.length());
                } else {
                    s1 = "";
                }
                if (messageBean.getStatus() == MessageBean.Status.SUCCESS.getValue()) {
                    cache.remove(messageBean.getMessage_id());
                    sendMsg(s);
                    return;
                }
                //从标点切分s
                String[] split = s.split("[，。！？；：,.!?;:]");
                if (split.length >= 2) {
                    String s2 = "";
                    for (int i = 0; i < split.length - 1; i++) {
                        s2 = s.substring(0, (s2 + split[i] + 1).length());
                    }
                    s1 = s1 + s2;
                    cache.put(messageBean.getMessage_id(), s1);
                    sendMsg(s2);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String viewContent(String content) {
        if (StrUtil.isBlank(content)) {
            return "";
        }
        int i = content.indexOf("\"对话\":\"");
        if (i > 0) {
            String trim = content.substring(i + 6).trim();
            int i1 = trim.indexOf("\"}");
            if (i1 > 0) {
                return JSONObject.parseObject(content).getString("对话");
            }
            i1 = trim.indexOf("\"");
            if (i1 > 0) {
                return trim.substring(0, i1);
            }
            return trim;
        }
        return "";
    }
}
