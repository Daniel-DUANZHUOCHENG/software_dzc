/// <reference path="./types/index.d.ts" />

interface IAppOption {
  globalData: {
    userInfo?: WechatMiniprogram.UserInfo,
  }
  userInfoReadyCallback?: WechatMiniprogram.GetUserInfoSuccessCallback,
}

// 声明模块
declare module 'miniprogram-api-promise' {
  const promisifyAll: any;
  export { promisifyAll };
} 