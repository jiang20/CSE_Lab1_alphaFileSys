package main.user;

import main.entity.Id;
import main.entity.implement.BlockId;
import main.entity.implement.FileId;
import main.repository.BlockRepo.Block;
import main.repository.BlockRepo.BlockManager;
import main.repository.BlockRepo.implement.BlockImpl;
import main.repository.BlockRepo.implement.BlockManagerImpl;
import main.repository.FileRepo.File;
import main.repository.FileRepo.FileManager;
import main.repository.FileRepo.implement.FileImpl;
import main.repository.FileRepo.implement.FileManagerImpl;
import main.utility.ErrorCode;
import main.utility.FileUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

/**
 * @param :
 * @author : Jiang Erling
 * @date : created in 2019/10/16
 * @return :
 * @description :
 */
public class alpha_sys {
    private ArrayList<FileManager> fileManagers = new ArrayList<>();
    static BlockManager bm1 = new BlockManagerImpl();
    static BlockManager bm2 = new BlockManagerImpl();
    static BlockManager bm3 = new BlockManagerImpl();
    static FileManager fm1 = new FileManagerImpl();
    static FileManager fm2 = new FileManagerImpl();
    static FileManager fm3 = new FileManagerImpl();
    FileUtil fileUtil = new FileUtil();

    //直接读取文件中的内容
    public void alpha_cat(String fileName){
        byte[] answer = null;
        File file = FileManagerImpl.getFileByName(fileName);
        file.move(0,File.MOVE_HEAD);
        if(file.size() <Integer.MAX_VALUE) {
            try {
                System.out.print(new String(file.read((int)file.size()),"ascii"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        else {
            long size = file.size(), hasRead = 0;
            while (size - hasRead > Integer.MAX_VALUE) {
                try {
                    System.out.print(new String(file.read((int) (size - hasRead)), "ascii"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                file.move(hasRead, File.MOVE_HEAD);
                hasRead += Integer.MAX_VALUE;
            }
        }
        System.out.print("\n");
    }

    //直接从block的信息，读取block中的内容；
    //并且以十六进制的形式输出
    //每16个十六进制的值输出一行
    public void alpha_hex(String bmId,String bId){
        byte[] answer = null;
        java.io.File blockContent = new java.io.File(bmId+"\\"+bId);
        answer = fileUtil.getContentsByFile(blockContent);
        String hex_str = bytesToHexString(answer);
        for (int i = 0; i < hex_str.length(); i+=32) {
            System.out.println(hex_str.substring(i,32+i));
        }
    }

    //在文件的point处写入文件fileName中
    //写入的内容为写在 控制台中的内容
    public void alpha_write(String fileName,int point) {
        File file = FileManagerImpl.getFileByName(fileName);
        file.move(point, File.MOVE_HEAD);
        Scanner input = new Scanner(System.in);
        String in = "";
        String temp = "";
        while (!"end".equals(temp = input.nextLine())){
            in += temp;
        }
        if(!in.isEmpty())
            file.write(in.getBytes());
    }

    //将文件from中的内容写入到文件to中
    //这里是通过直接修改fileMeta中的内容实现的
    public void alpha_copy(String from,String to){
        File fromFile = FileManagerImpl.getFileByName(from);
        File toFile = FileManagerImpl.getFileByName(to);
        BufferedReader reader;
        FileOutputStream fos;
        try {
            java.io.File fromMetaFile = ((FileImpl)fromFile).getFileMeta();
            java.io.File toMetaFile = ((FileImpl)toFile).getFileMeta();
            reader = new BufferedReader(new FileReader(fromMetaFile));
            fos = new FileOutputStream(toMetaFile);
            String content;
            while ((content = reader.readLine()) != null)
                fos.write((content+"\n").getBytes());
            fos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //工具方法
    //将字节数组以十六进制的字符串形式输出
    private String bytesToHexString(byte[] bytes){
        StringBuilder result = new StringBuilder("");
        int length = bytes.length;
        for (int i = 0; i < length; i++) {
            int hex = bytes[i]&0xFF;
            String hv = Integer.toHexString(hex);
            if(hv.length() < 2)
                result.append(0);
            result.append(hv);
        }
        return new String(result);
    }

//将字节数组以字节的形式输出到控制台（目前的代码中没有使用到）
    private void printByteArray(byte[] array){
        for (byte b : array) {
            System.out.print(b);
        }
    }



    public static void main(String[] args){

        alpha_sys sys = new alpha_sys();
        Id fId1 = new FileId("happy2");
        Id fId2 = new FileId("no");

        File f2 = fm2.newFile(fId2);
        File f1 = fm1.newFile(fId1);
        sys.alpha_write("happy2",0);
        sys.alpha_cat("happy2");
//        sys.alpha_write("happy2",4);
//        f1.move(3,File.MOVE_TAIL);
//        f1.setSize(8);
//        f1.setSize(13);
//        sys.alpha_hex("bm-2","7.data");
//        sys.alpha_cat("happy2");
//        sys.alpha_copy("happy2","no");
//        sys.alpha_cat("no");
//        sys.alpha_write("no",8);
    }
}
