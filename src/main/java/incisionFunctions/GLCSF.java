package incisionFunctions;

import java.util.*;

import sets.CredibilityElement;
import sets.CredibilityOrder;

// Least Credible Selection Function
public class GLCSF<C, A> implements IncisionFunction<A, C> {
	
	private Comparator<A> comparator;
	private CredibilityOrder<Integer, Integer> filter1, filter2;

	public GLCSF(Comparator<A> comparator) {
		this.comparator = comparator;
	}

	public void setFilters(CredibilityOrder<Integer, Integer> co1, CredibilityOrder<Integer, Integer> co2){
		filter1 = co1;
		filter2 = co2;
	}

	@Override
	public List<CredibilityElement<A>> select(List<List<CredibilityElement<A>>> kernelSet) {
		List<CredibilityElement<A>> toReturn = new ArrayList<CredibilityElement<A>>();
		boolean alreadySelected;
		for(List<CredibilityElement<A>> kernel : kernelSet) {
			List<CredibilityElement<A>> selected = splitCredibilityElements(kernel);
			for(CredibilityElement<A> ceSelected : selected){
				alreadySelected = false;
				for(CredibilityElement<A> ce : toReturn) {
					if(ce.equals(ceSelected)) {
						alreadySelected = true;
						break;
					}
				}
				if (!alreadySelected)
					toReturn.addAll(selected);
			}
		}
		
		return toReturn;
	}

	private List<CredibilityElement<A>> splitCredibilityElements(List<CredibilityElement<A>> kernel){
		List<CredibilityElement<A>> toReturn = new ArrayList<>();
		List<CredibilityElement<A>> kernel1 = new ArrayList<>();
		List<CredibilityElement<A>> kernel2 = new ArrayList<>();

		A most;
		A less;
		for(CredibilityElement<A> element : kernel){
			most = element.getMostCredible();
			less = element.getLessCredible();
			
			if (filter1.containsCredibilityElement((CredibilityElement<Integer>) element)) // (actual > prev) ; (prev-->actual)
				kernel1.add(new CredibilityElement<A>(most, less));
			if (filter2.containsCredibilityElement((CredibilityElement<Integer>) element)) // (actual > prev) ; (prev-->actual)
				kernel2.add(new CredibilityElement<A>(most, less));
		}

		toReturn.add(leastCredible(kernel1));
		toReturn.add(leastCredible(kernel2));

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