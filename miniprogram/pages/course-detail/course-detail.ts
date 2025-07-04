interface Lesson {
  id: string;
  title: string;
  duration: number;
  isFree: boolean;
  isLearned: boolean;
}

interface Chapter {
  id: string;
  title: string;
  duration: number;
  lessons: Lesson[];
  isExpanded: boolean;
}

interface Teacher {
  name: string;
  avatar: string;
  title: string;
  introduction: string;
}

interface CourseInfo {
  id: string;
  title: string;
  coverImage: string;
  price: number;
  duration: number;
  studentCount: number;
  introduction: string;
  targetAudience: string[];
  learningGoals: string[];
  chapters: Chapter[];
  teacher: Teacher;
}

interface PageScrollOption {
  scrollTop: number;
}

interface ICourseSection {
  id: string;
  title: string;
  video: string;
}

interface ICourse {
  id: string;
  title: string;
  provider: string;
  image: string;
  video: string;
  description: string;
  sections: ICourseSection[];
}

// 视频地址映射
const VIDEO_URLS: { [key: string]: string } = {
  '1': 'https://stream7.iqilu.com/10339/upload_transcode/202002/09/20200209104902N3v5Vpxuvb.mp4',
  '2': 'https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218114723HDu3hhxqIT.mp4',
  '3': 'http://vjs.zencdn.net/v/oceans.mp4',
  '4': 'https://www.w3schools.com/html/movie.mp4',
  '5': 'http://www.w3school.com.cn/example/html5/mov_bbb.mp4',
  '1-1': 'https://stream7.iqilu.com/10339/upload_transcode/202002/09/20200209104902N3v5Vpxuvb.mp4',
  '1-2': 'https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218114723HDu3hhxqIT.mp4',
  '2-1': 'https://media.w3.org/2010/05/sintel/trailer.mp4',
  '2-2': 'http://www.w3school.com.cn/example/html5/mov_bbb.mp4',
  '3-1': 'http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&bizid=1023&hy=SH&fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400',
  '4-1': 'https://www.w3schools.com/html/movie.mp4',
  '4-2': 'http://vjs.zencdn.net/v/oceans.mp4',
  '4-3': 'https://media.w3.org/2010/05/sintel/trailer.mp4',
  '4-4': 'http://www.w3school.com.cn/example/html5/mov_bbb.mp4',
  '5-1': 'http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&bizid=1023&hy=SH&fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400'
};

