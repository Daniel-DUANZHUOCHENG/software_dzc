import { router } from "@kit.ArkUI"

@Entry
@Component
export struct CooperationGalleryPage {
  private galleryImages: Array<Resource> = [
    $r('app.media.cmh'),
    $r('app.media.tp1'),
    $r('app.media.tp2')
  ]

  build() {
    Column() {
      Column() {
        Row(){
          Image($r('app.media.Arrow_left'))
            .width(24)
            .height(24)
            .fillColor(Color.White)
            .margin({ left: 16 ,right:85})
            .objectFit(ImageFit.Contain)
            .onClick(() => {
              router.back()
            })
          Text('合作图片展示')
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
          Image(this.galleryImages[0])
            .width('90%')
            .aspectRatio(1.5)
            .objectFit(ImageFit.Contain)
            .margin({ bottom: 5 })
          Column() {
            Image(this.galleryImages[1])
              .width('90%')
              .objectFit(ImageFit.Fill)
              .margin({ bottom: 0 })
            Image(this.galleryImages[2])
              .width('90%')
              .objectFit(ImageFit.Fill)
              .margin({ top: 0, bottom: 20 })
          }
        }

        .width('100%')
      }
      .layoutWeight(1)
    }
    .width('100%')
    .height('100%')

  }
} 