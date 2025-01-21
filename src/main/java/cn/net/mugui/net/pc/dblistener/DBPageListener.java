package cn.net.mugui.net.pc.dblistener;


import com.mugui.base.base.Component;
import com.mugui.sql.SqlServer;

@DBListener
@Component
public class DBPageListener extends SqlServer.SelectListenerImpl {

	@Override
	public String handleSql(String sql) {
		Page localPage = Page.getLocalPage();
		if (localPage == null||sql.toLowerCase().indexOf("select")<0)
			return sql;
		localPage.close();
		int page_num = localPage.getPageNum() <= 0 ? 0 : localPage.getPageNum() - 1;
		int page_size = localPage.getPageSize();
		if (page_size > 0 && sql.toLowerCase().indexOf(" limit ") < 0) {
			sql = sql.replaceAll("[;]", " ");
			sql += " limit  " + page_num * page_size + "," + page_size;
		}
		return sql;
	}
}
