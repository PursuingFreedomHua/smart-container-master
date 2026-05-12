/**
 * 常见问题页（FAQ）
 * 功能：搜索框、分类折叠面板、联系客服入口
 * 后续对接：
 *   1. 列表接口：GET /api/faq/list?keyword=xxx
 *   2. 搜索接口：GET /api/faq/search?keyword=xxx
 *   3. 从服务器获取 FAQ 数据，支持关键词搜索
 */
const mock = require('../../utils/mock.js');

Page({
  data: {
    // FAQ 分类数据
    faqData: [],

    // 当前展开项的 key（格式：categoryIndex-itemIndex）
    // null 表示无展开项（手风琴模式）
    expandedKey: null,

    // 搜索关键词
    searchKeyword: '',

    // 过滤后的 FAQ 数据（搜索用）
    filteredFaqData: [],
  },

  onLoad() {
    // 加载 FAQ 数据
    // 后续对接：调用 API 获取 FAQ 列表
    const faqData = mock.faqData;
    this.setData({
      faqData: faqData,
      filteredFaqData: faqData,
    });
  },

  /**
   * 切换折叠面板展开/收起
   * @param {object} e - 事件对象
   */
  handleToggleExpand(e) {
    const { categoryIndex, itemIndex } = e.currentTarget.dataset;
    const key = categoryIndex + '-' + itemIndex;
    // 手风琴模式：点击已展开的则收起，否则展开新的
    this.setData({
      expandedKey: this.data.expandedKey === key ? null : key,
    });
  },

  /**
   * 搜索输入变化
   * @param {object} e - 事件对象
   */
  handleSearchInput(e) {
    const keyword = e.detail.value;
    this.setData({ searchKeyword: keyword });

    if (!keyword.trim()) {
      // 无关键词时显示全部
      this.setData({ filteredFaqData: this.data.faqData });
      return;
    }

    // 按关键词过滤（匹配问题或答案）
    const filtered = this.data.faqData
      .map(function (category) {
        const matchedItems = category.items.filter(function (item) {
          return (
            item.question.indexOf(keyword) !== -1 ||
            item.answer.indexOf(keyword) !== -1
          );
        });
        if (matchedItems.length > 0) {
          return {
            category: category.category,
            items: matchedItems,
          };
        }
        return null;
      })
      .filter(function (cat) {
        return cat !== null;
      });

    this.setData({
      filteredFaqData: filtered,
    });
  },

  /**
   * 联系人工客服
   * 后续对接：跳转到客服聊天页或拨打客服电话
   */
  handleContactService() {
    wx.switchTab({
      url: '/pages/chat/chat',
    });
  },
});
