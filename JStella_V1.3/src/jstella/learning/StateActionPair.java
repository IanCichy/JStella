package jstella.learning;
import java.util.Arrays;

public class StateActionPair implements Comparable<Object>{

	private int[] state;
	private int[] action;

	public StateActionPair(int[] m, int[] a){
		state = m;
		action = a;
	}

	public String toString(){
		String a = "State: ";
		for( int x : state){
			a += x +", ";
		}

		a+=" Action: ";
		for( int t : action){
			a += t +", ";
		}
		return a;
	}

	public int[] getAction() {
		return action;
	}

	public void setAction(int action[]) {
		this.action = action;
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

		int[] act1 = this.getAction();
		int[] act2 = S.getAction();

		if(Arrays.equals(state1, state2) && Arrays.equals(act1, act2))
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
		StateActionPair S = (StateActionPair)other;

		int[] state1 = this.getState();
		int[] state2 = S.getState();

		int[] act1 = this.getAction();
		int[] act2 = S.getAction();

		if(Arrays.equals(state1, state2) && Arrays.equals(act1, act2))
			return true;
		else
			return false;
	}


}
