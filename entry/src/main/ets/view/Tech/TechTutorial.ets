import { ArticleClass } from '../../model/ArticleClass'
import { router } from '@kit.ArkUI'

interface GeneratedTypeLiteralInterface_1 {
  url: string;
  title: string;
  description:string;
}

@Component
export struct TechTutorialView{
  @Link searchText: string

  @State tutorialList: Array<ArticleClass> = [
    new ArticleClass('1',$r('app.media.tech1'),'Leming WebRunner','迎风聚智'),
    new ArticleClass('2',$r('app.media.tech2'),'Leming TPC-E2.0','迎风聚智'),
    new ArticleClass('3',$r('app.media.tech3'),'龙测AI-TestOps云平台','苏州市龙测智能科技有限公司'),
    new ArticleClass('4',$r('app.media.tech4'),'软件造价评估系统','东北大学 郭军'),
    new ArticleClass('5',$r('app.media.tech5'),'信息安全风评系统','沈阳织知科技有限公司 武旭春')
  ];

  private getVideoListByTitle(title: string): Array<GeneratedTypeLiteralInterface_1> {
    const videoMap: Record<string, Array<GeneratedTypeLiteralInterface_1>> = {
      'Leming WebRunner': [
        {url: 'http://vjs.zencdn.net/v/oceans.mp4', title: '应用系统性测试工具',
          description:'Leming WebRunner由武汉迎风聚智科技有限公司研发，是一款适用于国产环境的应用系统性能测试工具。Leming WebRunner深入了解企业数据库的应用现状，依据数据库国际与行业标准，用心满足数据库软硬件厂家与企业用户需求，不断揭开数据库的神秘面纱。'},
        {url: 'https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4', title: '迎风聚智简介',
          description:'Leming WebRunner由武汉迎风聚智科技有限公司研发，是一款适用于国产环境的应用系统性能测试工具。Leming WebRunner深入了解企业数据库的应用现状，依据数据库国际与行业标准，用心满足数据库软硬件厂家与企业用户需求，不断揭开数据库的神秘面纱。'}
      ],
      'Leming TPC-E2.0': [
        {url: 'http://www.w3school.com.cn/example/html5/mov_bbb.mp4', title: '数据库性能基准测试工具',
          description:'Leming TPC-E是一款基于国际TPC-E基准的测试数据库在线事务处理(OLTP)性能的基准测试工具。该工具提供可视化的参数设置，测试者可灵活定义整个测试任务，测试以数据库为对象，通过被测数据库中进行股票交易的客户数量把控测试规模，执行过程严格遵守基准约束，运行状态全程监控，测试结束自动输出报告，结果反映数据库软件性能，同时也体现服务器硬件性能。'},
        {url: 'https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4', title: '迎风聚智简介',
          description:'Leming TPC-E是一款基于国际TPC-E基准的测试数据库在线事务处理(OLTP)性能的基准测试工具。该工具提供可视化的参数设置，测试者可灵活定义整个测试任务，测试以数据库为对象，通过被测数据库中进行股票交易的客户数量把控测试规模，执行过程严格遵守基准约束，运行状态全程监控，测试结束自动输出报告，结果反映数据库软件性能，同时也体现服务器硬件性能。'}
      ],
      '龙测AI-TestOps云平台': [
        {url: 'https://media.w3.org/2010/05/sintel/trailer.mp4', title: '龙测自动化测试云平台',
          description:'龙测科技以自主AI+Robot+Model为技术核心，即AI学习生成业务流程图，测试用户通过组合流程图成为积木图，机器人通过视觉和代码+机械化方式稳定执行。龙测产品采用图形化、零代码、全自动的方式取代手工测试，帮助客户快速发现Bug，保障产品准时上线。与市面上的主流产品相比，龙测产品不仅适用于传统软件测试，还适用于工业软件测试、机械设备测试等诸多领域，应用广泛。产品凭借低成本、准确率高、24小时全天候全自动化代替人工测试等优势与微信、支付宝、潍柴动力、中国移动、中软国际、东软、TCL等多家知名客户建立了长期合作关系。'}
      ],
      '软件造价评估系统': [
        {url: 'https://stream7.iqilu.com/10339/upload_transcode/202002/09/20200209105011F0zPoYzHry.mp4', title: '基本概念',
          description:'软件造价是一个复杂的技术，它受到项目规模，用户需求.时间约束、资源约束、市场竞争、人工成本和传统文化等诸多因素的影响。全球软件造价的标准、方法、工具还在不断发展中，远未达到精准、完美的程度。大数据、人工智能和信创产业的发展，给软件造价带来了新的解决方案，国产化的软件造价分析工具恰逢其时。'},
        {url: 'https://stream7.iqilu.com/10339/upload_transcode/202002/09/20200209104902N3v5Vpxuvb.mp4', title: '功能评估',
          description:'软件造价是一个复杂的技术，它受到项目规模，用户需求.时间约束、资源约束、市场竞争、人工成本和传统文化等诸多因素的影响。全球软件造价的标准、方法、工具还在不断发展中，远未达到精准、完美的程度。大数据、人工智能和信创产业的发展，给软件造价带来了新的解决方案，国产化的软件造价分析工具恰逢其时。'},
        {url: 'http://vjs.zencdn.net/v/oceans.mp4', title: '工量评估',
          description:'软件造价是一个复杂的技术，它受到项目规模，用户需求.时间约束、资源约束、市场竞争、人工成本和传统文化等诸多因素的影响。全球软件造价的标准、方法、工具还在不断发展中，远未达到精准、完美的程度。大数据、人工智能和信创产业的发展，给软件造价带来了新的解决方案，国产化的软件造价分析工具恰逢其时。'},
        {url: 'https://www.w3schools.com/html/movie.mp4', title: '综合评估',
          description:'软件造价是一个复杂的技术，它受到项目规模，用户需求.时间约束、资源约束、市场竞争、人工成本和传统文化等诸多因素的影响。全球软件造价的标准、方法、工具还在不断发展中，远未达到精准、完美的程度。大数据、人工智能和信创产业的发展，给软件造价带来了新的解决方案，国产化的软件造价分析工具恰逢其时。'}
      ],
      '信息安全风评系统': [
        {url: 'http://www.w3school.com.cn/example/html5/mov_bbb.mp4', title: '信息安全风险评估',
          description:'信息安全风险评估是参照风险评估标准和管理规范，对信息系统的资产价值、潜在威胁、薄弱环节、已采取的防护措施等进行分析，判断安全事件发生的概率以及可能造成的损失，提出风险管理措施的过程。'}
      ]
    };
    return videoMap[title] || [];
  }

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
          Grid() {
            ForEach(this.filteredList(), (item: ArticleClass) => {
              GridItem() {
                Column() {
                  Image(item.imageSrc)
                    .width('100%')
                    .aspectRatio(1.5)
                    .objectFit(ImageFit.Contain)
                    .borderRadius(8)
                  Column() {
                    Text(item.title)
                      .width('100%')
                      .fontSize(14)
                      .fontWeight(FontWeight.Medium)
                      .textAlign(TextAlign.Center)
                      .margin({ top: 8, bottom: 4 })
                    Text(item.brief)
                      .width('100%')
                      .fontSize(10)
                      .fontColor('#666666')
                      .textAlign(TextAlign.End)
                      .margin({ right: 4,bottom:1 })
                  }
                  .height('40%')
                  .justifyContent(FlexAlign.SpaceBetween)
                }
                .width('100%')
                .height(180)
                .backgroundColor(Color.White)
                .borderRadius(8)
                .shadow({
                  radius: 4,
                  color: '#00000010',
                  offsetX: 1,
                  offsetY: 1
                })
                .onClick(() => {
                  let videoList = this.getVideoListByTitle(item.title);

                  router.pushUrl({
                    url:'view/Tech/TechDetail',
                    params:{
                      title:item.title,
                      videoList: JSON.stringify(videoList)
                    }
                  })
                })

              }
              .padding(4)
            })
          }
          .columnsTemplate('1fr 1fr')
          .rowsGap(16)
          .columnsGap(16)
          .width('100%')
          .height('100%')
          .padding(10)
          .margin({ top: 10 })
          .nestedScroll({
            scrollForward: NestedScrollMode.SELF_FIRST,
            scrollBackward: NestedScrollMode.SELF_FIRST
          })

        }
      }
      .width('100%')
      .alignItems(HorizontalAlign.Start)

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
struct TechTutorialItem {
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

  }
}