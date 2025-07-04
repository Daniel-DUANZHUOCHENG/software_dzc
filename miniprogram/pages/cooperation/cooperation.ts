interface CooperationType {
  id: string;
  title: string;
  description: string;
  icon: string;
  image: string;
  fullDescription: string;
  features: string[];
}

interface Partner {
  id: string;
  name: string;
  logo: string;
  type: string;
}

interface ContactInfo {
  email: string;
  phone: string;
  address: string;
}

Page({
  data: {
    cooperationTypes: [] as CooperationType[],
    partners: [] as Partner[],
    contactInfo: {
      email: 'contact@example.com',
      phone: '400-123-4567',
      address: '北京市海淀区中关村科技园'
    } as ContactInfo,
    showModal: false,
    currentCoop: {} as CooperationType
  },

  onLoad() {
    this.loadCooperationData();
  },

  // 加载合作数据
  loadCooperationData() {
    // TODO: 替换为实际的API调用
    const mockCoopTypes: CooperationType[] = [
      {
        id: '1',
        title: '技术合作',
        description: '共同开发创新技术解决方案',
        icon: '/assets/icons/tech-coop.png',
        image: '/assets/icons/tech-coop-detail.png',
        fullDescription: '我们提供全面的技术合作解决方案，包括但不限于技术研发、产品创新、知识产权保护等领域。通过强强联合，实现互利共赢。',
        features: [
          '联合技术研发',
          '专利共享',
          '人才交流',
          '资源互补'
        ]
      },
      {
        id: '2',
        title: '商务合作',
        description: '建立长期稳定的商业合作关系',
        icon: '/assets/icons/business-coop.png',
        image: '/assets/icons/business-coop-detail.png',
        fullDescription: '面向企业客户提供全方位的商务合作机会，包括市场拓展、渠道共享、品牌联合等多个维度的深度合作。',
        features: [
          '市场资源共享',
          '渠道合作',
          '品牌推广',
          '活动合办'
        ]
      }
    ];

    const mockPartners: Partner[] = [
      {
        id: '1',
        name: '腾讯科技',
        logo: '/assets/icons/partner1.png',
        type: '战略合作'
      },
      {
        id: '2',
        name: '阿里巴巴',
        logo: '/assets/icons/partner2.png',
        type: '技术合作'
      },
      {
        id: '3',
        name: '百度',
        logo: '/assets/icons/partner3.png',
        type: '生态合作'
      }
    ];

    this.setData({
      cooperationTypes: mockCoopTypes,
      partners: mockPartners
    });
  },

  // 显示合作详情
  showCoopDetail(e: WechatMiniprogram.TouchEvent) {
    const { id } = e.currentTarget.dataset;
    const currentCoop = this.data.cooperationTypes.find(item => item.id === id);
    
    if (currentCoop) {
      this.setData({
        showModal: true,
        currentCoop
      });
    }
  },

  // 关闭弹窗
  closeModal() {
    this.setData({
      showModal: false,
      currentCoop: {} as CooperationType
    });
  },

  // 阻止事件冒泡
  preventDefault() {
    return;
  },

  // 申请合作
  onApplyCooperation() {
    const { currentCoop } = this.data;
    wx.navigateTo({
      url: `/pages/cooperation-form/cooperation-form?type=${currentCoop.id}&title=${currentCoop.title}`
    });
  },

  // 联系我们
  onContactUs() {
    const { phone } = this.data.contactInfo;
    wx.makePhoneCall({
      phoneNumber: phone,
      fail() {
        wx.showToast({
          title: '拨打电话失败',
          icon: 'none'
        });
      }
    });
  },

  onShareAppMessage() {
    return {
      title: '诚邀合作，共创未来',
      path: '/pages/cooperation/cooperation',
      imageUrl: '/assets/icons/cooperation-share.png'
    };
  },

  // 跳转到会议详情页
  goToMeetingDetail(e: any) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/meeting-detail/meeting-detail?id=${id}`
    });
  },

  // 跳转到公益小程序
  goToPublicWelfare() {
    wx.showModal({
      title: '提示',
      content: '即将打开"云南故事农业科技有限公司"小程序',
      confirmText: '允许',
      cancelText: '取消',
      success(res) {
        if (res.confirm) {
          wx.navigateToMiniProgram({
            shortLink: '#小程序://云南故事农业科技有限公司/pn428yLd1Oj5dCw',
            success(res) {
              console.log('跳转成功');
            },
            fail(err) {
              console.error('跳转失败:', err);
              wx.showToast({
                title: '跳转失败，请稍后重试',
                icon: 'none',
                duration: 2000
              });
            }
          });
        }
      }
    });
  }
}); 