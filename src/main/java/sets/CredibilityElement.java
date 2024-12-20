package sets;

public class CredibilityElement<A> {
	private A agentA;
	private A agentB;
	
	public CredibilityElement(A agentA, A agentB) {
		this.agentA = agentA;
		this.agentB = agentB;
	}
	
	public A getMostCredible() {
		return agentA;
	}
	
	public A getLessCredible() {
		return agentB;
	}
	
	public String toString() {
		return "(" + agentA + ">" + agentB + ")";
	}
	
	public boolean equals(CredibilityElement<A> ce) {
		return agentA.equals(ce.getMostCredible()) && agentB.equals(ce.getLessCredible());
	}
	
	public int hashCode(){
		return (agentA.hashCode() + agentB.hashCode());
	}
	
}
