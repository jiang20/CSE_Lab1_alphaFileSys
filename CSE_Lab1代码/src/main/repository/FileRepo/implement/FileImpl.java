package main.repository.FileRepo.implement;

import jdk.nashorn.api.tree.NewTree;
import main.entity.Id;
import main.entity.implement.BM_Id;
import main.entity.implement.BlockId;
import main.entity.implement.FileId;
import main.repository.BlockRepo.Block;
import main.repository.BlockRepo.BlockManager;
import main.repository.BlockRepo.implement.BlockImpl;
import main.repository.BlockRepo.implement.BlockManagerImpl;
import main.repository.FileRepo.File;
import main.repository.FileRepo.FileManager;
import main.utility.ErrorCode;
import main.utility.FileUtil;
import main.utility.MD5Impl;


import java.io.*;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static main.constant.FileConstant.BLOCK_SIZE;
import static main.constant.FileConstant.COPY_OF_BLOCK;

/**
 * @param :
 * @author : Jiang Erling
 * @date : created in 2019/10
 * @return :
 * @description :
 */
public class FileImpl implements File {
    private FileId id;
    private FileManager fileManager;
    private java.io.File fileMeta;
    private FileUtil fileUtil = new FileUtil();
    private long cursor;

    public FileImpl(FileManager fileManager,String filePath){
        this.fileManager = fileManager;
        id = new FileId(filePath);
        if(fileManager instanceof FileManagerImpl)
            fileMeta = new java.io.File(((FileManagerImpl) fileManager).getDir().getPath()+"\\"+filePath+".meta");
        try {
            if (!fileMeta.exists())
                fileMeta.createNewFile();
        }catch (IOException E){
            E.printStackTrace();
        }
        fileUtil.writeIntoFile(("size:"+0+"\nblockSize:"+BLOCK_SIZE+"\nlogic block:\n").getBytes(),fileMeta);//完成fileMeta的初始化
        cursor = 0;
        ((FileManagerImpl)this.fileManager).getIdFileMap().put(id,this);
    }
    public java.io.File getFileMeta(){
        return fileMeta;
    }
    @Override
    public Id getFileId() {
        return id;
    }

    @Override
    public FileManager getFileManager() {
        return fileManager;
    }

