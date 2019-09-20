var data = [
  {
    name: "基坑",
    page: "文案文案文案文案文案文案文案文案文案文案文案文案文案文案"
  },
  {
    name: "模板工程",
    page: "文案文案文案文案文案文案文案文案文案文案文案文案文案文案"
  },
  {
    name: "临时施工用电",
    page: "文案文案文案文案文案文案文案文案文案文案文案文案文案文案"
  },
  {
    name: "脚手架工程",
    page: "文案文案文案文案文案文案文案文案文案文案文案文案文案文案"
  },
  {
    name: "物料提取机",
    page: "文案文案文案文案文案文案文案文案文案文案文案文案文案文案"
  },
  {
    name: "吊塔",
    page: "文案文案文案文案文案文案文案文案文案文案文案文案文案文案"
  }
];
var html = '';
for (var i in data) {
  if (i == data.length - 1) {
    html += '<li class="btn" style="margin-right: 0;">';
    html += '<a class="btna">';
    html += '<span>' + data[i].name + '</span></a>';
    html += '<img src="../img/stick.png" class="stick">';
    html += '<div class="stickBox" >' + data[i].page;
    html += '<img class="lt" src="../img/stickL.png"/>';
    html += '<img class="rt" src="../img/stickL.png"/>';
    html += '<img class="rb" src="../img/stickL.png"/>';
    html += '<img class="lb" src="../img/stickL.png"/>';
    html += '</div>';
    html += '</li>';
  } else {
    html += '<li class="btn">';
    html += '<a class="btna">';
    html += '<span>' + data[i].name + '</span></a>';
    html += '<img src="../img/stick.png" class="stick">';
    html += '<div class="stickBox">' + data[i].page;
    html += '<img src="../img/stickL.png" class="lt"/>';
    html += '<img class="rt" src="../img/stickL.png"/>';
    html += '<img class="rb" src="../img/stickL.png"/>';
    html += '<img class="lb" src="../img/stickL.png"/>';
    html += '</div>';
    html += '</li>';
  }
}
$("#btns").append(html);

$('canvas').on('click', function () {
  //         $modal.modal('close');
  //
  $('#your-modal').modal({
    dimmer: false
  });
});


//选择天气图片
function chooseImg(id) {
  switch (id) {
    case '0':
      return '../static/img/weather/1.png';

    case '1':
      return '../static/img/weather/2.png';

    case '2':
      return '../static/img/weather/3.png';

    case '3':
    case '4':
    case '5':
    case '7':
    case '8':
      return '../static/img/weather/4.png';

    case '6':
      return '../static/img/weather/6.png';

    case '13':
    case '14':
    case '15':
    case '16':
      return '../static/img/weather/5.png';

    case '33':
      return '../static/img/weather/7.png';

    default:
      return '../static/img/weather/7.png';
  }
}

