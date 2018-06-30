// RBXAssetDownloader
// Written by Ben Brown (Philosophofee)
// I tried to make this program as neat as possible, enjoy!

package zone.arctic.rbxassetdownloader;

import java.util.Scanner;

public class MainApp {
    //Initialize scanner which we will use for user input
    public static Scanner sc = new Scanner(System.in);
    
    //Initialize main program
    public static void main(String[] args) {
        
        //Ask the user for starting and ending Asset IDs using a Validator
        int intStartingAssetID = Validator.getInt(sc, "Enter starting Asset ID: ");
        int intEndingAssetID = Validator.getInt(sc, "Enter ending Asset ID: ");
        
        //Separate folders by creator name (neater, but more annoying to sift through)
        boolean booleanUseUserFolders = Validator.getYN(sc, "Copy into folders separated by creator name? (y/n): ");
        
        //Grab all versions of assets
        boolean booleanDownloadAllVersions = Validator.getYN(sc, "Download all versions of assets? (y/n): ");
        
        //If the user doesn't want all asset versions, ask him if he wants just the first or newest version of asset
        boolean booleanOnlyFirstVersion = false;
        if (booleanDownloadAllVersions==false) {
            booleanOnlyFirstVersion = Validator.getYN(sc, "Download only first version of asset (y), or newest (n): ");
        }
        
        
    }
    
}
