import { showError, showLoading, hideLoading } from '../../utils/util';

interface DynamicDetail {
  title: string;
  source: string;
  date: string;
  content: string;
  images?: string[];
  files?: Array<{
    name: string;
    url: string;
  }>;
  certificates?: string[];
}

Page({
  data: {
    dynamic: {} as DynamicDetail
  },

  onLoad(options) {
    try {
      // 从页面参数中获取动态数据
      const dynamic = JSON.parse(decodeURIComponent(options.dynamic || '{}'));
      
      // 处理富文本内容
      if (dynamic.content) {
        dynamic.content = this.processContent(dynamic.content);
      }
      
      this.setData({
        dynamic
      });
    } catch (error) {
      console.error('解析动态数据失败:', error);
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    }
  },

  // 处理文章内容，将普通文本转换为富文本格式
  processContent(content: string): string {
    // 将换行符转换为<p>标签
    content = content.replace(/\n/g, '</p><p>');
    content = '<p>' + content + '</p>';
    
    // 处理加粗文本
    content = content.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
    
    return content;
  },

  // 返回上一页
  navigateBack() {
    wx.navigateBack();
  },

  // 预览图片
  previewImage(e: any) {
    const url = e.currentTarget.dataset.url;
    const urls = this.data.dynamic.images || [];
    
    wx.previewImage({
      current: url,
      urls: urls
    });
  },

  // 预览证书图片
  previewCertificate(e: any) {
    const url = e.currentTarget.dataset.url;
    const urls = this.data.dynamic.certificates || [];
    
    wx.previewImage({
      current: url,
      urls: urls
    });
  },

  // 打开文件
  openFile(e: any) {
    const url = e.currentTarget.dataset.url;
    
    wx.showLoading({
      title: '打开中...'
    });

    // 下载并打开文件
    wx.downloadFile({
      url: url,
      success: (res) => {
        if (res.statusCode === 200) {
          wx.openDocument({
            filePath: res.tempFilePath,
            success: () => {
              console.log('文件打开成功');
            },
            fail: (error) => {
              console.error('打开文件失败', error);
              wx.showToast({
                title: '打开文件失败',
                icon: 'none'
              });
            }
          });
        }
      },
      fail: (error) => {
        console.error('下载文件失败', error);
        wx.showToast({
          title: '下载文件失败',
          icon: 'none'
        });
      },
      complete: () => {
        wx.hideLoading();
      }
    });
  }
}); 