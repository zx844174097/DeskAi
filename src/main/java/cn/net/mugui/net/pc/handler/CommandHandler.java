package cn.net.mugui.net.pc.handler;

import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.StrUtil;
import cn.net.mugui.net.music.domain.Music;
import cn.net.mugui.net.music.source.impl.NetEaseMusic;
import cn.net.mugui.net.music.task.Mp3Task;
import cn.net.mugui.net.pc.bean.MessageBean;
import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;

import java.util.List;

/**
 * 命令处理器
 */
@Component
public class CommandHandler {

    private static TimedCache<Integer, String> cache = new TimedCache<>(1000 * 60 * 5);

    static {
        cache.schedulePrune(1000 * 60 * 5);
    }

    public void add(MessageBean messageBean) {
        try {
            if (cache.get(messageBean.getMessage_id()) != null) {
                return;
            }
            String content = viewContent(messageBean.getContent());
            if (StrUtil.isBlank(content)) {
                return;
            }
            cache.put(messageBean.getMessage_id(), content);
            System.out.println("命令：" + content);
            //执行命令
            handleCommand(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理命令
     *
     * @param content
     */
    private void handleCommand(String content) {
        if (content.startsWith("cmd")) {
            try {
                Runtime.getRuntime().exec(content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (content.startsWith("playMusic")) {
            handlePlayMusic(content.substring(9));
        } else if (content.startsWith("toggleMusic")) {
            handleToggleMusic(content.substring(11));
        }
    }

    private void handleToggleMusic(String substring) {
        substring=substring.trim();
        if (StrUtil.contains(substring, "pause")) {
            mp3Util.pause();
        } else if (StrUtil.contains(substring, "play")) {
            mp3Util.play();
        } else if (StrUtil.contains(substring, "nextMusic")) {
            mp3Util.nextMusic();
        } else if (StrUtil.contains(substring, "lastMusic")) {
            mp3Util.lastMusic();
        }
    }

    @Autowired
    private NetEaseMusic netEaseMusic;


    /**
     * 播放歌曲
     *
     * @param substring
     */
    private void handlePlayMusic(String substring) {
        substring=substring.trim();
        if (substring.startsWith("[")) {
            substring = substring.substring(1);
        }
        if (substring.endsWith("]")) {
            substring = substring.substring(0, substring.length() - 1);
        }
        if(StrUtil.isBlank(substring)){
            mp3Util.play();
            return;
        }
        boolean isRandom=false;
        if(StrUtil.startWith(substring,"random")){
            substring=substring.substring(6);
            isRandom=true;
        }


        List<Music> music = netEaseMusic.searchSimilar(substring, 0, 10);
        music.forEach(m -> {
            System.out.println(m);
        });
        if (music.size() > 0){
            if(isRandom){
                mp3Util.add(music.get((int) (Math.random()*music.size())));
                return;
            }
            mp3Util.add(music.get(0));

        }
    }

    @Autowired
    private Mp3Task mp3Util;

    private String viewContent(String content) {
        if (StrUtil.isBlank(content)) {
            return "";
        }
        int i = content.indexOf("\"命令\":\"");
        if (i > 0) {
            String trim = content.substring(i + 6).trim();
            int i1 = trim.indexOf("\"");
            if (i1 > 0) {
                trim = trim.substring(0, i1);
                return trim;
            }
        }
        return "";
    }
}
