package sets;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

public class CredibilityBase<A,C> {
	
	protected HashMap<C, DirectedAcyclicGraph<A, DefaultEdge>> cb;
	
	// ---------------------------------
	
	public CredibilityBase() {
		cb = new HashMap<C, DirectedAcyclicGraph<A, DefaultEdge>>();	
	}
	
	// ---------------------------------
	
	public void addCredibilityOrder(C context) {
		checkContext(context, false);
		cb.put(context, new DirectedAcyclicGraph<A, DefaultEdge>(DefaultEdge.class));
	}
	
	public void addCredibilityObject(A agentA, A agentB, C context) {
		DirectedAcyclicGraph<A, DefaultEdge> co;
		boolean addedA = false, addedB = false;
		
		if (cb.get(context) == null) {
			cb.put(context, new DirectedAcyclicGraph<A, DefaultEdge>(DefaultEdge.class));
		}
		
		co = cb.get(context);
		
		try {
		
			addedA = co.addVertex(agentA);
			addedB = co.addVertex(agentB);
			co.addEdge(agentB, agentA);
		
		}catch(IllegalArgumentException e) {
			if (addedA) { co.removeVertex(agentA); }
			if (addedB) { co.removeVertex(agentB); }
			throw new IllegalArgumentException("The credibility relation " + agentA + " > " + agentB + " induce an invalid credibility order.");
		}
		
	}
	
	// ---------------------------------
	
	public void removeCredibilityOrder(C context) {
		checkContext(context, true);		
		cb.remove(context);
	}
	
	public void removeCredibilityObject(A agentA, A agentB, C context) {
		DirectedAcyclicGraph<A, DefaultEdge> co;
		
		checkContext(context,true);
		
		co = cb.get(context);
		co.removeEdge(agentB, agentA);
		
		if (co.incomingEdgesOf(agentA).isEmpty() && co.outgoingEdgesOf(agentA).isEmpty()) {
			co.removeVertex(agentA);
		}
		
		if (co.incomingEdgesOf(agentB).isEmpty() && co.outgoingEdgesOf(agentB).isEmpty()) {
			co.removeVertex(agentB);
		}
		
	}
	
	// ---------------------------------
	
	public CredibilityOrder<A,C> getCredibilityOrder(C context) {
		checkContext(context, true);
		return new CredibilityOrder<A,C>(context, cb.get(context));
	}
	
	public Set<CredibilityOrder<A,C>> getCredibilityOrders(){
		Set<CredibilityOrder<A,C>> toReturn = new LinkedHashSet<CredibilityOrder<A,C>>();
		for(Entry<C, DirectedAcyclicGraph<A, DefaultEdge>> e : cb.entrySet())
			toReturn.add(new CredibilityOrder<A,C>(e.getKey(), e.getValue()));
		return toReturn;
	}
	
	// ---------------------------------
	
	protected void checkContext(C context, boolean mustExist) {
		if (mustExist) {
			if (cb.get(context) == null) {
				throw new IllegalArgumentException("The credibility order in context " + context + " does not exists.");
			}
		}else {
			if (cb.get(context) != null) {
				throw new IllegalArgumentException("The credibility order in context " + context + " already exists.");
			}
		}
	}
}