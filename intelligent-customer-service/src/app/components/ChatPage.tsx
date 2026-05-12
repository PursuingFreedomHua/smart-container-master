import { useState, useRef, useEffect } from 'react';
import { Send } from 'lucide-react';

interface Message {
  id: string;
  type: 'user' | 'bot';
  content: string;
  time: string;
}

const quickQuestions = [
  '货柜门打不开',
  '商品卡住了',
  '扣款但没出货',
  '查询订单',
];

const autoReplies: Record<string, string> = {
  '货柜门打不开': '您好，货柜门打不开可能是以下原因：\n1. 请确认支付是否成功\n2. 请稍等3-5秒后再次尝试\n3. 如仍无法打开，请点击"故障报修"联系我们',
  '商品卡住了': '很抱歉给您带来不便。请点击底部"故障报修"填写详细信息，我们将在10分钟内为您处理退款。',
  '扣款但没出货': '非常抱歉！请点击底部"退款申请"，填写订单信息后我们将立即为您退款。',
  '查询订单': '请点击底部"订单查询"即可查看您的所有订单记录。',
};

export function ChatPage() {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '1',
      type: 'bot',
      content: '您好！我是智能货柜客服助手，有什么可以帮您？',
      time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
    },
  ]);
  const [inputValue, setInputValue] = useState('');
  const [isTyping, setIsTyping] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSendMessage = (content: string) => {
    if (!content.trim()) return;

    const userMessage: Message = {
      id: Date.now().toString(),
      type: 'user',
      content,
      time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
    };

    setMessages((prev) => [...prev, userMessage]);
    setInputValue('');
    setIsTyping(true);

    setTimeout(() => {
      const botReply = autoReplies[content] || '感谢您的咨询，人工客服将在5分钟内回复您。';
      const botMessage: Message = {
        id: (Date.now() + 1).toString(),
        type: 'bot',
        content: botReply,
        time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
      };
      setMessages((prev) => [...prev, botMessage]);
      setIsTyping(false);
    }, 1000);
  };

  return (
    <div className="flex flex-col h-full bg-background">
      {/* 货柜信息卡片 */}
      <div className="bg-card p-4 shadow-[0_2px_8px_0_rgba(0,0,0,0.06)]">
        <div className="flex items-center justify-between mb-2.5">
          <div className="flex items-center gap-2">
            <div className="w-2 h-2 rounded-full bg-success shadow-[0_0_8px_rgba(82,196,26,0.4)]"></div>
            <span className="text-[15px] font-medium text-text-primary">货柜编号: GG-2024-0512</span>
          </div>
          <span className="text-xs text-text-tertiary">在线</span>
        </div>
        <div className="text-[13px] text-text-secondary leading-5">
          📍 北京市朝阳区建国路88号SOHO现代城B座1层
        </div>
      </div>

      {/* 快捷问题 */}
      <div className="bg-card px-4 py-3 border-t border-border">
        <div className="text-xs text-text-tertiary mb-2.5">常见问题</div>
        <div className="flex flex-wrap gap-2">
          {quickQuestions.map((question) => (
            <button
              key={question}
              onClick={() => handleSendMessage(question)}
              className="px-4 py-2 bg-accent text-primary text-[13px] rounded-full hover:bg-primary hover:text-primary-foreground transition-all duration-200 active:scale-95 shadow-sm"
            >
              {question}
            </button>
          ))}
        </div>
      </div>

      {/* 消息列表 */}
      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {messages.map((message) => (
          <div
            key={message.id}
            className={`flex gap-2.5 ${message.type === 'user' ? 'flex-row-reverse' : 'flex-row'} animate-in fade-in slide-in-from-bottom-2 duration-300`}
          >
            {/* 头像 */}
            <div className="w-10 h-10 rounded-full flex-shrink-0 flex items-center justify-center text-lg overflow-hidden shadow-md">
              {message.type === 'user' ? (
                <div className="w-full h-full bg-gradient-to-br from-[#4E5969] to-[#1D2129] flex items-center justify-center text-white">
                  👤
                </div>
              ) : (
                <div className="w-full h-full bg-gradient-to-br from-primary to-[#4096FF] flex items-center justify-center text-white">
                  🤖
                </div>
              )}
            </div>

            {/* 消息内容 */}
            <div className="flex flex-col max-w-[65%]">
              <div
                className={`rounded-2xl p-3.5 shadow-sm ${
                  message.type === 'user'
                    ? 'bg-primary text-primary-foreground shadow-[0_4px_12px_0_rgba(22,119,255,0.15)]'
                    : 'bg-card text-card-foreground border border-border shadow-[0_2px_8px_0_rgba(0,0,0,0.06)]'
                }`}
              >
                <div className="text-[14px] leading-[20px] whitespace-pre-line">{message.content}</div>
              </div>
              <div
                className={`text-[11px] mt-1.5 px-1 ${
                  message.type === 'user' ? 'text-right text-text-tertiary' : 'text-left text-text-tertiary'
                }`}
              >
                {message.time}
              </div>
            </div>
          </div>
        ))}
        {isTyping && (
          <div className="flex gap-2.5">
            {/* 客服头像 */}
            <div className="w-10 h-10 rounded-full flex-shrink-0 flex items-center justify-center text-lg overflow-hidden shadow-md">
              <div className="w-full h-full bg-gradient-to-br from-primary to-[#4096FF] flex items-center justify-center text-white">
                🤖
              </div>
            </div>
            {/* 输入中动画 */}
            <div className="bg-card border border-border rounded-2xl p-3.5 shadow-sm">
              <div className="flex gap-1.5">
                <div className="w-2 h-2 bg-text-tertiary rounded-full animate-bounce"></div>
                <div className="w-2 h-2 bg-text-tertiary rounded-full animate-bounce [animation-delay:0.2s]"></div>
                <div className="w-2 h-2 bg-text-tertiary rounded-full animate-bounce [animation-delay:0.4s]"></div>
              </div>
            </div>
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* 输入框 */}
      <div className="bg-card p-4 border-t border-border shadow-[0_-2px_8px_0_rgba(0,0,0,0.04)]">
        <div className="flex items-center gap-3">
          <input
            type="text"
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleSendMessage(inputValue)}
            placeholder="请输入您的问题..."
            className="flex-1 px-4 py-2.5 bg-input-background rounded-xl text-[14px] text-text-primary placeholder:text-text-tertiary outline-none focus:ring-2 focus:ring-primary/20 focus:bg-card transition-all duration-200"
          />
          <button
            onClick={() => handleSendMessage(inputValue)}
            disabled={!inputValue.trim()}
            className="w-11 h-11 bg-primary text-primary-foreground rounded-xl flex items-center justify-center hover:bg-[#4096FF] transition-all duration-200 active:scale-95 shadow-[0_4px_12px_0_rgba(22,119,255,0.25)] disabled:opacity-50 disabled:cursor-not-allowed disabled:shadow-none"
          >
            <Send size={18} />
          </button>
        </div>
      </div>
    </div>
  );
}
