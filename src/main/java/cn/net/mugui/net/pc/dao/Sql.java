package cn.net.mugui.net.pc.dao;

import com.mugui.base.base.Component;
import com.mugui.base.client.net.classutil.DataSave;
import com.mugui.sql.SqlModel;

@Component
public class Sql extends SqlModel{

    public static Sql getInstance() {
        return DataSave.context.getBean(Sql.class);
    }
}
