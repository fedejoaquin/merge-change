package algorithms;

import org.jgrapht.*;
import org.jgrapht.alg.util.*;
import org.jgrapht.graph.builder.*;

import sets.CredibilityElement;
import sets.CredibilityOrder;

import java.util.*;

public class JohnsonBasedKernelDetector<A, C, E> {
    // The graph.
    private Graph<A, E> graph;
    
    // Modified code -------------------------------------
    private CredibilityOrder<A,C> filter;  // The credibility order to filtering elements of cycles.
    // ---------------------------------------------------

    // The main state of the algorithm.
    // Modified code -------------------------------------
    private List<List<CredibilityElement<A>>> filteredCycles = null;
    // ---------------------------------------------------
    private A[] iToV = null;
    private Map<A, Integer> vToI = null;
    private Set<A> blocked = null;
    private Map<A, Set<A>> bSets = null;
    private ArrayDeque<A> stack = null;

    // The state of the embedded Tarjan SCC algorithm.
    private List<Set<A>> SCCs = null;
    private int index = 0;
    private Map<A, Integer> vIndex = null;
    private Map<A, Integer> vLowlink = null;
    private ArrayDeque<A> path = null;
    private Set<A> pathSet = null;

    // Type of operation
    // Modified code merge ---------------------------------
    private boolean revision;
    // ---------------------------------------------------

    // Modified code -------------------------------------
    public JohnsonBasedKernelDetector(Graph<A, E> graph, CredibilityOrder<A,C> filter){
        this.graph = graph;
        this.filter = filter;
    }
    // ---------------------------------------------------

    // Modified code merge -------------------------------------
    public void setOperationType(boolean isRevision) {
        revision = isRevision;
    }
    // ---------------------------------------------------

    // Modified code -------------------------------------
    public List<List<CredibilityElement<A>>> findKernels(){
   	// ---------------------------------------------------
        initState();

        int startIndex = 0;
        int size = graph.vertexSet().size();
        while (startIndex < size) {
            Pair<Graph<A, E>, Integer> minSCCGResult = findMinSCSG(startIndex);
            if (minSCCGResult != null) {
                startIndex = minSCCGResult.getSecond();
                Graph<A, E> scg = minSCCGResult.getFirst();
                A startV = toV(startIndex);
                for (E e : scg.outgoingEdgesOf(startV)) {
                    A v = graph.getEdgeTarget(e);
                    blocked.remove(v);
                    getBSet(v).clear();
                }
                findCyclesInSCG(startIndex, startIndex, scg);
                startIndex++;
            } else {
                break;
            }
        }
        
        // Modified code -------------------------------------
        List<List<CredibilityElement<A>>> result = filteredCycles;
        // ---------------------------------------------------
        
        clearState();
        return result;
    }

    private Pair<Graph<A, E>, Integer> findMinSCSG(int startIndex)
    {
        /*
         * Per Johnson : "adjacency structure of strong component $K$ with least vertex in subgraph
         * of $G$ induced by $(s, s + 1, n)$". Or in contemporary terms: the strongly connected
         * component of the subgraph induced by $(v_1, \dotso ,v_n)$ which contains the minimum
         * (among those SCCs) vertex index. We return that index together with the graph.
         */
        initMinSCGState();

        List<Set<A>> SCCs = findSCCS(startIndex);

        // find the SCC with the minimum index
        int minIndexFound = Integer.MAX_VALUE;
        Set<A> minSCC = null;
        for (Set<A> scc : SCCs) {
            for (A v : scc) {
                int t = toI(v);
                if (t < minIndexFound) {
                    minIndexFound = t;
                    minSCC = scc;
                }
            }
        }
        if (minSCC == null) {
            return null;
        }

        // build a graph for the SCC found
        Graph<A,
            E> resultGraph = GraphTypeBuilder
                .<A, E> directed().edgeSupplier(graph.getEdgeSupplier())
                .vertexSupplier(graph.getVertexSupplier()).allowingMultipleEdges(false)
                .allowingSelfLoops(true).buildGraph();
        for (A v : minSCC) {
            resultGraph.addVertex(v);
        }
        for (A v : minSCC) {
            for (A w : minSCC) {
                E edge = graph.getEdge(v, w);
                if (edge != null) {
                    resultGraph.addEdge(v, w, edge);
                }
            }
        }

        Pair<Graph<A, E>, Integer> result = Pair.of(resultGraph, minIndexFound);
        clearMinSCCState();
        return result;
    }

