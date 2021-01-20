package pkg.FileIO;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileIO {

    private static String userFilePath = "users.json";
    private static String smtpFilePath = "smtpConfig.json";

    public static void main(String[] args) throws Exception {
        setSmtpFilePath("prueba.json");
        writeSMTPConfiguration();
    }

    public static void writeSMTPConfiguration(String host, String port, String encr, String user, String password) throws IOException {
        JSONObject smtpConfig = new JSONObject();
        smtpConfig.put("host", host);
        smtpConfig.put("port", port);
        smtpConfig.put("auth", "true");
        if (encr.toLowerCase().equals("none")){
            smtpConfig.put("TLS", "false");
            smtpConfig.put("SSL", "false");
        } else if (encr.toLowerCase().equals("ssl")) {
            smtpConfig.put("TLS", "false");
            smtpConfig.put("SSL", "true");
        } else {
            smtpConfig.put("TLS", "true");
            smtpConfig.put("SSL", "false");
        }

        smtpConfig.put("username", user);
        smtpConfig.put("password",password);

        writeJSONFile(smtpFilePath, smtpConfig.toString());
    }

    public static void writeSMTPConfiguration() throws IOException {
        writeSMTPConfiguration("smtp.gmail.com", "587", "TLS", "dam.multitech@gmail.com", "multitech17#");
    }

    public static void writeSMTPProp(String prop, String newValue) throws Exception {
        JSONObject smtpConfig = (JSONObject) readJSONFile(smtpFilePath);
        smtpConfig.put(prop, newValue);
        writeJSONFile(smtpFilePath, smtpConfig.toString());
    }

    public static String getSMTPProp(String prop) throws Exception {
        JSONObject jsonObject = (JSONObject) readJSONFile(smtpFilePath);
        return jsonObject.get(prop).toString();
    }

    public static void writeUsersFile() throws IOException {
        JSONArray userList = new JSONArray();
        JSONObject adminUser = new JSONObject();
        adminUser.put("username", "admin");
        adminUser.put("password",sha1("admin"));
        JSONObject user = new JSONObject();
        user.put("username", "user");
        user.put("password",sha1("user"));
        userList.add(adminUser);
        userList.add(user);
        writeJSONFile(userFilePath, userList.toString());
    }

    public static boolean addUser(String username, String password) throws Exception {
        JSONArray users = (JSONArray) readJSONFile(userFilePath);
        JSONObject newUser = new JSONObject();
        newUser.put("username", username);
        newUser.put("password", sha1(password));

        for (int i = 0; i < users.size(); i++) {
            JSONObject user = (JSONObject) users.get(i);
            if (user.get("username").toString().equals(username)){
                return false;
            }
        }

        users.add(newUser);
        writeJSONFile(userFilePath, users.toString());
        return true;
    }

    public static boolean deleteUser(String username) throws Exception {
        int index = -1;
        JSONArray users = (JSONArray) readJSONFile(userFilePath);
        index = getJSONArrayIndex(username, index, users);
        if (index >= 0){
            users.remove(index);
            writeJSONFile(userFilePath, users.toString());
            return true;
        } else {
            return false;
        }
    }

    public static boolean changePassword(String username, String newPassword) throws Exception {
        int index = -1;
        JSONArray users = (JSONArray) readJSONFile(userFilePath);
        index = getJSONArrayIndex(username, index, users);
        if (index >= 0){
            ((JSONObject) users.get(index)).put("password", sha1(newPassword));
            writeJSONFile(userFilePath, users.toString());
            return true;
        } else {
            return false;
        }
    }

    public static boolean authenticateUser(String username, String password) throws Exception {
        JSONArray users = (JSONArray) readJSONFile(userFilePath);
        for (Object user : users) {
            if (((JSONObject) user).get("username").toString().equals(username)) {
                return ((JSONObject) user).get("password").toString().equals(sha1(password));
            }
        }
        return false;
    }

    public static String[] getUserList() throws Exception {
        int i = 0;
        JSONArray users = (JSONArray) readJSONFile(userFilePath);
        String[] userList = new String[users.size()];
        for (Object user : users){
            userList[i] = ((JSONObject) user).get("username").toString();
            i++;
        }
        return userList;
    }

    private static Object readJSONFile(String filename) throws Exception {
        FileReader reader = new FileReader(filename);
        JSONParser jsonParser = new JSONParser();
        return jsonParser.parse(reader);
    }

    private static void writeJSONFile(String path, String jsonData) throws IOException {
        Files.write(Paths.get(path), jsonData.getBytes());
    }

    private static int getJSONArrayIndex(String username, int index, JSONArray users) {
        for (int i = 0; i < users.size(); i++) {
            if (((JSONObject) users.get(i)).get("username").toString().equals(username)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static boolean fileExists(String filePath){
        File f = new File(filePath);
        return f.exists() && !f.isDirectory();
    }

    private static String sha1(String password) {
        String salt1 = "P4#";
        String salt2 = "sp-";
        password = DigestUtils.sha1Hex(salt1 + password + salt2);
        return password;
    }

    public static void setUserFilePath(String newUserFilePath){
        userFilePath = newUserFilePath;
    }

    public static void setSmtpFilePath(String newSmtpFilePath){
        smtpFilePath = newSmtpFilePath;
    }

}