function getWeather(trHeight) {
  var data = {
    "today": "THU",
    "todayMaxTemp": "30",
    "todayMinTemp": "25",
    "windSpeed": "4mph",
    "weatherCode": "1",
    "otherDayWeather": [{
      "datetime": "FRI",
      "weatherCode": "0",
      "temperature": "28"
    }, {
      "datetime": "THU",
      "weatherCode": "7",
      "temperature": "28"
    }, {
      "datetime": "STA",
      "weatherCode": "1",
      "temperature": "28"
    }, {
      "datetime": "SUN",
      "weatherCode": "3",
      "temperature": "28"
    }, {
      "datetime": "MON",
      "weatherCode": "4",
      "temperature": "28"
    }, {
      "datetime": "TUE",
      "weatherCode": "5",
      "temperature": "28"
    }, {
      "datetime": "WED",
      "weatherCode": "6",
      "temperature": "28"
    }]
  };
  //使用ajax调用接口
  // 			$.post("",{},function(data){
  //
  // 			});
  //天气状况判断 拼接天气table样式
  if (data != null) { // && !(data instanceof undefined)

    var html =
      "<table border='0' width='100%' style='text-align: center;vertical-align: middle;font-size:10px'>";

    for (var i = 0; i < 3; i++) { //三列

      var size = data.otherDayWeather.length;

      if (i == 0) { //日期
        html += "<tr style='color: #FFFFFF' height='" + trHeight + "'>";
        html += "<td rowspan='2' style='color: #00C6FB'>" + data.todayMaxTemp + "</td>";
        html += "<td rowspan='2' style='color: #00C6FB'><img src='" + chooseImg(data.weatherCode) +
          "' width='20px' height='20px' /></td>";
        for (var j = 0; j < size; j++) {
          html += "<td>" + data.otherDayWeather[j].datetime + "</td>";
        }
      } else if (i == 1) { //天气图片
        html += "<tr height='" + trHeight + "'>";
        for (var j = 0; j < size; j++) {
          var src = chooseImg(data.otherDayWeather[j].weatherCode);
          html += "<td><img src='" + src + "' width='20px' height='20px' /></td>";
        }
      } else if (i == 2) { //天气温度
        html += "<tr style='color: #00C6FB' height='" + trHeight + "'>";
        html += "<td>" + data.today + "</td>";
        html += "<td>" + data.windSpeed + "/" + data.todayMinTemp + "</td>";
        for (var j = 0; j < size; j++) {
          html += "<td>" + data.otherDayWeather[j].temperature + "</td>";
        }
      }

      html += "</tr>";
    }
    //插入到页面
    $("#weather").css("display", "");
    $("#weather").html(html);
  }

}

$('#select1').selected({
  // btnWidth: '300px',
  btnSize: 'sm',
  // btnStyle: 'primary',
  maxHeight: '100px'
});

$(window).resize(function () {
  var windowHeight = $(window).height(); //窗口可视高度
  var head = $(".header").height(); //头部高度
  var foot = $("#footer").height(); //底部高度

  var otherHeight = windowHeight - (head + foot); //整个页面的剩余高度

  var marginHeight = parseInt($('.videoUl').css('margin-left')); //获取ul margin值

  var liHeight = (otherHeight - (marginHeight) * 3) / 2; //每个模块的高度也就是li的高度

  $(".videoUl").css("height", liHeight); //设置每个ul的高度与模块的高度一致

  var w = $("#ul").width(); //视频模块整体宽度
  //视频模块
  $(".video-js").css("width", w / 2);
  $(".video-js").css("height", liHeight / 2);
  console.log("  marginHeight" + marginHeight + "  liHeight" + liHeight + "  liHeight/2  " + liHeight / 2);

  //模态框高度
  $("#modelBox").css("height", windowHeight * 0.7);


  function lineChartMethod() {
    var w = $("#bar_line").width() - 50;
    //设置图表宽高
    $("#lineChart").css("width", w);
    //$("#lineChart").css("height", parseFloat(9 * w / 16 * 0.8).toFixed(3));
    $("#lineChart").css("height", parseFloat(liHeight * 0.66).toFixed(3)); //占整个高度的70%
    //重新绘制图表
    lineChart.resize();
  }


  function barChartMethod() {
    var w = $("#bar").width();
    //设置图表宽高
    $("#barBox").css("height", liHeight);
    $("#bar_chart").css("width", w);
    //$("#bar_chart").css("height", parseFloat(9 * w / 16).toFixed(3));
    $("#bar_chart").css("height", (liHeight / 2) * 1.5);
    $(".sign").css("height", (liHeight / 2) * 0.5);
    //重新绘制图表
    barChart.resize();
  }

  function pieChartMethod() {
    var w = $("#pie").width();
    //设置图表宽高
    $("#pie_chart").css("width", w);
    $("#pie_chart").css("height", liHeight);
    //重新绘制图表
    pieChart.resize();
  }


  function planChartMethod() {
    var w = $("#plan").width();
    //设置图表宽高
    $("#plan_bar").css("width", w);
    $("#plan_bar").css("height", liHeight);
    //重新绘制图表
    planChart.resize();
  }

  function trendChartMethod() {
    var w = $("#trend").width();
    //设置图表宽高
    $("#line_chart").css("width", w);
    $("#line_chart").css("height", liHeight);
    //重新绘制图表
    trendChart.resize();
  }

  var btnw = $(".btn").width();
  $(".btna").css("height", btnw);
  $(".stick").css("height", btnw / 2.5);
  $(".stick").css("bottom", -btnw / 2.5);
  $(".stickBox").css("top", btnw * 1.5);


  function methods(resolve, reject) {
    getWeather(liHeight * 0.056);
    lineChartMethod();
    barChartMethod();
    pieChartMethod();
    planChartMethod();
    trendChartMethod();
  }

  var p = new Promise(methods);
  p.then(
    //重新设置div垂直居中
    $(".mydiv").css({
      position: "absolute",
      left: (($(window).width() - $(".mydiv").width()) / 2) - "10px",
      top: (($(window).height() - $(".mydiv").height()) / 2) - "10px"
    }));

});

