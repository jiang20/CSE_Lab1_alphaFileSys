package main.repository.FileRepo.implement;

import main.entity.Id;
import main.entity.implement.FM_Id;
import main.entity.implement.FileId;
import main.repository.FileRepo.File;
import main.repository.FileRepo.FileManager;
import main.utility.ErrorCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @param :
 * @author : Jiang Erling
 * @date : created in 2019/10/16
 * @return :
 * @description :
 */
public class FileManagerImpl implements FileManager {
    private Map<Id,File> idFileMap = new HashMap<>();
    private FM_Id fm_id;
    public static ArrayList<FileManager> allFileManagers = new ArrayList<>();
    private java.io.File dir;

    //构造函数
    //生成相应目录
    public FileManagerImpl(){
        fm_id = new FM_Id();
        allFileManagers.add(this);
        dir = new java.io.File(fm_id.getId());
        if(!dir.exists())
            dir.mkdir();
    }


    public FM_Id getFm_id() {
        return fm_id;
    }

    public Map<Id, File> getIdFileMap() {
        return idFileMap;
    }

    public java.io.File getDir() {
        return dir;
    }

    @Override
    public File getFile(Id fileId) {
        File temp;
        if((temp = idFileMap.get(fileId)) != null)
            return temp;
        throw new ErrorCode(ErrorCode.FILE_NOT_EXIST);
    }

    @Override
    public File newFile(Id fileId) {
        File newFile;
        if(fileId instanceof FileId) {
            newFile = new FileImpl(this, ((FileId) fileId).getId());
            //idFileMap.put(fileId,newFile);
            return newFile;
        }
        throw new ErrorCode(ErrorCode.WRONG_FILE_ID);
    }

    //通过文件的名字得到文件
    public static File getFileByName(String fileName){
        for (int i = 0; i < FileManagerImpl.allFileManagers.size(); i++) {
            FileManager tempManager = FileManagerImpl.allFileManagers.get(i);
            if(tempManager instanceof FileManagerImpl)
                for (Map.Entry<Id, File> entry:((FileManagerImpl) tempManager).getIdFileMap().entrySet()) {
                    if(entry.getKey() instanceof FileId){
                        if(((FileId) entry.getKey()).getId().equals(fileName))
                            return entry.getValue();
                    }
                }
        }
        throw new ErrorCode(ErrorCode.FILE_NOT_EXIST);
    }
}
