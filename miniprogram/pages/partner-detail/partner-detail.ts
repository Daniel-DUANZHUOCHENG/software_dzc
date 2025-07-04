interface IPartnerInfo {
  id: string;
  name: string;
  logo: string;
  website: string;
  intro: string;
  history: Array<{
    year: string;
    events: string[];
  }>;
  contact: {
    phone: string;
    addresses: Array<{
      name: string;
      address: string;
    }>;
  };
}

type PartnersType = {
  [key: string]: IPartnerInfo;
}

Page({
  data: {
    partnerId: '',
    partnerInfo: {} as IPartnerInfo,
    partners: {
      '1': {
        id: '1',
        name: '道普信息技术有限公司',
        logo: '/assets/partners/partner1.png',
        website: 'https://www.daopu.com',
        intro: '道普信息技术有限公司是一家专注于信息化第三方风险管控的机构，致力于帮助客户全面控制信息化风险。公司营销总部位于北京，管理总部位于济南，在华北、西北、西南、华南、华东、青岛、安徽、烟台等地设有分部。公司作为山东省计算中心（国家超级计算济南中心）在软件工程、信息安全等多领域科研成果应用与服务载体，以多年的科研成果积累和雄厚的技术能力为基础，面向社会提供信息化战略方向风险、项目建设过程风险、信息安全风险等相关的检测、咨询服务，致力于让客户的信息化更规范更安全。',
        history: [
          {
            year: '2002',
            events: ['软件评测中心通过中国实验室合格评定委员会的认可']
          },
          {
            year: '2016',
            events: ['由山东省计算中心（国家超级计算济南中心）所属山东省软件评测中心转化成立']
          }
        ],
        contact: {
          phone: '',
          addresses: [
            {
              name: '北京总部',
              address: '北京市海淀区翠微路12号 新华联国际5-1801'
            },
            {
              name: '济南总部',
              address: ''
            }
          ]
        }
      },
      '2': {
        id: '2',
        name: '上海计算机软件技术开发中心',
        logo: '/assets/partners/partner2.png',
        website: 'https://www.ssc.sh.cn',
        intro: '上海计算机软件技术开发中心（简称：上海软件中心）于1984年由原国家科委批准成立，是上海科学院直属事业单位。上海计算机软件技术开发中心长期致力于软件技术标准研究和软件应用技术研究，通过技术服务和成果应用推动产业发展，逐步形成了"服务行业，发展产业"的核心理念。',
        history: [
          {
            year: '1984',
            events: ['由原国家科委批准成立']
          },
          {
            year: '近年来',
            events: [
              '软件中心紧紧围绕上海具有全球影响力的科技创新中心建设战略目标',
              '对标上海城市数字化转型，持续推进应用技术创新体系建设'
            ]
          }
        ],
        contact: {
          phone: '',
          addresses: []
        }
      },
      '3': {
        id: '3',
        name: '北方实验室（沈阳）股份有限公司',
        logo: '/assets/partners/partner3.png',
        website: 'https://www.north-lab.cn',
        intro: '北方实验室（沈阳）股份有限公司成立于2003年8月，是一家以网络安全检测评估为主营业务的网络安全服务机构。公司为政府部门、央企国企、军工单位、大型企业、中小企业公共服务示范平台、国家高新技术企业、网约车、网游、直播等1000多个行业的7万多个客户、提供产品安全检测、系统安全评估、网络安全咨询等服务。',
        history: [
          {
            year: '2003',
            events: ['公司成立']
          },
          {
            year: '2011',
            events: ['获得国家公安部授权的涉密信息系统检测资质']
          }
        ],
        contact: {
          phone: '400-664-5588',
          addresses: [
            {
              name: 'IT产业园',
              address: '沈阳市浑南区文溯街8-1号'
            }
          ]
        }
      }
    } as PartnersType
  },

  onLoad(options) {
    const { id } = options;
    if (id && this.data.partners[id]) {
      this.setData({
        partnerId: id,
        partnerInfo: this.data.partners[id]
      });
    }
  },

  // 返回上一页
  navigateBack() {
    wx.navigateBack({
      delta: 1
    });
  },

  // 跳转到合作伙伴官网
  navigateToWebsite() {
    const { website } = this.data.partnerInfo;
    if (website) {
      wx.navigateTo({
        url: `/pages/web-view/web-view?url=${encodeURIComponent(website)}`
      });
    }
  }
}); 