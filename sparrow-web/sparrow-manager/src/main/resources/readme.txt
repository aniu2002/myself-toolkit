  eq(0) => jquery obj
  [0] => dom

     这样获取的是dom对象，不是jquery包装的
  $('#id')[0]
 

1、  http://192.168.100.137:9090/collect/reset ，可以清空data文件，并重新从xml文件导入数据进去
2、  http://192.168.100.137:9090/cmd/statistic?date=2012-12-03  将2012-12-03的数据按照模板生成可浏览的html文件和email信息,date默认为当天的