    @Override
    public byte[] read(int length) {
        if(length > this.size())
            throw new ErrorCode(ErrorCode.WRONG_READ);
        byte[] contents = new byte[length];
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(fileMeta));
            for (int i = 0; i < 3; i++) {
                in.readLine();
            }
            long startBlock = cursor / BLOCK_SIZE;
            for (int i = 0; i < startBlock; i++) {
                in.readLine();
            }
            int startByte = (int)(cursor % BLOCK_SIZE);
            int hasRead = 0 ;
            String blockStr;
            String[] bm_b;
            byte[] blockByNow;
            int lengthAddedEachTime;
            while (hasRead<length){
                blockStr = in.readLine();
                blockStr = blockStr.split(":|;")[(int)(Math.random()*COPY_OF_BLOCK+1)+1];
                bm_b = blockStr.split(",");
                blockByNow = BlockManagerImpl.getBlockByName(bm_b[0],bm_b[1]).read();
                lengthAddedEachTime = (length - hasRead > BLOCK_SIZE) ? BLOCK_SIZE:length-hasRead;
                System.arraycopy(blockByNow,startByte,contents,hasRead,lengthAddedEachTime);
                hasRead += lengthAddedEachTime;
                startByte = 0;
            }
        } catch (java.lang.Exception e) {
          //  e.printStackTrace();
            throw new ErrorCode(ErrorCode.UNKNOWN);
        }

        return contents;
    }


    @Override
    public void write(byte[] b) {
      //开始写入的block所排的位数
        int startBlock = (int)cursor /BLOCK_SIZE;//第一个需要被写的块
        int lastBlock = (int) (b.length + cursor -1)/BLOCK_SIZE;//最后一个需要被写的块的个数
        int blockAdded = lastBlock - startBlock + 1;//需要新建的block的数量
        int newCursor = (int)cursor % BLOCK_SIZE;
        Block[] blockNeeded = new Block[blockAdded];
        int hasWritten = 0;
        int nextWritten = 0;
        if(newCursor != 0) {//对第一块在中间部位开始写的block进行操作
            blockNeeded = changeFirstBlock(startBlock, b, blockNeeded);//得到第一块
            hasWritten++;//表明已经写了第一块
            nextWritten += BLOCK_SIZE - cursor % BLOCK_SIZE;//表明已经写了这么多内容,nextWritten指向的是下一个被写的位置
        }
        blockNeeded = totalNewBlock(hasWritten,b,nextWritten,blockNeeded);//得到所有各不相同块的内容
        Block[] allNewBlocks = new BlockImpl[blockAdded*(COPY_OF_BLOCK + 1)];
        allNewBlocks = copyBlock(blockNeeded);//得到副本的所有内容
        updateFileMeta(b.length+cursor,startBlock,allNewBlocks);
        cursor += b.length;
    }

    //生成随机的blockManager
    public BlockManager randomBM(){
        int blockManagerNum = BlockManagerImpl.allBlockManagerList.size();
        BlockManager blockManager = BlockManagerImpl.allBlockManagerList.get((int)(Math.random()*blockManagerNum));
        return blockManager;
    }

    //生成第一个block
    public Block[] changeFirstBlock(int startBlock,byte[] b,Block[] blockNeeded){
        String blockIn = null;
        BufferedReader reader = null;
        int newCursor = (int) cursor % BLOCK_SIZE;//从第一个块的这个位置开始写
        try {
            reader = new BufferedReader(new FileReader(fileMeta));
            for (int i = 0; i < 3; i++) {
                reader.readLine();
            }
            for (int i = 0; i <= startBlock; i++) {
                blockIn = reader.readLine();
            }
            assert blockIn != null;
            String[] strs = blockIn.split(":|;");
            String[] subStrs = null;
            int index = (int) (Math.random() * (COPY_OF_BLOCK+1)) + 1;//随机选择一块
            byte[] newContents = new byte[BLOCK_SIZE];//每个新块里面的内容
            int nextWritten = 0;
            //对第一个块大的修改
            subStrs = strs[index].split(",");
            int i;
            for (i = 0; i < BlockManagerImpl.allBlockManagerList.size(); i++) {
                if (subStrs[0].equals(BlockManagerImpl.allBlockManagerList.get(i).getId().getId()))
                    break;
            }
            Block tempBlock = null;
            for (Map.Entry<Id, Block> entry : BlockManagerImpl.allBlockManagerList.get(i).getIdBlockMap().entrySet()) {
                if (entry.getKey() instanceof BlockId) {
                    if (((BlockId) entry.getKey()).getId() == Long.parseLong(subStrs[1])) {
                        tempBlock = BlockManagerImpl.allBlockManagerList.get(i).blockIdToBlock(entry.getKey());
                        if (MD5Impl.md5Encode(new String(tempBlock.read())).equals(((BlockImpl)tempBlock).getChecksum())) {
                            System.arraycopy(tempBlock.read(), 0, newContents, 0, (int)cursor % BLOCK_SIZE);
                            System.arraycopy(b, 0, newContents, newCursor, (b.length >= BLOCK_SIZE - (int)cursor % BLOCK_SIZE)?BLOCK_SIZE - (int)cursor % BLOCK_SIZE:b.length);
                            blockNeeded[0] = randomBM().newBlock(newContents);
                            break;
                        } else
                            throw new RuntimeException("content of block has been changed");
                    }
                }
            }
        }catch (java.lang.Exception e){
            //e.printStackTrace();
            throw new ErrorCode(ErrorCode.UNKNOWN);
        }
        return blockNeeded;
    }

    //根据内容生成完全新的block（不含只“修改一部分的内容
    public Block[] totalNewBlock(int hasWritten,byte[] b,int nextWritten,Block[] blockNeeded){
        BlockManager blockManager;
        byte[] newContents = new byte[BLOCK_SIZE];
        try {
            for (int j = hasWritten; j < blockNeeded.length; j++) {//对于完全属于新加的块进行读写
                blockManager = randomBM();
                if (nextWritten + BLOCK_SIZE < b.length) {
                    //newContents = Arrays.copyOfRange(b, nextWritten, nextWritten + BLOCK_SIZE);
                    System.arraycopy(b,nextWritten,newContents,0,BLOCK_SIZE);
                    nextWritten += BLOCK_SIZE;
                } else {
                    //newContents = Arrays.copyOfRange(b, nextWritten, b.length);
                    System.arraycopy(b,nextWritten,newContents,0,b.length - nextWritten);
                    for (int i = b.length ; i < BLOCK_SIZE; i++) {
                        newContents[i] = (byte)0;//后面没有写满的块，在后面空余的位置补0
                    }
                }
                blockNeeded[hasWritten] = blockManager.newBlock(newContents);//new BlockImpl(blockManager, newContents);
                //这里要把上面的blockNeeded和blockCopied的信息记入到fileMeta中
                hasWritten++;
            }
        }catch (Exception e){
          //  e.printStackTrace();
            throw new ErrorCode(ErrorCode.UNKNOWN);
        }
        return blockNeeded;
    }

    //生成block的副本，并得到最终block版本
    public Block[] copyBlock(Block[] blocks){
        Block[] blocksAfterCopied = new BlockImpl[blocks.length*(COPY_OF_BLOCK+1)];
        int copiedTimes = 0;
        int length = blocks.length;
        try {
            for (int i = 0; i < length; i++) {
                blocksAfterCopied[i* (COPY_OF_BLOCK+1)] = blocks[i];
                for (int j = 1; j <= COPY_OF_BLOCK; j++, copiedTimes++) {
                    blocksAfterCopied[ i*(COPY_OF_BLOCK+1)+j] = randomBM().newBlock(blocks[i].read());// new BlockImpl(randomBM(), blocks[i].read());
                }
            }
        } catch (Exception e) {
           // e.printStackTrace();
            throw new ErrorCode(ErrorCode.UNKNOWN);
        }
        return blocksAfterCopied;
    }

    //由生成的block数组，更新fileMeta
    public void updateFileMeta(long newSize,int startBlock,Block[] blocks){
        BufferedReader reader = null;
        FileOutputStream fos = null;
        StringBuffer fileContents = new StringBuffer("size:"+ newSize+"\n");
        try {
            reader = new BufferedReader(new FileReader(fileMeta));
            reader.readLine();
            for(int i = 1; i < 3+startBlock; i++){
                fileContents.append( reader.readLine()+"\n");
            }
            fos = new FileOutputStream(fileMeta);
            fos.write(new String(fileContents).getBytes());//完成前面内容的复制
            //由于对fos的使用要求不一样，无法实现复用
            for(int i = 0; i < blocks.length / (1 + COPY_OF_BLOCK); i++) {
               fileContents = new StringBuffer(i+":");
                for (int j = 0; j < COPY_OF_BLOCK + 1; j++) {
                    if(blocks[i+j].getBlockManager() instanceof BlockManagerImpl  && blocks[i+j].getIndexId() instanceof BlockId)
                        fileContents.append(((BlockManagerImpl) blocks[i+j].getBlockManager()).getId().getId()+","+((BlockId) blocks[i+j].getIndexId()).getId()+";");
                }
                fileContents.append("\n");
                fos.write(new String(fileContents).getBytes());
            }
        }catch (IOException e){
            throw new ErrorCode(ErrorCode.IO_EXCEPTION);
        }
    }

    @Override
    //移动cursor
    public long move(long offset, int where) {
        switch (where){
            case MOVE_CURR :{
                if(cursor+offset < size() && cursor + offset >= 0) {
                    cursor = (cursor + offset);
                    return cursor;
                }
                else if(cursor+offset>size()) {
                    setSize(cursor + offset);
                    cursor = cursor + offset ;
                    return cursor;
                }
                else
                    throw new ErrorCode(ErrorCode.WRONG_MOVE);
            }
            case MOVE_HEAD:{
                if(offset <size()&& offset>=0||offset == size()) {
                    cursor = offset;
                    return cursor;
                }
                else if(offset > size()) {
                    setSize(offset);
                    cursor = offset;
                    return cursor;
                }
                else
                    throw new ErrorCode(ErrorCode.WRONG_MOVE);
            }
            case MOVE_TAIL:{
                if(offset>=0) {
                    setSize(size() + offset);
                    cursor = size()-1;
                    return cursor;
                }
                else if(size()+offset>=0){
                    cursor = size()+offset;
                    return cursor;
                }
                else
                    throw new ErrorCode(ErrorCode.WRONG_MOVE);
            }
            default:throw new ErrorCode(ErrorCode.WRONG_MOVE);
        }
    }

    @Override
    public void close() {

    }

    @Override
    //得到文件大小
    public long size() {
        long size = 0;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileMeta));
            String firstLine = reader.readLine();
            size = Long.parseLong(firstLine.substring(5));//从fileMeta的第一行中得到信息
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }

    @Override
    //修改文件大小
    public void setSize(long newSize) {
        long oldCursor = cursor;
        if(size() < newSize){
            cursor = size();
            byte[] addedContents = new byte[(int)(newSize-size())];
            write(addedContents);
            cursor = oldCursor;
        }
        else {
            BufferedReader reader;
            FileOutputStream fos;
            int blockNum = (int)((newSize % BLOCK_SIZE == 0)?(newSize / BLOCK_SIZE):(newSize / BLOCK_SIZE + 1));
            StringBuffer stringBuffer = new StringBuffer("size:"+newSize+"\n");
            try {
                reader = new BufferedReader(new FileReader(fileMeta));
                reader.readLine();
                for (int i = 1; i < 3 + blockNum; i++) {
                    stringBuffer.append(reader.readLine()+"\n");
                }
                fos = new FileOutputStream(fileMeta);
                fos.write(new String(stringBuffer).getBytes());
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
