// 云函数入口文件
const cloud = require('wx-server-sdk')

cloud.init({
  env: cloud.DYNAMIC_CURRENT_ENV
})

// 云函数入口函数
exports.main = async (event, context) => {
  const db = cloud.database()
  
  try {
    // 检查meetings集合是否存在
    const collections = await db.listCollections().get()
    const hasCollection = collections.data.some(collection => collection.name === 'meetings')
    
    if (!hasCollection) {
      await db.createCollection('meetings')
    }

    // 初始化会议数据
    const meetings = [
      {
        id: '1',
        title: '技术交流会',
        description: '暨系统测评技术赋能基础软硬件产业高质量发展论坛',
        detailImage: '/assets/banners/banner1.png',
        date: '2023.08',
        location: '云南昌宁'
      },
      {
        id: '2',
        title: '2023年会员大会',
        description: '暨系统测评技术赋能基础软硬件产业高质量发展论坛',
        detailImage: '/assets/banners/banner2.png',
        date: '2023.09',
        location: '北京'
      },
      {
        id: '3',
        title: '软件质量管理研讨会',
        description: '暨系统测评技术赋能基础软硬件产业高质量发展论坛',
        detailImage: '/assets/banners/banner3.jpg',
        date: '2023.10',
        location: '深圳'
      }
    ]

    // 检查meeting_registrations集合是否存在
    const hasRegistrations = collections.data.some(collection => collection.name === 'meeting_registrations')
    if (!hasRegistrations) {
      await db.createCollection('meeting_registrations')
    }

    // 上传会议数据
    for (const meeting of meetings) {
      // 检查会议是否已存在
      const existingMeeting = await db.collection('meetings')
        .where({
          id: meeting.id
        })
        .get()

      if (existingMeeting.data.length === 0) {
        // 如果会议不存在，则添加
        await db.collection('meetings').add({
          data: meeting
        })
      }
    }

    return {
      success: true,
      message: '数据库初始化成功'
    }
  } catch (error) {
    console.error('初始化数据库失败：', error)
    return {
      success: false,
      error: error
    }
  }
} 