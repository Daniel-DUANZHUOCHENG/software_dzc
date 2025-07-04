"use strict";
Page({
    data: {
        meetingInfo: {
            id: '1',
            title: '技术交流会',
            description: '暨系统测评技术赋能基础软硬件产业高质量发展论坛',
            date: '2023.08',
            location: '云南昌宁'
        },
        showRegistrationForm: false,
        formData: {
            company: '',
            name: '',
            gender: 'male',
            phone: '',
            email: '',
            arrivalMethod: '',
            arrivalTime: '',
            remarks: ''
        },
        arrivalMethods: ['飞机', '高铁', '自驾', '其他'],
        showModal: false,
        selectedDate: '',
        meetingImage: '',
        meetingId: ''
    },
    onLoad(options) {
        const { id } = options;
        console.log('会议ID:', id);
        // 根据ID设置会议信息
        if (id) {
            const meetings = {
                '1': {
                    id: '1',
                    title: '技术交流会',
                    description: '暨系统测评技术赋能基础软硬件产业高质量发展论坛',
                    date: '2023.08',
                    location: '云南昌宁'
                },
                '2': {
                    id: '2',
                    title: '2023年会员大会',
                    description: '暨系统测评技术赋能基础软硬件产业高质量发展论坛',
                    date: '2023.09',
                    location: '北京'
                },
                '3': {
                    id: '3',
                    title: '软件质量管理研讨会',
                    description: '暨系统测评技术赋能基础软硬件产业高质量发展论坛',
                    date: '2023.10',
                    location: '深圳'
                }
            };
            if (meetings[id]) {
                this.setData({
                    meetingInfo: meetings[id],
                    meetingId: id,
                    meetingImage: `/assets/meetings/meeting${id}.png`
                });
            }
            else {
                wx.showToast({
                    title: '未找到会议信息',
                    icon: 'none'
                });
            }
        }
    },
    // 返回上一页
    navigateBack() {
        wx.navigateBack({
            delta: 1
        });
    },
    // 显示注册表单
    showRegistrationForm() {
        this.setData({
            showModal: true
        });
    },
    // 隐藏注册表单
    hideRegistrationForm() {
        this.setData({
            showModal: false
        });
    },
    // 阻止表单点击事件冒泡
    catchFormTap() {
        return;
    },
    // 性别选择改变
    onGenderChange(e) {
        this.setData({
            ['formData.gender']: e.detail.value
        });
    },
    // 到达方式改变
    onArrivalMethodChange(e) {
        this.setData({
            ['formData.arrivalMethod']: this.data.arrivalMethods[parseInt(e.detail.value)]
        });
    },
    // 到达时间改变
    onArrivalTimeChange(e) {
        this.setData({
            ['formData.arrivalTime']: e.detail.value
        });
    },
    // 分享到朋友圈
    onShareTimeline() {
        return {
            title: this.data.meetingInfo.title,
            query: `id=${this.data.meetingInfo.id}`,
            imageUrl: this.data.meetingImage
        };
    },
    // 分享给朋友
    onShareAppMessage() {
        return {
            title: this.data.meetingInfo.title,
            path: `/pages/meeting-detail/meeting-detail?id=${this.data.meetingInfo.id}`,
            imageUrl: this.data.meetingImage
        };
    },
    // 日期选择器变化事件
    bindDateChange(e) {
        this.setData({
            selectedDate: e.detail.value
        });
    },
    // 提交表单
    async submitForm(e) {
        const formData = e.detail.value;
        // 表单验证
        if (!formData.company) {
            wx.showToast({ title: '请输入单位名称', icon: 'none' });
            return;
        }
        if (!formData.name) {
            wx.showToast({ title: '请输入姓名', icon: 'none' });
            return;
        }
        if (!formData.phone) {
            wx.showToast({ title: '请输入手机号码', icon: 'none' });
            return;
        }
        if (!/^1\d{10}$/.test(formData.phone)) {
            wx.showToast({ title: '请输入正确的手机号码', icon: 'none' });
            return;
        }
        if (!formData.email) {
            wx.showToast({ title: '请输入电子邮箱', icon: 'none' });
            return;
        }
        if (!/^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(formData.email)) {
            wx.showToast({ title: '请输入正确的电子邮箱', icon: 'none' });
            return;
        }
        if (!formData.arrivalMethod) {
            wx.showToast({ title: '请输入到达方式', icon: 'none' });
            return;
        }
        if (!formData.arrivalTime) {
            wx.showToast({ title: '请选择到达时间', icon: 'none' });
            return;
        }
        try {
            wx.showLoading({ title: '提交中...' });
            // 调用云函数添加回执
            const db = wx.cloud.database();
            await db.collection('meeting_registrations').add({
                data: {
                    ...formData,
                    meetingId: this.data.meetingId,
                    createTime: db.serverDate(),
                    status: 'pending' // 状态：pending-待审核, approved-已通过, rejected-已拒绝
                }
            });
            wx.hideLoading();
            wx.showToast({
                title: '提交成功',
                icon: 'success'
            });
            // 关闭弹窗
            this.hideRegistrationForm();
        }
        catch (error) {
            console.error('提交回执失败：', error);
            wx.hideLoading();
            wx.showToast({
                title: '提交失败，请重试',
                icon: 'none'
            });
        }
    }
});
