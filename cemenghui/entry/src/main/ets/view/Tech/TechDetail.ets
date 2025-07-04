import { router } from "@kit.ArkUI";

interface VideoItem {
  url: string;
  title: string;d
  description: string;
}

@Entry
@Component
export struct TechDetail {
  @State title: string = ''
  @State videoList: Array<VideoItem> = [];
  @State currentVideoIndex: number = 0;

  aboutToAppear() {
    const params = router.getParams() as Record<string, string>;
    if (params) {
      this.title = params['title'] || '';
      try {
        this.videoList = JSON.parse(params['videoList'] || '[]');
      } catch (e) {
        console.error('解析视频列表失败:', e);

      }
    }
  }

  private switchVideo(index: number) {
    this.currentVideoIndex = index;
  }

  build() {
    Column() {
      //标题栏
      Column() {
        Row() {
          Image($r('app.media.Arrow_left'))
            .width(24)
            .height(24)
            .fillColor(Color.White)
            .margin({ left: 16, right: 50 })
            .objectFit(ImageFit.Contain)
            .onClick(() => {
              router.back()
            })

          Text(this.title)
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

      //其他
      Scroll() {
        Column() {
          if (this.videoList.length > 0) {
            Video({
              src: this.videoList[this.currentVideoIndex].url,
              currentProgressRate: 1.0
            })
              .width('95%')
              .aspectRatio(1.78)
              .borderRadius(16)
              .margin({ top: 16 })
              .autoPlay(false)

            Blank().height(10)

            //视频简介
            Column(){
              Text(this.videoList[this.currentVideoIndex].title)
                .fontSize(14)
                .fontWeight(FontWeight.Bold)
                .fontColor(Color.Black)
                .margin({ top: 12, left: 16 })
                .alignSelf(ItemAlign.Start)

              Text('【课程简介】')
                .fontSize(14)
                .margin({ top: 10, left: 5, bottom: 20 })
                .alignSelf(ItemAlign.Start)
                .fontColor(Color.Black)

              Text("  "+this.videoList[this.currentVideoIndex].description)
                .fontSize(14)
                .fontColor('#666666')
                .margin({ top: 8, left: 16, right: 16 ,bottom:5})
                .lineHeight(20)
                .textAlign(TextAlign.Start)
            }
            .width('95%')
            .borderRadius(16)
            .backgroundColor(Color.White)
            .margin({ bottom: 16 })
          }

          Column() {
            if (this.videoList.length > 0) {
              Blank()
                .height(10)

              Column() {
                Text('【课程目录】')
                  .fontSize(14)
                  .margin({ top: 10, left: 5, bottom: 20 })
                  .alignSelf(ItemAlign.Start)
                  .fontColor(Color.Black)

                ForEach(this.videoList, (item: VideoItem, index: number) => {
                  Row() {
                    Text(`第 ${index + 1} 节`)
                      .fontSize(14)
                      .fontColor(index === this.currentVideoIndex ? '#89CFF0' : Color.Black)
                      .margin({ left: 16 })

                    Text(item.title)
                      .fontSize(14)
                      .fontColor(index === this.currentVideoIndex ? '#89CFF0' : Color.Black)
                      .layoutWeight(1)
                      .textAlign(TextAlign.End)
                      .margin({right:5})
                  }
                  .width('100%')
                  .padding(12)
                  .height(40)
                  .backgroundColor(Color.White)
                  .borderRadius(4)
                  .onClick(() => {
                    this.switchVideo(index);
                  })
                })
              }
              .width('95%')
              .backgroundColor(Color.White)
              .borderRadius(16)
              .padding({ top: 10, bottom: 10 })
            } else {
              // 无视频提示
              Text('暂无视频内容')
                .fontSize(16)
                .margin({ top: 50 })
            }
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
}