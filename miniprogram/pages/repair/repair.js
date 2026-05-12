/**
 * 故障报修页
 * 功能：货柜信息展示、故障类型选择、问题描述、联系方式、提交报修
 * 后续对接：
 *   1. 提交接口：POST /api/repair/submit
 *   2. 请求参数：{ issueType, description, contact, cabinetId }
 *   3. 从扫码或设备编号获取 cabinetInfo
 */
const mock = require('../../utils/mock.js');

Page({
  data: {
    // 货柜信息
    cabinetInfo: mock.cabinetInfo,

    // 故障类型选项
    issueTypes: mock.issueTypes,

    // 当前选中的故障类型
    selectedIssue: '',

    // 问题描述
    description: '',

    // 联系方式（手机号）
    contact: '',

    // 提交中状态
    isSubmitting: false,

    // 提交成功状态
    isSuccess: false,
  },

  onLoad() {
    // 页面初始化
    // 后续对接：根据扫码参数或设备ID从服务器获取货柜信息
  },

  /**
   * 选择故障类型
   * @param {object} e - 事件对象
   */
  handleSelectIssue(e) {
    const issue = e.currentTarget.dataset.issue;
    this.setData({
      selectedIssue: issue,
    });
  },

  /**
   * 问题描述输入变化
   * @param {object} e - 事件对象
   */
  handleDescInput(e) {
    this.setData({
      description: e.detail.value,
    });
  },

  /**
   * 联系方式输入变化
   * @param {object} e - 事件对象
   */
  handleContactInput(e) {
    this.setData({
      contact: e.detail.value,
    });
  },

  /**
   * 提交报修
   * 表单验证：所有字段必填
   */
  handleSubmit() {
    const { selectedIssue, description, contact } = this.data;

    // 表单验证
    if (!selectedIssue) {
      wx.showToast({ title: '请选择故障类型', icon: 'none' });
      return;
    }
    if (!description.trim()) {
      wx.showToast({ title: '请填写问题描述', icon: 'none' });
      return;
    }
    if (!contact.trim()) {
      wx.showToast({ title: '请输入联系方式', icon: 'none' });
      return;
    }

    // 手机号简单验证
    if (!/^1[3-9]\d{9}$/.test(contact.trim())) {
      wx.showToast({ title: '请输入正确的手机号码', icon: 'none' });
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

      // 2秒后重置表单并恢复默认状态
      this._successTimer = setTimeout(() => {
        this.setData({
          selectedIssue: '',
          description: '',
          contact: '',
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
