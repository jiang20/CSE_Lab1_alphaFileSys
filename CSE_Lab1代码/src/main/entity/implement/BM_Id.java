package main.entity.implement;

import main.entity.Id;

/**
 * @param :
 * @author : Jiang Erling
 * @date : created in 2019/10
 * @return :
 * @description :blockManager的ID
 */
public class BM_Id implements Id {
    private static long count = 0;
    private String id;
    public BM_Id(){
        id = "bm-"+count;
        count++;
    }
    public String getId(){
        return id;
    }
}
