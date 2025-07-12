import { ArticleClass } from '../../model/ArticleClass'
import { router } from '@kit.ArkUI'

@Component
export struct TutorialView {
  @Link searchText: string

  @State tutorialList: Array<ArticleClass> = [
    new ArticleClass('1', $r('app.media.Tutorial1'),
      '【资讯】江苏出台政策措施加快工业软件自主创新', '江苏省软件产品检测中心'),
    new ArticleClass('2', $r('app.media.Tutorial2'),
      '关于开展软件工程造价师培训及认证培训班的通知', '吉林省电子信息产品检验研究院'),
    new ArticleClass('3', $r('app.media.Tutorial3'),
      '喜报 | 北方实验室成功晋级国家级工业信息安全技能大赛30强', '北方实验室（沈阳）股份有限公司'),
    new ArticleClass('4', $r('app.media.Tutorial4'),
      '道普信息荣获"2023年度标准化工作先进集体"称号，助力软件产业高质量发展', '道普信息技术有限公司'),
    new ArticleClass('5', $r('app.media.Tutorial5'),
      '【政策措施】省政府办公厅印发关于加快工业软件自主创新若干政策措施的通知','江苏省软件产品检测中心'),
    new ArticleClass('6', $r('app.media.Tutorial6'),
      '北碚区市场监督管理局党组书记、局长刘禾带队到我公司调研','重庆市软件评测中心有限公司'),
    new ArticleClass('7', $r('app.media.Tutorial7'),
      '沈阳鲲鹏产业孵化基地项目验收会顺利召开','北方实验室'),
    new ArticleClass('8', $r('app.media.Tutorial8'),
      '喜报！关于公布智能监管业务模型创新活动获奖结果的通知','喜报')
  ]

  build() {
      Column() {
      if (this.searchText && this.filteredList().length === 0) {
        Column() {
          Image($r('app.media.NoData'))
            .width(120)
            .height(120)
            .margin({ bottom: 16 })
          Text('没有相关内容')
            .fontSize(16)
            .fontColor('#999999')
        }
        .width('100%')
        .height('100%')
        .justifyContent(FlexAlign.Center)
        .alignItems(HorizontalAlign.Center)
      } else {
        List({ space: 12 }) {
          ForEach(this.filteredList(), (item: ArticleClass) => {
            ListItem() {
              TutorialItem({ tutorialItem: item })
            }
          }, (item: ArticleClass) => item.id)
        }
        .width('100%')
        .height('100%')
        .scrollBar(BarState.Off)
        .nestedScroll({
          scrollForward: NestedScrollMode.SELF_FIRST,
          scrollBackward: NestedScrollMode.SELF_FIRST
        })

        }
      }
      .width('100%')
      .height('100%')
    }


  filteredList(): Array<ArticleClass> {
    if (!this.searchText) {
      return this.tutorialList
    }

    const searchLower = this.searchText.toLowerCase()
    return this.tutorialList.filter(item =>
    item.title.toLowerCase().includes(searchLower) ||
    item.brief.toLowerCase().includes(searchLower)
    )
  }
}

@Component
struct TutorialItem {
  @Prop tutorialItem: ArticleClass;

  build() {
    Row() {
      Image(this.tutorialItem.imageSrc)
        .height(108)
        .width(108)
        .objectFit(ImageFit.Cover)
        .borderRadius(10)
        .margin({right:16})

      Column() {
        Text(this.tutorialItem.title)
          .width('100%')
          .fontSize(15)
          .textAlign(TextAlign.Start)
          .textOverflow({ overflow: TextOverflow.Ellipsis })
          .maxLines(2)
          .fontWeight(500)
          .margin({bottom:8})

        Text(this.tutorialItem.brief)
          .width('100%')
          .fontSize(14)
          .textAlign(TextAlign.Start)
          .textOverflow({ overflow: TextOverflow.Ellipsis })
          .maxLines(1)
          .fontColor('rgba(0, 0, 0, 0.6)')
      }
      .height('100%')
      .layoutWeight(1)
      .alignItems(HorizontalAlign.Start)
    }
    .width('100%')
    .height(120)
    .borderRadius(0)
    .backgroundColor(Color.White)
    .padding(12)
    .alignItems(VerticalAlign.Center)
    .onClick(() => {
      router.pushUrl({
        url: 'view/News/TutorialDetail',
        params: {
          title: this.tutorialItem.title
        }
      })
    })
  }
}
