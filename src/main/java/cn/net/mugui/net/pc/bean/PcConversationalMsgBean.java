package cn.net.mugui.net.pc.bean;

import com.mugui.bean.JsonBean;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 *  会话消息
 */
@Getter
@Setter
@Accessors(chain = true)
public class PcConversationalMsgBean extends JsonBean {

    private String userId="master";

    private String userName="主人";


    private String context;


}
