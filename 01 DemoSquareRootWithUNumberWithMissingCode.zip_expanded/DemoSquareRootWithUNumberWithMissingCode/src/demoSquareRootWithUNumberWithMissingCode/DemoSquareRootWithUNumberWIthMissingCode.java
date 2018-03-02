package demoSquareRootWithUNumberWithMissingCode;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import uNumberLibrary.UNumber;

/**
 * <p> Title: DemoNewtonMethod. </p>
 * 
 * <p> Description: Mainline to demo the Newton-Raphson square root method </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2017 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00	Initial baseline
 * 
 */

public class DemoSquareRootWithUNumberWIthMissingCode {
	
	private static int numSignificantDigits = 40;
//	private static int numIters = 20;
    private static String filename;
	
	/*****
	 * This private method counts how many digits are the same between two estimates
	 */
	private static int howManyDigitsMatch(UNumber newGuess, UNumber oldGuess) {
		// If the characteristics is not the same, the digits in the mantissa do not matter
		if (newGuess.getCharacteristic() != oldGuess.getCharacteristic()) return 0;
		
		// The characteristic is the same, so fetch the mantissas so we can compare them
		byte[] newG = newGuess.getMantissa();
		byte[] oldG = oldGuess.getMantissa();
		
		// Computer the shorter of the two
		int size = newGuess.length();
		int otherOne = oldGuess.length();
		if (otherOne < size) size = otherOne;
		
//		System.out.println("Old: " + oldGuess.toDecimalString() + ", New: " + newGuess.toDecimalString() + ", Size: " + size);
//		numIters--;
		
//		if(numIters == 0)
//			System.exit(0);
		
		// Loop through the digits as long as they match
		for (int ndx = 0; ndx < size; ndx++)
			if (newG[ndx] != oldG[ndx]) return ndx;	// If the don't match, ndx is the result
		
		// If the loop completes, then the size of the shorter is the length of the match
		return size;
	}
	

	/*****
	 * This is the mainline 
	 * 
	 * @param args	The program parameters are ignored
	 */
	public static void main(String [] args) {
//		activityOne();
		activityTwo();
	}
	
	private static void activityTwo() {
		// Open a scanner
		Scanner keyboard = new Scanner(System.in);

        // Ask the user for the number that they'd like the square root of...
        Long number;
        do {
            System.out.println("Enter a non-negative number: ");
            String line = keyboard.nextLine();
            number = Long.parseLong(line);
            if(number < 0)
                System.out.println("Error. The number must be non-negative!");
        } while (number < 0);

		// Ask the user for the number of significant digits they'd like
		// in the answer for the square root of two.
		System.out.print("Enter an integer between 10 and 10000 or just press return (enter) to stop the loop: ");
		
		// Fetch the input from the user, removing leading and trailing white space characters
		String input = keyboard.nextLine().trim();
		while(input.length() > 0) {
			Scanner value = new Scanner(input);

            filename = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss").format(new Date());
			
			int userInput = value.nextInt();
			//System.out.println("You entered: " + number + " and " + userInput);
			
			// If the user entered an invalid value, say so and have them
			// enter again.
			if(!(userInput >= 10 && userInput <= 10000)) {
				System.out.println("Error. Invalid value!");
				System.out.print("Enter an integer between 10 and 10000 or just press return (enter) to stop the loop: ");
				input = keyboard.nextLine().trim();
				
				continue;
			}
			
			UNumber two = new UNumber(2.0),
					//theValue = new UNumber(number),
                    theValue = new UNumber(number.toString(), number.toString().length(), true, userInput),
					newGuess = new UNumber(theValue);				// Compute the estimate
			newGuess.div(two);
			System.out.println("two: " + two.toDecimalString());
            System.out.println("value: " + theValue.toDecimalString());
			System.out.println(newGuess + "\n");					// Display the first estimate
			
			UNumber oldGuess;										// Temporary value for determining when to terminate the loop
			
			int iteration = 0;										// Count the number of iterations
			int digitsMatch = 0;
			
			do {
				long start = System.nanoTime();
				iteration++;										// Next iteration

				oldGuess = new UNumber(newGuess);			// Store old guess...

				// ...and compute the new one
				newGuess = new UNumber(theValue);			// theValue
				newGuess.div(oldGuess);						// theValue/oldGuess
				newGuess.add(oldGuess);						// theValue/oldGuess + oldGuess
				newGuess.div(two);							// (theValue/oldGuess + oldGuess)/two

				long stop = System.nanoTime();
				
				digitsMatch = howManyDigitsMatch(newGuess, oldGuess);

				// Write the iteration number, the old guess, the new guess, and the number of
                // matched digits to a CSV file.
                //writeToCSVFile(iteration + "," + oldGuess.toDecimalString() + "," + newGuess.toDecimalString() + "," + digitsMatch + "\n");
                writeToCSVFile(iteration + "," + (stop - start) + "\n");

				System.out.println("     " + iteration + " estimate " + newGuess.toString() + " with " + digitsMatch + " digits matching taking " + (stop-start)/1000000000.0 + " seconds" );		// Display the intermediate result
									
			} while (digitsMatch < userInput);			// Determine if the old and the new guesses are "close enough"
			
			System.out.println("\nThe square root of 2 to " + userInput + " digits:");
			System.out.println(newGuess);							// Display the final result
			
			System.out.println("Calculated: " + newGuess.toDecimalString());
			
			// Get the actual value from file and compare results.
			UNumber reference = readSquareRootOf2(userInput);
			
			// The comparison can only be done if the reference is not null, meaning
			// nothing went wrong while opening or closing the file.
			if(reference != null) {
				int numMatchedDigits = howManyDigitsMatch(newGuess, reference);
				System.out.println("Reference: " + reference.toDecimalString());
				System.out.println(numMatchedDigits + " digits matched.");
			}
			
			// Ask for more input
			System.out.print("\nEnter a double value or just press return (enter) to stop the loop: ");
			input = keyboard.nextLine().trim();
			value.close();
		}
		
		// An empty input line has been entered, so the tell the user we are stopping
		System.out.print("Empty line detected... the program stops");
		
		// Done with the scanner. Close it.
		keyboard.close();
	}
	
