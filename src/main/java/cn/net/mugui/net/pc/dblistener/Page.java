
package cn.net.mugui.net.pc.dblistener;

import com.mugui.bean.JsonBean;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.Getter;

@Getter
@Setter
@Accessors(chain = true)
public class Page extends JsonBean {

	/**
	 * 页码，从1开始
	 */
	private int pageNum;
	/**
	 * 页面大小
	 */
	private int pageSize;

	public Page() {
		super();
	}

	/**
	 * @param pagNumber 当前页码
	 * @param pagSize   每页显示条数
	 * @param sumPage   总页数,每页的话传0
	 * @return
	 */
	public Page getPage(Integer pagNumber, Integer pagSize, Integer sumPage) {
		Page page = new Page();
		if (pagNumber-1 <= 0) {
			pagNumber = 1;
		} else if (pagNumber > sumPage) {
			pagNumber = sumPage;
		}
		/*page.setPagNumber((pagNumber-1) * pagSize);
		page.setPagSize(pagSize);*/
		return page;
	}


	private static ThreadLocal<Page> local = new ThreadLocal<Page>();

	public void close() {
		local.remove();
	}

	public void set() {
		local.set(this);
	}

	public static Page getLocalPage() {
		return local.get();
	}
}