    private List<Set<A>> findSCCS(int startIndex)
    {
        // Find SCCs in the subgraph induced
        // by vertices startIndex and beyond.
        // A call to StrongConnectivityAlgorithm
        // would be too expensive because of the
        // need to materialize the subgraph.
        // So - do a local search by the Tarjan's
        // algorithm and pretend that vertices
        // with an index smaller than startIndex
        // do not exist.
        for (A v : graph.vertexSet()) {
            int vI = toI(v);
            if (vI < startIndex) {
                continue;
            }
            if (!vIndex.containsKey(v)) {
                getSCCs(startIndex, vI);
            }
        }
        List<Set<A>> result = SCCs;
        SCCs = null;
        return result;
    }

    private void getSCCs(int startIndex, int vertexIndex)
    {
        A vertex = toV(vertexIndex);
        vIndex.put(vertex, index);
        vLowlink.put(vertex, index);
        index++;
        path.push(vertex);
        pathSet.add(vertex);

        Set<E> edges = graph.outgoingEdgesOf(vertex);
        for (E e : edges) {
            A successor = graph.getEdgeTarget(e);
            int successorIndex = toI(successor);
            if (successorIndex < startIndex) {
                continue;
            }
            if (!vIndex.containsKey(successor)) {
                getSCCs(startIndex, successorIndex);
                vLowlink.put(vertex, Math.min(vLowlink.get(vertex), vLowlink.get(successor)));
            } else if (pathSet.contains(successor)) {
                vLowlink.put(vertex, Math.min(vLowlink.get(vertex), vIndex.get(successor)));
            }
        }
        if (vLowlink.get(vertex).equals(vIndex.get(vertex))) {
            Set<A> result = new HashSet<>();
            A temp;
            do {
                temp = path.pop();
                pathSet.remove(temp);
                result.add(temp);
            } while (!vertex.equals(temp));
            if (result.size() == 1) {
                A v = result.iterator().next();
                if (graph.containsEdge(vertex, v)) {
                    SCCs.add(result);
                }
            } else {
                SCCs.add(result);
            }
        }
    }

    private boolean findCyclesInSCG(int startIndex, int vertexIndex, Graph<A, E> scg)
    {
        /*
         * Find cycles in a strongly connected graph per Johnson.
         */
        boolean foundCycle = false;
        A vertex = toV(vertexIndex);
        stack.push(vertex);
        blocked.add(vertex);

        for (E e : scg.outgoingEdgesOf(vertex)) {
            A successor = scg.getEdgeTarget(e);
            int successorIndex = toI(successor);
            if (successorIndex == startIndex) {
                
            	// Modified code -------------------------------------
            	List<CredibilityElement<A>> filteredCycle = filteredCredibilityElements(stack.descendingIterator());
                if (filteredCycle.size() > 0)
                	addIfNotUnderSetInclusion(filteredCycle);
                // ---------------------------------------------------
                
                foundCycle = true;
            } else if (!blocked.contains(successor)) {
                boolean gotCycle = findCyclesInSCG(startIndex, successorIndex, scg);
                foundCycle = foundCycle || gotCycle;
            }
        }
        if (foundCycle) {
            unblock(vertex);
        } else {
            for (E ew : scg.outgoingEdgesOf(vertex)) {
                A w = scg.getEdgeTarget(ew);
                Set<A> bSet = getBSet(w);
                bSet.add(vertex);
            }
        }
        stack.pop();
        return foundCycle;
    }

