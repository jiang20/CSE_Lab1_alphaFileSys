package main.repository.FileRepo;

import main.entity.Id;

/**
 * @param :
 * @author : Jiang Erling
 * @date : created in 2019/10/16
 * @return :
 * @description :
 */
public interface FileManager {
    File getFile(Id fileId);
    File newFile(Id fileId);
}