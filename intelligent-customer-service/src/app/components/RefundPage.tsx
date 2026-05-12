import { useState } from 'react';
import { CheckCircle2, AlertCircle } from 'lucide-react';

const refundReasons = [
  '支付成功但未出货',
  '商品已过期',
  '商品损坏',
  '误操作购买',
  '其他原因',
];

export function RefundPage() {
  const [orderId, setOrderId] = useState('');
  const [selectedReason, setSelectedReason] = useState('');
  const [amount, setAmount] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);

  const handleSubmit = () => {
    if (!orderId || !selectedReason || !amount) {
      alert('请填写完整信息');
      return;
    }

    setIsSubmitting(true);
    setTimeout(() => {
      setIsSubmitting(false);
      setIsSuccess(true);
      setTimeout(() => {
        setOrderId('');
        setSelectedReason('');
        setAmount('');
        setIsSuccess(false);
      }, 2000);
    }, 1500);
  };

  if (isSuccess) {
    return (
      <div className="flex flex-col items-center justify-center h-full bg-card">
        <div className="w-20 h-20 rounded-full bg-success/10 flex items-center justify-center mb-4">
          <CheckCircle2 size={48} className="text-success" />
        </div>
        <h3 className="text-lg font-semibold text-text-primary mb-2">退款申请已提交！</h3>
        <p className="text-sm text-text-secondary">预计1-3个工作日到账</p>
      </div>
    );
  }

  return (
    <div className="h-full overflow-y-auto bg-background">
      {/* 提示信息 */}
      <div className="bg-warning/10 border border-warning/20 p-3.5 mx-4 my-4 rounded-xl shadow-sm">
        <div className="flex gap-2.5">
          <AlertCircle size={18} className="text-warning flex-shrink-0 mt-0.5" />
          <div className="flex-1 text-[13px] text-text-secondary leading-5">
            <p className="font-medium text-text-primary mb-1.5">温馨提示</p>
            <p className="mb-1">• 退款将原路返回至支付账户</p>
            <p className="mb-1">• 到账时间1-3个工作日</p>
            <p>• 如有疑问请联系客服</p>
          </div>
        </div>
      </div>

      {/* 订单号 */}
      <div className="bg-card p-4 mb-4 shadow-[0_2px_8px_0_rgba(0,0,0,0.06)]">
        <h3 className="text-[15px] font-semibold text-text-primary mb-3">订单号</h3>
        <input
          type="text"
          value={orderId}
          onChange={(e) => setOrderId(e.target.value)}
          placeholder="请输入订单号（如：20240510143256）"
          className="w-full px-4 py-3 bg-input-background border border-border rounded-xl text-[14px] text-text-primary placeholder:text-text-tertiary outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all duration-200"
        />
        <p className="text-xs text-text-tertiary mt-2.5">
          可在"订单查询"页面查看订单号
        </p>
      </div>

      {/* 退款原因 */}
      <div className="bg-card p-4 mb-4 shadow-[0_2px_8px_0_rgba(0,0,0,0.06)]">
        <h3 className="text-[15px] font-semibold text-text-primary mb-3">退款原因</h3>
        <div className="space-y-2.5">
          {refundReasons.map((reason) => (
            <button
              key={reason}
              onClick={() => setSelectedReason(reason)}
              className={`w-full px-4 py-3 rounded-xl text-left text-[14px] transition-all duration-200 ${
                selectedReason === reason
                  ? 'bg-destructive text-destructive-foreground shadow-[0_4px_12px_0_rgba(255,77,79,0.2)]'
                  : 'bg-secondary text-text-primary hover:bg-muted border border-border'
              }`}
            >
              {reason}
            </button>
          ))}
        </div>
      </div>

      {/* 退款金额 */}
      <div className="bg-card p-4 mb-4 shadow-[0_2px_8px_0_rgba(0,0,0,0.06)]">
        <h3 className="text-[15px] font-semibold text-text-primary mb-3">退款金额</h3>
        <div className="flex items-center gap-2 px-4 py-3 bg-input-background border border-border rounded-xl focus-within:ring-2 focus-within:ring-primary/20 focus-within:border-primary transition-all duration-200">
          <span className="text-lg text-text-primary font-medium">¥</span>
          <input
            type="number"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            placeholder="0.00"
            step="0.01"
            className="flex-1 bg-transparent text-[14px] text-text-primary placeholder:text-text-tertiary outline-none"
          />
        </div>
      </div>

      {/* 提交按钮 */}
      <div className="px-4 pb-4">
        <button
          onClick={handleSubmit}
          disabled={isSubmitting}
          className="w-full h-12 bg-destructive text-destructive-foreground rounded-2xl text-[15px] font-medium hover:bg-[#FF6B6D] transition-all duration-200 active:scale-98 shadow-[0_4px_12px_0_rgba(255,77,79,0.3)] disabled:opacity-50 disabled:cursor-not-allowed disabled:shadow-none"
        >
          {isSubmitting ? (
            <span className="flex items-center justify-center gap-2">
              <span className="w-4 h-4 border-2 border-destructive-foreground/30 border-t-destructive-foreground rounded-full animate-spin"></span>
              提交中...
            </span>
          ) : (
            '提交退款申请'
          )}
        </button>
      </div>
    </div>
  );
}
