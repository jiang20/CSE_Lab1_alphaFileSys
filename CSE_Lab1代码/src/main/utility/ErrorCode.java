package main.utility;

import java.util.HashMap;
import java.util.Map;

/**
 * @param :
 * @author : Jiang Erling
 * @date : created in 2019/10
 * @return :
 * @description :
 */
public class ErrorCode extends RuntimeException {
    public static final int IO_EXCEPTION = 1;
    public static final int CHECKSUM_CHECK_FAIL = 2;
    public static final int FILE_NOT_EXIST = 3;
    public static final int BLOCK_NOT_EXIST = 4;
    public static final int WRONG_MOVE = 5;
    public static final int WRONG_READ = 6;
    public static final int WRONG_FILE_ID = 7;
    public static final int UNKNOWN = 1000;
    private int errorCode;
    private static Map<Integer,String> ErrorCodeMap = new HashMap<>();
    static {//各种异常
        ErrorCodeMap.put(IO_EXCEPTION,"IO Exception");
        ErrorCodeMap.put(CHECKSUM_CHECK_FAIL,"block checksum check failed");
        ErrorCodeMap.put(FILE_NOT_EXIST," file not exist");
        ErrorCodeMap.put(BLOCK_NOT_EXIST,"block not exist");
        ErrorCodeMap.put(WRONG_MOVE ," move of cursor wrong");
        ErrorCodeMap.put(WRONG_READ,"file read wrong");
        ErrorCodeMap.put(WRONG_FILE_ID,"a wrong fileId");

        ErrorCodeMap.put(UNKNOWN,"an unknown error");
    }

    public static String getErrorText(int errorCode){
        return ErrorCodeMap.getOrDefault(errorCode,"invalid");
    }
    public ErrorCode(int errorCode){
        super(String.format("errorCode '%d'  \"%s\"",errorCode,getErrorText(errorCode) ));
        this.errorCode = errorCode;
    }

    public int getErrorCode(){
        return errorCode;
    }
}
