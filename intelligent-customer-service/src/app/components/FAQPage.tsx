import { useState } from 'react';
import { ChevronDown, Search, MessageCircle } from 'lucide-react';

interface FAQ {
  category: string;
  items: {
    question: string;
    answer: string;
  }[];
}

const faqs: FAQ[] = [
  {
    category: '支付相关',
    items: [
      {
        question: '支持哪些支付方式？',
        answer: '目前支持微信支付、支付宝、云闪付等主流支付方式。',
      },
      {
        question: '支付后多久开门？',
        answer: '支付成功后3-5秒内自动开门，请稍作等待。如超过10秒未开门，请联系客服。',
      },
      {
        question: '支付失败怎么办？',
        answer: '请检查网络连接和账户余额，如仍失败可尝试更换支付方式或联系客服。',
      },
    ],
  },
  {
    category: '取货相关',
    items: [
      {
        question: '商品卡住了怎么办？',
        answer: '请点击"故障报修"填写信息，我们将在10分钟内处理并退款。',
      },
      {
        question: '取到的商品有问题怎么办？',
        answer: '如商品过期或损坏，请拍照并点击"退款申请"，我们将第一时间为您退款。',
      },
      {
        question: '能否取消订单？',
        answer: '支付成功后订单无法取消，如未成功取货可申请退款。',
      },
    ],
  },
  {
    category: '退款相关',
    items: [
      {
        question: '退款多久到账？',
        answer: '退款将原路返回，一般1-3个工作日到账。',
      },
      {
        question: '支付了但没出货能退款吗？',
        answer: '可以的，请点击"退款申请"填写订单信息即可。',
      },
      {
        question: '退款失败怎么办？',
        answer: '请联系客服人工处理，提供订单号和支付凭证。',
      },
    ],
  },
  {
    category: '其他问题',
    items: [
      {
        question: '货柜门打不开怎么办？',
        answer: '1. 确认支付是否成功\n2. 等待3-5秒后再次尝试\n3. 如仍无法打开请联系客服',
      },
      {
        question: '如何查看历史订单？',
        answer: '点击底部"订单查询"即可查看所有订单记录。',
      },
      {
        question: '客服工作时间是？',
        answer: '人工客服：9:00-21:00\n智能客服：24小时在线',
      },
    ],
  },
];

export function FAQPage() {
  const [expandedIndex, setExpandedIndex] = useState<string | null>(null);

  const toggleExpand = (categoryIndex: number, itemIndex: number) => {
    const key = `${categoryIndex}-${itemIndex}`;
    setExpandedIndex(expandedIndex === key ? null : key);
  };

  return (
    <div className="h-full overflow-y-auto bg-background">
      {/* 搜索框 */}
      <div className="bg-card p-4 mb-4 shadow-[0_2px_8px_0_rgba(0,0,0,0.06)]">
        <div className="relative">
          <Search size={18} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-text-tertiary" />
          <input
            type="text"
            placeholder="搜索常见问题..."
            className="w-full pl-10 pr-4 py-2.5 bg-input-background border border-border rounded-xl text-[14px] text-text-primary placeholder:text-text-tertiary outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all duration-200"
          />
        </div>
      </div>

      {/* FAQ列表 */}
      {faqs.map((category, categoryIndex) => (
        <div key={categoryIndex} className="mb-4">
          <div className="bg-muted px-4 py-2.5 border-y border-border">
            <h3 className="text-[13px] font-semibold text-text-secondary">{category.category}</h3>
          </div>
          <div className="bg-card shadow-[0_2px_8px_0_rgba(0,0,0,0.06)]">
            {category.items.map((item, itemIndex) => {
              const key = `${categoryIndex}-${itemIndex}`;
              const isExpanded = expandedIndex === key;
              return (
                <div key={itemIndex} className="border-b border-border last:border-b-0">
                  <button
                    onClick={() => toggleExpand(categoryIndex, itemIndex)}
                    className="w-full px-4 py-3.5 flex items-center justify-between text-left hover:bg-secondary/50 transition-colors duration-200"
                  >
                    <span className="text-[14px] text-text-primary font-medium pr-4">{item.question}</span>
                    <ChevronDown
                      size={18}
                      className={`text-text-tertiary transition-transform duration-200 flex-shrink-0 ${
                        isExpanded ? 'rotate-180' : ''
                      }`}
                    />
                  </button>
                  {isExpanded && (
                    <div className="px-4 pb-4 text-[13px] text-text-secondary leading-5 whitespace-pre-line bg-secondary/30">
                      {item.answer}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        </div>
      ))}

      {/* 联系客服 */}
      <div className="bg-card p-5 mx-4 mb-4 rounded-xl text-center shadow-[0_2px_8px_0_rgba(0,0,0,0.06)]">
        <p className="text-[14px] text-text-secondary mb-3">没找到您的问题？</p>
        <button className="inline-flex items-center gap-2 px-6 py-2.5 bg-primary text-primary-foreground rounded-xl text-[14px] font-medium hover:bg-[#4096FF] transition-all duration-200 active:scale-95 shadow-[0_4px_12px_0_rgba(22,119,255,0.25)]">
          <MessageCircle size={16} />
          联系人工客服
        </button>
      </div>
    </div>
  );
}
