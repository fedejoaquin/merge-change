package controllers;

import java.util.List;
import java.util.Set;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import algorithms.Kernels;
import gui.SimpleAppView;
import incisionFunctions.DefaultComparator;
import incisionFunctions.LCSF;
import incisionFunctions.LSF;
import incisionFunctions.TSF;
import incisionFunctions.GLCSF;
import sets.CredibilityBase;
import sets.CredibilityElement;
import sets.CredibilityOrder;

public class AppController {
	
	public static final String ACTUAL_VERSION = "2.0.0";
	public static final int MAX_AGENT_ID = 99;
	public static final int MAX_CONTEXT_ID = 99;
	
	public static final int CREDIBILITY_ORDER_A = 1;
	public static final int CREDIBILITY_ORDER_B = 2;
	public static final int CREDIBILITY_ORDER_REVISION = 3;
	public static final int CREDIBILITY_ORDER_REVISION_TSF = 4;
	public static final int CREDIBILITY_ORDER_REVISION_LSF = 5;
	public static final int CREDIBILITY_ORDER_REVISION_LCSF_GLCSF = 6;
		
	public static final int RUNNING_EXAMPLE_1 = 100;
	public static final int RUNNING_EXAMPLE_2 = 101;
	public static final int RUNNING_EXAMPLE_7 = 107;

	public static final int RUNNING_EXAMPLE_1_MERGE = 108;
	public static final int RUNNING_EXAMPLE_JOBS_MERGE = 109;
	
	public static final int CREDIBILITY_ORDER_OPERATIONS = 1000;
	public static final int REVISION_OPERATOR = 1001;
	
	private SimpleAppView view;
	private CredibilityBase<Integer, Integer> cb;

	private boolean isRevision;
	
	public AppController() {
		view = null;
	}
	
	//----------------------------------------------------
	
	public void registerView(SimpleAppView v) {
		view = v;
		view.setLeftInformation("Program is running.", SimpleAppView.TEXT_SUCCESS);
		view.setRightInformation("Version: "+ ACTUAL_VERSION);
		view.setEnabledMenuOption(CREDIBILITY_ORDER_OPERATIONS, false);
		view.setEnabledMenuOption(REVISION_OPERATOR, false);
	}
	
	//----------------------------------------------------
	
	public void newCredibilityBase() {
		cb = new CredibilityBase<Integer, Integer>();
		
		cb.addCredibilityOrder(1);
		cb.addCredibilityOrder(2);
		
		view.resetRevisionPanels();
		view.setContextsList(getContextList());
		
		view.drawGraphInVisualizer(CREDIBILITY_ORDER_A, new DirectedAcyclicGraph<Integer, DefaultEdge>(DefaultEdge.class));
		view.drawGraphInVisualizer(CREDIBILITY_ORDER_B, new DirectedAcyclicGraph<Integer, DefaultEdge>(DefaultEdge.class));
		
		view.setEnabledMenuOption(CREDIBILITY_ORDER_OPERATIONS, true);
		view.setEnabledMenuOption(REVISION_OPERATOR, true);
		
		view.setLeftInformation("Default orders were created.", SimpleAppView.TEXT_SUCCESS); 
	}
		
	public void newCredibilityBase(int example) {
		
		switch(example) {
		
			case RUNNING_EXAMPLE_1:{
				cb = RunningExamples.get_example_1_prioritized_multiple_change();
				view.setLeftInformation("Credibility orders of Running Example 1 were created.", SimpleAppView.TEXT_SUCCESS);
				break;
			}

			case RUNNING_EXAMPLE_1_MERGE:{
				cb = RunningExamples.get_example_1_non_prioritized_multiple_change();
				view.setLeftInformation("Credibility orders of Running Example 1 were created.", SimpleAppView.TEXT_SUCCESS);
				break;
			}
			
			case RUNNING_EXAMPLE_2:{
				cb = RunningExamples.get_example_2_prioritized_multiple_change();
				view.setLeftInformation("Credibility orders of Running Example 2 were created.", SimpleAppView.TEXT_SUCCESS);
				break;
			}
			
			case RUNNING_EXAMPLE_7:{
				cb = RunningExamples.get_example_7_prioritized_multiple_change();
				view.setLeftInformation("Credibility orders of Running Example 7 were created.", SimpleAppView.TEXT_SUCCESS); 
				break;
			}
			
			case RUNNING_EXAMPLE_JOBS_MERGE:{
				cb = RunningExamples.get_example_jobs_non_prioritized_multiple_change();
				view.setLeftInformation("Credibility orders of Running Example 'Jobs' were created.", SimpleAppView.TEXT_SUCCESS);
				break;
			}
		}
		
		view.resetRevisionPanels();
		view.setContextsList(getContextList());
		view.setEnabledMenuOption(CREDIBILITY_ORDER_OPERATIONS, true);
		view.setEnabledMenuOption(REVISION_OPERATOR, true);	
	}

	public void readFromFileNewCredibilityBase(String pathA, String pathB) {
		cb = FileReaderCredibilityOrder.processCredibilityBaseFromFile(pathA, pathB);

		if(cb != null){
			view.resetRevisionPanels();
			view.setContextsList(getContextList());
			view.setEnabledMenuOption(CREDIBILITY_ORDER_OPERATIONS, true);
			view.setEnabledMenuOption(REVISION_OPERATOR, true);
			view.setLeftInformation("Credibility orders successfully loaded.", SimpleAppView.TEXT_SUCCESS);
		}else{
			view.setLeftInformation("An error occurs while loading credibility orders.", SimpleAppView.TEXT_ERROR);
		}

	}

