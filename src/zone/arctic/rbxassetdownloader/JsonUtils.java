/*
 * @author:         Carlo Fontanos
 * @author_url:     carlofontanos.com
 * 
 * A simple code snippet for parsing JSON data from a URL
 */
package zone.arctic.rbxassetdownloader;
                
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

public class JsonUtils {

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
    
    public static String[] getAssetDetails(String assetID) {
        JSONObject json;
        String[] details = new String[8];
        //details[0] = asset exists & is copyable
        //details[1] = name
        //details[2] = id
        //details[3] = asset type
        //details[4] = created date
        //details[5] = last updated
        //details[6] = creator name
        //details[7] = creator id
        ////details[7] = version count (we don't do this due to potentially outdated APIs..)
        try {
            URL u = new URL ("https://assetgame.roblox.com/asset/?id=" + assetID);
            HttpURLConnection huc = ( HttpURLConnection )  u.openConnection (); 
            huc.setRequestMethod ("GET");
            huc.connect(); 
            int code = huc.getResponseCode();
            details[0] = Integer.toString(code);
        } catch (Exception ex) {}
        
        if (!details[0].contains("302")) {
            return details;
        }
        try {
            json = readJsonFromUrl("https://api.roblox.com/Marketplace/ProductInfo?assetId=" + assetID);
            details[1] = formatHtml((String)json.get("Name"));
            details[2] = assetID;
            details[3] = assetTypeIdToString((int)json.get("AssetTypeId"));
            details[4] = formatHtml((String)json.get("Created"));
            details[5] = formatHtml((String)json.get("Updated"));
            
            //Creator details are in its own sub-json object
            JSONObject creatorDeets = (JSONObject)json.get("Creator");
            details[6] = (String)creatorDeets.get("Name");
            details[7] = Integer.toString((int)creatorDeets.get("Id"));
            return details;
            
        } catch (IOException ex) {
            //Logger.getLogger(JsonUtils.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Could not download file: " + assetID);
            details[0]="failed";
            return details;
        }
        //return null;
    }
    
    public static boolean isAssetDownloadable(String assetID, String versionID) {
        try {
            URL u = new URL ("https://assetgame.roblox.com/asset/?id=" + assetID + "&version=" + versionID);
            HttpURLConnection huc = ( HttpURLConnection )  u.openConnection (); 
            huc.setRequestMethod ("GET");
            huc.connect(); 
            int code = huc.getResponseCode();
            if (Integer.toString(code).contains("302")) {
                return true;
            }
        } catch (Exception ex) { return false; }
        return false;
    }
    
    public static String formatHtml(String input){ 
        try {
            return java.net.URLEncoder.encode(input, "UTF-8").replace("+", " ").replace("%3A", "-");
        } catch (UnsupportedEncodingException ex) {}
        
        return "";
    }
    
    public static String assetTypeIdToString(int inputId) {
        switch (inputId) {
            case 1: return "Image";
            case 2: return "T-Shirt"; 
            case 3: return "Audio"; 
            case 4: return "Mesh"; 
            case 5: return "Lua"; 
            case 6: return "HTML"; 
            case 7: return "Text"; 
            case 8: return "Hat"; 
            case 9: return "Place"; 
            case 10: return "Model"; 
            case 11: return "Shirt"; 
            case 12: return "Pants"; 
            case 13: return "Decal"; 
            case 16: return "Avatar"; 
            case 17: return "Head"; 
            case 18: return "Face"; 
            case 19: return "Gear"; 
            case 21: return "Badge"; 
            case 22: return "Group Emblem"; 
            case 24: return "Animation"; 
            case 25: return "Arms"; 
            case 26: return "Legs"; 
            case 27: return "Torso"; 
            case 28: return "Right Arm"; 
            case 29: return "Left Arm"; 
            case 30: return "Left Leg"; 
            case 31: return "Right Leg"; 
            case 32: return "Package"; 
            case 33: return "YouTubeVideo"; 
            case 34: return "Game Pass"; 
            case 35: return "App"; 
            case 37: return "Code"; 
            case 38: return "Plugin"; 
            case 39: return "SolidModel"; 
            case 40: return "MeshPart"; 
            case 41: return "Hair Accessory"; 
            case 42: return "Face Accessory"; 
            case 43: return "Neck Accessory"; 
            case 44: return "Shoulder Accessory"; 
            case 45: return "Front Accessory"; 
            case 46: return "Back Accessory"; 
            case 47: return "Waist Accessory"; 
            case 48: return "Climb Animation"; 
            case 49: return "Death Animation"; 
            case 50: return "Fall Animation"; 
            case 51: return "Idle Animation"; 
            case 52: return "Jump Animation"; 
            case 53: return "Run Animation"; 
            case 54: return "Swim Animation"; 
            case 55: return "Walk Animation"; 
            case 56: return "Pose Animation"; 
            default: return ""; 
        }
                 
    }
    
}