// RBXAssetDownloader
// Written by Ben Brown (Philosophofee)
// I tried to make this program as neat as possible, enjoy!

package zone.arctic.rbxassetdownloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class MainApp {
    //Initialize scanner which we will use for user input
    public static Scanner sc = new Scanner(System.in);
    
    //Initialize variables that will be accessed later
    public static boolean booleanUseUserFolders;
    public static boolean booleanDownloadAllVersions;
    public static boolean booleanOnlyFirstVersion;
    public static boolean booleanDates;
    
    //Set download folder name
    public static String folderName = "Archive-"+Long.toString(System.currentTimeMillis());
    
    //Initialize main program
    public static void main(String[] args) {
        
        System.out.println("ROBLOX Asset Downloader by Philosophofee version 1");
        //Ask the user for starting and ending Asset IDs using a Validator
        int intStartingAssetID = Validator.getInt(sc, "Enter starting Asset ID: ");
        int intEndingAssetID = Validator.getInt(sc, "Enter ending Asset ID: ");
        
        //Separate folders by creator name (neater, but more annoying to sift through)
        booleanUseUserFolders = Validator.getYN(sc, "Copy into folders separated by creator name? (y/n): ");
        
        //Grab all versions of assets
        booleanDownloadAllVersions = Validator.getYN(sc, "Download all versions of assets? **NOT RECOMMENDED FOR MASS DOWNLOADS...** (y/n): ");
        
        //Grab dates
        booleanDates = Validator.getYN(sc, "Write dates in filenames? (y/n): ");
        
        //If the user doesn't want all asset versions, ask him if he wants just the first or newest version of asset
        booleanOnlyFirstVersion = false;
        if (booleanDownloadAllVersions==false) {
            booleanOnlyFirstVersion = Validator.getYN(sc, "Download only first version of asset (y), or newest (n): ");
        }
        
        //Put range in array and have at it
        int[] arrayIdentifiers = IntStream.rangeClosed(intStartingAssetID, intEndingAssetID).toArray();
        downloadAssets(arrayIdentifiers);
    }
    
    public static void downloadAssets(int[] identifiers) {
        
        //Get starting and ending IDs.
        int intStartingAssetID = identifiers[0];
        int intEndingAssetID = identifiers[identifiers.length-1];
        
        //Sometimes due to network issues it will fail to download assets. 
        //So we will let the user know of these problems.
        ArrayList<Integer> alFailedDownloads =new ArrayList<Integer>();
        
        //Also, we can avoid this by sleeping for a few seconds every 500 or so assets.
        //Imperfect.
        int intSleepytime = 0;
        
        for (int i=intStartingAssetID; i <= intEndingAssetID; i++) {
            //Check for sleepy time. This is to prevent the downloader from shitting the bed on us.
            intSleepytime +=1;
            if (intSleepytime==500) {
                System.out.println("Taking a quick nap so I don't get tired.");
                try {Thread.sleep(5000);} catch (InterruptedException ex) {}
                System.out.println("Much better.");
                intSleepytime=0;
            }
            
            String[] details = JsonUtils.getAssetDetails(Integer.toString(i));
            //System.out.println(debugMyDeets[0]);
            if (details[0].contains("failed")) {
                alFailedDownloads.add(i);
            }
            if (details[0].contains("302")) {
                System.out.println("Found downloadable asset: " + details[1] + " by " + details[6] + ". " + (((float)(i-intStartingAssetID+1)/(float)(intEndingAssetID-intStartingAssetID)) * 100) + "%");
                try {
                    String stringExtension = "";
                    switch (details[3]) {
                        case "Place":
                            stringExtension = ".rbxl";
                            break;
                        case "Image":
                            stringExtension = ".png";
                            break;
                        case "Mesh":
                            stringExtension = ".mesh";
                            break;
                        default:
                            stringExtension = ".rbxm";
                            break;
                    }
                    String stringDateFirst = "";
                    String stringDateLast = "";
                    if (booleanDates==true) {
                        stringDateFirst = " (" + details[4] + ")";
                        stringDateLast = " (" + details[5] + ")";
                    }
                    if (booleanUseUserFolders==true) {
                        if (booleanDownloadAllVersions==false) {
                            if (booleanOnlyFirstVersion==false) {
                                copyURLToFile(
                                        new URL("http://assetgame.roblox.com/asset/?id=" + i), 
                                        new File(folderName + "/" + details[6] + "/" + details[3] + "/" + details[1] + stringDateLast + stringExtension)
                                        //       downloads     /    username      /    type          /    name         date             .extension
                                );
                            }
                            if (booleanOnlyFirstVersion==true) {
                                copyURLToFile(
                                        new URL("http://assetgame.roblox.com/asset/?id=" + i + "&version=1"), 
                                        new File(folderName + "/" + details[6] + "/" + details[3] + "/" + details[1] + " (v1)" + stringDateFirst + stringExtension)
                                        //       downloads     /    username      /    type          /    name           (v1)    date              .extension
                                );
                            }
                        }
                        if (booleanDownloadAllVersions==true) {
                            int version = 1; //Start with 1
                            while(true) {
                                if (JsonUtils.isAssetDownloadable(Integer.toString(i), Integer.toString(version))) {
                                    if (version==1) {
                                        copyURLToFile(
                                            new URL("http://assetgame.roblox.com/asset/?id=" + i + "&version=" + version), 
                                            new File(folderName + "/" + details[6] + "/" + details[3] + "/" + details[1] + " (v" + version + ")" + stringDateFirst + stringExtension)
                                            //       downloads     /    username      /    type          /    name           (v     1          )   date              .extension
                                        );
                                    }
                                    else {
                                        copyURLToFile(
                                            new URL("http://assetgame.roblox.com/asset/?id=" + i + "&version=" + version), 
                                            new File(folderName + "/" + details[6] + "/" + details[3] + "/" + details[1] + " (v" + version + ")" + stringExtension)
                                            //       downloads     /    username      /    type          /    name           (v     1          )    .extension
                                        );
                                    }
                                } else { break; }
                                version+=1;
                            }
                        }
                    }
                    if (booleanUseUserFolders==false) {
                        if (booleanDownloadAllVersions==false) {
                            if (booleanOnlyFirstVersion==false) {
                                copyURLToFile(
                                        new URL("http://assetgame.roblox.com/asset/?id=" + i), 
                                        new File(folderName + "/" + details[3] + "/" + details[1] + " by " + details[6] + stringDateLast + stringExtension)
                                        //       downloads     /    type          /    name           by     username     date             .extension
                                );
                            }
                            if (booleanOnlyFirstVersion==true) {
                                copyURLToFile(
                                        new URL("http://assetgame.roblox.com/asset/?id=" + i + "&version=1"), 
                                        new File(folderName + "/" + details[3] + "/" + details[1] + " by " + details[6] + stringDateFirst + stringExtension)
                                        //       downloads     /    type          /    name           by     username     date              .extension
                                );
                            }
                        }
                        if (booleanDownloadAllVersions==true) {
                            int version = 1; //Start with 1
                            while(true) {
                                if (JsonUtils.isAssetDownloadable(Integer.toString(i), Integer.toString(version))) {
                                    if (version==1) {
                                        copyURLToFile(
                                            new URL("http://assetgame.roblox.com/asset/?id=" + i + "&version=" + version), 
                                            new File(folderName + "/" + details[3] + "/" + details[1] + " by " + details[6] + " (v" + version + ")" + stringDateFirst + stringExtension)
                                            //       downloads     /    type          /    name           by     username       (v     1          )   date              .extension
                                        );
                                    } 
                                    else {
                                        copyURLToFile(
                                            new URL("http://assetgame.roblox.com/asset/?id=" + i + "&version=" + version), 
                                            new File(folderName + "/" + details[3] + "/" + details[1] + " by " + details[6] + " (v" + version + ")" + stringExtension)
                                            //       downloads     /    type          /    name           by     username       (v     1          )    .extension
                                        );
                                    }
                                } else { break; }
                                version+=1;
                            }
                        }
                    }
                } catch (MalformedURLException ex) {
                }
            }
            //System.out.println();
        }
        //If failed downloads are detected, add all the failed IDs to a list and try them again. Wuss.
        if (alFailedDownloads.size()>0) {
            System.out.println("Potentially failed to download some assets. Retrying...");
            int[] arrayFailedIDs = new int[alFailedDownloads.size()];
            for (int i = 0; i < arrayFailedIDs.length; i++) {
                arrayFailedIDs[i] = alFailedDownloads.get(i);
            }
            downloadAssets(arrayFailedIDs);
        }
    }
    
    public static void copyURLToFile(URL url, File file) {
        try {
            InputStream input = url.openStream();
            if (file.exists()) {
                if (file.isDirectory()) {
                    throw new IOException("File '" + file + "' is a directory");
                }

                if (!file.canWrite()) {
                    throw new IOException("File '" + file + "' cannot be written");
                }
            } else {
                File parent = file.getParentFile();
                if ((parent != null) && (!parent.exists()) && (!parent.mkdirs())) {
                    throw new IOException("File '" + file + "' could not be created");
                }
            }

            FileOutputStream output = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }

            input.close();
            output.close();

            System.out.println("File '" + file + "' downloaded successfully!");
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }
    
}
