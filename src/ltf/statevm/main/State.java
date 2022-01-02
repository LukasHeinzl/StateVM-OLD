package ltf.statevm.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class State{

	private String				name;
	private ArrayList<String[]>	inputJumps;
	private boolean				isStartState;
	private boolean				isAcceptState;
	private boolean				hasDefaultState;
	private String				defaultState;
	private static List<String>	invalidNames	= (List<String>) Arrays.asList("state", "loop", "default", "start_state", "accept_state", "back");

	private State(String name, ArrayList<String[]> inputJumps, boolean isStartState, boolean isAcceptState, boolean hasDefaultState, String defaultState){
		this.name = name;
		this.inputJumps = inputJumps;
		this.isStartState = isStartState;
		this.isAcceptState = isAcceptState;
		this.hasDefaultState = hasDefaultState;
		this.defaultState = defaultState;
	}

	public String getName(){
		return name;
	}

	public ArrayList<String[]> getInputJumps(){
		return inputJumps;
	}

	public boolean isStartState(){
		return isStartState;
	}

	public boolean isAcceptState(){
		return isAcceptState;
	}

	public boolean hasDefaultState(){
		return hasDefaultState;
	}

	public String getDefaultState(){
		return defaultState;
	}

	public String toString(){
		String str = name + "{\n" + (isStartState ? "\tstart_state;\n" : "") + (isAcceptState ? "\texcept_state;\n" : "");
		for(String[] split: inputJumps){
			str += "\t" + split[0] + ": " + split[1] + ";\n";
		}
		return str + "}";
	}

	public static State[] parse(File f){
		ArrayList<String> lines = new ArrayList<>();
		try{
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while(line != null){
				lines.add(line);
				line = br.readLine();
			}
			br.close();
		} catch(Exception e){
			return null;
		}
		if(lines.isEmpty()){
			return null;
		}

		ArrayList<State> states = new ArrayList<>();
		ArrayList<String> stateNames = new ArrayList<>();
		boolean hadError = false;
		boolean foundSS = false;

		String name = "";
		ArrayList<String[]> iJ = new ArrayList<>();
		boolean iSS = false;
		boolean iAS = false;
		boolean hDS = false;
		String dS = "";

		for(int i = 0; i < lines.size(); i++){
			String line = lines.get(i);
			if(line.isEmpty()){
				continue;
			}

			if(line.startsWith("state ") && line.endsWith("{")){
				name = line.substring(6, line.length() - 1).trim();
				if(invalidNames.contains(name)){
					System.out.println("ERR: <line " + i + "> Invalid name: " + line);
					hadError = true;
				}
				if(stateNames.contains(name)){
					System.out.println("ERR: <line " + i + "> Duplicate name: " + line);
					hadError = true;
				}
				stateNames.add(name);
			} else if(line.equals("}")){
				states.add(new State(name, iJ, iSS, iAS, hDS, dS));
				name = "";
				iJ = new ArrayList<>();
				iSS = false;
				iAS = false;
				hDS = false;
				dS = "";
			} else if(line.startsWith("\t")){
				line = line.trim();
				if(line.equals("start_state;")){
					iSS = true;
					if(foundSS){
						System.out.println("ERR: <line " + i + "> Found another starting point: " + line);
						hadError = true;
					}
					foundSS = true;
				} else if(line.equals("accept_state;")){
					iAS = true;
				} else if(line.contains(":") && line.split(":").length == 2 && line.endsWith(";")){
					String[] split = line.split(":");
					split[0] = split[0].trim();
					split[1] = split[1].substring(0, split[1].length() - 1).trim();
					iJ.add(split);
					if(split[0].length() != 1){
						if(split[0].equals("default")){
							hDS = true;
							dS = split[1];
						} else{
							System.out.println("ERR: <line " + i + "> InputCase must be a single character: " + line);
							hadError = true;
						}
					}
				} else{
					System.out.println("ERR: <line " + i + "> Invalid line: " + line);
					hadError = true;
				}
			} else{
				System.out.println("ERR: <line " + i + "> Invalid line: " + line);
				hadError = true;
			}
		}
		if(hadError){
			return null;
		}

		return states.toArray(new State[states.size()]);
	}
}
