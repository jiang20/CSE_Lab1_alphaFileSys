package main.entity.implement;

import main.entity.Id;

/**
 * @param :
 * @author : Jiang Erling
 * @date : created in 2019/10
 * @return :
 * @description :blockId
 */



public class BlockId implements Id {
    private static long bCount = 0;
    private long id;
    public BlockId(){
        id = bCount;
        bCount++;
    }
    public long getId(){
        return id;
    }

}
