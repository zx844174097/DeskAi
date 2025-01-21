package cn.net.mugui.net.web.app;

import cn.net.mugui.net.pc.bean.MessageBean;
import com.mugui.Mugui;
import com.mugui.base.base.Component;
import com.mugui.base.client.net.base.Module;
import com.mugui.base.client.net.bean.Message;
import com.mugui.base.client.net.bean.NetBag;

/**
 * @author mjy
 * @date 2023/3/7
 */
@Component
@Module(type = "method",name = "index")
public class Index implements Mugui {

    public Message index(NetBag bag){
        return Message.ok();
    }

}
