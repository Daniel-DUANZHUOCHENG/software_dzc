"use strict";
Page({
    data: {
        courseId: '',
        courseTitle: '',
        videoUrl: '',
        messages: [],
        inputValue: '',
        loading: false,
        scrollToMessage: '',
        botId: '7522836555506614287',
        token: 'pat_XFJxVX7d9HlJ0YXaaDs4HRHr0v1CKAZq3MFshjAVY2jRKTjfkAzr9PgX31FUMe4X',
        // 新增流式响应相关状态
        isStreaming: false,
        streamBuffer: '' // 用于拼接流式响应内容
    },
    onLoad(options) {
        if (options.id && options.title && options.videoUrl) {
            this.setData({
                courseId: options.id,
                courseTitle: decodeURIComponent(options.title),
                videoUrl: decodeURIComponent(options.videoUrl)
            });
        }
    },
    navigateBack() {
        wx.navigateBack();
    },
    onInput(e) {
        this.setData({
            inputValue: e.detail.value
        });
    },
    async sendMessage() {
        const { inputValue, messages, loading, videoUrl, botId, token } = this.data;
        if (!inputValue.trim() || loading)
            return;
        const userDisplayContent = inputValue.trim();
        const cozeMessage = `视频网络地址为${videoUrl}，我的问题或要求是${userDisplayContent}`;
        const userMessage = {
            id: Date.now().toString(),
            type: 'user',
            content: userDisplayContent
        };
        // 创建一个空的bot消息占位
        const botMessageId = (Date.now() + 1).toString();
        const botMessage = {
            id: botMessageId,
            type: 'bot',
            content: ''
        };
        this.setData({
            messages: [...messages, userMessage, botMessage],
            inputValue: '',
            loading: true,
            isStreaming: true,
            streamBuffer: '',
            scrollToMessage: `msg-${botMessageId}`
        });
        const requestBody = {
            bot_id: botId,
            user_id: `user_${Date.now()}`,
            stream: true, // 启用流式响应
            auto_save_history: false, // 流式响应必须设为false
            additional_messages: [
                {
                    role: "user",
                    content: cozeMessage,
                    content_type: "text"
                }
            ]
        };
        console.log('发送请求数据:', requestBody);
        const that = this;
        let requestTask;
        let decoder = new TextDecoder('utf-8');
        let buffer = ''; // 用于处理分块不完整的情况
        try {
            requestTask = wx.request({
                url: 'https://api.coze.cn/v3/chat',
                method: 'POST',
                header: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                data: requestBody,
                responseType: 'arraybuffer', // 必须使用arraybuffer接收二进制流
                enableChunked: true, // 启用分块传输
                timeout: 60000, // 流式响应超时时间设长一些
                success(res) {
                    console.log('流式请求成功完成');
                },
                fail(err) {
                    console.error('流式请求失败:', err);
                    that.handleStreamError(`网络错误: ${err.errMsg}`);
                },
                complete() {
                    // 请求完成但可能未正常结束
                    if (that.data.isStreaming) {
                        that.handleStreamError('连接已断开');
                    }
                }
            });
            // 监听分块数据接收
            requestTask.onChunkReceived((res) => {
                try {
                    // 将ArrayBuffer转换为字符串
                    const chunk = decoder.decode(res.data);
                    buffer += chunk;
                    // 按SSE协议分割事件 (格式: event: xxx\ndata: xxx\n\n)
                    const events = buffer.split('\n\n');
                    // 保留最后一个不完整的事件
                    buffer = events.pop() || '';
                    events.forEach(event => {
                        if (!event.trim())
                            return;
                        // 解析event和data字段
                        const lines = event.split('\n');
                        let eventType = '';
                        let eventData = '';
                        lines.forEach(line => {
                            if (line.startsWith('event:')) {
                                eventType = line.substring(6).trim();
                            }
                            else if (line.startsWith('data:')) {
                                eventData = line.substring(5).trim();
                            }
                        });
                        // 处理不同类型的事件
                        if (eventType === 'conversation.message.delta' && eventData) {
                            const data = JSON.parse(eventData);
                            if (data.role === 'assistant' && data.type === 'answer' && data.content) {
                                // 拼接流式内容
                                that.setData({
                                    streamBuffer: that.data.streamBuffer + data.content,
                                    [`messages[${that.data.messages.length - 1}].content`]: that.data.streamBuffer + data.content
                                });
                            }
                        }
                        // 响应结束事件
                        else if (eventType === 'done' || (eventData === '[DONE]')) {
                            that.setData({
                                loading: false,
                                isStreaming: false
                            });
                            requestTask.offChunkReceived();
                        }
                    });
                }
                catch (error) {
                    console.error('流式数据处理错误:', error);
                    that.handleStreamError('数据解析错误');
                }
            });
        }
        catch (error) {
            console.error('发送消息异常:', error);
            this.handleStreamError('请求异常');
        }
    },
    // 流式响应错误处理
    handleStreamError(errorMsg) {
        if (!this.data.isStreaming)
            return;
        this.setData({
            loading: false,
            isStreaming: false,
            [`messages[${this.data.messages.length - 1}].content`]: `请求失败: ${errorMsg}`
        });
        wx.showToast({
            title: errorMsg,
            icon: 'none',
            duration: 3000
        });
    },
    onScrollToUpper() {
        console.log('到顶部了');
    }
});
