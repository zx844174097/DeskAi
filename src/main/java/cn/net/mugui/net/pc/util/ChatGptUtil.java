package cn.net.mugui.net.pc.util;

import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.net.mugui.net.pc.dao.Sql;
import cn.net.mugui.net.pc.bean.MessageBean;
import cn.net.mugui.net.web.util.SysConf;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mugui.base.base.Autowired;
import com.mugui.base.base.Component;
import com.mugui.base.client.net.classutil.DataSave;
import com.mugui.util.Other;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

@Component
public class ChatGptUtil {

    @Autowired
    private SysConf sysConf;

    @Autowired
    private Sql sql;




    public ConcurrentLinkedQueue<String> sendMsg(MessageBean msgBean, String gptModel) {
        HashMap<String, String> header = createHeader();

        List<MessageBean> all = MessageBean.all(msgBean.getSession_id());


        LinkedList<MessageBean> list = new LinkedList<>();
        MessageBean messageBean = MessageBean.initSystem();
        list.add(messageBean);

        LinkedList<MessageBean> messageBeans = MessageBean.handleGptMessage(all);


        JSONObject object = new JSONObject();
        object.put("model", gptModel);
        object.put("messages", messageBeans);
        object.put("stream", true);
        object.put("functions", MessageBean.newFunction());

        return sendSteamGptMsg(header, "https://api.openai.com/v1/chat/completions", object.toString());
    }


