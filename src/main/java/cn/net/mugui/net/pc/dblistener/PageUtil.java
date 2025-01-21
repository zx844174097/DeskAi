package cn.net.mugui.net.pc.dblistener;

import com.mugui.base.client.net.bean.NetBag;
import com.mugui.bean.JsonBean;

/**
 * 分页插件工具
 *
 * @author 王涛
 */
public class PageUtil {
	/**
	 * 自动分页，但是这种方式有编写隐患
	 *
	 * @param bag bag
	 * @return
	 */
	public static Page offsetPage(NetBag bag) {
		Page page = Page.newBean(Page.class, bag.getData());
		if (page.getPageSize() > 0) {
			page.set();
		}
		return page;
	}

	public static Page offsetPage(JsonBean jsonBean) {
		Page page = Page.newBean(Page.class, jsonBean.get());
		if (page.getPageSize() > 0) {
			page.set();
		}
		return page;
	}

	public static Page offsetPage(Integer pageNum, Integer pageSize) {
		if (pageNum != null && pageSize != null && pageSize > 0) {
			Page page = new Page().setPageNum(pageNum).setPageSize(pageSize);
			page.set();
			return page;
		}
		return null;
	}

	public static Page offsetPage(Page objects) {
		Page page = Page.newBean(objects);
		if (page.getPageSize() > 0) {
			page.set();
		}
		return page;
	}
}
