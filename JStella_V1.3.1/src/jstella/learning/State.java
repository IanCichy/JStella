package jstella.learning;
import java.util.Arrays;

public class State implements Comparable<Object>{

	private int[] state;

	public State(int[] m){
		state = m;
	}

	public String toString(){
		String a = "State: ";
		for( int x : state){
			a += x +", ";
		}

		return a;
	}

	public int[] getState() {
		return state;
	}

	public void setState(int[] state) {
		this.state = state;
	}

	@Override
	public int compareTo(Object other) {
		
		StateActionPair S = (StateActionPair)other;

		int[] state1 = this.getState();
		int[] state2 = S.getState();

		if(Arrays.equals(state1, state2))
			return 0;
		else
			return -1;
	}

	public int hashCode(){
		int hashcode = 1;
		for(int x : state){
			hashcode += 37 * hashcode + x;
		}
		return hashcode;
	}

	@Override
	public boolean equals(Object other) {
		State S = (State)other;

		int[] state1 = this.getState();
		int[] state2 = S.getState();

		if(Arrays.equals(state1, state2))
			return true;
		else
			return false;
	}


}
