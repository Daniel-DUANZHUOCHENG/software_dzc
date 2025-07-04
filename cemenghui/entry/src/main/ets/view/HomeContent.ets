import { router } from "@kit.ArkUI"

@Component
export struct HomeContent {
  @State currentBannerIndex: number = 0
  private bannerImages = [
    $r('app.media.cmh'),
  ]
  private memberLogos = [
    $r('app.media.daopu'), // 道普信息
    $r('app.media.shanghai'), // 上海计算机
    $r('app.media.rthl'), // RTHL 北方实业
    $r('app.media.hunan'), // 湖南佳策
    $r('app.media.shanghai_soft'), // 上海软评
    $r('app.media.zist'), // ZIST
    $r('app.media.jilin'), // 吉林电检院
    $r('app.media.zero610'), // 0610
    $r('app.media.chongqing'), // 重庆软评
    $r('app.media.xian863'), // 西安863
    $r('app.media.jiangsu')// 江苏软检
  ]

  build() {
    Column() {
      Column() {
        Text('测盟汇')
          .fontSize(20)
          .fontWeight(FontWeight.Medium)
          .fontColor(Color.White)
          .margin({ top: 20 })
      }
      .width('100%')
      .height(56)
      .linearGradient({
        angle: 90,
        colors: [
          [0x7D5FFF, 0.0],
          [0x9050FF, 1.0]
        ]
      })

      Scroll() {
        Column() {
          Blank()
            .height(20)

          Swiper() {
            ForEach(this.bannerImages, (image: Resource) => {
              Image(image)
                .width('100%')
                .height(180)
                .borderRadius(16)
            })
          }
          .indicator(true)
          .autoPlay(true)
          .width('95%')
          .height(180)

          Blank()
            .height('3%')

          Column() {
            Text('中国电子质量管理协会')
              .fontSize(20)
              .fontColor('#000000')
              .fontWeight(FontWeight.Bold)
              .margin({ bottom: 8 })
              .textAlign(TextAlign.Start)

            Text('计算机软硬件和信息系统质量测评分会')
              .fontSize(16)
              .fontColor('#000000')
              .margin({ bottom: 16 })
              .textAlign(TextAlign.Start)

            Text('       计算机软硬件和信息系统质量测评分会是中国电子质量管理协会设立的17个分支机构之一。分会可以对计算机软硬件和信息系统所有潜在的、现有的风险进行评估及分析并对自愿申请参加能力评审的造价评估机构进行客观、公正的能力评审，最终给出能力评审结论。')
              .fontSize(14)
              .fontColor('#000000')
              .lineHeight(24)
          }
          .width('95%')
          .borderRadius(16)
          .padding(16)
          .backgroundColor('#FFFFFF')
          .alignItems(HorizontalAlign.Start)


          Grid() {
            ForEach(this.memberLogos, (logo: Resource, index: number) => {
              GridItem() {
                Image(logo)
                  .width(80)
                  .height(80)
                  .objectFit(ImageFit.Cover)
                  .onClick(() => {
                    router.pushUrl({
                      url: 'pages/DetailPage',
                      params: {
                        logoIndex: index
                      }
                    })
                  })
              }
            })
          }
          .columnsTemplate('1fr 1fr 1fr 1fr')
          .rowsTemplate('1fr 1fr 1fr')
          .columnsGap(0)
          .rowsGap(0)
          .width('100%')
          .aspectRatio(4 / 3)
          .margin({ top: 16 })

          Blank().height(50)
        }
      }
      .width('100%')
      .height('100%')
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