package com.discern.discern.utils;

import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @ClassName: FileUtile
 * @Description: TODO(文件操作工具包)
 *
 */
public class FileUtil {

    private static List<File> list = new ArrayList<File>(0);
    private static List<String> listImages = null;
    private static List<String> listVideos = null;
    private static List<String> listVoices = null;
    private static List<String> listFiles = null;
    private static List<String> listBooks = null;
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSS");
    private static Date currentTime = new Date();
    static {
        list = new ArrayList<File>(0);
        String imageTypes = "gif,jpg,jpeg,png,bmp";
        String videosTypes = "swf,flv,mp4,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb";
        String voiceTypes = "mp3,aac,wav,wma,cda,flac,m4a,mid,mka,mp2,mpa,mpc,ape,ofr,ogg,ra,wv,tta,ac3,dts,m4r";
        String fileTypes = "doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2";
        String bookTypes = "epub,xml,pdf";
        listBooks = Arrays.asList(bookTypes.split(","));
        listImages = Arrays.asList(imageTypes.split(","));
        listVideos = Arrays.asList(videosTypes.split(","));
        listVoices = Arrays.asList(voiceTypes.split(","));
        listFiles = Arrays.asList(fileTypes.split(","));
    }

    /**
     * 获取系统信息
     *
     * @Title: getSystemInfo
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @return
     * @return String 返回类型
     */
    public static String getSystemInfo() {
        Properties props = System.getProperties(); // 获得系统属性集
        String osName = props.getProperty("os.name"); // 操作系统名称
        String osArch = props.getProperty("os.arch"); // 操作系统构架
        String osVersion = props.getProperty("os.version"); // 操作系统版本
        return osName + osArch + osVersion;
    }

