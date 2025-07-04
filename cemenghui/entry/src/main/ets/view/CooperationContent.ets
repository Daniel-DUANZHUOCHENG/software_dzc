import { TabItem1 } from "../model/Types"
import router from '@ohos.router';

@Component
export struct CooperationContent {
  private cardData:Array<TabItem1> = [
    { id: 1, Img: $r('app.media.hyyt') },  // 会议研讨
    { id: 2, Img: $r('app.media.bzzd') },  // 标准制定
    { id: 3, Img: $r('app.media.jspx') },  // 技术培训
    { id: 4, Img: $r('app.media.gjyf') },   // 工具研发
    { id: 5, Img: $r('app.media.guxd') }    // 公益行动
  ]

  build() {
    Column() {
      // 1. 顶部标题栏（保持原样）
      Row() {
        Text('合作')
          .fontSize(20)
          .fontWeight(FontWeight.Medium)
          .fontColor(Color.White)
          .margin({top:12})
      }
      .width('100%')
      .height(56)
      .justifyContent(FlexAlign.Center)
      .linearGradient({
        angle: 90,
        colors: [[0x7D5FFF, 0], [0x9050FF, 1]]
      })


      List({ space: 8 }) {
        ForEach(this.cardData, (item:TabItem1) => {
          ListItem() {
            Column() {
              if (item.id === 5) {
                Image(item.Img)
                  .width('100%')
                  .height('100%')
                  .aspectRatio(0.9)
                  .borderRadius(10)
                  .objectFit(ImageFit.Contain)
                  .onClick(() => {
                    router.pushUrl({
                      url: 'pages/CooperationGalleryPage'
                    })
                  })
              } else {
                // 普通双列卡片
                Image(item.Img)
                  .width('100%')
                  .height('100%')
                  .aspectRatio(0.9)
                  .borderRadius(10)
                  .objectFit(ImageFit.Contain)
                  .onClick(() => {
                    router.pushUrl({
                      url: 'pages/CooperationGalleryPage'
                    })
                  })
              }
            }
            .width('100%')

            .borderRadius(10)
          }
          .margin({
            left: '10vp',
            right: '10vp',
            top: '4vp',
            bottom: '4vp'
          })
        })
      }
      .width('100%')
      .lanes(2)
      .flexGrow(1)
      .linearGradient({
        angle: 180,
        colors: [
          [0xFFF5F5, 0.0],
          [0xFFFAFA, 1.0]
        ]
      })
    }
    .width('100%')
    .height('100%')
  }
}