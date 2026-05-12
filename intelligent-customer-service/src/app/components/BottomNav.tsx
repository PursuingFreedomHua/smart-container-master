import { MessageCircle, Wrench, RotateCcw, ShoppingBag, HelpCircle } from 'lucide-react';

export type TabType = 'chat' | 'repair' | 'refund' | 'order' | 'faq';

interface BottomNavProps {
  activeTab: TabType;
  onTabChange: (tab: TabType) => void;
}

const tabs = [
  { id: 'chat' as TabType, label: '客服', icon: MessageCircle },
  { id: 'repair' as TabType, label: '报修', icon: Wrench },
  { id: 'refund' as TabType, label: '退款', icon: RotateCcw },
  { id: 'order' as TabType, label: '订单', icon: ShoppingBag },
  { id: 'faq' as TabType, label: '帮助', icon: HelpCircle },
];

export function BottomNav({ activeTab, onTabChange }: BottomNavProps) {
  return (
    <div className="bg-card border-t border-border shadow-[0_-2px_12px_0_rgba(0,0,0,0.06)]">
      <div className="flex items-center justify-around h-16">
        {tabs.map((tab) => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => onTabChange(tab.id)}
              className="flex-1 flex flex-col items-center justify-center gap-1 transition-all duration-200 active:scale-95"
            >
              <Icon
                size={22}
                className={`transition-colors duration-200 ${
                  isActive ? 'text-primary' : 'text-text-tertiary'
                }`}
                strokeWidth={isActive ? 2.5 : 2}
              />
              <span
                className={`text-[11px] transition-colors duration-200 ${
                  isActive ? 'text-primary font-medium' : 'text-text-tertiary'
                }`}
              >
                {tab.label}
              </span>
            </button>
          );
        })}
      </div>
    </div>
  );
}
