/**
 * 智能货柜客服小程序 - 模拟数据
 * 功能：提供所有页面的静态模拟数据
 * 后续对接真实后端 API 后，将此文件中的函数替换为 wx.request 调用
 */

// ==================== 客服聊天页模拟数据 ====================

/**
 * 初始消息列表（客服欢迎语）
 * 后续对接：打开聊天页时从服务器获取历史消息
 */
function getInitialMessages() {
  const now = formatTime(new Date());
  return [{
    id: '1',
    type: 'bot', // 消息类型：'user' 用户消息, 'bot' 客服消息
    content: '您好！我是智能货柜客服助手，有什么可以帮您？',
    time: now,
  }];
}

/**
 * 快捷问题列表
 * 后续对接：从服务器配置中获取快捷问题列表
 */
const quickQuestions = [
  '货柜门打不开',
  '商品卡住了',
  '扣款但没出货',
  '查询订单',
];

/**
 * 自动回复映射表
 * 后续对接：替换为真实的客服 AI 接口调用
 */
const autoReplies = {
  '货柜门打不开': '您好，货柜门打不开可能是以下原因：\n1. 请确认支付是否成功\n2. 请稍等3-5秒后再次尝试\n3. 如仍无法打开，请点击"故障报修"联系我们',
  '商品卡住了': '很抱歉给您带来不便。请点击底部"故障报修"填写详细信息，我们将在10分钟内为您处理退款。',
  '扣款但没出货': '非常抱歉！请点击底部"退款申请"，填写订单信息后我们将立即为您退款。',
  '查询订单': '请点击底部"订单查询"即可查看您的所有订单记录。',
};

/**
 * 默认回复（无匹配时）
 */
const defaultReply = '感谢您的咨询，人工客服将在5分钟内回复您。';

// ==================== 货柜信息模拟数据 ====================

/**
 * 当前货柜信息
 * 后续对接：通过扫码或设备编号从服务器获取
 */
const cabinetInfo = {
  id: 'GG-2024-0512',
  location: '北京市朝阳区建国路88号SOHO现代城B座1层',
  recentOrder: '20240510143256',
};

// ==================== 故障报修页模拟数据 ====================

/**
 * 故障类型选项
 * 后续对接：从服务器配置中获取故障类型列表
 */
const issueTypes = [
  '货柜门无法打开',
  '商品卡住无法掉落',
  '扫码无反应',
  '触摸屏故障',
  '其他问题',
];

// ==================== 退款申请页模拟数据 ====================

/**
 * 退款原因选项
 * 后续对接：从服务器配置中获取退款原因列表
 */
const refundReasons = [
  '支付成功但未出货',
  '商品已过期',
  '商品损坏',
  '误操作购买',
  '其他原因',
];

// ==================== 订单查询页模拟数据 ====================

/**
 * 订单状态配置
 */
const statusConfig = {
  completed: {
    text: '已完成',
    color: '#52C41A',
    bg: 'rgba(82, 196, 26, 0.1)',
    borderColor: 'rgba(82, 196, 26, 0.2)',
  },
  refunded: {
    text: '已退款',
    color: '#86909C',
    bg: '#F2F3F5',
    borderColor: '#E5E6EB',
  },
  pending: {
    text: '处理中',
    color: '#FAAD14',
    bg: 'rgba(250, 173, 20, 0.1)',
    borderColor: 'rgba(250, 173, 20, 0.2)',
  },
};

/**
 * 模拟订单列表
 * 后续对接：从服务器获取用户订单列表（支持分页）
 */
const mockOrders = [{
  id: '20240510143256',
  time: '2024-05-10 14:32',
  items: ['可口可乐 330ml', '三只松鼠坚果'],
  amount: 15.5,
  status: 'completed',
  cabinetId: 'GG-2024-0512',
}, {
  id: '20240509092145',
  time: '2024-05-09 09:21',
  items: ['康师傅矿泉水 550ml'],
  amount: 3.0,
  status: 'completed',
  cabinetId: 'GG-2024-0512',
}, {
  id: '20240508183012',
  time: '2024-05-08 18:30',
  items: ['士力架巧克力', '怡宝矿泉水'],
  amount: 8.5,
  status: 'refunded',
  cabinetId: 'GG-2024-0386',
}, {
  id: '20240507151820',
  time: '2024-05-07 15:18',
  items: ['农夫山泉 550ml', '百事可乐 330ml', '乐事薯片'],
  amount: 18.0,
  status: 'completed',
  cabinetId: 'GG-2024-0512',
}, {
  id: '20240506123048',
  time: '2024-05-06 12:30',
  items: ['红牛维生素饮料 250ml'],
  amount: 6.0,
  status: 'pending',
  cabinetId: 'GG-2024-0386',
}];

// ==================== 常见问题页模拟数据 ====================

/**
 * FAQ 数据
 * 后续对接：从服务器获取 FAQ 列表
 */
const faqData = [{
  category: '支付相关',
  items: [{
    question: '支持哪些支付方式？',
    answer: '目前支持微信支付、支付宝、云闪付等主流支付方式。',
  }, {
    question: '支付后多久开门？',
    answer: '支付成功后3-5秒内自动开门，请稍作等待。如超过10秒未开门，请联系客服。',
  }, {
    question: '支付失败怎么办？',
    answer: '请检查网络连接和账户余额，如仍失败可尝试更换支付方式或联系客服。',
  }],
}, {
  category: '取货相关',
  items: [{
    question: '商品卡住了怎么办？',
    answer: '请点击"故障报修"填写信息，我们将在10分钟内处理并退款。',
  }, {
    question: '取到的商品有问题怎么办？',
    answer: '如商品过期或损坏，请拍照并点击"退款申请"，我们将第一时间为您退款。',
  }, {
    question: '能否取消订单？',
    answer: '支付成功后订单无法取消，如未成功取货可申请退款。',
  }],
}, {
  category: '退款相关',
  items: [{
    question: '退款多久到账？',
    answer: '退款将原路返回，一般1-3个工作日到账。',
  }, {
    question: '支付了但没出货能退款吗？',
    answer: '可以的，请点击"退款申请"填写订单信息即可。',
  }, {
    question: '退款失败怎么办？',
    answer: '请联系客服人工处理，提供订单号和支付凭证。',
  }],
}, {
  category: '其他问题',
  items: [{
    question: '货柜门打不开怎么办？',
    answer: '1. 确认支付是否成功\n2. 等待3-5秒后再次尝试\n3. 如仍无法打开请联系客服',
  }, {
    question: '如何查看历史订单？',
    answer: '点击底部"订单查询"即可查看所有订单记录。',
  }, {
    question: '客服工作时间是？',
    answer: '人工客服：9:00-21:00\n智能客服：24小时在线',
  }],
}];

// ==================== 工具函数 ====================

/**
 * 格式化时间为 HH:MM 格式
 */
function formatTime(date) {
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');
  return hours + ':' + minutes;
}

/**
 * 生成唯一 ID
 */
function generateId() {
  return Date.now().toString() + Math.random().toString(36).substr(2, 9);
}

module.exports = {
  getInitialMessages,
  quickQuestions,
  autoReplies,
  defaultReply,
  cabinetInfo,
  issueTypes,
  refundReasons,
  statusConfig,
  mockOrders,
  faqData,
  formatTime,
  generateId,
};
