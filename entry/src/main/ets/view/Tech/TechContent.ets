import { TechTutorialView } from './TechTutorial'

@Component
export struct TechContent {
  @State isSearching: boolean = false
  @State searchText: string = ''
  @State searchKeyword: string = ''

  build() {
    Column() {
      Column() {
        Text('技术')
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
            .height(5)

          Row() {
            if (this.isSearching) {
              Row() {
                Image($r('app.media.search'))
                  .width(20)
                  .height(20)

                TextInput({ placeholder: '请搜索课程、工具或技术名称', text: this.searchText })
                  .placeholderColor('rgba(0,0,0,0.6)')
                  .placeholderFont({ size: 14 })
                  .backgroundColor('#f5f5f5')
                  .height(36)
                  .width('80%')
                  .onChange((value: string) => {
                    this.searchText = value
                  })
              }
              .backgroundColor('#f5f5f5')
              .borderRadius(18)

              Button('取消', { type: ButtonType.Normal })
                .width('20%')
                .height(36)
                .fontSize(14)
                .fontColor('#7D5FFF')
                .backgroundColor(Color.White)
                .onClick(() => {
                  this.isSearching = false
                  this.searchText = ''
                })
            } else {
              Row() {
                Image($r('app.media.search'))
                  .width(20)
                  .height(20)
                Text('请搜索课程、工具或技术名称')
                  .fontSize(14)
                  .opacity(0.6)
                  .margin({ left: 8 })
              }
              .width('100%')
              .height(36)
              .borderRadius(18)
              .backgroundColor('#f5f5f5')
              .onClick(() => {
                this.isSearching = true
              })
            }
          }
          .width('95%')
          .margin({ top: 15 })
          .justifyContent(FlexAlign.Center)

          Blank()
            .height(20)

          TechTutorialView({ searchText: $searchText })

          Text('没有更多内容了')
            .margin({top:10 , bottom :10})

          Blank()
            .height(100)
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