Page({
  data: {
    courseId: '',
    currentTab: 'intro',
    isCollected: false,
    courseInfo: {} as CourseInfo,
    windowHeight: 0,
    windowWidth: 0,
    navHeight: 0,
    course: {} as ICourse,
    currentVideo: '',
    currentSectionId: '',
    isPlaying: false
  },

  onLoad(options: any) {
    const { id } = options;
    this.setData({ courseId: id });
    this.loadCourseDetail();
    this.checkCollectionStatus();
    this.initSystemInfo();

    if (options.course) {
      const course = JSON.parse(decodeURIComponent(options.course));
      const firstSectionId = course.sections[0].id;
      
      // 根据课程ID和章节ID获取对应的视频
      const courseId = course.id;
      const initialVideo = VIDEO_URLS[courseId] || VIDEO_URLS['1'];
      
      console.log('课程ID:', courseId);
      console.log('初始视频地址:', initialVideo);

      this.setData({
        course,
        currentVideo: initialVideo,
        currentSectionId: firstSectionId
      });

      // 创建视频上下文
      const videoContext = wx.createVideoContext('courseVideo', this);
      // 设置初始状态
      videoContext.stop();
    }
  },

  // 初始化系统信息
  initSystemInfo() {
    const systemInfo = wx.getSystemInfoSync();
    const menuButtonInfo = wx.getMenuButtonBoundingClientRect();
    
    this.setData({
      windowHeight: systemInfo.windowHeight,
      windowWidth: systemInfo.windowWidth,
      navHeight: menuButtonInfo.bottom + 10
    });
  },

  // 加载课程详情
  async loadCourseDetail() {
    try {
    
      this.setData({
       
      });
    } catch (error) {
      console.error('加载课程详情失败:', error);
      wx.showToast({
        title: '加载失败，请重试',
        icon: 'none'
      });
    }
  },

  // 切换标签页
  switchTab(e: WechatMiniprogram.TouchEvent) {
    const { tab } = e.currentTarget.dataset;
    this.setData({
      currentTab: tab
    });

    // 使用选择器获取元素位置并滚动
    wx.createSelectorQuery()
      .select('.tab-content')
      .boundingClientRect((rect) => {
        if (rect) {
          wx.pageScrollTo({
            scrollTop: rect.top,
            duration: 300
          });
        }
      })
      .exec();
  },

  // 展开/收起章节
  toggleChapter(e: WechatMiniprogram.TouchEvent) {
    const { index } = e.currentTarget.dataset;
    const { chapters } = this.data.courseInfo;
    
    chapters[index].isExpanded = !chapters[index].isExpanded;
    
    this.setData({
      'courseInfo.chapters': chapters
    });
  },

  // 检查收藏状态
  async checkCollectionStatus() {
    try {
      // TODO: 替换为实际的API调用
      const isCollected = false;
      this.setData({ isCollected });
    } catch (error) {
      console.error('检查收藏状态失败:', error);
    }
  },

  // 切换收藏状态
  async toggleCollect() {
    try {
      // TODO: 替换为实际的API调用
      const newStatus = !this.data.isCollected;
      this.setData({ isCollected: newStatus });
      
      wx.showToast({
        title: newStatus ? '收藏成功' : '已取消收藏',
        icon: 'success'
      });
    } catch (error) {
      console.error('操作收藏失败:', error);
      wx.showToast({
        title: '操作失败，请重试',
        icon: 'none'
      });
    }
  },

  // 报名课程
  enrollCourse() {
    const { id, title, price } = this.data.courseInfo;
    wx.navigateTo({
      url: `/pages/course-enroll/course-enroll?id=${id}&title=${encodeURIComponent(title)}&price=${price}`
    });
  },

  // 监听页面尺寸变化
  onResize() {
    this.initSystemInfo();
  },

  // 监听页面滚动
  onPageScroll(options: PageScrollOption) {
    // 可以在这里处理页面滚动事件
    // 比如固定导航栏等
  },

  onShareAppMessage() {
    const { title } = this.data.courseInfo;
    return {
      title: `推荐课程：${title}`,
      path: `/pages/course-detail/course-detail?id=${this.data.courseId}`,
      imageUrl: this.data.courseInfo.coverImage
    };
  },

  // 返回上一页
  navigateBack() {
    wx.navigateBack();
  },

  // 切换视频章节
  switchVideo(e: any) {
    const section = e.currentTarget.dataset.section as ICourseSection;
    console.log('切换到章节:', section);
    
    // 获取对应章节的视频地址
    const videoPath = VIDEO_URLS[section.id];
    console.log('视频地址:', videoPath);
    
    if (this.data.currentSectionId === section.id) {
      // 如果点击当前正在播放的章节，则切换播放状态
      const videoContext = wx.createVideoContext('courseVideo', this);
      if (this.data.isPlaying) {
        videoContext.pause();
      } else {
        videoContext.play();
      }
      this.setData({
        isPlaying: !this.data.isPlaying
      });
    } else {
      // 切换到新章节
      if (!videoPath) {
        console.error('未找到对应章节的视频:', section.id);
        wx.showToast({
          title: '该章节暂无视频',
          icon: 'none'
        });
        return;
      }

      this.setData({
        currentVideo: videoPath,
        currentSectionId: section.id,
        isPlaying: true
      }, () => {
        // 在状态更新后播放视频
        const videoContext = wx.createVideoContext('courseVideo', this);
        videoContext.play();
      });
    }
  },

  // 视频播放状态改变
  onVideoPlay() {
    console.log('视频开始播放');
    this.setData({
      isPlaying: true
    });
  },

  onVideoPause() {
    console.log('视频暂停播放');
    this.setData({
      isPlaying: false
    });
  },

  // 视频错误处理
  onVideoError(e: any) {
    console.error('视频播放错误:', e.detail.errMsg);
    wx.showToast({
      title: '视频加载失败',
      icon: 'none'
    });
  },

  // 跳转到问答页面
  goToQA() {
    const { course, currentVideo } = this.data;
    wx.navigateTo({
      url: `/pages/course-detail-qa/course-detail-qa?id=${course.id}&title=${encodeURIComponent(course.title)}&videoUrl=${encodeURIComponent(currentVideo)}`
    });
  }
}); 