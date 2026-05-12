/**
 * 订单查询页
 * 功能：订单统计、订单列表、订单详情
 * 后续对接：
 *   1. 列表接口：GET /api/orders?page=1&pageSize=20
 *   2. 详情接口：GET /api/orders/:id
 *   3. 支持下拉刷新和上拉加载更多
 */
const mock = require('../../utils/mock.js');

Page({
  data: {
    // 订单列表
    orders: [],

    // 订单状态配置映射
    statusConfig: mock.statusConfig,

    // 当前选中的订单（null 表示列表视图，有值表示详情视图）
    selectedOrder: null,

    // 统计数据
    stats: {
      total: 0,
      completed: 0,
      refunded: 0,
    },
  },

  onLoad() {
    // 加载订单数据
    // 后续对接：调用 API 获取订单列表
    this.loadOrders();
  },

  /**
   * 加载订单数据
   */
  loadOrders() {
    // 预处理订单数据：将 items 数组转为顿号分隔文本，amount 转为两位小数文本
    const orders = mock.mockOrders.map(function (order) {
      return Object.assign({}, order, {
        itemsText: order.items.join('、'),
        amountText: order.amount.toFixed(2),
      });
    });

    const stats = {
      total: orders.length,
      completed: orders.filter(function (o) { return o.status === 'completed'; }).length,
      refunded: orders.filter(function (o) { return o.status === 'refunded'; }).length,
    };

    this.setData({
      orders: orders,
      stats: stats,
    });
  },

  /**
   * 点击订单卡片，进入订单详情
   * @param {object} e - 事件对象
   */
  handleOrderClick(e) {
    const orderId = e.currentTarget.dataset.id;
    const order = this.data.orders.find((o) => o.id === orderId);
    if (order) {
      this.setData({
        selectedOrder: order,
      });
    }
  },

  /**
   * 返回订单列表
   */
  handleBackToList() {
    this.setData({
      selectedOrder: null,
    });
  },

  /**
   * 下拉刷新
   * 后续对接：重新请求 API 获取最新订单列表
   */
  onPullDownRefresh() {
    this.loadOrders();
    wx.stopPullDownRefresh();
  },
});