    private void unblock(A vertex)
    {
        blocked.remove(vertex);
        Set<A> bSet = getBSet(vertex);
        while (bSet.size() > 0) {
            A w = bSet.iterator().next();
            bSet.remove(w);
            if (blocked.contains(w)) {
                unblock(w);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void initState()
    {
    	// Modified code -------------------------------------
    	filteredCycles = new LinkedList<List<CredibilityElement<A>>>();
        // ---------------------------------------------------

        iToV = (A[]) graph.vertexSet().toArray();
        vToI = new HashMap<>();
        blocked = new HashSet<>();
        bSets = new HashMap<>();
        stack = new ArrayDeque<>();

        for (int i = 0; i < iToV.length; i++) {
            vToI.put(iToV[i], i);
        }
    }

    private void clearState()
    {
    	// Modified code -------------------------------------
        filteredCycles = null;
        // ---------------------------------------------------
        iToV = null;
        vToI = null;
        blocked = null;
        bSets = null;
        stack = null;
    }

    private void initMinSCGState()
    {
        index = 0;
        SCCs = new ArrayList<>();
        vIndex = new HashMap<>();
        vLowlink = new HashMap<>();
        path = new ArrayDeque<>();
        pathSet = new HashSet<>();
    }

    private void clearMinSCCState()
    {
        index = 0;
        SCCs = null;
        vIndex = null;
        vLowlink = null;
        path = null;
        pathSet = null;
    }

    private Integer toI(A vertex)
    {
        return vToI.get(vertex);
    }

    private A toV(Integer i)
    {
        return iToV[i];
    }

    private Set<A> getBSet(A v)
    {
        // B sets typically not all needed,
        // so instantiate lazily.
        return bSets.computeIfAbsent(v, k -> new HashSet<>());
    }
    
    // New code -------------------------------------
    private List<CredibilityElement<A>> filteredCredibilityElements (Iterator<A> it){
    	List<CredibilityElement<A>> toReturn = new ArrayList<>();
    	
    	A first = it.next();
    	A prev = first;
    	A actual;

        // Modified code merge ---------------------------------
        if(revision) {
            while (it.hasNext()) {
                actual = it.next();
                if (!filter.containsCredibilityElement(actual, prev)) 
                    toReturn.add(new CredibilityElement<A>(actual, prev));
                prev = actual;
            }

            if (!filter.containsCredibilityElement(first, prev))
                toReturn.add(new CredibilityElement<A>(first, prev));
        }else{
            while (it.hasNext()) {
                actual = it.next();
                toReturn.add(new CredibilityElement<A>(actual, prev));
                prev = actual;
            }

            toReturn.add(new CredibilityElement<A>(first, prev));
        }
        // ---------------------------------------------------

    	return toReturn;
    }
    
    private void addIfNotUnderSetInclusion(List<CredibilityElement<A>> filteredCycle) {
    	boolean is_kernel;
    	int index;

    	if(filteredCycles.isEmpty()) {
			filteredCycles.add(filteredCycle);
    	}else{
			if(filteredCycle.size() <= filteredCycles.get(0).size()){
				filteredCycles.add(0, filteredCycle);
					
				//Process that checks that the old kernels remain it.
				index = 1;
				while(index < filteredCycles.size()){
					if(containsAll(filteredCycles.get(index), filteredCycle)) {
						filteredCycles.remove(index);
					}else {
						index++;
					}
				}
			}else{
				//Finding bucket in vector to the path.
				is_kernel = true; index = 0;
				while( index < filteredCycles.size() && is_kernel && filteredCycles.get(index).size() < filteredCycle.size() ) {
					is_kernel = !containsAll(filteredCycle, filteredCycles.get(index++));
				}
				
				if (is_kernel) {
					if (index < filteredCycles.size()){
						filteredCycles.add(index, filteredCycle);
						
						index++;
						while(index < filteredCycles.size()){
							if(containsAll(filteredCycles.get(index), filteredCycle)) {
								filteredCycles.remove(index);
							}else {
								index++;
							}
						}
					}else {
						filteredCycles.add(filteredCycle);
					}
				}
			}
		}
    }
    
    // Check if all elements of listB belongs to listA
    private boolean containsAll(List<CredibilityElement<A>> listA, List<CredibilityElement<A>> listB) {
    	CredibilityElement<A> actual;
    	boolean toReturn = true;
    	int index = 0;
    	
    	while(toReturn && index < listB.size()) {
    		actual = listB.get(index++);
    		toReturn = belongs(actual, listA);
    	}
    	
    	return toReturn;
    }
    
    private boolean belongs(CredibilityElement<A> ce, List<CredibilityElement<A>> list) {
    	boolean found = false;
    	int index = 0;
    	
    	while(!found && index < list.size()) {
    		found = ce.equals(list.get(index++));
    	}
    	
    	return found;
    }
    
    // --------------------------------------------
}