var style = {
  color: '#FFFFFF',
  //字体风格,'normal','italic','oblique'
  fontStyle: 'normal',
  //字体粗细 'normal','bold','bolder','lighter',100 | 200 | 300 | 400...
  fontWeight: 'normal',
  //字体系列
  fontFamily: 'PingFang',
  //字体大小
  fontSize: 18
};

var lineChart;
var barChart;
var pieChart;
var planChart;
var trendChart;

//加载时就执行
$(function () {

  function initChart(resolve, reject) {
    lineChart = echarts.init(document.getElementById("lineChart"));
    barChart = echarts.init(document.getElementById("bar_chart"));
    pieChart = echarts.init(document.getElementById("pie_chart"));
    planChart = echarts.init(document.getElementById("plan_bar"));
    trendChart = echarts.init(document.getElementById("line_chart"));
  }

  var c = new Promise(initChart);
  c.then(new Promise(function (resolve, reject) {
    lineChartMethod();
    barChartMethod();
    pieChartMethod();
    planChartMethod();
    trendChartMethod();
  })).then($(window).resize());

});


function lineChartMethod() {

  // X轴时间
  var daysOfMonth = [];

  function createMonthDay(year, month) {
    var d = new Date();
    var lastDayOfMonth = new Date(year, month, 0).getDate();
    for (var i = 1; i <= lastDayOfMonth; i++) {
      daysOfMonth.push(year + '-' + month + '-' + i);
    }
    return daysOfMonth;
  }
  createMonthDay(2017, 12);
  // X轴时间
  var axisData = daysOfMonth;

  var option = {

    //             title: {
    //                 text: '空气质量趋势图',
    //                 subtext: '',
    //                 left: '8%',
    //                 top: '8%',
    //                 textStyle: style
    //             },
    tooltip: { //houver时提示数据
      trigger: 'axis'
    },
    calculable: true,
    // legend: {
    //     data: ['线下', '线上', '总计'],
    //     x: 'right',
    //     y: 'top',
    //     top: '5%',
    //     // itemGap: 15,
    //     // itemHeight: 19,
    //     // itemWidth: 30,
    //     textStyle: {
    //         color: '#085DBA'
    //     }
    // },
    dataZoom: { //滚动条
      show: false,
      y: 270,
      // realtime: true,
      start: 30,
      end: 70,
      height: 12,
      handleColor: '#1a8edc',
      handleSize: 5,
      fillerColor: '#a5cdea'
    },
    grid: { //整体图表位置
      y: 40,
      y2: 80
    },
    xAxis: [{
      type: 'category',
      axisLine: { //坐标轴
        show: true,
        lineStyle: {
          width: 1,
        }
      },
      axisTick: { //刻度
        lineStyle: {
          width: 1,
        }
      },
      axisLabel: { //坐标轴文字设置
        textStyle: {
          color: '#085DBA'
          // baseline: 'middle'
        },
        rotate: 28 //文字旋转
      },
      splitLine: {
        show: true,
        lineStyle: {
          color: '#085DBA',
          width: 1,
          type: 'solid'
        }
      },
      data: axisData
    }],
    yAxis: [{
      type: 'value',
      // name : '收入',
      nameTextStyle: { //坐标轴标题文字设置

      },
      axisLine: {
        show: true,
        onZero: true,
        lineStyle: {
          color: '#085DBA',
          width: 2,
          type: 'solid'
        }
      },
      splitLine: {
        show: true,
        lineStyle: {
          color: '#085DBA',
          width: 1,
          type: 'solid'
        }
      },
      axisTick: { //刻度
        show: false
      },
      axisLabel: { //坐标轴文字设置
        textStyle: {

        }
      }

    },
      {
        type: 'value', //曲线图y轴
        // name : '温度',
        axisLine: {
          show: true,
          onZero: true,
          lineStyle: {
            color: '#085DBA',
            width: 2,
            type: 'solid'
          }
        },
        axisLabel: { //坐标轴文字设置
          show: false,
          formatter: '{value} °C'
        },
        splitLine: false, //网格线
        axisTick: { //刻度
          show: false,
          lineStyle: {
            width: 1
          }
        }
      }
    ],
    series: [

      {
        name: '线下',
        type: 'bar',
        barGap: 0, //两柱子距离
        data: [2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3, 2.0, 4.9, 7.0,
          23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3, 3.3, 2.0, 4.9, 7.0, 23.2,
          25.6, 76.7
        ],
        itemStyle: {
          normal: {
            color: new echarts.graphic.LinearGradient(
              0, 0, 0, 1, [{
                offset: 0,
                color: '#22dffe'
              },
                {
                  offset: 0.2,
                  color: '#22dffe'
                },
                {
                  offset: 1,
                  color: '#5b62b5'
                }
              ]
            )
          },
          emphasis: {
            color: new echarts.graphic.LinearGradient(
              0, 0, 0, 1, [{
                offset: 0,
                color: '#2378f7'
              },
                {
                  offset: 0.7,
                  color: '#2378f7'
                },
                {
                  offset: 1,
                  color: '#83bff6'
                }
              ]
            )
          }
        }
      },
      {
        name: '线上',
        type: 'bar',
        barGap: 0, //两柱子距离
        data: [2.6, 5.9, 9.0, 26.4, 28.7, 70.7, 175.6, 182.2, 48.7, 18.8, 6.0, 2.3, 2.6, 5.9, 9.0,
          26.4, 28.7, 70.7, 175.6, 182.2, 48.7, 18.8, 6.0, 2.3, 182.2, 48.7, 18.8, 6.0, 2.3,
          2.6, 5.9
        ],
        itemStyle: {
          normal: {
            color: new echarts.graphic.LinearGradient(
              0, 0, 0, 1, [{
                offset: 0,
                color: '#f77062'
              },
                {
                  offset: 0.2,
                  color: '#f77062'
                },
                {
                  offset: 1,
                  color: '#fe5196'
                }
              ]
            )
          },
          emphasis: {
            color: new echarts.graphic.LinearGradient(
              0, 0, 0, 1, [{
                offset: 0,
                color: '#2378f7'
              },
                {
                  offset: 0.7,
                  color: '#2378f7'
                },
                {
                  offset: 1,
                  color: '#83bff6'
                }
              ]
            )
          }
        },
      },
      {
        name: '总计',
        showSymbol: true,
        symbol: 'circle', //设定为实心点
        symbolSize: 10, //设定实心点的大小
        type: 'line',
        yAxisIndex: 1,
        data: [2.0, 2.2, 3.3, 4.5, 6.3, 10.2, 20.3, 23.4, 23.0, 16.5, 12.0, 6.2, 2.0, 2.2, 3.3, 4.5,
          6.3, 10.2, 20.3, 23.4, 23.0, 16.5, 12.0, 6.2, 10.2, 20.3, 23.4, 23.0, 16.5, 12.0,
          6.2
        ],
        itemStyle: {
          normal: {
            color: "rgba(34,223,254,0.6)", //圆点颜色
            lineStyle: {
              color: "#00C6FB"
            }
          }
        }

      }
    ]

  }

  lineChart.setOption(option);
}