    private ConcurrentLinkedQueue<String> sendSteamGptMsg(HashMap<String, String> header, String url, String postBody) {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        try {
            System.out.println(url + "----->" + postBody);
            String value1 = sysConf.getValue("proxy.http.port");
            HttpRequest body = HttpRequest.post(url).body(postBody);
            if (StrUtil.isNotBlank(value1)) {

                String value2 = sysConf.getValue("proxy.http.ip");
                if (StrUtil.isNotBlank(value2)) {
                    body.setHttpProxy(value2, Integer.parseInt(value1));
                } else {
                    body.setHttpProxy("127.0.0.1", Integer.parseInt(value1));
                }
            }
            HttpRequest request = body.headerMap(header, true).timeout(60000);
            ThreadUtil.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpResponse httpResponse = request.executeAsync();
                        if (httpResponse.isOk()) {
                            InputStream inputStream = httpResponse.bodyStream();
                            Scanner scanner = new Scanner(inputStream);
                            while (scanner.hasNextLine()) {
                                String line = scanner.nextLine();
                                if (line.startsWith("data:")) {
                                    String data = line.substring(5).trim();
                                    if (!data.isEmpty() && !"[DONE]".equals(data)) {
                                        JSONObject chunk = JSONObject.parseObject(data);
                                        JSONObject jsonObject = chunk.getJSONArray("choices")
                                                .getJSONObject(0)
                                                .getJSONObject("delta");
                                        String content = jsonObject.getString("content");
                                        if (content != null) {
                                            queue.add(content);
                                        }
                                        JSONObject function_call = jsonObject.getJSONObject("function_call");
                                        if (function_call != null) {
                                            String string = function_call.getString("arguments");
                                            if (string != null)
                                                queue.add(string);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        queue.add("[done]");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            queue.add("系统错误" + e.getMessage());
            queue.add("[done]");
        }
        return queue;
    }

    private LinkedList<AbstractMap.SimpleEntry<String, String>> getGptRunsSteps(String threadId, String runsId) {
        String url = "https://api.openai.com/v1/threads/" + threadId + "/runs/" + runsId + "/steps";
        HashMap<String, String> hashMap = createHeader();
        String gptMsg = getGptMsg(hashMap, url);
        JSONObject object = JSONObject.parseObject(gptMsg);
        JSONArray data = object.getJSONArray("data");

        LinkedList<AbstractMap.SimpleEntry<String, String>> list = new LinkedList<>();

        for (int i = data.size() - 1; i >= 0; i--) {
            JSONObject jsonObject = data.getJSONObject(i);
            String type = jsonObject.getString("type");
            if ("tool_calls".equals(type)) {
                JSONArray jsonArray = jsonObject.getJSONObject("step_details").getJSONArray("tool_calls");
                for (int j = 0; j < jsonArray.size(); j++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(j);
                    String type1 = jsonObject1.getString("type");
                    if ("code_interpreter".equals(type1)) {
                        JSONObject function = jsonObject1.getJSONObject(type1);
                        String input = function.getString("input");
                        list.add(new AbstractMap.SimpleEntry<>("code", input));
                        JSONArray output = function.getJSONArray("outputs");
                        for (Object o : output) {
                            JSONObject jsonObject2 = (JSONObject) o;
                            String type2 = jsonObject2.getString("type");
                            if (type2.equals("logs")) {
                                list.add(new AbstractMap.SimpleEntry<>("text", jsonObject2.getString("logs")));
                            } else if (type2.equals("image")) {
                                String file_id = jsonObject2.getJSONObject("image").getString("file_id");
                                File file = downGptFile(file_id);
                                if (file != null) {
                                    list.add(new AbstractMap.SimpleEntry<>("image", file.getAbsolutePath()));
                                }
                            }
                        }
                    }
                }
            } else if ("message_creation".equals(type)) {
                JSONObject jsonObject1 = jsonObject.getJSONObject("step_details").getJSONObject("message_creation");
                list.add(new AbstractMap.SimpleEntry<>("msg_id", jsonObject1.getString("message_id")));
            }
        }
        return list;
    }

    private String callTheFunction(JSONObject jsonObject) {
        Function<JSONObject, String> name = hashMap.get(jsonObject.getString("name"));
        if (name != null) {
            String arguments = name.apply(jsonObject.getJSONObject("arguments"));
            return StrUtil.isNotBlank(arguments) ? arguments : "true";
        }
        return "true";
    }

    private void sendRequiresSuccess(String threadId, String runsId, String callsId, String o) {
        String url = "https://api.openai.com/v1/threads/" + threadId + "/runs/" + runsId + "/submit_tool_outputs";
        HashMap<String, String> hashMap = createHeader();
        String string = "{\n" +
                "  \"threadId\": \"" + threadId + "\",\n" +
                "  \"runId\": \"" + runsId + "\",\n" +
                "  \"functionResponses\": {\n" +
                "    \"tool_outputs\": [\n" +
                "      {\n" +
                "        \"tool_call_id\": \"" + callsId + "\",\n" +
                "        \"output\": \"{success:\\\"" + o + "\\\"}\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        sendGptMsg(hashMap, url, JSONObject.parseObject(string).getJSONObject("functionResponses").toString());
    }

    private MessageBean getGptMessages(String threadId, LinkedList<AbstractMap.SimpleEntry<String, String>> gptRunsSteps) {
        String url = "https://api.openai.com/v1/threads/" + threadId + "/messages";
        HashMap<String, String> hashMap = createHeader();
        String gptMsg = getGptMsg(hashMap, url);
        JSONObject object = JSONObject.parseObject(gptMsg);
        JSONArray data = object.getJSONArray("data");
        MessageBean messageBean = new MessageBean();
        messageBean.setRole("assistant");
        messageBean.setContent(null);
        for (AbstractMap.SimpleEntry<String, String> gptRunsStep : gptRunsSteps) {
            if (gptRunsStep.getKey().equals("msg_id")) {
                String value = gptRunsStep.getValue();
                for (int i = 0; i < data.size(); i++) {
                    JSONObject jsonObject = data.getJSONObject(i);
                    if (!jsonObject.getString("id").equals(value)) {
                        continue;
                    }
                    String role = jsonObject.getString("role");
                    messageBean.setRole(role);
                    JSONArray content1 = jsonObject.getJSONArray("content");
                    for (int j = 0; j < content1.size(); j++) {
                        JSONObject content = content1.getJSONObject(j);
                        String type = content.getString("type");
                        if ("text".equals(type)) {
                            String string = content.getJSONObject("text").getString("value");
                            messageBean.getMsgList().add(new AbstractMap.SimpleEntry<>("text", string));
                        }
//                        else if("image_file".equals(type)){
//                            String file_id = content.getJSONObject("image_file").getString("file_id");
//                            File file = downGptFile(file_id);
//                            if(file!=null){
//                                messageBean.getMsgList().add(new AbstractMap.SimpleEntry<>("image",file.getAbsolutePath()));
//                            }
//                        }
                    }
                }
            } else {
                messageBean.getMsgList().add(gptRunsStep);
            }
        }

        return messageBean;
    }

    private File downGptFile(String file_id) {
        while (true) {
            try {
                HashMap<String, String> header = createHeader();
                HttpRequest body = HttpUtil.createGet("\n" +
                                "https://api.openai.com/v1/files/" + file_id + "/content", true)
                        .headerMap(header, true)
                        .timeout(120000);

                String value1 = sysConf.getValue("proxy.http.port");
                if (StrUtil.isNotBlank(value1)) {

                    String value2 = sysConf.getValue("proxy.http.ip");
                    if (StrUtil.isNotBlank(value2)) {
                        body.setHttpProxy(value2, Integer.parseInt(value1));
                    } else {
                        body.setHttpProxy("127.0.0.1", Integer.parseInt(value1));
                    }
                }
                final HttpResponse response = body
                        .executeAsync();
                if (response.isOk()) {
                    InputStream inputStream = response.bodyStream();
                    File file = FileUtil.file(DataSave.APP_PATH + "\\gptFile\\", file_id);
                    FileUtil.exist(file.getParentFile());
                    FileUtil.writeFromStream(inputStream, file);
                    return file;
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                Other.sleep(3000);
            }
        }

    }

    private JSONObject getRunsStatus(String threadId, String runsId) {
        String url = "https://api.openai.com/v1/threads/" + threadId + "/runs/" + runsId;
        HashMap<String, String> hashMap = createHeader();
        String gptMsg = getGptMsg(hashMap, url);
        JSONObject object = JSONObject.parseObject(gptMsg);
        return object;
    }

    private String getGptMsg(HashMap<String, String> hashMap, String url) {
        while (true) {
            try {
                String value1 = sysConf.getValue("proxy.http.port");
                HttpRequest body = HttpRequest.get(url);
                if (StrUtil.isNotBlank(value1)) {
                    String value2 = sysConf.getValue("proxy.http.ip");
                    if (StrUtil.isNotBlank(value2)) {
                        body.setHttpProxy(value2, Integer.parseInt(value1));
                    } else {
                        body.setHttpProxy("127.0.0.1", Integer.parseInt(value1));
                    }
                }
                String get = body.headerMap(hashMap, true).timeout(60000).execute().body();
                System.out.println(url + "-get---->" + get);
                return get;
            } catch (Exception e) {
                e.printStackTrace();
                Other.sleep(3000);
            }
        }
    }

    private String gptRuns(String threadId, String gptModel) {
        String url = "https://api.openai.com/v1/threads/" + threadId + "/runs";
        HashMap<String, String> hashMap = createHeader();
        JSONObject object = new JSONObject();
        object.put("assistant_id", gptModel);
        Object post = sendGptMsg(hashMap, url, object.toString());
        JSONObject object1 = JSONObject.parseObject(post.toString());
        return object1.getString("id");
    }

    private void addGptMessage(String threadId, MessageBean msgBean) {
        String url = "https://api.openai.com/v1/threads/" + threadId + "/messages";
        sendGptMsg(createHeader(), url, msgBean.toString());
    }

    private String sendGptMsg(HashMap<String, String> hashMap, String url, String postBody) {
        while (true) {
            try {
                System.out.println(url + "----->" + postBody);
                String value1 = sysConf.getValue("proxy.http.port");
                HttpRequest body = HttpRequest.post(url).body(postBody);
                if (StrUtil.isNotBlank(value1)) {

                    String value2 = sysConf.getValue("proxy.http.ip");
                    if (StrUtil.isNotBlank(value2)) {
                        body.setHttpProxy(value2, Integer.parseInt(value1));
                    } else {
                        body.setHttpProxy("127.0.0.1", Integer.parseInt(value1));
                    }
                }
                String post = body.headerMap(hashMap, true).timeout(60000).execute().body();
                System.out.println(url + "----->" + post);
                return post;
            } catch (Exception e) {
                e.printStackTrace();
                Other.sleep(3000);
            }
        }
    }

    private HashMap<String, String> createHeader() {
        HashMap hashMap = new HashMap<>();
        String value = sysConf.getValue("chatGpt.key");
        hashMap.put("Authorization", "Bearer " + value);
        String name = sysConf.getValue("chatGpt.organization");
        hashMap.put("OpenAI-Organization", name);
        hashMap.put("Content-Type", "application/json");
        hashMap.put("OpenAI-Beta", "assistants=v2");
        hashMap.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36 Edg/121.0.0.0");
        return hashMap;
    }




    /**
     * 创建gpt线程id
     *
     * @param getUserId
     * @return
     */
    public String createThread(String getUserId) {
        String threadId =sysConf.getValue("chatGpt.threadId."+getUserId);
        if (StrUtil.isNotBlank(threadId)) {
            return threadId;
        }
        String string = UUID.fastUUID().toString(true);
        sysConf.setValue("chatGpt.threadId."+getUserId,string);
        return string;
    }

    public void removeThread(String getUserId) {
        sysConf.remove("chatGpt.threadId."+getUserId);
    }

    HashMap<String, Function<JSONObject, String>> hashMap = new HashMap<>();

    public void addCallFunction(String callFunctionName, Function<JSONObject, String> runnable) {
        hashMap.put(callFunctionName, runnable);
    }
}
