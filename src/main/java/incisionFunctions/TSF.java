package incisionFunctions;

import java.util.ArrayList;
import java.util.List;

import sets.CredibilityElement;

// Total Selection Function
public class TSF<A, C> implements IncisionFunction<A, C> {
	
	public TSF() { }
	
	@Override
	public List<CredibilityElement<A>> select(List<List<CredibilityElement<A>>> kernelSet) {

		List<CredibilityElement<A>> toReturn = new ArrayList<CredibilityElement<A>>();
		for(List<CredibilityElement<A>> kernel : kernelSet) {
			for(CredibilityElement<A> ce : kernel) {
				toReturn.add(ce);
			}
		}

		// Modified code merge ---------------------------------
		for(int i = toReturn.size()-1; i>=0; i--) {
			for(int j = 0; j<i; j++){
				if(toReturn.get(i).equals(toReturn.get(j))){
					toReturn.remove(i);
					break;
				}
			}
		}
		// ---------------------------------------------------

		return toReturn;
	}

}
