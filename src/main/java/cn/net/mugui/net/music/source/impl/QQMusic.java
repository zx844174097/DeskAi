package cn.net.mugui.net.music.source.impl;

import cn.net.mugui.net.douyu.danmu.common.util.HttpRequestUtil;
import cn.net.mugui.net.music.domain.Music;
import cn.net.mugui.net.music.source.IMusicAPI;
import com.mugui.base.base.Component;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class QQMusic implements IMusicAPI {

	HttpRequestUtil util;

	public QQMusic() {
		util = new HttpRequestUtil();
	}

	@Override
	public List<Music> searchWithoutLink(String keyWord, int pageSize, int pn) {
		List<Music> list = new ArrayList<Music>();
		try {
			String url;
			url = String.format(
					"https://c6.y.qq.com/splcloud/fcgi-bin/smartbox_new.fcg?_=%d&cv=4747474&ct=24&format=json&inCharset=utf-8&outCharset=utf-8&notice=0&platform=yqq.json&needNewCode=1&uin=0&g_tk_new_20200303=5381&g_tk=5381&hostUin=0&is_xml=0&key=%s",
					System.currentTimeMillis(), URLEncoder.encode(keyWord, "UTF-8").replace("+", "%20"));
			HashMap<String, String> headers = new HashMap<>();
			headers.put("Accept", "*/*");
			// headers.put("Accept-Encoding", "gzip");
			headers.put("Accept-Language", "zh-CN,zh;q=0.8");
			headers.put("Content-Type", "application/json;charset=UTF-8");
			headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64)");
			headers.put("Referer", "https://y.qq.com/portal/player.html");
			String result = util.getContent(url, headers);
//			System.out.println(result);

			JSONArray jsonArr = new JSONObject(result).getJSONObject("data").getJSONObject("song").getJSONArray("itemlist");
			for (int i = 0; i < jsonArr.length(); i++) {
//				System.out.printf(" 当前第%d个歌曲信息:\n",i+1);
				Music song = new Music();
				JSONObject json = jsonArr.getJSONObject(i);
				song.id = json.getString("mid");
				song.name = json.getString("name");
				song.singer = json.getString("singer");
				song.album = "-";
//				song.print();
				list.add(song);
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
		for(int i = list.size() -1; i>=0; i--) {
			//list.get(i).print();
			if(list.get(i).url == null)
				list.remove(i);
		}
		return list;
	}
	@Override
	public String source(String id) {
		try {
			String url = "https://u.y.qq.com/cgi-bin/musicu.fcg?data=";
			String param = String.format(
					"{\"req_0\":{\"module\":\"vkey.GetVkeyServer\",\"method\":\"CgiGetVkey\",\"param\":{\"guid\":\"12345678\",\"songmid\":[\"%s\"],\"songtype\":[0],\"uin\":\"0\",\"loginflag\":1,\"platform\":\"20\"}},\"comm\":{\"uin\":0,\"format\":\"json\",\"ct\":20,\"cv\":0}}",
					id);
			url += URLEncoder.encode(param, "UTF-8");
			HashMap<String, String> headers = new HashMap<>();
			headers.put("Accept", "*/*");
			// headers.put("Accept-Encoding", "gzip");
			headers.put("Accept-Language", "zh-CN,zh;q=0.8");
			headers.put("Content-Type", "application/json;charset=UTF-8");
			headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64)");
			headers.put("Referer", "https://y.qq.com/portal/player.html");
			String result = util.getContent(url, headers);
//			System.out.println(result);
			JSONObject jObj = new JSONObject(result).getJSONObject("req_0").getJSONObject("data");
			String purl = jObj.getJSONArray("midurlinfo").getJSONObject(0).getString("purl");
			if (purl.isEmpty()) {
				return null;
			}
			String sip = jObj.getJSONArray("sip").getString(0);
//			System.out.println(sip + purl);
			return sip + purl;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
