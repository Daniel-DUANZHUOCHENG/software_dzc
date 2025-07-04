import { router } from "@kit.ArkUI";

@Entry
@Component
export struct TutorialDetail {
  @State title: string = ''

  aboutToAppear() {
    const params = router.getParams() as Record<string, string>;
    if (params && params['title']) {
      this.title = params['title'];
    }
  }

  build() {
    Column() {
      Column() {
        Row() {
          Image($r('app.media.Arrow_left'))
            .width(24)
            .height(24)
            .fillColor(Color.White)
            .margin({ left: 16, right: 100 })
            .objectFit(ImageFit.Contain)
            .onClick(() => {
              router.back()
            })

          Text('动态详情')
            .fontSize(20)
            .fontWeight(FontWeight.Medium)
            .fontColor(Color.White)
            .alignSelf(ItemAlign.Center)
        }
        .width('100%')
        .height(56)
        .justifyContent(FlexAlign.Start)
      }
      .width('100%')
      .linearGradient({
        angle: 90,
        colors: [
          [0x7D5FFF, 0.0],
          [0x9050FF, 1.0]
        ]
      })

      Scroll(){
        Column(){
          Text(this.title)
            .fontSize(15)
            .margin(20)
            .alignSelf(ItemAlign.Start)

          if (this.title.includes('【资讯】江苏出台政策措施加快工业软件自主创新')) {
            Image($r('app.media.Detail1'))
              .objectFit(ImageFit.Contain)
          } else if (this.title.includes('关于开展软件工程造价师培训及认证培训班的通知')) {
            Image($r('app.media.Detail21'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail22'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail23'))
              .objectFit(ImageFit.Contain)
          } else if (this.title.includes('喜报 | 北方实验室成功晋级国家级工业信息安全技能大赛30强')) {
            Image($r('app.media.Detail31'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail32'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail33'))
              .objectFit(ImageFit.Contain)
          } else if (this.title.includes('道普信息荣获"2023年度标准化工作先进集体"称号，助力软件产业高质量发展')) {
            Image($r('app.media.Detail41'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail42'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail43'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail44'))
              .objectFit(ImageFit.Contain)
          } else if (this.title.includes('【政策措施】省政府办公厅印发关于加快工业软件自主创新若干政策措施的通知')) {
            Image($r('app.media.Detail51'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail52'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail53'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail54'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail55'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail56'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail57'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail58'))
              .objectFit(ImageFit.Contain)
          } else if (this.title.includes('北碚区市场监督管理局党组书记、局长刘禾带队到我公司调研')) {
            Image($r('app.media.Detail61'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail62'))
              .objectFit(ImageFit.Contain)
          } else if (this.title.includes('沈阳鲲鹏产业孵化基地项目验收会顺利召开')) {
            Image($r('app.media.Detail71'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail72'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail73'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail74'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail75'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail76'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail77'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail78'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail79'))
              .objectFit(ImageFit.Contain)
            Image($r('app.media.Detail70'))
              .objectFit(ImageFit.Contain)
          } else {
            Image($r('app.media.Detail8'))
              .objectFit(ImageFit.Contain)
          }
        }
        .width('95%')
        .borderRadius(16)
        .backgroundColor(Color.White)
      }
      .width('100%')
      .height('100%')
      .margin({top:0})
    }
    .width('100%')
    .height('100%')
    .linearGradient({
      angle: 180,
      colors: [
        [0xFFF5F5, 0.0],
        [0xFFFAFA, 1.0]
      ]
    })
  }

}