	public void saveCredibilityBaseDataIntoFiles(String folderPath, String name) {
		FileReaderCredibilityOrder.writeCredibilityBaseFromData(folderPath, name, cb.getCredibilityOrders());
	}
	
	//----------------------------------------------------
	
	public void addCredibilityOrder(int context) {
		if (cb == null) {
			cb = new CredibilityBase<Integer, Integer>();
		}
		
		try {
			cb.addCredibilityOrder(context);
			
			view.resetRevisionPanels();
			view.setContextsList(getContextList());
			
			view.setEnabledMenuOption(CREDIBILITY_ORDER_OPERATIONS, true);
			view.setEnabledMenuOption(REVISION_OPERATOR, true);
			
			view.setLeftInformation("The credibility order " + context + " was created.", SimpleAppView.TEXT_SUCCESS);
		}catch(IllegalArgumentException e) {
			view.setLeftInformation("The credibility order " + context + " already exists.", SimpleAppView.TEXT_ERROR);
		}
	}

	//----------------------------------------------------
	
	public void addCredibilityElement(int agentA, int agentB, int context, int credibility_order_tab) {
		try {
			cb.addCredibilityObject(agentA, agentB, context);
			view.resetRevisionPanels();
			view.setLeftInformation("The credibility element "+agentA+">"+agentB+" in context "+context+" was added.", SimpleAppView.TEXT_SUCCESS);
			if(credibility_order_tab!=0) {
				view.drawGraphInVisualizer(credibility_order_tab, cb.getCredibilityOrder(context).getGraphRepresentation());
			}
		}catch(IllegalArgumentException e) {
			view.setLeftInformation("The credibility element "+agentA+">"+agentB+" in context "+context+" was not added.", SimpleAppView.TEXT_ERROR);
		}
	}
	
	public void removeCredibilityElement(int agentA, int agentB, int context, int credibility_order_tab) {
		try {
			cb.removeCredibilityObject(agentA, agentB, context);
			view.resetRevisionPanels();
			view.setLeftInformation("The credibility element "+agentA+">"+agentB+" in context "+context+" was removed.", SimpleAppView.TEXT_SUCCESS);
			view.drawGraphInVisualizer(credibility_order_tab, cb.getCredibilityOrder(context).getGraphRepresentation());
		}catch(IllegalArgumentException e) {
			view.setLeftInformation("The credibility element "+agentA+">"+agentB+" in context "+context+" was not removed.", SimpleAppView.TEXT_ERROR);
		}
	}
	
	public void changeActiveContextInOrderVisualizer(int credibility_order_tab, int context) {
		view.drawGraphInVisualizer(credibility_order_tab, cb.getCredibilityOrder(context).getGraphRepresentation());
	}

	// Modified code merge ---------------------------------
	public void applySelectionFunctions(int context1, int context2, boolean isRevision) {
		this.isRevision = isRevision;

		CredibilityOrder<Integer, Integer> co1, co2;
		Kernels<Integer, Integer> kernelsClass;
		TSF<Integer, Integer> selectedTSF;
		LSF<Integer, Integer> selectedLSF;
		LCSF<Integer, Integer> selectedLCSF;
		GLCSF<Integer, Integer> selectedGLCSF;
		List<List<CredibilityElement<Integer>>> kernels;
		List<CredibilityElement<Integer>> tsf, lsf, lcsf_glcsf;
		
		co1 = cb.getCredibilityOrder(context1);
		co2 = cb.getCredibilityOrder(context2);
		
		kernelsClass = new Kernels<Integer, Integer>(co1,co2);
		kernels = kernelsClass.computeKernels(isRevision);
		
		selectedTSF = new TSF<Integer, Integer>();
		tsf = selectedTSF.select(kernels);
		
		selectedLSF = new LSF<Integer, Integer>(new DefaultComparator<Integer>());
		lsf = selectedLSF.select(kernels);

		if(isRevision){
			selectedLCSF = new LCSF<Integer, Integer>(new DefaultComparator<Integer>());
			lcsf_glcsf = selectedLCSF.select(kernels);
		}else{
			selectedGLCSF = new GLCSF<Integer, Integer>(new DefaultComparator<Integer>());
			selectedGLCSF.setFilters(co1, co2);
			lcsf_glcsf = selectedGLCSF.select(kernels);
		}

		view.drawSimpleRevision(co1.getGraphRepresentation(), co2.getGraphRepresentation(), kernels, tsf, lsf, lcsf_glcsf);
		String operationString;

		// Modified code merge ---------------------------------
		if(isRevision)
			operationString = "revision";
		else
			operationString = "merge";
		view.setLeftInformation("Results of the "+operationString+" of Cred. Order " + SimpleAppView.aLabel + " by Cred. Order " + SimpleAppView.bLabel + ".", SimpleAppView.TEXT_SUCCESS);
		// ---------------------------------------------------
	}
	// ---------------------------------------------------

	//----------------------------------------------------
	
	private String [] getContextList() {
		Set<CredibilityOrder<Integer,Integer>> cos;
		String [] toReturn;
		int i = 0;
		
		cos = cb.getCredibilityOrders();
		toReturn = new String [cos.size()];
		
		for(CredibilityOrder<Integer, Integer> co : cos) {
			toReturn[i++] = new String("" + co.getID());
		}
		
		return toReturn;
	}

	public boolean isRevision(){
		return isRevision;
	}
	
}