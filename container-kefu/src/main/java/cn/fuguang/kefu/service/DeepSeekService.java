package cn.fuguang.kefu.service;

import cn.fuguang.kefu.pojo.bean.DeepSeekChatReqBean;

import java.util.List;
import java.util.function.Consumer;

/**
 * DeepSeek Chat API 服务接口
 */
public interface DeepSeekService {

    /**
     * 流式对话（SSE 逐 token 回调）
     *
     * @param messages 消息列表（system + history + user）
     * @param onToken  每个 token 的回调
     * @param onFinish 完成时的回调
     */
    void chatStream(List<DeepSeekChatReqBean.Message> messages,
                    Consumer<String> onToken,
                    Runnable onFinish);

    /**
     * 非流式对话（同步等待完整响应）
     *
     * @param messages 消息列表
     * @return 完整响应文本
     */
    String chatSync(List<DeepSeekChatReqBean.Message> messages);
}
