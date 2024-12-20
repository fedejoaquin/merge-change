package algorithms;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import sets.CredibilityElement;
import sets.CredibilityOrder;

import java.util.*;

public class Kernels<A,C> {
	
    private CredibilityOrder<A,C> order_1;
    private CredibilityOrder<A,C> order_2;
    private SimpleDirectedGraph<A, DefaultEdge> union;
    
    // Compute O2-inconsistent kernels of O1.
    // The elements of each kernel belongs to O1.
    public Kernels(CredibilityOrder<A,C> o1, CredibilityOrder<A,C> o2){
    	order_1 = o1;
        order_2 = o2;
        
        // Union of order_2 and order_1 without multiple-edges 
        // The order of union is very important: all edges of order_2 prevail over the repeated edges of order_1
        union = new SimpleDirectedGraph<A, DefaultEdge>(DefaultEdge.class);
        Graphs.addGraph(union, order_2.getGraphRepresentation());
        Graphs.addGraph(union, order_1.getGraphRepresentation());
        
    }

    public List<List<CredibilityElement<A>>> computeKernels(boolean revision) {
    	JohnsonBasedKernelDetector<A,C,DefaultEdge> kernelDetector = new JohnsonBasedKernelDetector<A,C,DefaultEdge>(union, order_2);
        // Modified code merge ---------------------------------
        kernelDetector.setOperationType(revision);
        // ---------------------------------------------------
        return kernelDetector.findKernels();
    }
}
