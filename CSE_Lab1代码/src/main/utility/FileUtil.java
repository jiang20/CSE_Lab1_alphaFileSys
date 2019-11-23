package main.utility;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;


/**
 * @param :
 * @author : Jiang Erling
 * @date : created in 2019/10
 * @return :
 * @description :read file by name byte by byte
 */
public class FileUtil {

    //通过文件名得到文件内容
    //并以byte数组形式返回
    public byte[] getContent(String fileId) throws IOException {
        File file = new File(fileId);
        if (!file.exists()) {
            throw new FileNotFoundException(fileId);
        }
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");//异常
            return null;
        }
        FileInputStream fi = new FileInputStream(file);
        byte[] buffer = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        while (offset < buffer.length
                && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += numRead;
        }
        // 确保所有数据均被读取
        if (offset != buffer.length) {
            throw new IOException("Could not completely read file "
                    + file.getName());
        }
        fi.close();
        return buffer;

    }

    //将byte[]写入文件中
    public void writeIntoFile(byte[] contents, File file){
        if(!file.exists()){
            throw new RuntimeException("object not exist when write");
        }
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            out.write(contents);
            out.close();
        } catch (IOException|NullPointerException e) {
            e.printStackTrace();
        }
    }

    //直接通过file得到文件内容
    //并以byte[]形式返回
    public byte[] getContentsByFile(java.io.File file){
        byte[] answer = null;
        if(!file.exists())
            throw new ErrorCode(ErrorCode.BLOCK_NOT_EXIST);
        BufferedReader reader = null;
        String content = null;
        String temp;
        try {
            reader = new BufferedReader(new FileReader(file));
            content = reader.readLine();
            while((temp = reader.readLine()) != null){
                content += "\n"+temp;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        answer = content.getBytes();
        return answer;
    }

    //读取文件特定行的内容
    //并以字符串的形式返回
    //在此lab中没有使用
    public String  readAppointedLineNumber(File sourceFile, int lineNumber) throws IOException {
        FileReader in = new FileReader(sourceFile);
        LineNumberReader reader = new LineNumberReader(in);
        String s = reader.readLine();
        if (s==null||lineNumber < 0 || lineNumber > getTotalLines(sourceFile)) {//System.out.println("不在文件的行数范围之内。");
            throw new RuntimeException("不在文件的行数范围之内。");
        }
        while (s != null) {//System.out.println("当前行号为:" + reader.getLineNumber());//System.out.println(s);//System.exit(0);
            s = reader.readLine();
        }
        reader.close();
        in.close();
        return s;
    }

    //得到文件的总行数
    public int getTotalLines(File file) throws IOException {
        FileReader in = new FileReader(file);
        LineNumberReader reader = new LineNumberReader(in);
        String s = reader.readLine();
        int lines = 0;
        while (s != null) {
            lines++;
            s = reader.readLine();
        }
        reader.close();
        in.close();
        return lines;
    }
}
