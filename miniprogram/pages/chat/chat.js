/**
 * 智能客服聊天页
 * 功能：消息列表、快捷问题、输入框发送、自动回复
 * 后续对接：
 *   1. 接入真实客服 WebSocket 实现实时聊天
 *   2. 接入 AI 大模型实现智能回复
 *   3. 从服务器加载历史消息
 */
const mock = require('../../utils/mock.js');

Page({
  data: {
    // 消息列表
    messages: [],

    // 输入框内容
    inputValue: '',

    // 是否正在输入中（显示输入指示器）
    isTyping: false,

    // 快捷问题列表
    quickQuestions: mock.quickQuestions,

    // 货柜信息
    cabinetInfo: mock.cabinetInfo,

    // 滚动到指定消息的 ID（用于自动滚动到底部）
    scrollToId: '',
  },

  onLoad() {
    // 初始化消息列表（显示欢迎语）
    this.setData({
      messages: mock.getInitialMessages(),
      scrollToId: 'msg-1',
    });
  },

  onReady() {
    // 页面渲染完成后滚动到底部
    this.scrollToBottom();
  },

  /**
   * 发送消息（用户消息）
   * @param {string} content - 消息内容
   */
  handleSendMessage(content) {
    // 去除首尾空格，空内容不发送
    if (!content || !content.trim()) return;

    const now = mock.formatTime(new Date());

    // 构建用户消息对象
    const userMessage = {
      id: mock.generateId(),
      type: 'user',
      content: content,
      time: now,
    };

    // 添加用户消息到列表，清空输入框，显示输入中状态
    const messages = this.data.messages.concat([userMessage]);
    this.setData({
      messages: messages,
      inputValue: '',
      isTyping: true,
      scrollToId: 'msg-' + userMessage.id,
    });

    // 模拟客服延迟回复（1秒后回复）
    // 后续对接：替换为真实的 AI 客服接口调用
    this._replyTimer = setTimeout(() => {
      const replyContent = mock.autoReplies[content] || mock.defaultReply;
      const replyTime = mock.formatTime(new Date());
      const botMessage = {
        id: mock.generateId(),
        type: 'bot',
        content: replyContent,
        time: replyTime,
      };

      const newMessages = this.data.messages.concat([botMessage]);
      this.setData({
        messages: newMessages,
        isTyping: false,
        scrollToId: 'msg-' + botMessage.id,
      });
    }, 1000);
  },

  /**
   * 点击快捷问题
   * @param {object} e - 事件对象
   */
  handleQuickQuestion(e) {
    const question = e.currentTarget.dataset.question;
    this.handleSendMessage(question);
  },

  /**
   * 输入框内容变化
   * @param {object} e - 事件对象
   */
  handleInputChange(e) {
    this.setData({
      inputValue: e.detail.value,
    });
  },

  /**
   * 输入框确认（键盘完成按钮）
   */
  handleInputConfirm() {
    this.handleSendMessage(this.data.inputValue);
  },

  /**
   * 点击发送按钮
   */
  handleSendClick() {
    this.handleSendMessage(this.data.inputValue);
  },

  /**
   * 滚动到列表底部
   */
  scrollToBottom() {
    const messages = this.data.messages;
    if (messages.length > 0) {
      const lastMsg = messages[messages.length - 1];
      this.setData({
        scrollToId: 'msg-' + lastMsg.id,
      });
    }
  },

  onUnload() {
    // 清除定时器，避免内存泄漏
    if (this._replyTimer) {
      clearTimeout(this._replyTimer);
    }
  },
});