function barChartMethod() {
  var dataAxis = ['点', '击', '柱', '子', '或', '者', '两', '指', '在', '触', '屏', '上', '滑', '动', '能', '够', '自', '动', '缩',
    '放'
  ];
  var data = [220, 182, 191, 234, 290, 330, 310, 123, 442, 321, 90, 149, 210, 122, 133, 334, 198, 123, 125, 220];
  var yMax = 500;
  var dataShadow = [];

  for (var i = 0; i < data.length; i++) {
    dataShadow.push(yMax);
  }

  var option = {
    title: {
      text: '项目计量统计图',
      subtext: '',
      left: '8%',
      top: '2%',
      textStyle: style
    },
    xAxis: {
      data: dataAxis,
      axisLabel: {
        inside: true,
        textStyle: {
          color: '#fff'
        }
      },
      axisTick: {
        show: false
      },
      axisLine: {
        show: true,
        onZero: true,
        lineStyle: {
          color: '#085DBA',
          width: 2,
          type: 'solid'
        }
      },
      splitLine: {
        show: true,
        lineStyle: {
          color: '#085DBA',
          width: 1,
          type: 'solid'
        }
      },
      z: 1
    },
    yAxis: {
      axisLine: {
        show: true,
        onZero: true,
        lineStyle: {
          color: '#085DBA',
          width: 2,
          type: 'solid'
        }
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        textStyle: {

        }
      },
      splitLine: {
        show: true,
        lineStyle: {
          color: '#085DBA',
          width: 1,
          type: 'solid'
        }
      }
    },
    dataZoom: [{
      type: 'inside'
    }],
    series: [{ // For shadow
      type: 'bar',
      itemStyle: {
        normal: {
          color: 'rgba(0,0,0,0.05)'
        }
      },
      barGap: '-100%',
      barCategoryGap: '40%',
      data: dataShadow,
      animation: false
    },
      {
        type: 'bar',
        itemStyle: {
          normal: {
            color: new echarts.graphic.LinearGradient(
              0, 0, 0, 1, [{
                offset: 0,
                color: '#22dffe'
              },
                {
                  offset: 0.2,
                  color: '#22dffe'
                },
                {
                  offset: 1,
                  color: '#5b62b5'
                }
              ]
            )
          },
          emphasis: {
            color: new echarts.graphic.LinearGradient(
              0, 0, 0, 1, [{
                offset: 0,
                color: '#2378f7'
              },
                {
                  offset: 0.7,
                  color: '#2378f7'
                },
                {
                  offset: 1,
                  color: '#83bff6'
                }
              ]
            )
          }
        },
        data: data
      }
    ]
  };
  //设置图表信息
  barChart.setOption(option);
}


