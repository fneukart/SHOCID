package shocid.readFile;

import java.io.*;
import java.util.Scanner;

import shocid.hoann.HOANNJSP;
import shocid.hoann.HOANNCommitteeJSP;
import shocid.som.SelfOrganizingFeatureMapJSP;

public class ReadWithScanner {

	String inputNeuronsString = null;
	String outputNeuronsString = null;


	//	public static void main(String... args) throws FileNotFoundException {
	//  ReadWithScanner parser = new ReadWithScanner("C:\\Temp\\test.txt");

	//
	//		ReadWithScanner parser = new ReadWithScanner();
	//		//ReadWithScanner parser = new ReadWithScanner(inPath);
	//		parser.processLineByLine();
	//		log("Done.");
	//	}

	public void ReadIn(ReadWithScanner parser) throws FileNotFoundException {

		//ReadWithScanner parser = new ReadWithScanner();
		parser.processInputLineByLine();
		log("Done.");
	}

	public void ReadSomIn(ReadWithScanner parser) throws FileNotFoundException {

		//ReadWithScanner parser = new ReadWithScanner();
		parser.processSomInputLineByLine();
		log("Done.");
	}

	public void ReadOut(ReadWithScanner parser) throws FileNotFoundException {

		//ReadWithScanner parser = new ReadWithScanner();
		parser.processOutputLineByLine();
		log("Done.");
	}

	public ReadWithScanner(){

		//  prompt to enter the path to the input file
		System.out.print("Enter the path to the file: ");
		BufferedReader brinf = new BufferedReader(new InputStreamReader(System.in));
		String inPath = null;
		try
		{
			inPath = brinf.readLine();
			System.out.println("Reading file...");
		}
		catch (IOException ioe)
		{
			System.out.println("IO error trying to read the path to the input file.");
			System.exit(1);
		}
		fFile = new File(inPath);
	}

	/**
   Constructor.
   @param aFileName full name of an existing, readable file.
	 */
	public ReadWithScanner(String aFileName){
		fFile = new File(aFileName);  
	}



	/** Template method that calls {@link #processLine(String)}.  */
	public final void processInputLineByLine() throws FileNotFoundException {
		//Note that FileReader is used, not File, since File is not Closeable
		Scanner scanner = new Scanner(new FileReader(fFile));
		try {
			//first use a Scanner to get each line
			while ( scanner.hasNextLine() ){
				processInputLine(scanner.nextLine() );
			}
		}
		finally {
			//ensure the underlying stream is always closed
			//this only has any effect if the item passed to the Scanner
			//constructor implements Closeable (which it does in this case).
			scanner.close();
		}
	}

	public final void processSomInputLineByLine() throws FileNotFoundException {
		//Note that FileReader is used, not File, since File is not Closeable
		Scanner scanner = new Scanner(new FileReader(fFile));
		try {
			//first use a Scanner to get each line
			while ( scanner.hasNextLine() ){
				processSomInputLine(scanner.nextLine() );
			}
		}
		finally {
			//ensure the underlying stream is always closed
			//this only has any effect if the item passed to the Scanner
			//constructor implements Closeable (which it does in this case).
			scanner.close();
		}
	}

	public final void processOutputLineByLine() throws FileNotFoundException {
		//Note that FileReader is used, not File, since File is not Closeable
		Scanner scanner = new Scanner(new FileReader(fFile));
		try {
			//first use a Scanner to get each line
			while ( scanner.hasNextLine() ){
				processOutputLine(scanner.nextLine() );
			}
		}
		finally {
			//ensure the underlying stream is always closed
			//this only has any effect if the item passed to the Scanner
			//constructor implements Closeable (which it does in this case).
			scanner.close();
		}
	}
	/** 
   Overridable method for processing lines in different ways.

   <P>This simple default implementation expects simple name-value pairs, separated by an 
   '=' sign. Examples of valid input : 
   <tt>height = 167cm</tt>
   <tt>mass =  65kg</tt>
   <tt>disposition =  "grumpy"</tt>
   <tt>this is the name = this is the value</tt>
	 */
	protected void processInputLine(String aLine){
		//use a second Scanner to parse the content of each line 
		Scanner scanner = new Scanner(aLine);
		scanner.useDelimiter(";;");
		if ( scanner.hasNext() ){
			String input = scanner.next();
			setFfannInputNeuronsString(input);	
			//log("Input is : " + quote(input.trim()));
		}
		else {
			log("Empty or invalid line. Unable to process.");
		}
		//no need to call scanner.close(), since the source is a String
		//System.out.println("input: "+ getInputNeuronsString());
		//System.exit(1);
	}

	protected void processSomInputLine(String aLine){
		String input = aLine;
		setSomInputNeuronsString(input);	
		log("Input is : " + input.trim());
	}

	//method for counting the lines in an input file
	public static int lineCount(String inPath) throws Exception
	{
		int count = 0;
		File f = new File(inPath);
		Scanner input = new Scanner(f);
		while (input.hasNextLine())
		{
			String line = input.nextLine();
			count++;
		}
		//System.out.println("Number of Line: " + count);
		return count;
	}

	protected void processOutputLine(String aLine){
		//use a second Scanner to parse the content of each line 
		Scanner scanner = new Scanner(aLine);
		scanner.useDelimiter(";;");
		if ( scanner.hasNext() ){
			//String input = scanner.next();
			scanner.next();
			String output = scanner.next();
			setFfannOutputNeuronsString(output);
			//log("Output is : " + quote(output.trim()));
		}
		else {
			log("Empty or invalid line. Unable to process.");
		}
		//no need to call scanner.close(), since the source is a String
		//System.out.println("input: "+ getInputNeuronsString());
		//System.exit(1);
	}
	// PRIVATE 
	private final File fFile;

	private static void log(Object aObject){
		System.out.println(String.valueOf(aObject));
	}

	//	private String quote(String aText){
	//		String QUOTE = "'";
	//		return QUOTE + aText + QUOTE;
	//	}


	public void setFfannInputNeuronsString(String ins)
	{
		HOANNJSP.inputNeuronsString = ins;
		HOANNCommitteeJSP.inputNeuronsString = ins;
	}

	public void setFfannOutputNeuronsString(String out)
	{
		HOANNJSP.outputNeuronsString = out;
		HOANNCommitteeJSP.outputNeuronsString = out;
	}

	public void setSomInputNeuronsString(String ins)
	{
		//SelfOrganizingFeatureMapJSP.inputNeuronsString = ins;
	}
} 
