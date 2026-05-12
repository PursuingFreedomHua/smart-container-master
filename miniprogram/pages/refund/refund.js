/**
 * 退款申请页
 * 功能：提示信息、订单号输入、退款原因选择、退款金额、提交申请
 * 后续对接：
 *   1. 提交接口：POST /api/refund/submit
 *   2. 请求参数：{ orderId, reason, amount }
 *   3. 响应：{ code, message, data: { refundId } }
 */
const mock = require('../../utils/mock.js');

Page({
  data: {
    // 订单号
    orderId: '',

    // 退款原因选项
    refundReasons: mock.refundReasons,

    // 当前选中的退款原因
    selectedReason: '',

    // 退款金额
    amount: '',

    // 提交中状态
    isSubmitting: false,

    // 提交成功状态
    isSuccess: false,
  },

  /**
   * 订单号输入变化
   * @param {object} e - 事件对象
   */
  handleOrderIdInput(e) {
    this.setData({
      orderId: e.detail.value,
    });
  },

  /**
   * 选择退款原因
   * @param {object} e - 事件对象
   */
  handleSelectReason(e) {
    const reason = e.currentTarget.dataset.reason;
    this.setData({
      selectedReason: reason,
    });
  },

  /**
   * 退款金额输入变化
   * @param {object} e - 事件对象
   */
  handleAmountInput(e) {
    this.setData({
      amount: e.detail.value,
    });
  },

  /**
   * 提交退款申请
   * 表单验证：所有字段必填
   */
  handleSubmit() {
    const { orderId, selectedReason, amount } = this.data;

    // 表单验证
    if (!orderId.trim()) {
      wx.showToast({ title: '请输入订单号', icon: 'none' });
      return;
    }
    if (!selectedReason) {
      wx.showToast({ title: '请选择退款原因', icon: 'none' });
      return;
    }
    if (!amount || parseFloat(amount) <= 0) {
      wx.showToast({ title: '请输入有效的退款金额', icon: 'none' });
      return;
    }

    // 设置提交中状态
    this.setData({ isSubmitting: true });

    // 模拟提交（1.5秒延迟）
    // 后续对接：替换为 wx.request POST 请求
    this._submitTimer = setTimeout(() => {
      this.setData({
        isSubmitting: false,
        isSuccess: true,
      });

      // 2秒后重置表单
      this._successTimer = setTimeout(() => {
        this.setData({
          orderId: '',
          selectedReason: '',
          amount: '',
          isSuccess: false,
        });
      }, 2000);
    }, 1500);
  },

  onUnload() {
    // 清除定时器
    if (this._submitTimer) clearTimeout(this._submitTimer);
    if (this._successTimer) clearTimeout(this._successTimer);
  },
});
