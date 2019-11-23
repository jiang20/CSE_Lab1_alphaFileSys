package main.repository.BlockRepo;

import main.entity.Id;

/**
 * @param :
 * @author : Jiang Erling
 * @date : created in 2019/10/16
 * @return :
 * @description :block 没有write操作，immutable
 */
public interface Block {
    Id getIndexId();
    BlockManager getBlockManager();
    byte[] read();
    int blockSize();
}
