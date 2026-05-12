import { useState } from 'react';
import { ChevronLeft, Package } from 'lucide-react';

interface Order {
  id: string;
  time: string;
  items: string[];
  amount: number;
  status: 'completed' | 'refunded' | 'pending';
  cabinetId: string;
}

const mockOrders: Order[] = [
  {
    id: '20240510143256',
    time: '2024-05-10 14:32',
    items: ['可口可乐 330ml', '三只松鼠坚果'],
    amount: 15.5,
    status: 'completed',
    cabinetId: 'GG-2024-0512',
  },
  {
    id: '20240509092145',
    time: '2024-05-09 09:21',
    items: ['康师傅矿泉水 550ml'],
    amount: 3.0,
    status: 'completed',
    cabinetId: 'GG-2024-0512',
  },
  {
    id: '20240508183012',
    time: '2024-05-08 18:30',
    items: ['士力架巧克力', '怡宝矿泉水'],
    amount: 8.5,
    status: 'refunded',
    cabinetId: 'GG-2024-0386',
  },
  {
    id: '20240507151820',
    time: '2024-05-07 15:18',
    items: ['农夫山泉 550ml', '百事可乐 330ml', '乐事薯片'],
    amount: 18.0,
    status: 'completed',
    cabinetId: 'GG-2024-0512',
  },
];

const statusConfig = {
  completed: { text: '已完成', color: 'text-success', bg: 'bg-success/10', border: 'border-success/20' },
  refunded: { text: '已退款', color: 'text-text-tertiary', bg: 'bg-muted', border: 'border-border' },
  pending: { text: '处理中', color: 'text-warning', bg: 'bg-warning/10', border: 'border-warning/20' },
};

export function OrderPage() {
  const [orders] = useState<Order[]>(mockOrders);
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);

  if (selectedOrder) {
    return (
      <div className="h-full overflow-y-auto bg-background">
        {/* 返回按钮 */}
        <div className="bg-card p-4 border-b border-border shadow-[0_2px_8px_0_rgba(0,0,0,0.04)]">
          <button
            onClick={() => setSelectedOrder(null)}
            className="flex items-center gap-2 text-primary text-[14px] font-medium hover:text-[#4096FF] transition-colors duration-200"
          >
            <ChevronLeft size={18} />
            返回订单列表
          </button>
        </div>

        {/* 订单详情 */}
        <div className="bg-card p-4 mb-4 shadow-[0_2px_8px_0_rgba(0,0,0,0.06)]">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-[15px] font-semibold text-text-primary">订单详情</h3>
            <span
              className={`px-3 py-1 rounded-full text-xs font-medium border ${statusConfig[selectedOrder.status].bg} ${statusConfig[selectedOrder.status].color} ${statusConfig[selectedOrder.status].border}`}
            >
              {statusConfig[selectedOrder.status].text}
            </span>
          </div>
          <div className="space-y-3 text-[14px]">
            <div className="flex justify-between items-center">
              <span className="text-text-secondary">订单号</span>
              <span className="text-text-primary font-medium">{selectedOrder.id}</span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-text-secondary">下单时间</span>
              <span className="text-text-primary">{selectedOrder.time}</span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-text-secondary">货柜编号</span>
              <span className="text-text-primary">{selectedOrder.cabinetId}</span>
            </div>
          </div>
        </div>

        {/* 商品列表 */}
        <div className="bg-card p-4 mb-4 shadow-[0_2px_8px_0_rgba(0,0,0,0.06)]">
          <h3 className="text-[15px] font-semibold text-text-primary mb-3">商品清单</h3>
          <div className="space-y-3">
            {selectedOrder.items.map((item, index) => (
              <div key={index} className="flex items-center gap-3">
                <div className="w-12 h-12 bg-muted rounded-xl flex items-center justify-center text-2xl shadow-sm">
                  🥤
                </div>
                <div className="flex-1">
                  <div className="text-[14px] text-text-primary font-medium">{item}</div>
                  <div className="text-xs text-text-tertiary mt-0.5">× 1</div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* 金额信息 */}
        <div className="bg-card p-4 shadow-[0_2px_8px_0_rgba(0,0,0,0.06)]">
          <div className="space-y-3 text-[14px]">
            <div className="flex justify-between items-center">
              <span className="text-text-secondary">商品总额</span>
              <span className="text-text-primary">¥{selectedOrder.amount.toFixed(2)}</span>
            </div>
            <div className="flex justify-between items-center pt-3 border-t border-border">
              <span className="text-text-primary font-medium">实付金额</span>
              <span className="text-xl text-destructive font-semibold">
                ¥{selectedOrder.amount.toFixed(2)}
              </span>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="h-full overflow-y-auto bg-background">
      {/* 统计信息 */}
      <div className="bg-card p-5 mb-4 shadow-[0_2px_8px_0_rgba(0,0,0,0.06)]">
        <div className="flex justify-around text-center">
          <div>
            <div className="text-2xl font-semibold text-text-primary mb-1">{orders.length}</div>
            <div className="text-xs text-text-tertiary">总订单</div>
          </div>
          <div className="w-px bg-border"></div>
          <div>
            <div className="text-2xl font-semibold text-success mb-1">
              {orders.filter((o) => o.status === 'completed').length}
            </div>
            <div className="text-xs text-text-tertiary">已完成</div>
          </div>
          <div className="w-px bg-border"></div>
          <div>
            <div className="text-2xl font-semibold text-text-tertiary mb-1">
              {orders.filter((o) => o.status === 'refunded').length}
            </div>
            <div className="text-xs text-text-tertiary">已退款</div>
          </div>
        </div>
      </div>

      {/* 订单列表 */}
      <div className="px-4 space-y-3 pb-4">
        {orders.map((order) => (
          <div
            key={order.id}
            onClick={() => setSelectedOrder(order)}
            className="bg-card p-4 rounded-xl cursor-pointer hover:shadow-lg transition-all duration-200 shadow-[0_2px_8px_0_rgba(0,0,0,0.06)] active:scale-98"
          >
            <div className="flex items-center justify-between mb-3">
              <div className="flex items-center gap-2">
                <div className="w-8 h-8 rounded-lg bg-primary/10 flex items-center justify-center">
                  <Package size={16} className="text-primary" />
                </div>
                <span className="text-xs text-text-tertiary">{order.time}</span>
              </div>
              <span
                className={`px-2.5 py-0.5 rounded-full text-xs font-medium border ${statusConfig[order.status].bg} ${statusConfig[order.status].color} ${statusConfig[order.status].border}`}
              >
                {statusConfig[order.status].text}
              </span>
            </div>
            <div className="mb-3">
              <div className="text-[14px] text-text-primary font-medium mb-1.5">
                {order.items.join('、')}
              </div>
              <div className="text-xs text-text-secondary">
                货柜编号：{order.cabinetId}
              </div>
            </div>
            <div className="flex items-center justify-between pt-3 border-t border-border">
              <span className="text-xs text-text-tertiary">订单号：{order.id}</span>
              <span className="text-sm text-destructive font-semibold">¥{order.amount.toFixed(2)}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
