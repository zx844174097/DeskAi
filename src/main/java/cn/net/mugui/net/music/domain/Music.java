package cn.net.mugui.net.music.domain;

import cn.net.mugui.net.pc.dao.Sql;
import com.mugui.bean.JsonBean;
import com.mugui.sql.SQLDB;
import com.mugui.sql.SQLField;
import com.mugui.sql.loader.Select;
import com.mugui.sql.loader.Where;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@SQLDB(TABLE = "music", KEY = "music_id")
public class Music extends JsonBean {

    @SQLField(PRIMARY_KEY = true, AUTOINCREMENT = true, AUTOINCREMENT_value = "AUTOINCREMENT")
    private Integer music_id;

    @SQLField
    public String id;

    @SQLField
    public String name;

    @SQLField(DATA_TYPE = "TEXT")
    public String singer;

    @SQLField(DATA_TYPE = "TEXT")
    public String url;

    @SQLField
    public String album;

    @SQLField(DATA_TYPE = "TEXT")
    public String remark;

    public static void saveOrUpdate(Music data) {

        Music select = Sql.getInstance().select(new Music().setId(data.getId()));
        if (select == null) {
            select = Sql.getInstance().save(data);
        } else {
            select.setAlbum(data.getAlbum());
            select.setName(data.getName());
            select.setSinger(data.getSinger());
            select.setUrl(data.getUrl());
            select.setRemark(data.getRemark());
        }
        Sql.getInstance().updata(select);
    }

    public static Music by(Music music) {
        return Sql.getInstance().select(music);
    }

    public static Music next(Music now) {
        if (now == null) {
            return by(new Music());
        }
        Music music = new Music();
        Select where = Select.q(music).where(Where.q(music).gt("music_id", now.getMusic_id()).limit(1));
        Music select = Sql.getInstance().select(Music.class, where);
        if (select == null) {
            return by(new Music());
        }
        return select;
    }

    public static Music last(Music now) {
        if (now == null) {
            return by(new Music());
        }
        Music music = new Music();
        Select where = Select.q(music).where(Where.q(music).lt("music_id", now.getMusic_id()).limit(1));
        Music select = Sql.getInstance().select(Music.class, where);
        if (select == null) {
            return by(new Music());
        }
        return select;
    }

    public static List<Music> list() {
        return Sql.getInstance().selectList(new Music());

    }
}