function pieChartMethod() {

  var downloadJson = [{
    name: "未整改",
    value: 17365
  },
    {
      name: "已整改",
      value: 4079
    }
  ];

  var themeJson = [{
    name: "未整改",
    value: 1594
  }, {
    name: "已整改",
    value: 925
  }];

  var waterMarkText = 'ECHARTS';

  var canvas = document.createElement('canvas');
  var ctx = canvas.getContext('2d');
  canvas.width = canvas.height = 100;
  ctx.textAlign = 'center';
  ctx.textBaseline = 'middle';
  ctx.globalAlpha = 0.08;
  ctx.font = '20px Microsoft Yahei';
  ctx.translate(50, 50);
  ctx.rotate(-Math.PI / 4);
  ctx.fillText(waterMarkText, 0, 0);

  var option = {
    //     backgroundColor: {
    //         type: 'pattern',
    //         image: canvas,
    //         repeat: 'repeat'
    //     },

    title: [{
      text: '工程问题统计图',
      left: '8%',
      top: '2%',
      textStyle: style,
    }, {
      text: '质量类',
      x: '25%',
      y: '80%',
      textAlign: 'center',
      textStyle: style
    }, {
      text: '安全环保类',
      x: '65%',
      y: '80%',
      textAlign: 'center',
      textStyle: style
    }],

    series: [{
      type: 'pie',
      radius: ['40%', '45%'],
      center: ['25%', '50%'],
      data: [{
        value: 0,
        name: ''
      }],
      itemStyle: {
        normal: {
          labelLine: {
            show: false
          }
        }
      }
    }, {
      type: 'pie',
      selectedMode: 'single',
      radius: [0, '30%'],
      center: ['25%', '50%'],
      label: {
        normal: {
          position: 'outer',
          color: '#00C6FB',
          textStyle: {
            fontWeight: 200,
            fontSize: 10 //文字的字体大小
          }
        }
      },
      labelLine: {
        normal: {
          show: true,
          smooth: 0.2,
          length: 50,
          length2: 20
        }
      },
      data: downloadJson
    },
      {
        type: 'pie',
        radius: ['40%', '45%'],
        center: ['65%', '50%'],
        data: [{
          value: 0,
          name: ''
        }],
        itemStyle: {
          normal: {
            labelLine: {
              show: false
            }
          }
        }
      },
      {
        type: 'pie',
        radius: [0, '30%'],
        center: ['65%', '50%'],
        label: {
          normal: {
            formatter: '{b}：{c}  {d}%', //{b|{b}：}{c}  {per|{d}%}
            color: '#00C6FB',
            //                     backgroundColor: '#eee',
            //                     borderColor: '#aaa',
            //                     borderWidth: 1,
            //                     borderRadius: 4,
            // shadowBlur:3,
            // shadowOffsetX: 2,
            // shadowOffsetY: 2,
            // shadowColor: '#999',
            // padding: [0, 7],
            rich: {
              b: {
                color: '#334455',
                fontSize: 16,
                lineHeight: 33
              },
              per: {
                color: '#eee',
                backgroundColor: '#334455',
                padding: [2, 4],
                borderRadius: 2
              }
            }
          }
        },
        labelLine: {
          normal: {
            show: true,
            smooth: 0.2,
            length: 50,
            length2: 20
          }
        },
        data: themeJson
      }

    ],
    color: [new echarts.graphic.LinearGradient(
      0, 0, 0, 1, [{
        offset: 0,
        color: '#22dffe'
      },
        {
          offset: 0.2,
          color: '#22dffe'
        },
        {
          offset: 1,
          color: '#5b62b5'
        }
      ]
    ), 'rgb(89,102,184)', new echarts.graphic.LinearGradient(
      0, 0, 0, 1, [{
        offset: 0,
        color: '#22dffe'
      },
        {
          offset: 0.2,
          color: '#22dffe'
        },
        {
          offset: 1,
          color: '#5b62b5'
        }
      ]
    ), 'rgba(0,198,251,0.3)']
  }
  pieChart.setOption(option);
}


