package come.cloudcore.minder.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cloudcore.minder.core.Config;
import com.cloudcore.minder.core.FileSystem;
import org.json.JSONException;

public class FileUtils {
/* Methods */
    
    /**
     * Returns an array containing all filenames in a directory.
     *
     * @param folderPath the folder to check for files.
     * @return String Array.
     */
    public static String getCommandFileContnet(String folderPath) {
        File folder = new File(folderPath);
        String commandFile = "";
        if (folder.isDirectory()) {

            File[] filenames = folder.listFiles();

            if (null != filenames) {
                for (File file : filenames) {
                    if (file.isFile()) {
                        if (file.getName().contains(Config.TAG_file_name)) {
                            commandFile = readCommandFile(file.getAbsolutePath());
                            break;
                        }
                    }
                }
            }
        }
        return commandFile;
    }

    /**
     * Returns an array containing all filenames in a directory.
     *
     * @param accountName the account from passwords folder.
     * @return String Array.
     */
    public static String getAccountFileName(String accountName) {
        File folder = new File(FileSystem.AccountFolder);
        String commandFile = "";
        if (folder.isDirectory()) {

            File[] filenames = folder.listFiles();

            if (null != filenames) {
                for (File file : filenames) {
                    if (file.isFile()) {
                        if (file.getName().contains(accountName)) {
                            commandFile = file.getAbsolutePath();
                            commandFile = fetchAccountContent(commandFile);
                            break;
                        }
                    }
                }
            }
        }
        return commandFile;
    }

    public static String readCommandFile(String filePath) {

        InputStream is;
        try {
            is = new FileInputStream(filePath);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));

            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();

            while (line != null) {
                sb.append(line).append("\n");
                line = buf.readLine();
            }
           
            return sb.toString();
//            String fileAsString = sb.toString();
//            JSONObject jObj =new JSONObject(fileAsString);
//            if(jObj.has("toPath")){
//                return jObj.getString("toPath");
//            }
        } catch (IOException | JSONException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public static String fetchAccountContent(String filePath) {

        InputStream is;
        try {
            is = new FileInputStream(filePath);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));

            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();

            while (line != null) {
                sb.append(line).append("\n");
                line = buf.readLine();
            }
           
            return sb.toString();
        } catch (IOException | JSONException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
}
