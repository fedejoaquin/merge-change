package incisionFunctions;

import java.util.List;

import sets.CredibilityElement;

public interface IncisionFunction<A, C> {
	
	public List<CredibilityElement<A>> select ( List<List<CredibilityElement<A>>> kernelSet );

}
