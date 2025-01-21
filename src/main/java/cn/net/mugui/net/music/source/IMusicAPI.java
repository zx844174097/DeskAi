package cn.net.mugui.net.music.source;

import cn.net.mugui.net.music.domain.Music;

import java.util.List;

public interface IMusicAPI {

	/**
	 * 只查询歌曲信息
	 * @param keyWord
	 * @param pageSize
	 * @param pn
	 * @return
	 */
	public List<Music> searchWithoutLink(String keyWord, int pageSize, int pn);
	
	/**
	 * 查询歌曲信息(包括音源链接)
	 * @param keyWord
	 * @param pageSize
	 * @param pn
	 * @return
	 */
	public List<Music> searchWithLink(String keyWord, int pageSize, int pn);
	
	/**
	 * 查询歌曲信息(包括音源链接)
	 * 当歌曲返回链接为空，从结果中删除
	 * @param keyWord
	 * @param pageSize
	 * @param pn
	 * @return
	 */
	public List<Music> searchSimilar(String keyWord, int pageSize, int pn);
	
	
	public String source(String id);
}
