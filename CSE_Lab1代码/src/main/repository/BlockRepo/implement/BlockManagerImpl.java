package main.repository.BlockRepo.implement;

import main.entity.Id;
import main.entity.implement.BM_Id;
import main.entity.implement.BlockId;
import main.repository.BlockRepo.Block;
import main.repository.BlockRepo.BlockManager;
import main.utility.ErrorCode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @param :
 * @author : Jiang Erling
 * @date : created in 2019/10
 * @return :
 * @description :implement of blockManager
 */
public class BlockManagerImpl implements BlockManager {
    public static ArrayList<BlockManagerImpl> allBlockManagerList = new ArrayList<>();
    private BM_Id id;
    private Map<Id,Block> idBlockMap = new HashMap<>();
    java.io.File dir;

    //构造函数
    //创建目录，加入bmList中
    public BlockManagerImpl(){
        id = new BM_Id();
        allBlockManagerList.add(this);
        dir = new java.io.File(id.getId());
        if(!dir.exists())
            dir.mkdir();
    }

    public BM_Id getId() {
        return id;
    }

    public Map<Id, Block> getIdBlockMap() {
        return idBlockMap;
    }

    public Block blockIdToBlock(Id indexId){
        return idBlockMap.get(indexId);
    }

    public File getDir() {
        return dir;
    }

    @Override
    public Block getBlock(Id indexId) {
        return idBlockMap.get(indexId);
    }

    @Override
    public Block newBlock(byte[] b) {
        if(b.length > 512){
            throw new RuntimeException("content is larger than the size of block");
        }
        BlockImpl newBlock;
        try {
            newBlock = new BlockImpl(this,b);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        idBlockMap.put(newBlock.getIndexId(),newBlock);//加入idBlockMap中
        return newBlock;
    }

    //通过block的ID、其bm的ID得到block
    public static Block getBlockByName(String bmId,String bId){
        for (int i = 0; i < BlockManagerImpl.allBlockManagerList.size(); i++) {
            BlockManager tempManager = BlockManagerImpl.allBlockManagerList.get(i);
            if(((BlockManagerImpl) tempManager).getId().getId().equals(bmId)){
                for (Map.Entry<Id, Block> entry : ((BlockManagerImpl) tempManager).getIdBlockMap().entrySet()) {
                    if(entry.getKey() instanceof BlockId){
                        if(((BlockId) entry.getKey()).getId() == Long.parseLong(bId))
                            return entry.getValue();
                    }
                }
            }
        }
        throw new ErrorCode(ErrorCode.BLOCK_NOT_EXIST);//根据block的直接信息判断block不存在
    }
}
