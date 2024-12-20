package sets;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jgrapht.graph.AsUnmodifiableGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

public class CredibilityOrder<A,C> {
	
	private C id;
	private DirectedAcyclicGraph<A, DefaultEdge> co;
	
	// ---------------------------------
	
	public CredibilityOrder(C id, DirectedAcyclicGraph<A, DefaultEdge> co) {
		this.id = id;
		this.co = co;
	}
	
	// ---------------------------------
	
	public C getID() {
		return id;
	}
	
	// ---------------------------------
	
	public Set<A> agentSet() {
		return co.vertexSet();
	}
	
	public Set<CredibilityElement<A>> relationSet() {
		Set<CredibilityElement<A>> toReturn = new LinkedHashSet<CredibilityElement<A>>();
		CredibilityElement<A> ce;
		A agentA, agentB;
		
		for (DefaultEdge e :  co.edgeSet()) {
			agentB = co.getEdgeSource(e);
			agentA = co.getEdgeTarget(e);
			ce = new CredibilityElement<A>(agentA, agentB);
			toReturn.add(ce);
		}
		
		return toReturn;
	}
	
	// ---------------------------------
	
	public boolean containsCredibilityElement(A agentA, A agentB) {
		return co.containsEdge(agentB, agentA);
	}
	
	public AsUnmodifiableGraph<A, DefaultEdge> getGraphRepresentation(){
		AsUnmodifiableGraph<A, DefaultEdge> toReturn = new AsUnmodifiableGraph<A, DefaultEdge>(co);
		return toReturn;
	}

	// Modified code merge ---------------------------------
	public boolean containsCredibilityElement(CredibilityElement<A> elem) {
		A agentA, agentB;
		CredibilityElement<A> ce;

		for (DefaultEdge e :  co.edgeSet()) {
			agentB = co.getEdgeSource(e);
			agentA = co.getEdgeTarget(e);
			ce = new CredibilityElement<A>(agentA, agentB);
			if(ce.equals(elem)){
				return true;
			}
		}

		return false;
	}
	// ---------------------------------------------------
}
