package com.discern.discern.utils;

import org.apache.commons.io.FileUtils;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_core.cvCopy;
import static org.bytedeco.javacpp.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.equalizeHist;

public class JavaCVUtils {
    private static opencv_objdetect.CascadeClassifier cascade =null;
    static {
        //获取容器资源解析器
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            //获取所有匹配的文件
            Resource[] resources = resolver.getResources("lbpcascade_frontalface.xml");
            for(Resource resource : resources) {
                //获得文件流，因为在jar文件中，不能直接通过文件资源路径拿到文件，但是可以在jar包中拿到文件流
                InputStream stream = resource.getInputStream();
                File ttfFile = new File("lbp.xml");
                FileUtils.copyInputStreamToFile(stream, ttfFile);
                File ttfFile2 = new File("demo.xlsx");
                FileUtils.copyInputStreamToFile(stream, ttfFile2);
            }
        } catch (IOException e) {
        }
      /*  String path = ClassUtils.getDefaultClassLoader().getResource("").getPath()+"lbpcascade_frontalface.xml";
        String substring = path.replaceAll("/", "\\\\");
        String substring1 = substring.substring(1);
        System.out.println(substring1);*/
       cascade = cascade = new opencv_objdetect.CascadeClassifier("lbp.xml");//初始化人脸检测器

    }

    /**
     * 判断该图片是否存在人脸
     * @param src
     * @return
     */
    public static CvRect detectFace(Mat src)
    {

        Mat grayscr=new Mat();
        cvtColor(src,grayscr,COLOR_BGRA2GRAY);//摄像头是彩色图像，所以先灰度化下
        equalizeHist(grayscr,grayscr);//均衡化直方图
        RectVector faces=new RectVector();//创建用来装检测出来的人脸的容器
//        String path = ClassUtils.getDefaultClassLoader().getResource("").getPath()+"lbpcascade_frontalface.xml";
//        String substring = path.replaceAll("/", "\\\\");
//        String substring = System.getProperty("user.dir")+"/lbpcascade_frontalface.xml";
//        String substring1 = substring.substring(1);
//        System.out.println(substring1);
//        opencv_objdetect.CascadeClassifier cascade = new opencv_objdetect.CascadeClassifier(System.getProperty("user.dir") + "/src/main/resources/lbpcascade_frontalface.xml");//初始化人脸检测器
//        opencv_objdetect.CascadeClassifier cascade = new opencv_objdetect.CascadeClassifier(substring1);//初始化人脸检测器
        cascade.detectMultiScale(grayscr, faces);//检测人脸，grayscr为要检测的图片，faces用来存放检测结果
        CvRect rect = null;
        //人脸数量大于0开始遍历（并取出最大）
        if(faces.size()>0) {
            //标记最大人脸
            int flag = 0;
            int current = 0;
            for (int i = 0; i < faces.size(); i++)//遍历检测出来的人脸
            {
                opencv_core.Rect face_i = faces.get(i);
                if(face_i.height()>current){
                    current = face_i.height();
                    flag = i;
                }
                //            rectangle(src, face_i, new Scalar(0, 0, 255, 1));//在原图上画出人脸的区域
            }
            Rect rect1 = faces.get(flag);
            int height = rect1.height();
            int width = rect1.width();
            int x = rect1.x();
            int y = rect1.y();
            if (x > 50) {
                x -= 50;
            }
            if (y > 50) {
                y -= 50;
            }
            rect = new CvRect(x, y, width + 50, height + 50);
        }
        return rect;
    }

    /**
     * 剪切图片
     * @param inputImage
     * @param rect
     * @return
     */
    public static IplImage cutImg(IplImage inputImage,CvRect rect){
        IplImage outputImage = cvCreateImage(cvSize(inputImage.width(),inputImage.height()),  IPL_DEPTH_8U, inputImage.nChannels());
        IplImage dst = cvCreateImage(cvSize(rect.width(),rect.height()),  IPL_DEPTH_8U, inputImage.nChannels());
        if(rect.width()<=inputImage.width()&&rect.height()<=inputImage.height()&&rect.x()>=0&&rect.y()>=0&&rect.width()>=0&&rect.height()>=0){
            cvSetImageROI(inputImage, rect);
            cvCopy(inputImage, dst);
            return dst;
        }else{
            return inputImage;
        }
    }

    public static String SaveIplImage(Frame frame, String path) throws IOException {
        ImageIO.write(FrameToBufferedImage(frame), "jpg", new File(path));
        return path;
    }

    /**
     * frame 对象转
     * @param frame
     * @return
     */
    public static BufferedImage FrameToBufferedImage(Frame frame) {
        //创建BufferedImage对象
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage bufferedImage = converter.getBufferedImage(frame);
        return bufferedImage;
    }
}
