package ltf.statevm.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Main{

	private static boolean	checkOnly	= false;
	private static boolean	help		= false;
	private static boolean	error		= false;
	private static boolean	sepFile		= false;

	public static void main(String[] args){
		parseArgs(args);
		if(error){
			System.out.println("ERR: Please run with option -help to show help.");
			return;
		}
		if(help){
			System.out.println("FIN: Showed help.");
			return;
		}
		if(checkOnly || sepFile){
			args[0] = args[1];
		}

		File statesFile = new File(args[0]);
		if(!statesFile.exists()){
			System.out.println("ERR: " + args[0] + ": File not found!");
			return;
		}

		if(sepFile){
			args[1] = args[2];
			File inputFile = new File(args[1]);
			if(!inputFile.exists()){
				System.out.println("ERR: " + args[1] + ": File not found!");
				return;
			}
			try{
				BufferedReader br = new BufferedReader(new FileReader(inputFile));
				args[1] = "";
				String line = br.readLine();
				while(line != null){
					args[1] += line;
					line = br.readLine();
				}
				br.close();
				if(args[1].isEmpty()){
					System.out.println("ERR: Empty input file.");
					return;
				}
			} catch(Exception e){
				System.out.println("ERR: I/O Error while reading input file.");
				return;
			}

		}

		State[] states = State.parse(statesFile);

		if(states == null){
			System.out.println("ERR: One or more lines are invalid!");
			return;
		}

		if(checkOnly){
			System.out.println("FIN: Checked states file.");
			return;
		}
		Runner.run(states, args[1]);
	}

	private static void parseArgs(String[] args){
		if(args.length == 1){
			if(!args[0].equals("-help")){
				System.out.println("ERR: One parameter: -help");
				error = true;
				return;
			}
			showHelp();
			help = true;
			return;
		} else if(args.length == 2){
			if(args[0].equals("-c")){
				checkOnly = true;
			}
			return;
		} else if(args.length == 3){
			if(!args[0].equals("-f")){
				System.out.println("ERR: Three parameters: -f <stateFile> <input>");
				error = true;
				return;
			}
			sepFile = true;
			return;
		}
		error = true;
	}

	private static void showHelp(){
		System.out.println("StateVM [Version 1.0.1] | Options:");
		System.out.println("\t-help\t\t\t\t\tShows this page.");
		System.out.println("\t<statesFile> <input>\t\t\tRuns VM with input as second argument.");
		System.out.println("\t-c <statesFile>\t\t\t\tChecks stateFile for errors.");
		System.out.println("\t-f <statesFile> <inputFile>\t\tRuns VM with input from file.");
	}

}
