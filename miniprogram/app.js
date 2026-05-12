/**
 * 智能货柜客服小程序 - 应用入口
 * 功能：全局数据管理和生命周期
 */
App({
  globalData: {
    // 用户信息（后续对接微信登录后替换）
    userInfo: null,
    // 系统信息
    systemInfo: null,
    // API 基础地址（后续对接真实后端后替换）
    apiBaseUrl: 'https://api.example.com',
  },

  onLaunch() {
    // 获取系统信息
    this.globalData.systemInfo = wx.getSystemInfoSync();

    // 检查用户登录状态（后续对接真实登录逻辑后启用）
    // this.checkLoginStatus();

    console.log('智能货柜客服小程序启动成功');
  },

  /**
   * 检查登录状态（后续对接真实登录逻辑后启用）
   */
  // checkLoginStatus() {
  //   const token = wx.getStorageSync('token');
  //   if (!token) {
  //     // 跳转到登录页
  //     wx.navigateTo({ url: '/pages/login/login' });
  //   }
  // },
});
