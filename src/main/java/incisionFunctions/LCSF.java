package incisionFunctions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sets.CredibilityElement;

// Least Credible Selection Function
public class LCSF<C, A> implements IncisionFunction<A, C> {
	
	private Comparator<A> comparator;
	
	public LCSF(Comparator<A> comparator) {
		this.comparator = comparator;
	}
	
	@Override
	public List<CredibilityElement<A>> select(List<List<CredibilityElement<A>>> kernelSet) {
		List<CredibilityElement<A>> toReturn = new ArrayList<CredibilityElement<A>>();
		for(List<CredibilityElement<A>> kernel : kernelSet) {
			toReturn.add( leastCredible(kernel) );
		}
		
		return toReturn;
	}
	
	private CredibilityElement<A> leastCredible (List<CredibilityElement<A>> kernel){
		Map<A, List<CredibilityElement<A>>> agentToTuples = new HashMap<A, List<CredibilityElement<A>>>();
		Map<CredibilityElement<A>, Boolean> visited = new HashMap<CredibilityElement<A>, Boolean>();
		List<CredibilityElement<A>> tuplesList, candidates;
		Iterator<CredibilityElement<A>> it;
		CredibilityElement<A> min, actual;
		int compValue;
		
		for(CredibilityElement<A> ce : kernel) {
			tuplesList = agentToTuples.get(ce.getMostCredible());
			if (tuplesList == null) {
				tuplesList = new ArrayList<CredibilityElement<A>>();
				tuplesList.add(ce);
				agentToTuples.put(ce.getMostCredible(), tuplesList);
			}else {
				tuplesList.add(ce);
			}
		}
		
		candidates = new ArrayList<CredibilityElement<A>>();
		for(CredibilityElement<A> ce : kernel) {
			if (visited.get(ce) == null) {
				lastElementsOfPaths(ce, visited, agentToTuples, candidates);
			}
		}
		
		it = candidates.iterator();
		min = it.next();
		while(it.hasNext()) {
			actual = it.next();
			compValue = comparator.compare(min.getMostCredible(), actual.getMostCredible());
			if (compValue > 0 ) {
				min = actual;
			}else {
				if (compValue == 0) {
					compValue = comparator.compare(min.getLessCredible(), actual.getLessCredible());
					if (compValue > 0) {
						min = actual;
					}
				}
			}
		}
		
		return min;
	}
	
	private void lastElementsOfPaths(	CredibilityElement<A> tuple, 
										Map<CredibilityElement<A>, Boolean> visited, 
										Map<A, List<CredibilityElement<A>>> agentToTuples,
										List<CredibilityElement<A>> candidates ){
		
		List<CredibilityElement<A>> agentToTuplesList = agentToTuples.get(tuple.getLessCredible());
		visited.put(tuple, true);
		
		if (agentToTuplesList == null) {
			candidates.add(tuple);
		}else {
			for(CredibilityElement<A> ce : agentToTuplesList) {
				if (visited.get(ce) == null) {
					lastElementsOfPaths(ce, visited, agentToTuples, candidates);
				}
			}
		}
	}
}