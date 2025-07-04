"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.throttle = exports.debounce = exports.hideLoading = exports.showLoading = exports.showError = exports.showSuccess = exports.isValidEmail = exports.isValidPhone = exports.formatTime = void 0;
// 格式化时间
const formatTime = (date) => {
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const hour = date.getHours();
    const minute = date.getMinutes();
    const second = date.getSeconds();
    return ([year, month, day].map(formatNumber).join('-') +
        ' ' +
        [hour, minute, second].map(formatNumber).join(':'));
};
exports.formatTime = formatTime;
// 格式化数字
const formatNumber = (n) => {
    const s = n.toString();
    return s[1] ? s : '0' + s;
};
// 检查手机号格式
const isValidPhone = (phone) => {
    return /^1[3-9]\d{9}$/.test(phone);
};
exports.isValidPhone = isValidPhone;
// 检查邮箱格式
const isValidEmail = (email) => {
    return /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(email);
};
exports.isValidEmail = isValidEmail;
// 显示成功提示
const showSuccess = (message) => {
    wx.showToast({
        title: message,
        icon: 'success'
    });
};
exports.showSuccess = showSuccess;
// 显示错误提示
const showError = (message) => {
    wx.showToast({
        title: message,
        icon: 'none'
    });
};
exports.showError = showError;
// 显示加载提示
const showLoading = (message = '加载中...') => {
    wx.showLoading({
        title: message,
        mask: true
    });
};
exports.showLoading = showLoading;
// 隐藏加载提示
const hideLoading = () => {
    wx.hideLoading();
};
exports.hideLoading = hideLoading;
// 防抖函数
const debounce = (func, wait = 500) => {
    let timeout;
    return function (...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => {
            func.apply(this, args);
        }, wait);
    };
};
exports.debounce = debounce;
// 节流函数
const throttle = (func, wait = 500) => {
    let previous = 0;
    return function (...args) {
        const now = Date.now();
        if (now - previous > wait) {
            func.apply(this, args);
            previous = now;
        }
    };
};
exports.throttle = throttle;
