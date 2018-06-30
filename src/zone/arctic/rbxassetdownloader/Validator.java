/*
 * @topic T00840 Using DicePair with rollSeries() Nov 10 2015
 * @brief Validator class: user input handling and validation
 * i stole some code from my java class lol
*/
package zone.arctic.rbxassetdownloader;

import java.util.Scanner;

public class Validator
{
    public static String getString(Scanner sc, String prompt)
    {
        String s = null;
        do
        {
            System.out.print(prompt);
            s = sc.nextLine();  // read user entry
            if (s.equals(""))
            {
                System.out.println("String cannot be left blank!");
            }
        } while (s.equals(""));

        return s;
    }
    
    public static boolean getYN(Scanner sc, String prompt)
    {
        String s = null;
        do
        {
            System.out.print(prompt);
            s = sc.nextLine();  // read user entry
            if (!s.equals("y") && !s.equals("n"))
            {
                System.out.println("Please respond with y or n");
            }
        } while (!s.equals("y") && !s.equals("n"));
        if (s.equals("y")) return true;
        if (s.equals("n")) return false;
        return false;
    }

    public static int getInt(Scanner sc, String prompt)
    {
        int i = 0;
        boolean isValid = false;
        while (isValid == false)
        {
            System.out.print(prompt);
            if (sc.hasNextInt())
            {
                i = sc.nextInt();
                isValid = true;
            }
            else
            {
                System.out.println("Error! Invalid integer value. Try again.");
            }
            sc.nextLine();  // discard any other data entered on the line
        }
        return i;
    }

    public static int getInt(Scanner sc, String prompt,
    int min, int max)
    {
        int i = 0;
        boolean isValid = false;
        while (isValid == false)
        {
            i = getInt(sc, prompt);
            if (i <= min)
                System.out.println(
                    "Error! Number must be greater than " + min + ".");
            else if (i >= max)
                System.out.println(
                    "Error! Number must be less than " + max + ".");
            else
                isValid = true;
        }
        return i;
    }

    public static double getDouble(Scanner sc, String prompt)
    {
        double d = 0;
        boolean isValid = false;
        while (isValid == false)
        {
            System.out.print(prompt);
            if (sc.hasNextDouble())
            {
                d = sc.nextDouble();
                isValid = true;
            }
            else
            {
                System.out.println("Error! Invalid decimal value. Try again.");
            }
            sc.nextLine();  // discard any other data entered on the line
        }
        return d;
    }

    public static double getDouble(Scanner sc, String prompt,
    double min, double max)
    {
        double d = 0;
        boolean isValid = false;
        while (isValid == false)
        {
            d = getDouble(sc, prompt);
            if (d <= min)
                System.out.println(
                    "Error! Number must be greater than " + min + ".");
            else if (d >= max)
                System.out.println(
                    "Error! Number must be less than " + max + ".");
            else
                isValid = true;
        }
        return d;
    }
}