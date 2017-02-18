package jstella.learning;

public interface JSIAgent {

	default int[] getAction(){
		return new int[] {0};
	}

}
