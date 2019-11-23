package main.entity.implement;

import main.entity.Id;

/**
 * @param :
 * @author : Jiang Erling
 * @date : created in 2019/10
 * @return :
 * @description :file的id标记
 */
public class FileId implements Id {
    private String id = null;
    public FileId(String id){
        this.id = id;
    }
    public String getId(){
        return id;
    }
}
