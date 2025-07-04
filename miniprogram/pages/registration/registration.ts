interface IFormData {
  meetingId: string;
  company: string;
  name: string;
  gender: string;
  phone: string;
  email: string;
  roomType: string;
  arrivalMethod: string;
  arrivalNo: string;
  arrivalDate: string;
  arrivalTime: string;
  departureMethod: string;
  departureNo: string;
  departureDate: string;
  departureTime: string;
  remarks: string;
  [key: string]: string; // 添加索引签名
}

Page({
  data: {
    meetingId: '',
    form: {
      company: '',
      name: '',
      gender: '男',
      phone: '',
      email: '',
      roomType: '双人房',
      arrivalMethod: '保山机场',
      arrivalNo: '',
      arrivalDate: '',
      arrivalTime: '',
      departureMethod: '保山机场',
      departureNo: '',
      departureDate: '',
      departureTime: '',
      remarks: ''
    } as IFormData,
    genderOptions: [
      { label: '男', value: '男' },
      { label: '女', value: '女' }
    ],
    roomTypes: [
      { label: '双人房', value: '双人房' },
      { label: '单人房', value: '单人房' }
    ],
    arrivalMethods: [
      { label: '保山机场', value: '保山机场' },
      { label: '保山站', value: '保山站' },
      { label: '自驾', value: '自驾' }
    ]
  },

  onLoad(options: { id: string }) {
    if (options.id) {
      this.setData({ meetingId: options.id });
    }
  },

  // 输入框变化处理
  onInputChange(e: any) {
    const { field } = e.currentTarget.dataset;
    const { value } = e.detail;
    this.setData({
      [`form.${field}`]: value
    });
  },

  // 性别选择处理
  onGenderChange(e: any) {
    this.setData({
      'form.gender': e.detail.value
    });
  },

  // 房型选择处理
  onRoomTypeChange(e: any) {
    this.setData({
      'form.roomType': e.detail.value
    });
  },

  // 到达方式选择处理
  onArrivalMethodChange(e: any) {
    this.setData({
      'form.arrivalMethod': e.detail.value
    });
  },

  // 返程方式选择处理
  onDepartureMethodChange(e: any) {
    this.setData({
      'form.departureMethod': e.detail.value
    });
  },

  // 到达日期选择处理
  onArrivalDateChange(e: any) {
    this.setData({
      'form.arrivalDate': e.detail.value
    });
  },

  // 到达时间选择处理
  onArrivalTimeChange(e: any) {
    this.setData({
      'form.arrivalTime': e.detail.value
    });
  },

  // 返程日期选择处理
  onDepartureDateChange(e: any) {
    this.setData({
      'form.departureDate': e.detail.value
    });
  },

  // 返程时间选择处理
  onDepartureTimeChange(e: any) {
    this.setData({
      'form.departureTime': e.detail.value
    });
  },

  // 表单验证
  validateForm(): boolean {
    const { form } = this.data;
    const requiredFields = [
      { field: 'company', name: '单位' },
      { field: 'name', name: '姓名' },
      { field: 'phone', name: '手机号码' },
      { field: 'email', name: '电子邮箱' },
      { field: 'arrivalDate', name: '到达日期' },
      { field: 'arrivalTime', name: '到达时间' },
      { field: 'departureDate', name: '返程日期' },
      { field: 'departureTime', name: '返程时间' }
    ];

    for (const { field, name } of requiredFields) {
      if (!form[field]) {
        wx.showToast({
          title: `请填写${name}`,
          icon: 'none'
        });
        return false;
      }
    }

    // 验证手机号格式
    const phoneReg = /^1[3-9]\d{9}$/;
    if (!phoneReg.test(form.phone)) {
      wx.showToast({
        title: '请输入正确的手机号码',
        icon: 'none'
      });
      return false;
    }

    // 验证邮箱格式
    const emailReg = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
    if (!emailReg.test(form.email)) {
      wx.showToast({
        title: '请输入正确的邮箱地址',
        icon: 'none'
      });
      return false;
    }

    return true;
  },

  // 提交表单
  async submitForm() {
    if (!this.validateForm()) {
      return;
    }

    try {
      // TODO: 替换为实际的API调用
      const submitData = {
        ...this.data.form,
        meetingId: this.data.meetingId
      };
      console.log('提交数据:', submitData);

      wx.showToast({
        title: '提交成功',
        icon: 'success'
      });

      // 延迟返回上一页
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    } catch (error) {
      console.error('提交报名失败:', error);
      wx.showToast({
        title: '提交失败，请重试',
        icon: 'none'
      });
    }
  }
}); 