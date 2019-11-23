package main.repository.BlockRepo.implement;


import main.entity.Id;
import main.entity.implement.BM_Id;
import main.entity.implement.BlockId;
import main.repository.BlockRepo.Block;
import main.repository.BlockRepo.BlockManager;
import main.utility.FileUtil;
import main.utility.MD5Impl;

import java.io.*;


import static main.constant.FileConstant.BLOCK_SIZE;

/**
 * @param :
 * @author : Jiang Erling
 * @date : created in 2019/10
 * @return :
 * @description :
 */
public class BlockImpl implements Block {
    private Id blockId;
    private BlockManager blockManager;
    private File contentFile;
    private File metaFile;
    private FileUtil fileUtil = new FileUtil();

    //构造函数，创建block，并完善meta等信息
    public BlockImpl(BlockManager blockManager,byte[] content) throws Exception {
        this.blockManager = blockManager;
        blockId = new BlockId();
        contentFile = new File(((BlockManagerImpl)blockManager).getDir().getPath()+"\\"+blockIdToString((BlockId)blockId)+".data");
        try {
            if(!contentFile.exists())
                contentFile.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        fileUtil.writeIntoFile(content,contentFile);
        metaFile = new File(((BlockManagerImpl)blockManager).getDir().getPath()+"\\"+blockIdToString((BlockId)blockId)+".meta");
        try {
            if(!metaFile.exists())
                metaFile.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        fileUtil.writeIntoFile(("size:"+BLOCK_SIZE+"\nchecksum:"+MD5Impl.md5Encode(new String(content))).getBytes(),metaFile);
    }

    //读取metaFile的内容，得到存储的checksum
    public String getChecksum(){
        String checksum = null;
        BufferedReader metaIn = null;
        try {
            metaIn = new BufferedReader(new FileReader(metaFile));
            metaIn.readLine();
            checksum =  metaIn.readLine().split(":")[1];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return checksum;
    }

    //将blocId以字符串的形式返回
    private String blockIdToString(BlockId id){
        return id.getId()+"";
    }
    @Override
    public Id getIndexId() {
        return blockId;
    }

    @Override
    public BlockManager getBlockManager() {
        return blockManager;
    }

    @Override
    //以byte[]数组的形式读取block
    public byte[] read() {

        byte[] content = new byte[BLOCK_SIZE];
        try {
            content = fileUtil.getContent(contentFile.getPath());//利用fileUtil工具类
        } catch (IOException e) {
            e.printStackTrace();//异常
        }
        return content;
    }

    @Override
    public int blockSize() {
        return BLOCK_SIZE;
    }
}