    /**
     * 将map中的值存在属性文件中
     *
     * @param map
     * @param outFile
     *            生成的目标属性文件
     */
    public static void storePropertiesToFile(Map<String, Object> map,File outFile) {
        try {
            if (!map.isEmpty()) {
                if (!outFile.exists()) { // 如果目标文件不存在则创建
                    outFile.getParentFile().mkdirs();
                    outFile.createNewFile();
                }
                // outFile.createNewFile();
                OutputStream out = new FileOutputStream(outFile);
                Properties properties = new Properties();
                Set<String> keys = map.keySet();
                for (String key : keys) {
                    properties.setProperty(key, map.get(key).toString());
                }
                properties.store(out, "这是是提示");
                out.close();
                System.out.println("创建属性文件完成");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取属性文件中制定的键值
     *
     * @param key 键名
     * @param filePath 属性文件的路径
     * @return
     */
    public static Object getPropertyValueByKey(String key, String filePath) {
        Object value = null;
        try {
            InputStream in = new FileInputStream(filePath);
            Properties properties = new Properties();
            properties.load(in);
            value = properties.getProperty(key);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 获取
     *
     * @Title: getUrlCallBackInfo
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param fileurl
     * @return
     */
    public static String getUrlCallBackInfo(String fileurl, String charset) {
        StringBuffer sb = new StringBuffer();
        try {
            URL url = new URL(fileurl);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            InputStream bis = url.openStream();
            StringBuffer s = new StringBuffer();
            if (charset == null || "".equals(charset)) {
                charset = "utf-8";
            }
            String rLine = null;
            BufferedReader bReader = new BufferedReader(new InputStreamReader(
                    bis, charset));
            PrintWriter pw = null;

            FileOutputStream fo = new FileOutputStream("../index.html");
            OutputStreamWriter writer = new OutputStreamWriter(fo, "utf-8");
            pw = new PrintWriter(writer);
            while ((rLine = bReader.readLine()) != null) {
                String tmp_rLine = rLine;
                int str_len = tmp_rLine.length();
                if (str_len > 0) {
                    s.append(tmp_rLine);
                    pw.println(tmp_rLine);
                    pw.flush();
                }
                tmp_rLine = null;
            }
            bis.close();
            pw.close();
            return s.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 上传文件
     *
     * @Title: uploadFile
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param file
     * @param fileName
     * @return void 返回类型
     */
    public static void uploadFile(File file, String fileName) {
        try {
            if (!file.exists()) { // 如果文件的路径不存在就创建路径
                file.getParentFile().mkdirs();
            }
            InputStream bis = new FileInputStream(file);
            uploadFile(bis, fileName);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传文件
     *
     * @Title: uploadFile
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param in
     * @param fileName
     * @return void 返回类型
     */
    public static void uploadFile(InputStream in, String fileName) {
        if (in == null || fileName == null || fileName.equals("")) {
            return;
        }
        try {
            File uploadFile = new File(fileName);
            if (!uploadFile.exists()) { // 如果文件的路径不存在就创建路径
                uploadFile.getParentFile().mkdirs();
            }
            OutputStream out = new FileOutputStream(fileName);
            byte[] buffer = new byte[2048];
            int temp = 0;
            while ((temp = in.read(buffer)) != -1) {
                out.write(buffer, 0, temp);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传文件
     * @param file
     * @param filePath
     * @param fileName
     * @throws Exception
     */
    public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception {
        File targetFile = new File(filePath);
        if(!targetFile.exists()){
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(fileName);
        out.write(file);
        out.flush();
        out.close();
    }

    /**
     * 获得指定长度的随机数
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        String str = "abcdef0123456789";
        Random random = new Random();
        StringBuffer sf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(16);// 0~16
            sf.append(str.charAt(number));
        }
        return sf.toString();
    }

    /**
     * 将字符串输出到文件中
     * @Title: wireStringToFile
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @return void    返回类型
     */
    public static String wireStringToFile(String content,String filePath,String fileName){
        if(filePath==null || filePath.equals("")){
            return null;
        }
        BufferedWriter out=null;
        try {
            File uploadFile = new File(filePath);
            if (!uploadFile.exists()) { // 如果文件的路径不存在就创建路径
                uploadFile.mkdirs();
            }
            String file=filePath+ File.separator +fileName;
            out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"utf-8"));
            out.write(content);
            out.flush();
            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if(out!=null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 文件上传方法
     *
     * @author peng
     * @param file 上传的文件
     * @param uploadPath 上传的文件路径
     * @param fileName 双传的文件名称
     */
    public static void uploadFile(File file, String uploadPath, String fileName) {
        try {
            File uploadFile = new File(uploadPath);
            if (!uploadFile.exists()) { // 如果文件的路径不存在就创建路径
                uploadFile.mkdirs();
            }
            InputStream bis = new FileInputStream(file);
            uploadFile(bis, uploadPath + File.separator + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件后缀名
     * @param fileName 文件的名称
     * @return 文件的后缀名(即格式名称)
     */
    public static String getSuffix(String fileName) {
        if (fileName == null || "".equals(fileName)) {
            return "";
        }
        if (fileName.contains(".")) {
            String[] temp = fileName.split("\\.");
            return temp[temp.length - 1];
        }
        return null;
    }

    /**
     * 清空文件夹以及文件夹里面的所有文件
     * @param folderPath
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); // 删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除指定文件夹下所有文件
     * @param path
     * @return
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);// 再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    static byte[] buffer = new byte[204800];

    /**
     * 压缩文件或文件夹
     * @param files
     * @param baseFolder
     * @param zos
     * @throws Exception
     */
    public static void zip(File[] files, String baseFolder, ZipOutputStream zos)
            throws Exception {
        FileInputStream fis = null;
        ZipEntry entry = null;
        int count = 0;
        for (File file : files) {
            if (file.isDirectory()) {
                zip(file.listFiles(), file.getName() + File.separator, zos);
                continue;
            }
            entry = new ZipEntry(baseFolder + file.getName());
            zos.putNextEntry(entry);
            fis = new FileInputStream(file);
            while ((count = fis.read(buffer, 0, buffer.length)) != -1) {
                zos.write(buffer, 0, count);
            }
        }
    }

    /**
     * 获取文件的名称
     *
     * @Title: getFileName
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @return
     * @return String 返回类型
     */
    public static String getFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("hhmmssSSS");
        return sdf.format(new Date()) + getRandomString(4);
    }

    /**
     * 列出某个目录下的所有文件
     *
     * @param sourceFile
     */
    public static void listAllFiles(File sourceFile) {
        try {
            if (sourceFile.isDirectory()) {
                for (File file : sourceFile.listFiles()) {
                    listAllFiles(file);
                }
            } else {
                list.add(sourceFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件大小
     *
     * @Title: getFileSizes
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param f
     * @return
     * @throws Exception
     * @return long 返回类型
     */
    public static long getFileSizes(File f) throws Exception {// 取得文件大小
        long s = 0;
        if (f.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(f);
            s = fis.available();
        } else {
            f.createNewFile();
            System.out.println("文件不存在");
        }
        return s;
    }

    /**
     * 视频格式转换
     *
     * @Title: getFileSizes
     * @Description: TODO(视频格式转换)
     * @param type
     * @return
     * @throws Exception
     * @return long 返回类型
     */
    public static int checkContentType(String type) {
        // ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
        if (type.equals("avi")) {
            return 0;
        } else if (type.equals("mpg")) {
            return 0;
        } else if (type.equals("wmv")) {
            return 0;
        } else if (type.equals("3gp")) {
            return 0;
        } else if (type.equals("mov")) {
            return 0;
        } else if (type.equals("asf")) {
            return 0;
        } else if (type.equals("mp4")) {
            return 0;
        } else if (type.equals("asx")) {
            return 0;
        } else if (type.equals("flv")) {
            return 2;
        }
        // 对ffmpeg无法解析的文件格式(wmv9，rm，rmvb等),
        // 可以先用别的工具（mencoder）转换为avi(ffmpeg能解析的)格式.
        else if (type.equals("wmv9")) {
            return 1;
        } else if (type.equals("rm")) {
            return 1;
        } else if (type.equals("rmvb")) {
            return 1;
        }
        return 9;
    }

    // ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
    public static boolean processFLV(String oldfilepath, String outPath,
                                     String ffmpegPath) {
        List<String> commend = new ArrayList<String>();
        commend.add(ffmpegPath);
        commend.add("-i");
        commend.add(oldfilepath);
        commend.add("-ab");
        commend.add("128");
        commend.add("-acodec");
        commend.add("libmp3lame");
        commend.add("-ac");
        commend.add("1");
        commend.add("-ar");
        commend.add("22050");
        commend.add("-qscale");
        commend.add("6");
        commend.add("-r");
        commend.add("29.97");
        commend.add("-b");
        commend.add("512");
        commend.add("-y");
        commend.add(outPath);

        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            Process p = builder.start();
            ioWrite(p.getInputStream(), p.getErrorStream());
            p.waitFor();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将rmvb转换成avi
     * @param path
     * @param outPath
     * @param mencoderPath
     * @return
     */
    public static String processAVI(String path, String outPath,
                                    String mencoderPath) {
        List<String> commend = new ArrayList<String>();
        commend.add(mencoderPath);
        commend.add(path);
        commend.add("-oac");
        commend.add("mp3lame");
        commend.add("-lameopts");
        commend.add("preset=64");
        commend.add("-ovc");
        commend.add("xvid");
        commend.add("-xvidencopts");
        commend.add("bitrate=600");
        commend.add("-of");
        commend.add("avi");
        commend.add("-o");
        commend.add(outPath);
        // 命令类型：mencoder 1.rmvb -oac mp3lame -lameopts preset=64 -ovc xvid
        // -xvidencopts bitrate=600 -of avi -o rmvb.avi
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            Process p = builder.start();
            ioWrite(p.getInputStream(), p.getErrorStream());
            // 等Mencoder进程转换结束，再调用ffmepg进程
            p.waitFor();
            return outPath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param is1
     * @param is2
     */
    public static void ioWrite(final InputStream is1, final InputStream is2) {
        new Thread() {
            @Override
            public void run() {
                BufferedReader br = new BufferedReader(new InputStreamReader(is1));
                try {
                    String lineB = null;
                    while ((lineB = br.readLine()) != null) {
                        if (lineB != null) {
                            System.out.println(lineB);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("报错了！123");
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                BufferedReader br2 = new BufferedReader(new InputStreamReader(
                        is2));
                try {
                    String lineC = null;
                    while ((lineC = br2.readLine()) != null) {
                        if (lineC != null) {
                            System.out.println(lineC);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("报错了！456");
                }
            }
        }.start();
    }

    /**
     * 截图
     * @param Path
     * @param outPicPath
     * @param ffmpegPath
     * @throws IOException
     */
    public static boolean screenshot(String Path, String outPicPath,
                                     String ffmpegPath) throws IOException {
        List<String> commend = new ArrayList<String>();
        commend.add(ffmpegPath);
        commend.add("-i");
        commend.add(Path);
        commend.add("-y");
        commend.add("-f");
        commend.add("image2");
        commend.add("-ss");
        commend.add("8");
        commend.add("-t");
        commend.add("0.001");
        commend.add("-s");
        commend.add("420x320");
        commend.add(outPicPath);
        ProcessBuilder builder = new ProcessBuilder(commend);
        builder.command(commend);
        builder.start();
        return true;
    }

    /**
     * 获取文件的大小
     *
     * @Title: floatFormart
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param f
     * @throws NumberFormatException
     * @throws Exception
     * @return String 返回类型
     */
    public static String floatFormart(File f) {
        String str = "0K";
        try {
            NumberFormat numFormat = NumberFormat.getNumberInstance();
            numFormat.setMaximumFractionDigits(2);
            str = numFormat.format(Float.parseFloat(String.valueOf(FileUtil
                    .getFileSizes(f))) / 1024 / 1024) + "M";
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }


    /**
     * 获取上传时文件夹名称
     * @Title: getFolderName
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @return String    返回类型
     * @date 2015-6-17 上午11:26:15
     */
    public static String getFolderName(){
        String folderName = formatter.format(currentTime).toString().substring(0,8);
        return folderName;
    }

    /**
     * 获取转码后文件名称（实体文件存储名称）
     * @Title: getNewFileName
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @return String    返回类型
     */
    public static String getNewFileName(){
        String newFileName = formatter.format(currentTime).toString().substring(8,formatter.format(currentTime).toString().length());
        return newFileName;
    }

    /**
     * 创建文件夹
     * @Title: createFile
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param resourcePath    对应资源所保存的文件夹结构
     * @param data  当前时间作为文件夹
     * @return File    返回类型
     */
    public static File createFile(String resourcePath, String data){
        String path = FileUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        int index=path.indexOf("WEB-INF");
        if(index>0){
            path=path.substring(0, index);
        }
        System.out.println("path"+path);
        File file=new File(path);
        String savepath = file.getAbsolutePath()+File.separator+resourcePath+File.separator+data;

        System.out.println("savepath"+savepath);
        File savedir=new File(savepath);
        if(!savedir.exists()) {
            savedir.mkdirs();
        }
        return savedir;
    }


    /**
     * 获取项目根目录
     * @Title: getProjectPath
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @return String    返回类型
     */
    public static String getProjectPath(){
        String path = FileUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        int index=path.indexOf("WEB-INF");
        if(index>0){
            path=path.substring(0, index);
        }
        File file=new File(path);
        String savepath = file.getAbsolutePath()+File.separator;


        return savepath;
    }

    /**
     * 上传临时文件...
     * @Title: uploadTemp
     * @Description:
     * @param file
     * @throws Exception
     * @return String    返回类型
     * @date 2015-9-1 上午9:37:06
     */
    public static String uploadTemp(MultipartFile file, String userId) throws Exception{
        //文件全名
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
        String coverNewName = userId+"."+suffix;
        //文件保存路径 (使用随机名称: 项目名/WEB-INF/temp_file/随机数.jpg)
        File saveFile =new File(createFile("/resource/temp_file/"+userId,"").getPath()+"/"+coverNewName);
        //获取上传文件流
        InputStream in = file.getInputStream();

        //上传文件(复制)
        FileUtils.copyInputStreamToFile(in, saveFile);

        return coverNewName;

    }

    /**
     * 上传文件(Book)
     * @Title: uplodFile
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param file
     * @return String    返回类型
     * @date 2015-7-1 上午11:43:16
     */
    public static String uplodFile(MultipartFile file){

        //文件输入流
        InputStream in = null;
        //创建一个文件保存路径
        File filePath = null;
        //原文件全名
        String fileName = null;
        //文件名
        String fName = null;
        //保存文件对象
        File saveFile= null;
        try {
            //文件全名
            fileName = file.getOriginalFilename();

            //文件名
            fName = fileName.substring(0,fileName.lastIndexOf("."));

            //文件后缀名,没有 '.'
            String suffix = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();

            //图片资源保存路径
            if(listImages.contains(suffix)){
                filePath = createFile("/uploadfile/avatar/",getFolderName());
            }
            if(filePath == null){
                return "errorType";
            }
            //创建要保存的文件的对象
            saveFile= new File(filePath.getPath()+File.separator+getFileName()+"."+suffix);
            //获取上传文件流
            in = file.getInputStream();

            //上传文件(复制)
            FileUtils.copyInputStreamToFile(in, saveFile);


        } catch (Exception  e1) {
            System.out.println("上传失败!");
            e1.printStackTrace();
            try {
                in.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            return "fail";

        }
        String relaPath = saveFile.getPath().replaceAll("\\\\", "/");

        return relaPath;
    }

    /**
     * 根据绝对磁盘路径获取相对路径
     * @Title: getRelativePath
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param realPath
     * @return String    返回类型
     * @date 2015-7-16 下午5:14:45
     */
    public static String getRelativePath(String realPath){
        String relativePath = realPath;
        if(!realPath.equals("fail") && !realPath.equals("errorType")){
            relativePath= "/"+realPath.substring(realPath.indexOf("image"));
        }
        return relativePath;
    }


    /**
     * 上传头像
     * @Title: uploadHeadPortrait
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param file
     * @return String    返回类型
     */
    public static String uploadHeadPortrait(MultipartFile file){

        //文件输入流
        InputStream in = null;
        //创建一个文件保存路径
        File filePath = null;
        //原文件全名
        String fileName = null;
        //文件名
        String fName = null;
        //保存文件对象
        File saveFile= null;
        try {
            //文件全名
            fileName = file.getOriginalFilename();
            //文件名
            fName = fileName.substring(0,fileName.lastIndexOf("."));

            //文件后缀名,没有 '.'
            String suffix = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();

            //图片资源保存路径
            filePath = FileUtil.createFile(getProjectPath(),getFolderName());

            if(filePath == null){
                return "errorType";
            }

            long filerandomname=System.currentTimeMillis();

            //创建要保存的文件的对象
            saveFile= new File(getProjectPath()+"/uploadfile/avatar/"+File.separator+filerandomname+"."+suffix);
            //获取上传文件流
            in = file.getInputStream();
            //上传文件(复制)
            FileUtils.copyInputStreamToFile(in, saveFile);

        } catch (Exception  e1) {
            System.out.println("上传失败!");
            e1.printStackTrace();
            try {
                in.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            return "fail";

        }

        String relaPath = saveFile.getPath().replaceAll("\\\\", "/");

        return relaPath;
    }

    /**
     * 获取文件前6位16进制，用于过滤
     * @Title: bytesToHexString
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param file
     * @return String    返回类型
     * @date 2015-9-21 上午11:20:40
     */
    public static String bytesToHexString(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        byte[] bt;
        try {
            InputStream is = new FileInputStream(file);
            bt = new byte[3];
            is.read(bt);
            if (bt == null || bt.length <= 0) {
                return null;
            }
            for (int i = 0; i < bt.length; i++) {
                int v = bt[i] & 0xFF;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuilder.append(0);
                }
                stringBuilder.append(hv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    /**
     * 保存图片到本地返回保存路径
     * @param imgae
     * @param request
     * @return
     * @throws IOException
     */
    public static String saveImage(MultipartFile imgae, HttpServletRequest request) throws IOException {
        String path=null;// 文件路径
        if (imgae!=null) {// 判断上传的文件是否为空

            String type=null;// 文件类型
            String fileName=imgae.getOriginalFilename();// 文件原名称
            System.out.println("上传的文件原名称:"+fileName);
            // 判断文件类型
            type=fileName.indexOf(".")!=-1?fileName.substring(fileName.lastIndexOf(".")+1, fileName.length()):null;
            if (type!=null) {// 判断文件类型是否为空
                if ("GIF".equals(type.toUpperCase())||"PNG".equals(type.toUpperCase())||"JPG".equals(type.toUpperCase())) {
                    // 项目在容器中实际发布运行的根路径
                    String realPath=request.getSession().getServletContext().getRealPath("/");
                    // 自定义的文件名称
                    String trueFileName=String.valueOf(System.currentTimeMillis())+"_"+fileName;
                    // 设置存放图片文件的路径
                    path=realPath+"marker\\"+trueFileName;
                    System.out.println("存放图片文件的路径:"+path);
                    // 转存文件到指定的路径
                    imgae.transferTo(new File(path));
                    System.out.println("文件成功上传到指定目录下");
                }else {
                    System.out.println("不是我们想要的文件类型,请按要求重新上传");
                    return null;
                }
            }else {
                System.out.println("文件类型为空");
                return null;
            }
        }else {
            System.out.println("没有找到相对应的文件");
            return null;
        }
        return path;


    }

    /**
     * 保存二进制文件
     * @param renderingData
     * @return
     */
    public static String saveBinaryData(byte[] renderingData,HttpServletRequest request) throws IOException {
        String realPath=request.getSession().getServletContext().getRealPath("/");
        // 自定义的文件名称
        String trueFileName=String.valueOf(System.currentTimeMillis()+".obj");
        // 设置存放二进制文件的路径
        String path=realPath+"render\\"+trueFileName;
        System.out.println("存放二进制文件的路径:"+path);
        // 转存文件到指定的路径
        File file = new File(path);
        file.createNewFile();
        OutputStream os = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(os);
        bos.write(renderingData);
        bos.close();
        os.close();
        return  path;
    }

    /**
     *  新建文件
     *  @param  filePathAndName  String  文件路径及名称  如c:/fqf.txt
     *  @param  fileContent  String  文件内容
     *  @return  boolean
     */
    public  void  newFile(String  filePathAndName,  String  fileContent)  {
        try  {
            String  filePath  =  filePathAndName;
            filePath  =  filePath.toString();  //取的路径及文件名
            File  myFilePath  =  new  File(filePath);
            /**如果文件不存在就建一个新文件*/
            if  (!myFilePath.exists())  {
                myFilePath.createNewFile();
            }
            FileWriter  resultFile  =  new  FileWriter(myFilePath);  //用来写入字符文件的便捷类, 在给出 File 对象的情况下构造一个 FileWriter 对象
            PrintWriter  myFile  =  new  PrintWriter(resultFile);  //向文本输出流打印对象的格式化表示形式,使用指定文件创建不具有自动行刷新的新 PrintWriter。
            String  strContent  =  fileContent;
            myFile.println(strContent);
            resultFile.close();
        }
        catch  (Exception  e)  {
            System.out.println("新建文件操作出错");
            e.printStackTrace();
        }
    }

    /**
     * 创建文件并写入数据
     * @param fileName
     * @param fileContent
     * @param path
     * @return
     */
    public static boolean createFile(String fileName,String fileContent,String path){
        boolean bool = false;
        String fileNameTemp = path+fileName;    //文件路径+文件名称（带扩展名）
        File file = new File(fileNameTemp);
        try{
            if(!file.exists()){
                File filePath = new File(path);
                file.createNewFile();
                //System.out.println("文件创建成功，文件路径："+fileNameTemp);
            }
            FileWriter fw = new FileWriter(file,false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fileContent);
            bw.flush();
            bw.close();
            fw.close();
            bool = true;
            //System.out.println("文件写入完成，文件路径："+fileNameTemp);
        }catch (Exception e){
            e.printStackTrace();
        }
        return bool;
    }

    /**
     *  复制单个文件
     *  @param  oldPath  String  原文件路径  如：c:/fqf.txt
     *  @param  newPath  String  复制后路径  如：f:/fqf.txt
     *  @return  boolean
     */
    public void copyFile(String  oldPath,  String  newPath)  {
        try  {
            int  byteread  =  0;
            File  oldfile  =  new  File(oldPath);
            if  (oldfile.exists())  {  //文件存在时
                InputStream  inStream  =  new  FileInputStream(oldPath);  //读入原文件
                FileOutputStream  fs  =  new  FileOutputStream(newPath);
                byte[]  buffer  =  new  byte[1444];
                while  (  (byteread  =  inStream.read(buffer))  !=  -1)  {
                    fs.write(buffer,  0,  byteread);
                }
                inStream.close();
            }
        }
        catch  (Exception  e)  {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }

    /**
     * 复制文件（FileReader与FileWriter模式）
     * @param oldPath
     * @param newPath
     */
    public void copyFileByFileWriter(String oldPath,String newPath){
        try{
                FileReader fileReader = new FileReader(oldPath);
                FileWriter fileWriter = new FileWriter(newPath);
                int i;
                while((i=fileReader.read())!=-1){
                    fileWriter.write(i);
                }
                fileWriter.close();
                fileReader.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *  复制整个文件夹内容
     *  @param  oldPath  String  原文件路径  如：c:/fqf
     *  @param  newPath  String  复制后路径  如：f:/fqf/ff
     *  @return  boolean
     */
    public  void  copyFolder(String  oldPath,  String  newPath)  {
        try  {
            //如果文件夹不存在  则建立新文件夹
            (new  File(newPath)).mkdirs();
            File  a=new  File(oldPath);
            String[]  file=a.list();
            File  temp=null;
            for  (int  i  =  0;  i  <  file.length;  i++)  {
                if(oldPath.endsWith(File.separator)){
                    temp=new  File(oldPath+file[i]);
                }
                else{
                    temp=new  File(oldPath+File.separator+file[i]);
                }

                if(temp.isFile()){
                    FileInputStream  input  =  new  FileInputStream(temp);
                    FileOutputStream  output  =  new  FileOutputStream(newPath  +  "/"  +
                            (temp.getName()).toString());
                    byte[]  b  =  new  byte[1024  *  5];
                    int  len;
                    while  (  (len  =  input.read(b))  !=  -1)  {
                        output.write(b,  0,  len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                //如果是子文件夹
                if(temp.isDirectory()){
                    copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);
                }
            }
        }
        catch  (Exception  e)  {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }
    }

    /**
     *  删除文件
     *  @param  filePathAndName  String  文件路径及名称  如c:/fqf.txt
     *  @return  boolean
     */
    public  void  delFile(String  filePathAndName)  {
        try  {
            String  filePath  =  filePathAndName;
            filePath  =  filePath.toString();
            File  myDelFile  =  new  File(filePath);
            myDelFile.delete();
        }
        catch  (Exception  e)  {
            System.out.println("删除文件操作出错");
            e.printStackTrace();
        }
    }

    /**
     *  移动文件到指定目录
     *  @param  oldPath  String  如：c:/fqf.txt
     *  @param  newPath  String  如：d:/fqf.txt
     */
    public  void  moveFolder(String  oldPath,  String  newPath)  {
        copyFolder(oldPath,  newPath);
        delFolder(oldPath);
    }

    /**
     * 判断文件是否存在
     * @param file
     */
    public static void judeFileExists(File file) {
        if (file.exists()) {
            System.out.println("file exists");
        } else {
            System.out.println("file not exists, create it ...");
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断文件夹是否存在
     * @param file
     */
    public static boolean judeDirExists(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                System.out.println("dir exists");
            } else {
                System.out.println("the same name file exists, can not create dir");
            }
            return true;
        } else {
            System.out.println("dir not exists, create it ...");
            file.mkdir();
            return false;
        }
    }

    /**
     * 正则替换特殊字符
     * @param str
     * @return
     * @throws PatternSyntaxException
     */
    public String regular(String str) throws PatternSyntaxException {
        String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("_").trim();
    }
}