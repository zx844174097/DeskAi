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
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;

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
    }

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


    private Music now = null;


    @Override
    protected void handle(Music poll) {
//        if (!playSuccess) {
//            getCycleList().addLast(poll);
//            return;
//        }
//        playSuccess = false;
        now = poll;
        play();
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
    private NetEaseMusic netEaseMusic ;

    public void play() {
        if (now == null) {
            now = Music.by(new Music());
        }
        if(!playSuccess){
            mediaPlayer.controls().stop();
        }

        String source = netEaseMusic.source(now.getId());
        if(source!=null){
            mediaPlayer.media().play(source);
        }else {
            nextMusic();
        }
    }

    public void nextMusic() {
        now = Music.next(now);
        play();
    }

    public void lastMusic() {
        now = Music.last(now);
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

}
