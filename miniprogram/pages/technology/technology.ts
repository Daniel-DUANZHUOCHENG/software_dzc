interface ICourse {
  id: string;
  title: string;
  provider: string;
  image: string;
  video: string;
  description: string;
  sections: {
    id: string;
    title: string;
    video: string;
  }[];
}

Page({
  data: {
    searchKeyword: '',
    courses: [
      {
        id: '1',
        title: '应用系统性能测试工具',
        provider: '迎风聚智',
        image: '/assets/tech/tech1.png',
        video: 'http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&bizid=1023&hy=SH&fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400',
        description: 'Leming WebRunner 由武汉迎风聚智科技有限公司研发，是一款用于自产环境的应用系统性能测试工具。Leming WebRunner深入了解企业数据库的应用现状，依据数据库国际与行业标准，用心满足数据库软硬件厂家与企业用户需求，不断揭开数据库的神秘面纱。',
        sections: [
          {
            id: '1-1',
            title: '应用系统性能测试工具',
            video: 'https://www.w3schools.com/html/movie.mp4'
          },
          {
            id: '1-2',
            title: '迎风聚智简介',
            video: 'http://vjs.zencdn.net/v/oceans.mp4'
          }
        ]
      },
      {
        id: '2',
        title: '数据库性能基准测试工具',
        provider: '迎风聚智',
        image: '/assets/tech/tech2.png',
        video: 'https://media.w3.org/2010/05/sintel/trailer.mp4',
        description: 'Leming TPC-E 是一款基于国际 TPC-E 基准的测试数据库在线事务处理 (OLTP) 性能的基准测试工具。该工具提供可视化的参数设置，测试者可灵活定义整个测试任务，测试以数据库为对象，通过被测数据库中进行股票交易的客户数量把控测试规模，执行过程严格遵守基准约束，运行状态全程监控，测试结束自动输出报告，结果反映数据库软件性能，同时也体现服务器硬件性能。',
        sections: [
          {
            id: '2-1',
            title: '数据库性能基准测试工具',
            video: 'https://media.w3.org/2010/05/sintel/trailer.mp4'
          },
          {
            id: '2-2',
            title: '迎风聚智简介',
            video: 'http://www.w3school.com.cn/example/html5/mov_bbb.mp4'
          }
        ]
      },
      {
        id: '3',
        title: '龙测自动化测试平台',
        provider: '苏州市龙测智能科技有限公司',
        image: '/assets/tech/tech3.png',
        video: 'http://vjs.zencdn.net/v/oceans.mp4',
        description: '龙测科技以自主 AI+Robot+Model 为技术核心，即 AI 学习生成业务流程图，测试用户通过组合流程图成为积木图，机器人通过视觉和代码 + 机械化方式稳定执行。龙测产品采用图形化、零代码、全自动的方式取代手工测试，帮助客户快速发现 Bug，保障产品准时上线。与市面上的主流产品相比，龙测产品不仅适用于传统软件测试，还适用于工业软件测试、机械设备测试等诸多领域，应用广泛。产品凭借低成本、准确率高、24 小时全天候全自动化代替人工测试等优势与微信、支付宝、潍柴动力、中国移动、中软国际、东软、TCL 等多家知名客户建立了长期合作关系。',
        sections: [
          {
            id: '3-1',
            title: '龙测自动化测试云平台',
            video: 'http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&bizid=1023&hy=SH&fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400'
          }
        ]
      },
      {
        id: '4',
        title: '软件造价评估系统',
        provider: '东北大学 郭军',
        image: '/assets/tech/tech4.png',
        video: 'https://www.w3schools.com/html/movie.mp4',
        description: '软件造价是一个复杂的技术，它受到项目规模、用户需求、时间约束、资源约束、市场竞争、人工成本和传统文化等诸多因素的影响。全球软件造价的标准、方法、工具还在不断发展中，远未达到精准、完美的程度。大数据、人工智能和信创产业的发展，给软件造价带来了新的解决方案，国产化的软件造价分析工具恰逢其时。',
        sections: [
          {
            id: '4-1',
            title: '基本概念',
            video: 'https://www.w3schools.com/html/movie.mp4'
          },
          {
            id: '4-2',
            title: '规模评估',
            video: 'http://vjs.zencdn.net/v/oceans.mp4'
          },
          {
            id: '4-3',
            title: '工量评估',
            video: 'https://media.w3.org/2010/05/sintel/trailer.mp4'
          },
          {
            id: '4-4',
            title: '综合评估',
            video: 'http://www.w3school.com.cn/example/html5/mov_bbb.mp4'
          }
        ]
      },
      {
        id: '5',
        title: '信息安全风评系统',
        provider: '沈阳枫软科技有限公司 武旭春',
        image: '/assets/tech/tech5.png',
        video: 'http://www.w3school.com.cn/example/html5/mov_bbb.mp4',
        description: '信息安全风险评估是参照风险评估标准和管理规范，对信息系统的资产价值、潜在威胁、薄弱环节、已采取的防护措施等进行分析，判断安全事件发生的概率以及可能造成的损失，提出风险管理措施的过程',
        sections: [
          {
            id: '5-1',
            title: '信息安全风险评估',
            video: 'http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&bizid=1023&hy=SH&fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400'
          }
        ]
      }
    ] as ICourse[],
    filteredCourses: [] as ICourse[]
  },

  onLoad() {
    this.setData({
      filteredCourses: this.data.courses
    });
  },

  // 模糊搜索处理
  onSearchInput(e: any) {
    const keyword = e.detail.value.toLowerCase().trim();
    
    // 如果搜索关键词为空，显示所有课程
    if (!keyword) {
      this.setData({
        searchKeyword: '',
        filteredCourses: this.data.courses
      });
      return;
    }

    // 对每个课程进行模糊匹配
    const filtered = this.data.courses.filter(course => {
      // 将所有可搜索字段组合成一个字符串
      const searchableText = [
        course.title,
        course.provider,
        course.description,
        // 包含章节标题
        ...course.sections.map(section => section.title)
      ].join(' ').toLowerCase();

      // 将搜索关键词按空格分割，支持多个关键词搜索
      const keywords = keyword.split(/\s+/);
      
      // 所有关键词都要匹配才返回 true
      return keywords.every((kw: string) => searchableText.includes(kw));
    });

    this.setData({
      searchKeyword: keyword,
      filteredCourses: filtered
    });
  },

  // 跳转到课程详情
  goToCourseDetail(e: any) {
    const { id } = e.currentTarget.dataset;
    const course = this.data.courses.find(item => item.id === id);
    if (course) {
      wx.navigateTo({
        url: `/pages/course-detail/course-detail?course=${encodeURIComponent(JSON.stringify(course))}`
      });
    }
  }
}); 