function planChartMethod() {
  var dataAxis = ['点', '击', '柱', '子', '或', '者', '两', '指', '在', '触', '屏', '上', '滑', '动', '能', '够', '自', '动', '缩',
    '放'
  ];
  var data = [220, 182, 191, 234, 290, 330, 310, 123, 442, 321, 90, 149, 210, 122, 133, 334, 198, 123, 125, 220];
  var yMax = 500;
  var dataShadow = [];

  for (var i = 0; i < data.length; i++) {
    dataShadow.push(yMax);
  }

  var option = {
    title: {
      text: '形象进度统计图',
      subtext: '',
      left: '8%',
      top: '2%',
      textStyle: style
    },
    xAxis: {
      data: dataAxis,
      axisLabel: {
        inside: true,
        textStyle: {
          color: '#fff'
        }
      },
      axisTick: {
        show: false
      },
      axisLine: {
        show: true,
        onZero: true,
        lineStyle: {
          color: '#085DBA',
          width: 2,
          type: 'solid'
        }
      },
      splitLine: {
        show: true,
        lineStyle: {
          color: '#085DBA',
          width: 1,
          type: 'solid'
        }
      },
      z: 1
    },
    yAxis: {
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        textStyle: {

        }
      },
      axisLine: {
        show: true,
        onZero: true,
        lineStyle: {
          color: '#085DBA',
          width: 2,
          type: 'solid'
        }
      },
      splitLine: {
        lineStyle: {
          color: '#085DBA',
          width: 1,
          type: 'solid'
        }
      }
    },
    dataZoom: [{
      type: 'inside'
    }],
    series: [{ // For shadow
      type: 'bar',
      itemStyle: {
        normal: {
          color: 'rgba(0,0,0,0.05)'
        }
      },
      barGap: '-100%',
      barCategoryGap: '40%',
      data: dataShadow,
      animation: false
    },
      {
        type: 'bar',
        itemStyle: {
          normal: {
            color: new echarts.graphic.LinearGradient(
              0, 0, 0, 1, [{
                offset: 0,
                color: '#22dffe'
              },
                {
                  offset: 0.2,
                  color: '#22dffe'
                },
                {
                  offset: 1,
                  color: '#5b62b5'
                }
              ]
            )
          },
          emphasis: {
            color: new echarts.graphic.LinearGradient(
              0, 0, 0, 1, [{
                offset: 0,
                color: '#2378f7'
              },
                {
                  offset: 0.7,
                  color: '#2378f7'
                },
                {
                  offset: 1,
                  color: '#83bff6'
                }
              ]
            )
          }
        },
        data: data
      }
    ]
  };
  //设置图表信息
  planChart.setOption(option);
}


