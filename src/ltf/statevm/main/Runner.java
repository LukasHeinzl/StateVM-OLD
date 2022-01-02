package ltf.statevm.main;

public class Runner{

	public static void run(State[] states, String input){
		char[] chars = input.toCharArray();
		State start = getStartState(states);
		State current = start;
		State previous = null;

		if(start == null){
			System.out.println("ERR: No start state!");
			return;
		}

		for(char c: chars){
			String newStateName = getJumpStateNameForCase(current, c);
			if(newStateName == null){
				System.out.println("ERR: <state " + current.getName() + "> Unknown case: " + c);
				return;
			}

			if(newStateName.equals("loop")){
				continue;
			}

			if(newStateName.equals("back")){
				if(previous == null){
					System.out.println("ERR: <state " + current.getName() + "> No 'back' case!");
					return;
				}
				current = previous;
				continue;
			}

			previous = current;
			current = getByName(states, newStateName);
			if(current == null){
				System.out.println("ERR: <state " + previous.getName() + "> Unknown state: " + newStateName);
				return;
			}
		}

		if(current.isAcceptState()){
			System.out.println("IAS: " + current.getName());
		} else{
			System.out.println("NAS: " + current.getName());
		}

	}

	private static State getStartState(State[] states){
		for(State s: states){
			if(s.isStartState()){
				return s;
			}
		}
		return null;
	}

	private static State getByName(State[] states, String name){
		for(State s: states){
			if(s.getName().equals(name)){
				return s;
			}
		}
		return null;
	}

	public static String getJumpStateNameForCase(State s, char c){
		for(String[] split: s.getInputJumps()){
			if(split[0].equals(c + "")){
				return split[1];
			}
		}
		if(s.hasDefaultState()){
			return s.getDefaultState();
		}

		return null;
	}

}
