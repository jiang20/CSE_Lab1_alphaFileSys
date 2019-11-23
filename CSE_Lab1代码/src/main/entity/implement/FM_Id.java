package main.entity.implement;

import main.entity.Id;

/**
 * @param :
 * @author : Jiang Erling
 * @date : created in 2019/10/16
 * @return :
 * @description :fileManager的Id
 */
public class FM_Id implements Id {
    private String id;
    private static int count = 0;//使用count在删除了fm无法对int实现最充分的利用，之后再优化
    public FM_Id(){
        id = "fm-"+count;
        count++;
    }
    public String getId(){
        return id;
    }
}