function trendChartMethod() {
  var option = {
    title: {
      text: '项目计量趋势图',
      subtext: '',
      left: '8%',
      top: '2%',
      textStyle: style
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
      axisLine: {
        show: true,
        onZero: true,
        lineStyle: {
          color: '#085DBA',
          width: 2,
          type: 'solid'
        }
      },
      splitLine: {
        show: true,
        lineStyle: {
          color: '#085DBA',
          width: 1,
          type: 'solid'
        }
      },
      z: 1
    },
    yAxis: {
      type: 'value',
      axisLine: {
        show: true,
        onZero: true,
        lineStyle: {
          color: '#085DBA',
          width: 2,
          type: 'solid'
        }
      },
      splitLine: {
        show: true,
        lineStyle: {
          color: '#085DBA',
          width: 1,
          type: 'solid'
        }
      }
      // 								axisLabel : {
      // 						　　show : true,
      // 						　　textStyle : {
      // 						　　color : 'blue',
      // 						　　align : 'left'
      // 						　　}
      //             }
    },
    series: [{
      showSymbol: true,
      symbol: 'circle', //设定为实心点
      symbolSize: 10, //设定实心点的大小
      data: [820, 932, 901, 934, 1290, 1330, 1320],
      type: 'line',
      areaStyle: {
        normal: {
          color: new echarts.graphic.LinearGradient(
            0, 0, 0, 1, [{
              offset: 0,
              color: '#22dffe'
            },
              {
                offset: 0.2,
                color: '#22dffe'
              },
              {
                offset: 1,
                color: 'rgba(91,98,181,0.05)'
              }
            ]
          )
        }
      },
      itemStyle: {
        normal: {
          color: "rgba(34,223,254,0.6)", //圆点颜色
          lineStyle: {
            color: "#00C6FB"
          }
        }
      }
    }]
  };

  trendChart.setOption(option);
}
