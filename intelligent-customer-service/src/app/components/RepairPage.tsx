import { useState } from 'react';
import { CheckCircle2 } from 'lucide-react';

const issueTypes = [
  '货柜门无法打开',
  '商品卡住无法掉落',
  '扫码无反应',
  '触摸屏故障',
  '其他问题',
];

export function RepairPage() {
  const [selectedIssue, setSelectedIssue] = useState('');
  const [description, setDescription] = useState('');
  const [contact, setContact] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);

  const handleSubmit = () => {
    if (!selectedIssue || !description || !contact) {
      alert('请填写完整信息');
      return;
    }

    setIsSubmitting(true);
    setTimeout(() => {
      setIsSubmitting(false);
      setIsSuccess(true);
      setTimeout(() => {
        setSelectedIssue('');
        setDescription('');
        setContact('');
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
        <h3 className="text-lg font-semibold text-text-primary mb-2">提交成功！</h3>
        <p className="text-sm text-text-secondary">我们将在10分钟内联系您</p>
      </div>
    );
  }

  return (
    <div className="h-full overflow-y-auto bg-background">
      {/* 货柜信息 */}
      <div className="bg-card p-4 mb-4 shadow-[0_2px_8px_0_rgba(0,0,0,0.06)]">
        <h3 className="text-[15px] font-semibold text-text-primary mb-3">当前货柜信息</h3>
        <div className="space-y-2.5 text-[14px]">
          <div className="flex justify-between items-center">
            <span className="text-text-secondary">货柜编号</span>
            <span className="text-text-primary font-medium">GG-2024-0512</span>
          </div>
          <div className="flex justify-between items-start">
            <span className="text-text-secondary">所在位置</span>
            <span className="text-text-primary text-right max-w-[200px]">北京朝阳区建国路88号</span>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-text-secondary">最近订单</span>
            <span className="text-primary font-medium">20240510143256</span>
          </div>
        </div>
      </div>

      {/* 故障类型 */}
      <div className="bg-card p-4 mb-4 shadow-[0_2px_8px_0_rgba(0,0,0,0.06)]">
        <h3 className="text-[15px] font-semibold text-text-primary mb-3">故障类型</h3>
        <div className="space-y-2.5">
          {issueTypes.map((issue) => (
            <button
              key={issue}
              onClick={() => setSelectedIssue(issue)}
              className={`w-full px-4 py-3 rounded-xl text-left text-[14px] transition-all duration-200 ${
                selectedIssue === issue
                  ? 'bg-primary text-primary-foreground shadow-[0_4px_12px_0_rgba(22,119,255,0.2)]'
                  : 'bg-secondary text-text-primary hover:bg-muted border border-border'
              }`}
            >
              {issue}
            </button>
          ))}
        </div>
      </div>

      {/* 问题描述 */}
      <div className="bg-card p-4 mb-4 shadow-[0_2px_8px_0_rgba(0,0,0,0.06)]">
        <h3 className="text-[15px] font-semibold text-text-primary mb-3">问题描述</h3>
        <textarea
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder="请详细描述您遇到的问题，以便我们更好地为您服务..."
          rows={4}
          className="w-full px-4 py-3 bg-input-background border border-border rounded-xl text-[14px] text-text-primary placeholder:text-text-tertiary outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary resize-none transition-all duration-200"
        />
      </div>

      {/* 联系方式 */}
      <div className="bg-card p-4 mb-4 shadow-[0_2px_8px_0_rgba(0,0,0,0.06)]">
        <h3 className="text-[15px] font-semibold text-text-primary mb-3">联系方式</h3>
        <input
          type="tel"
          value={contact}
          onChange={(e) => setContact(e.target.value)}
          placeholder="请输入手机号码"
          className="w-full px-4 py-3 bg-input-background border border-border rounded-xl text-[14px] text-text-primary placeholder:text-text-tertiary outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all duration-200"
        />
      </div>

      {/* 提交按钮 */}
      <div className="px-4 pb-4">
        <button
          onClick={handleSubmit}
          disabled={isSubmitting}
          className="w-full h-12 bg-primary text-primary-foreground rounded-2xl text-[15px] font-medium hover:bg-[#4096FF] transition-all duration-200 active:scale-98 shadow-[0_4px_12px_0_rgba(22,119,255,0.3)] disabled:opacity-50 disabled:cursor-not-allowed disabled:shadow-none"
        >
          {isSubmitting ? (
            <span className="flex items-center justify-center gap-2">
              <span className="w-4 h-4 border-2 border-primary-foreground/30 border-t-primary-foreground rounded-full animate-spin"></span>
              提交中...
            </span>
          ) : (
            '提交报修'
          )}
        </button>
      </div>
    </div>
  );
}
