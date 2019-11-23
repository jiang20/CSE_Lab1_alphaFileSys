package main.repository.BlockRepo;

import main.entity.Id;

/**
 * @param :
 * @author : Jiang Erling
 * @date : created in 2019/10/16
 * @return :
 * @description :BlockManager的接口
 */
public interface BlockManager {
    Block getBlock(Id indexId);
    Block newBlock(byte[] b);
    default Block newEmptyBlock(int blockSize) {
        return newBlock(new byte[blockSize]);
    }
}
