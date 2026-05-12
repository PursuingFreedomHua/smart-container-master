import { useState } from 'react';
import { BottomNav, TabType } from './components/BottomNav';
import { ChatPage } from './components/ChatPage';
import { RepairPage } from './components/RepairPage';
import { RefundPage } from './components/RefundPage';
import { OrderPage } from './components/OrderPage';
import { FAQPage } from './components/FAQPage';

export default function App() {
  const [activeTab, setActiveTab] = useState<TabType>('chat');

  const renderPage = () => {
    switch (activeTab) {
      case 'chat':
        return <ChatPage />;
      case 'repair':
        return <RepairPage />;
      case 'refund':
        return <RefundPage />;
      case 'order':
        return <OrderPage />;
      case 'faq':
        return <FAQPage />;
      default:
        return <ChatPage />;
    }
  };

  return (
    <div className="size-full flex items-center justify-center bg-gradient-to-br from-[#F0F2F5] to-[#E5E6EB]">
      {/* 微信小程序容器 */}
      <div className="w-[375px] h-[812px] bg-card shadow-[0_8px_32px_0_rgba(0,0,0,0.12)] rounded-3xl overflow-hidden flex flex-col">
        {/* 状态栏 */}
        <div className="bg-card h-11 flex items-center justify-between px-4 border-b border-border rounded-t-3xl">
          <span className="text-xs text-text-primary font-medium">9:41</span>
          <span className="text-xs text-text-primary font-semibold">智能货柜客服</span>
          <div className="flex items-center gap-1">
            <div className="w-4 h-3 border border-text-primary rounded-sm relative">
              <div className="absolute inset-0.5 bg-text-primary"></div>
            </div>
          </div>
        </div>

        {/* 页面内容 */}
        <div className="flex-1 overflow-hidden">{renderPage()}</div>

        {/* 底部导航 */}
        <BottomNav activeTab={activeTab} onTabChange={setActiveTab} />
      </div>
    </div>
  );
}