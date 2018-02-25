package utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Oleh on 25-Feb-18.
 */
public class CheckHashFile {
public static String generateHashForFileOfType(File fileToCheck, HashType hashType) throws IOException {
    if ((!fileToCheck.exists())){
        throw new FileNotFoundException(fileToCheck + "doesn't exist");

    }
    switch (hashType){
        case MD5:
            return
                    DigestUtils.md5Hex(new FileInputStream(fileToCheck));
        case SHA1:
            return  DigestUtils.sha1Hex(new FileInputStream(fileToCheck));
            default:throw new UnsupportedOperationException(hashType.toString() + "hash type is not supported");
    }

}
}
