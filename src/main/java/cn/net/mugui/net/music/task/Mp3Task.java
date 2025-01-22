package cn.net.mugui.net.music.task;

import cn.hutool.core.thread.ThreadUtil;
import cn.net.mugui.net.music.domain.Music;
import cn.net.mugui.net.music.source.impl.NetEaseMusic;
import cn.net.mugui.net.pc.dao.Sql;
import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;
import com.mugui.base.client.net.auto.AutoTask;
import com.mugui.base.client.net.base.Task;
import com.mugui.base.client.net.task.TaskCycleImpl;
import lombok.Getter;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;

import java.util.List;
import java.util.Vector;

@Component
@AutoTask
@Task
public class Mp3Task extends TaskCycleImpl<Music> {

    // 创建 MediaPlayer
    MediaPlayer mediaPlayer = null;

    @Override
    public void init() {
        super.init();

        Sql.getInstance().createTable(Music.class);
        playSuccess = true;

        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
        mediaPlayer = mediaPlayerFactory.mediaPlayers().newMediaPlayer();
        mediaPlayer.audio().setVolume(80);
        mediaPlayer.events().addMediaPlayerEventListener(mediaPlayerEventAdapter);

        List<Music> list = Music.list();
        index = 0;
        for (Music music : list) {
            musicLinkedList.add(music);
        }

    }

    private int index = 0;
    private Vector<Music> musicLinkedList = new Vector<>();


    boolean playSuccess = true;


    MediaPlayerEventAdapter mediaPlayerEventAdapter = new MediaPlayerEventAdapter() {
        @Override
        public void finished(MediaPlayer mediaPlayer) {
            super.finished(mediaPlayer);
            playSuccess = true;
            ThreadUtil.execute(new Runnable() {
                @Override
                public void run() {
                    nextMusic();
                }
            });
        }

        @Override
        public void playing(MediaPlayer mediaPlayer) {
            super.playing(mediaPlayer);
            playSuccess = false;
        }
    };


    @Getter
    private int nowIndex = -1;


    @Override
    protected void handle(Music poll) {
        synchronized (this) {
            int index = getIndex(poll);
            if (index == -1) {
                if (nowIndex == -1) {
                    musicLinkedList.add(poll);
                    nowIndex = musicLinkedList.size() - 1;
                } else {
                    musicLinkedList.add(nowIndex + 1, poll);
                    nowIndex++;
                }
            } else {
                if(nowIndex==-1){
                    nowIndex=index;
                }
                if(nowIndex>index){
                    musicLinkedList.remove(index);
                    musicLinkedList.add(nowIndex, poll);
                }else if(nowIndex<index){
                    musicLinkedList.remove(index);
                    musicLinkedList.add(nowIndex+1, poll);
                    nowIndex++;
                }else {
                    nowIndex=index;
                }
            }
            play();
        }

    }

    private int getIndex(Music poll) {
        int index = -1;
        for (int i = 0; i < musicLinkedList.size(); i++) {
            Music music = musicLinkedList.get(i);
            if (music.getId().equals(poll.getId())) {
                index = i;
                break;
            }
        }
        return index;
    }


    @Override
    public void add(Music data) {
        super.add(data);
        Music.saveOrUpdate(data);
    }

    public void pause() {
        mediaPlayer.controls().pause();
    }

    @Autowired
    private NetEaseMusic netEaseMusic;

    public void play() {
        if (!playSuccess) {
            mediaPlayer.controls().stop();
        }
        if (nowIndex < 0) {
            nowIndex = 0;
        }
        Music music = musicLinkedList.get(nowIndex);
        String source = netEaseMusic.source(music.getId());
        if (source != null) {
            mediaPlayer.media().play(source);
        } else {
            nextMusic();
        }
    }

    public void nextMusic() {
        nowIndex++;
        if(nowIndex>=musicLinkedList.size()){
            nowIndex=0;
        }
        play();
    }

    public void lastMusic() {
        nowIndex--;
        if(nowIndex<0){
            nowIndex=musicLinkedList.size()-1;
        }
        play();
    }


    //降低音量
    public void lowerVolume() {
        mediaPlayer.audio().setVolume(mediaPlayer.audio().volume() - 10);
    }

    //增加音量
    public void increaseVolume() {
        mediaPlayer.audio().setVolume(mediaPlayer.audio().volume() + 10);
    }

    public Vector<Music> list() {
        return musicLinkedList;
    }
}
