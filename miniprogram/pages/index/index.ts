// index.ts
// 获取应用实例
const app = getApp<IAppOption>()
const defaultAvatarUrl = 'https://mmbiz.qpic.cn/mmbiz/icTdbqWNOwNRna42FI242Lcia07jQodd2FJGIYQfG0LAJGFxM4FbnQP6yfMxBgJ0F3YRqJCJ1aPAK2dQagdusBZg/0'

interface IBanner {
  id: string;
  title: string;
  description: string;
  image: string;
  date: string;
  location: string;
}

interface IAssociationInfo {
  introduction: string;
  memberCount: number;
  yearCount: number;
  projectCount: number;
}

interface IPartner {
  id: string;
  name: string;
  logo: string;
}

interface IMeeting {
  id: string;
  title: string;
  image: string;
  date: string;
  location: string;
  status?: string;
}

interface IPageData {
  motto: string;
  userInfo: {
    avatarUrl: string;
    nickName: string;
  };
  hasUserInfo: boolean;
  canIUseGetUserProfile: boolean;
  canIUseNicknameComp: boolean;
  bannerList: Array<{
    id: string;
    title: string;
    description: string;
    image: string;
    date: string;
    location: string;
  }>;
  associationInfo: {
    memberCount: number;
    yearCount: number;
    projectCount: number;
  };
  partnerList: Array<{
    id: string;
    name: string;
    logo: string;
  }>;
  meetingList: IMeeting[];
  partners: IPartner[];
}

type IPageInstance = WechatMiniprogram.Page.Instance<
  IPageData,
  WechatMiniprogram.Page.CustomOption
>

Page<IPageData, IPageInstance>({
  data: {
    userInfo: {
      avatarUrl: defaultAvatarUrl,
      nickName: '',
    },
    hasUserInfo: false,
    canIUseGetUserProfile: wx.canIUse('getUserProfile'),
    canIUseNicknameComp: wx.canIUse('input.type.nickname'),
    bannerList: [
      { 
        id: '1', 
        title: '技术交流会',
        description: '暨系统测评技术赋能基础软硬件产业高质量发展论坛',
        image: '../../assets/banners/banner1.png',
        date: '2023.08',
        location: '云南昌宁'
      },
      { 
        id: '2', 
        title: '技术交流会',
        description: '暨系统测评技术赋能基础软硬件产业高质量发展论坛',
        image: '../../assets/banners/banner2.png',
        date: '2023.08',
        location: '云南昌宁'
      },
      { 
        id: '3', 
        title: '技术交流会',
        description: '暨系统测评技术赋能基础软硬件产业高质量发展论坛',
        image: '../../assets/banners/banner3.jpg',
        date: '2023.08',
        location: '云南昌宁'
      }
    ],
    associationInfo: {
      memberCount: 200,
      yearCount: 20,
      projectCount: 1000
    },
    partnerList: [
      { id: '1', name: '合作伙伴1', logo: '../../assets/partners/partner1.png' },
      { id: '2', name: '合作伙伴2', logo: '../../assets/partners/partner2.png' },
      { id: '3', name: '合作伙伴3', logo: '../../assets/partners/partner3.png' },
      { id: '4', name: '合作伙伴4', logo: '../../assets/partners/partner4.png' },
      { id: '5', name: '合作伙伴5', logo: '../../assets/partners/partner5.png' },
      { id: '6', name: '合作伙伴6', logo: '../../assets/partners/partner6.png' },
      { id: '7', name: '合作伙伴7', logo: '../../assets/partners/partner7.png' },
      { id: '8', name: '合作伙伴8', logo: '../../assets/partners/partner8.png' },
      { id: '9', name: '合作伙伴9', logo: '../../assets/partners/partner9.png' },
      { id: '10', name: '合作伙伴10', logo: '../../assets/partners/partner10.png' },
      { id: '11', name: '合作伙伴11', logo: '../../assets/partners/partner11.png' }
    ],
    meetingList: [],
    partners: []
  },

  // 事件处理函数
  bindViewTap() {
    wx.navigateTo({
      url: '../logs/logs',
    })
  },

  onChooseAvatar(e: any) {
    const { avatarUrl } = e.detail
    const { nickName } = this.data.userInfo
    this.setData({
      "userInfo.avatarUrl": avatarUrl,
      hasUserInfo: nickName && avatarUrl && avatarUrl !== defaultAvatarUrl,
    })
  },

  onInputChange(e: any) {
    const nickName = e.detail.value
    const { avatarUrl } = this.data.userInfo
    this.setData({
      "userInfo.nickName": nickName,
      hasUserInfo: nickName && avatarUrl && avatarUrl !== defaultAvatarUrl,
    })
  },

  getUserProfile() {
    // 推荐使用wx.getUserProfile获取用户信息，开发者每次通过该接口获取用户个人信息均需用户确认，开发者妥善保管用户快速填写的头像昵称，避免重复弹窗
    wx.getUserProfile({
      desc: '展示用户信息', // 声明获取用户个人信息后的用途，后续会展示在弹窗中，请谨慎填写
      success: (res) => {
        console.log(res)
        this.setData({
          userInfo: res.userInfo,
          hasUserInfo: true
        })
      }
    })
  },

  onLoad() {
    console.log('页面加载完成');
    this.loadBanners();
    this.loadPartners();
    this.loadMeetings();
  },

  // 加载轮播图数据
  loadBanners() {
    const mockBanners: IBanner[] = [
      {
        id: '1',
        title: '技术交流会',
        description: '暨系统测评技术赋能基础软硬件产业高质量发展论坛',
        image: '../../assets/banners/banner1.png',
        date: '2023.08',
        location: '云南昌宁'
      },
      {
        id: '2',
        title: '2023年会员大会',
        description: '暨系统测评技术赋能基础软硬件产业高质量发展论坛',
        image: '../../assets/banners/banner2.png',
        date: '2023.09',
        location: '北京'
      },
      {
        id: '3',
        title: '软件质量管理研讨会',
        description: '暨系统测评技术赋能基础软硬件产业高质量发展论坛',
        image: '../../assets/banners/banner3.jpg',
        date: '2023.10',
        location: '上海'
      }
    ];
    this.setData({ bannerList: mockBanners });
  },

  // 加载合作成员数据
  loadPartners() {
    const mockPartners: IPartner[] = [
      {
        id: '1',
        name: '道普信息',
        logo: '../../assets/partners/partner1.png'
      },
      {
        id: '2',
        name: '上海计算机',
        logo: '../../assets/partners/partner2.png'
      }
    ];
    this.setData({ partners: mockPartners });
  },

  // 加载会议数据
  loadMeetings() {
    const mockMeetings: IMeeting[] = [
      
    ];
    this.setData({ meetingList: mockMeetings });
  },

  // 跳转到会议详情页
  goToMeetingDetail(e: WechatMiniprogram.TouchEvent) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/meeting-detail/meeting-detail?id=${id}`
    });
  },

  // 跳转到合作伙伴详情页
  goToPartnerDetail(e: WechatMiniprogram.TouchEvent) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/partner-detail/partner-detail?id=${id}`
    });
  },

  // 跳转到会议列表页
  goToMeetingList() {
    wx.navigateTo({
      url: '/pages/meeting-list/meeting-list'
    });
  },

  // 跳转到报名页面
  onRegisterTap() {
    wx.navigateTo({
      url: '/pages/registration/registration'
    });
  },

  // 切换标签页
  switchTab(e: WechatMiniprogram.TouchEvent) {
    const { tab } = e.currentTarget.dataset;
    wx.switchTab({
      url: `/pages/${tab}/${tab}`
    });
  }
})
