import { HomeContent } from '../view/HomeContent';
import { NewsContent } from '../view/News/NewsContent';
import { TechContent } from '../view/Tech/TechContent';
import { CooperationContent } from '../view/CooperationContent';
import {TabItem}from '../model/Types'

@Entry
@Component
struct MainPage {
  @State currentIndex: number = 0;
  private tabItems:Array<TabItem> = [
    {
      title: '首页',
      iconSelected: $r('app.media.home_on'),
      iconNormal: $r('app.media.home_off')
    },
    {
      title: '动态',
      iconSelected: $r("app.media.earth_americas_on"),
      iconNormal: $r("app.media.earth_americas_off")
    },
    {
      title: '技术',
      iconSelected: $r('app.media.tech_on'),
      iconNormal: $r('app.media.tech_off')
    },
    {
      title: '合作',
      iconSelected: $r('app.media.heart_on'),
      iconNormal: $r('app.media.heart_off')
    }
  ];

  // 构建底部导航项
  @Builder
  TabItemBuilder(item: TabItem, index: number) {
    Column() {
      Image(this.currentIndex === index ? item.iconSelected : item.iconNormal)
        .width(24)
        .height(24)
        .margin({ bottom: 4 })

      Text(item.title)
        .fontSize(12)
        .fontColor(this.currentIndex === index ? '#007AFF' : '#999999')
    }
    .width('25%')
    .height(56)
    .justifyContent(FlexAlign.Center)
    .onClick(() => {
      this.currentIndex = index; // 点击切换当前选中项
    })
  }

  build() {
    Column() {
      Scroll() {
        Column() {
          if (this.currentIndex === 0) {
            HomeContent(); // 首页内容
          } else if (this.currentIndex === 1) {
            NewsContent(); // 动态内容
          } else if (this.currentIndex === 2) {
            TechContent(); // 技术内容
          } else {
            CooperationContent(); // 合作内容
          }
          Blank()
            .height(30)
        }
        .width('100%')
      }
      .layoutWeight(1) // 占满除导航栏外所有高度
      .scrollBar(BarState.Auto)
      .scrollBarColor('#888888')
      .scrollBarWidth(6)
      // 底部导航栏
      Row() {
        ForEach(this.tabItems, (item:TabItem, index:number) => {
          this.TabItemBuilder(item, index)
        })
      }
      .width('100%')
      .height(56)
      .backgroundColor('#FFFFFF')
      .border({ width: 1, color: '#F0F0F0' })
    }
    .width('100%')
    .height('100%')
  }
}