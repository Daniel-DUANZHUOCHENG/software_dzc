// 格式化时间
export const formatTime = (date: Date): string => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()

  return (
    [year, month, day].map(formatNumber).join('-') +
    ' ' +
    [hour, minute, second].map(formatNumber).join(':')
  )
}

// 格式化数字
const formatNumber = (n: number): string => {
  const s = n.toString()
  return s[1] ? s : '0' + s
}

// 检查手机号格式
export const isValidPhone = (phone: string): boolean => {
  return /^1[3-9]\d{9}$/.test(phone)
}

// 检查邮箱格式
export const isValidEmail = (email: string): boolean => {
  return /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(email)
}

// 显示成功提示
export const showSuccess = (message: string): void => {
  wx.showToast({
    title: message,
    icon: 'success'
  })
}

// 显示错误提示
export const showError = (message: string): void => {
  wx.showToast({
    title: message,
    icon: 'none'
  })
}

// 显示加载提示
export const showLoading = (message: string = '加载中...'): void => {
  wx.showLoading({
    title: message,
    mask: true
  })
}

// 隐藏加载提示
export const hideLoading = (): void => {
  wx.hideLoading()
}

// 防抖函数
export const debounce = (func: Function, wait: number = 500): Function => {
  let timeout: number
  return function(this: any, ...args: any[]) {
    clearTimeout(timeout)
    timeout = setTimeout(() => {
      func.apply(this, args)
    }, wait)
  }
}

// 节流函数
export const throttle = (func: Function, wait: number = 500): Function => {
  let previous = 0
  return function(this: any, ...args: any[]) {
    const now = Date.now()
    if (now - previous > wait) {
      func.apply(this, args)
      previous = now
    }
  }
}
