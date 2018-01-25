# RegionAndGifViewPager
支持图片多区域点击,以及动态图的ViewPager

调用CirculatoryViewPager中的 setRegionAdData()方法显示图片

setRegionAdData中的传参List<AdvertInfo> adRegionList
AdvertInfo包括图片地址, 跳转相关数据
支持单区域、多区域点击事件
静态图, 动态图
多区域点击需设置AdvertInfo中的adPageList