	private static void activityOne() {
		// Set up keyboard as a Scanner object using the console keyboard for input
		Scanner keyboard = new Scanner(System.in);
		// Request a floating point value from the user in the form of three items
		System.out.print("Enter a double value or just press return (enter) to stop the loop: ");

		// Fetch the input from the user, removing leading and trailing white space characters
		String input = keyboard.nextLine().trim();

		// As long as the length of the input String is positive, continue processing the input
		while (input.length() > 0) {
			Scanner value = new Scanner(input);
			// Does this input line consist of a value?
				
			// As long as there is another double value, compute the square root of it
			System.out.println("*****************************************************");
			double inputValue = value.nextDouble();
			System.out.print("The value to be used: ");
			System.out.println(inputValue);
			System.out.println("The result of the square root with and estimate of one half the value");
			
			UNumber theValue =  new UNumber(inputValue);
			
			UNumber two = new UNumber(2.0);
			
			UNumber newGuess = new UNumber(theValue);				// Compute the estimate
			newGuess.div(two);
			System.out.println(newGuess + "\n");					// Display the first estimate
			
			UNumber oldGuess;										// Temporary value for determining when to terminate the loop
			
			int iteration = 0;										// Count the number of iterations
			int digitsMatch = 0;
			do {
				long start = System.nanoTime();
				iteration++;										// Next iteration
//
//				This old double code needs to be replace with UNumber code
//
//				oldGuess = newGuess;								// Save the old guess
				oldGuess = new UNumber(newGuess);
//							
//				newGuess = (theValue/oldGuess+oldGuess)/two;								// Compute the new guess
				newGuess = new UNumber(theValue);			// theValue
				newGuess.div(oldGuess);						// theValue/oldGuess
				newGuess.add(oldGuess);						// theValue/oldGuess + oldGuess
				newGuess.div(two);							// (theValue/oldGuess + oldGuess)/two

				long stop = System.nanoTime();
				
				digitsMatch = howManyDigitsMatch(newGuess, oldGuess);
				System.out.println("     " + iteration + " estimate " + newGuess.toString() + " with " + digitsMatch + " digits matching taking " + (stop-start)/1000000000.0 + " seconds" );		// Display the intermediate result
									
			} while (digitsMatch < numSignificantDigits);			// Determine if the old and the new guesses are "close enough"

			System.out.println("The square root");
			System.out.println(newGuess);							// Display the final result
			UNumber resultSquared = new UNumber(newGuess);
			resultSquared.mpy(newGuess);
			System.out.println("\nThe square of the computed square root.  (It should be *very* close to the input value!):");
			System.out.println(resultSquared);						// Display the result squared
			
			// Ask for more input
			System.out.print("\nEnter a double value or just press return (enter) to stop the loop: ");
			input = keyboard.nextLine().trim();
			value.close();
		}
		// An empty input line has been entered, so the tell the user we are stopping
		System.out.print("Empty line detected... the program stops");
		keyboard.close();
	}
	
	/**
	 * Read the value of square root of 2 from file.
	 */
	private static UNumber readSquareRootOf2(int numDigits) {
		try {
			BufferedReader inputStream = new BufferedReader(new FileReader("SquareRootOf2.txt"));
			String line = inputStream.readLine();
			inputStream.close();
			System.out.println("ref: " + line.substring(0, numDigits + 1));
			return new UNumber(line.substring(0, numDigits + 2), 1, true);
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

    /**
     * This private method is used to write results to a CSV (Comma-separated values)
     * file.
     * The data is already in Comma-separated form when it comes to this method.
     */
    private static void writeToCSVFile(String data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("SquareRootData_" + filename + ".csv", true));
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }
    }
}
