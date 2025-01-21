package cn.net.mugui.net.music.source.impl;

import cn.hutool.http.HttpUtil;
import cn.net.mugui.net.douyu.danmu.common.util.HttpRequestUtil;
import cn.net.mugui.net.music.domain.Music;
import cn.net.mugui.net.music.source.IMusicAPI;
import com.mugui.base.base.Component;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Component
public class NetEaseMusic implements IMusicAPI {

    HttpRequestUtil util;

    public NetEaseMusic() {
        util = new HttpRequestUtil();
    }

    @Override
    public List<Music> searchWithoutLink(String keyWord, int pageSize, int pn) {
        List<Music> list = new LinkedList<>();
        try {
            String url = "https://music.163.com/weapi/search/suggest/web?csrf_token=";
            HashMap<String, String> headers = new HashMap<>();
            headers.put("content-type", "application/x-www-form-urlencoded");
            headers.put("referer", "https://music.163.com/search/");
            headers.put("accept", "*/*");
            headers.put("origin", "https://music.163.com");
            // headers.put("accept-encoding", "gzip");
            headers.put("accept-language", "zh-CN,zh;q=0.8");
            headers.put("user-agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:124.0) Gecko/20100101 Firefox/124.0");

            JSONObject obj = new JSONObject();
            obj.put("s", keyWord);
            obj.put("offset", pn - 1);
            obj.put("limit", pageSize);
            obj.put("type", "1"); // type 单曲1，歌手100，专辑10，MV1004，歌词1006，歌单1000，主播电台1009，用户1002
            obj.put("csrf_token", "");
            // obj.put("total", "true");
            String params = NetEaseEncryptUtil.generateToken(obj.toString());
            String result = util.postContent(url, headers, params);
            System.out.println(result);
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(result).getJSONObject("result");
            if (jsonObject != null && jsonObject.containsKey("songs")) {
                com.alibaba.fastjson.JSONArray songs = jsonObject.getJSONArray("songs");
                if (songs != null)
                    for (int i = 0; i < songs.size(); i++) {
                        // System.out.printf(" 当前第一个歌曲%d信息:\n",i+1);
                        Music song = new Music();
                        com.alibaba.fastjson.JSONObject json = songs.getJSONObject(i);
                        song.id = "" + json.getLong("id");
                        song.name = json.getString("name");
                        // 有多个singer时，默认只取首个
                        song.singer = json.getJSONArray("artists").getJSONObject(0).getString("name");
                        song.album = json.getJSONObject("album").getString("name");
                        // song.print();
                        list.add(song);
                    }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Music> searchWithLink(String keyWord, int pageSize, int pn) {
        List<Music> list = searchWithoutLink(keyWord, pageSize, pn);
        for (Music song : list) {
            song.url = source(song.id);
        }
        return list;
    }

    @Override
    public List<Music> searchSimilar(String keyWord, int pageSize, int pn) {
        List<Music> list = searchWithLink(keyWord, pageSize, pn);
        for (int i = list.size() - 1; i >= 0; i--) {
            // list.get(i).print();
            if (list.get(i).url == null)
                list.remove(i);
        }
        return list;
    }

    @Override
    public String source(String id) {
        try {
            String url ="https://musicbox-web-api.mu-jie.cc/wyy/mp3?rid="+id;
            return HttpUtil.get(url);
        } catch (Exception e) {
            return null;
        }
    }

}
