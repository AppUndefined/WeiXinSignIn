package com.discern.discern.controller;

import com.alibaba.fastjson.JSONObject;
import com.discern.discern.entity.Baseface;
import com.discern.discern.entity.User;
import com.discern.discern.service.FaceService;
import com.discern.discern.service.RedisService;
import com.discern.discern.utils.*;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.bytedeco.javacpp.opencv_core.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

@Controller
@RequestMapping("/discern")
@ConfigurationProperties(prefix="facehost")
public class DiscernController {
    private String host;
    @Autowired
    private FaceService faceService;
    @Autowired
    private RedisService redisService;
    private final Logger logger = LoggerFactory.getLogger(DiscernController.class);

    /**
     * 批量导入底库
     * @param file
     * @param request
     * @return
     */
    @RequestMapping(value = "/batchAddBaseface", method = RequestMethod.POST,headers="content-type=multipart/form-data")
    @ResponseBody
    public Map importProject( MultipartFile file ,HttpServletRequest request) {
        HashMap<String, Object> map = new HashMap<>();
        String token = CookieUtils.getCookieValue(request, "JSESSIONID");
        String jsonUser = redisService.getData(token);
        User user = JSONObject.parseObject(jsonUser, User.class);
        String originalFilename = file.getOriginalFilename();
        Workbook wb = null;
        File file1 = new File(file.getName());
        try {
            FileUtils.copyInputStreamToFile(file.getInputStream(),file1);
            ZipUtil.unZipFiles(file1,"/zipfile/");
            File file2 = new File("/zipfile/");
            File[] files = file2.listFiles();
            for (File file3 : files) {
                    if (FileUtil.getSuffix(file3.getName()).equals("xlsx")) {
                        wb = new XSSFWorkbook(new FileInputStream(file3));
                    } else if(FileUtil.getSuffix(file3.getName()).equals("xls")){
                        wb = new HSSFWorkbook(new FileInputStream(file3));
                    }
            }
            if(wb!=null){
                //解析excel
                Sheet sheet = wb.getSheetAt(0);//获取第一张表
                Map result = ExcelUtil.ExcelDispose(sheet, files, user, host);
                map.put("code",200);
                if((int)result.get("fail")>0) {
                    map.put("msg", "导入成功" + result.get("success") + "条，失败" + result.get("fail") + "条，原因：未找到如下图片" + result.get("failList"));
                }else{
                    map.put("msg", "导入成功"+ result.get("success") + "条.");
                }
                FileUtils.deleteQuietly(file2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code",500);
            map.put("msg","导入失败");
            return  map;
        }

//        ZipUtil.unZip();

        return  map;
    }
    //在底库中添加一张人脸照片
    //@RequiresPermissions("add:face")
    @RequestMapping(value = "/addBaseface", method = RequestMethod.POST)
    @ResponseBody
    public Map add(Baseface face, HttpServletRequest request) throws Exception {

        HashMap<String, Object> map = new HashMap<>();
        String token = CookieUtils.getCookieValue(request, "JSESSIONID");
        String jsonUser = redisService.getData(token);
        User user = JSONObject.parseObject(jsonUser, User.class);
/*        String fileSufix = FileNameUtil.getFileSufix(file.getOriginalFilename());
        Base64Utils base64Utils = Base64Utils.getInstance();
        String base64face = base64Utils.file2Base64(file.getBytes());
        if(user==null||"".equals(jsonUser)||jsonUser==null||file==null){
            map.put("code",500);
            map.put("message","检查请求参数");
            return map;
        }*/
        try {
            if(face!=null){
                face.setDepartment(user.getDepartment());
                face.setCreateDate(new Date());
                face.setUser(user.getUsername());
                faceService.save(face);
            }
            //向底库添加人脸
            HashMap<String, String> param = new HashMap<>();
            param.put("face_base64",face.getFace());
            param.put("name",face.getName());
            //添加人姓名
            param.put("user",user.getUsername());
            param.put("sex",face.getSex());
            param.put("age",face.getAge());
            String s = HttpUtils.sendPost(host+"/baseface/", param);
            if(s==null&&"".equals(s))
            {
                map.put("code",500);
                map.put("msg","添加失败");
                return  map;
            }
            map.put("code",200);
            map.put("msg","添加成功");
        } catch (Exception e) {
            map.put("code",500);
            map.put("msg","添加失败");
            e.printStackTrace();
        }
        return map;
    }
    //比较照片
    //@RequiresPermissions("compare:face")
    @RequestMapping(value = "/compare", method = RequestMethod.POST)
    @ResponseBody
    public Map compare(Baseface  face, HttpServletRequest request) {

        HashMap<String, Object> map = new HashMap<>();
        String token = CookieUtils.getCookieValue(request, "JSESSIONID");
        String jsonUser = redisService.getData(token);
        User user = JSONObject.parseObject(jsonUser, User.class);
        try {
            if(face==null||face.getFace()==null){
                map.put("code",500);
                map.put("message","检查请求参数");
              return map;
            }
            //开始比较、、、
            HashMap<String, String> param = new HashMap<>();
            param.put("face_base64",face.getFace());
            String s = HttpUtils.sendPost(host+"/results/", param);
            JSONObject results = JSONObject.parseObject(s);

            //比较次数加一
            face.setDepartment(user.getDepartment());
            face.setCreateDate(new Date());
            face.setUser(user.getUsername());
            face.setBaseFace(results.getJSONObject("baseface").getString("face"));
            face.setBaseName(results.getJSONObject("baseface").getString("name"));
            face.setBaseAge(results.getJSONObject("baseface").getString("age"));
            face.setBaseSex(results.getJSONObject("baseface").getString("sex"));
            face.setSimilarity(results.getString("score").substring(0,5));
                    faceService.save(face);
//            face.setDepartment(face.getCompareNumber()+1);
            //给用户绑定角色
            map.put("code",200);
            map.put("oldData",face);
            map.put("results",results);
        } catch (Exception e) {
            map.put("code",500);
            map.put("msg","异常");

            e.printStackTrace();
            return map;
        }
        return map;
    }
    //抓拍照片返回baseface64
    //@RequiresPermissions("compare:face")
    @RequestMapping(value = "/snapshot")
    @ResponseBody
    public Map compare( HttpServletRequest request) throws IOException {
        String path = null;
        HashMap<String, Object> map = new HashMap<>();
        try {
            //-----------------------------------------开始抓拍------------------
            OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
            grabber.start();   //开始获取摄像头数据
//            CanvasFrame canvas = new CanvasFrame("snapshot");//新建一个窗口
//            canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            canvas.setAlwaysOnTop(true);
            OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();//转换器
            IplImage image = null;
            CvRect rect = null;
            while(true)
            {
                if(rect!=null)
//                if(!canvas.isDisplayable()||rect!=null)
                {//窗口是否关闭
                    grabber.stop();//停止抓取
                    grabber.close();//关闭摄像头
                    break;
                }
                Frame f=grabber.grab();

//            image = cvLoadImage("E:\\discern\\src\\main\\resources\\static\\img\\gallery\\111.png");//加载图像
//            Frame f = converter.convert(image);
                Mat scr=converter.convertToMat(f);//将获取的frame转化成mat数据类型
                 rect = JavaCVUtils.detectFace(scr);//人脸检测
                f=converter.convert(scr);//将检测结果重新的mat重新转化为frame
                //将头像区域进行剪裁
                if(rect!=null) {
                    image = converter.convert(f);

                    IplImage iplImage = JavaCVUtils.cutImg(image, rect);
                    Frame f1 = converter.convert(iplImage);
//                     path = JavaCVUtils.SaveIplImage(f1, System.getProperty("user.dir") + "/src/main/resources/static/img/gallery/" + System.currentTimeMillis() + ".jpg");
                     path = JavaCVUtils.SaveIplImage(f1,  System.currentTimeMillis() + ".jpg");
//                    canvas.showImage(f1);//获取摄像头图像并放到窗口上显示， 这里的Frame frame=grabber.grab(); frame是一帧视频图像
                }
                Thread.sleep(50);//50毫秒刷新一次图像
            }
            //------------------------------------------结束抓拍-------------------------------
//            face.setDepartment(face.getCompareNumber()+1);
            //给用户绑定角色
            File file = new File(path);
            Base64Utils base64Utils = Base64Utils.getInstance();
            String snapshot = base64Utils.file2Base64(Base64Utils.copyFile2Byte(file));
           file.delete();
            map.put("code",200);
            map.put("snapshot",snapshot);
        } catch (Exception e) {
            map.put("code",500);
            map.put("msg","异常");
            e.printStackTrace();
        }
        return map;
    }
  //删除底库人脸
  //@RequiresPermissions("delete:face")
  @RequestMapping(value = "/delete", method = RequestMethod.POST)
  @ResponseBody
  public Map delete(Integer faceId, HttpServletRequest request) {
      HashMap<String, Object> map = new HashMap<>();
      try {
          if(faceId==null){
              map.put("code",500);
              map.put("message","检查请求参数");
              return map;
          }
          String  s = host+"/baseface/"+faceId+"/";
          //远程删除、、、1111
          HttpUtils.DELETE(s,null);
          //本地删除
//          faceService.deketeById(faceId);
          map.put("code",200);
          map.put("data","");
      } catch (Exception e) {
          map.put("code",500);
          map.put("msg","异常");
          e.printStackTrace();
      }
      return map;
  }
      //查找底库人脸
      //@RequiresPermissions("read:face")
      @RequestMapping(value = "/find")
      @ResponseBody
      public Map find( Baseface  face, HttpServletRequest request, Integer offset,Integer limit) {
          HashMap<String, Object> map = new HashMap<>();
          try {
              if(face==null){
                  map.put("code",500);
                  map.put("message","检查请求参数");
                  return map;
              }
              //远程查找底库人脸
              HashMap<String, String> param = new HashMap<>();
              if(face.getId()!=null&&!"".equals(face.getId()))
              param.put("id",face.getId()+"");
              if(face.getName()!=null&&!"".equals(face.getName()))
              param.put("name",face.getName());
              if(face.getUser()!=null&&!"".equals(face.getUser()))
              param.put("user",face.getUser());
              if(face.getSex()!=null&&!"".equals(face.getSex()))
              param.put("sex",face.getSex());
//              param.put("age_0",request.getParameter("age_0"));
//              param.put("age_1",request.getParameter("age_1"));
//              param.put("created_0",request.getParameter("created_0"));
//              param.put("created_1",request.getParameter("created_1"));

              String s = HttpUtils.sendGet(host+"/baseface/", param);
              JSONObject jsonObject = pageUtils.adaption(offset/limit, limit,host+"/baseface/",param,(limit/5));
//              JSONObject jsonObject = JSONObject.parseObject(s);
              map.put("code",200);
              map.put("rows",jsonObject.get("results"));
              map.put("total", jsonObject.get("count"));
          } catch (Exception e) {
              map.put("code",500);
              map.put("msg","异常");
              e.printStackTrace();
          }
          return map;
      }
    //查找本地人脸
    //@RequiresPermissions("read:face")
    @RequestMapping(value = "/findLocal", method = RequestMethod.POST)
    @ResponseBody
    public Map findLocal(Baseface  face, HttpServletRequest request) {
        HashMap<String, Object> map = new HashMap<>();
        try {
            if(face==null){
                map.put("code",500);
                map.put("message","检查请求参数");
                return map;
            }
            List<Baseface> basefaceList = faceService.find(face);
            map.put("code",200);
            map.put("data",basefaceList);
        } catch (Exception e) {
            map.put("code",500);
            map.put("msg","异常");
            e.printStackTrace();
        }
        return map;
    }
    //查找最近10条本地人脸
    //@RequiresPermissions("read:face")
    @RequestMapping(value = "/findLocalHistory")
    @ResponseBody
    public Map findLocalHistory( HttpServletRequest request,Baseface baseface) {
        HashMap<String, Object> map = new HashMap<>();
        String token = CookieUtils.getCookieValue(request, "JSESSIONID");
        String jsonUser = redisService.getData(token);
        User user = JSONObject.parseObject(jsonUser, User.class);
        try {
            Page<Baseface> localHistory = faceService.findLocalHistory(user.getDepartment(), baseface);
            map.put("code",200);
            map.put("data",localHistory.getContent());
            map.put("total",localHistory.getTotalElements());
            logger.info(localHistory.getTotalElements()+"");
        } catch (Exception e) {
            map.put("code",500);
            map.put("msg","异常");
            e.printStackTrace();
        }
        return map;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
    @RequestMapping(value = "/improtDemo")
    public void exportParam(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "projectId",required = false)Long projectId) {
        HashMap<String, Object> map = new HashMap<>();
            try{
            File file = new File("/demo.xlsx");// 要下载的文件路径
            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            response.reset();
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
