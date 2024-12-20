package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import controllers.FileSelector;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.ListenableGraph;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.SimpleDirectedGraph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;

import controllers.AppController;
import sets.CredibilityElement;

public class SimpleAppView {
	
	public static final int TEXT_ERROR = 0;
	public static final int TEXT_SUCCESS = 1;
	
	public static final String aLabel = new String(Character.toChars(01661)); // alpha
	public static final String bLabel = new String(Character.toChars(01662)); // beta
	public static final String revisionLabel = new String(Character.toChars(120495)); // Revision operator
	public static final String mergeLabel = new String(Character.toChars(934)); // Merge operator
	
	public static final String waitingInitializationLabel = new String("... Waiting for Credibility Orders ...");
	public static final String waitingOperationLabel = new String("... Waiting for Operation Request ...");
	
	private JFrame frame;
	private AppController appController;
	
	private final Dimension leftMainPanelDimension = new Dimension(400,600);
	private final Dimension ordersABTabPanelDimension = new Dimension(380,280);
	private final Dimension ordersABGraphsDimension = new Dimension(360,210);
	
	private final Dimension rightMainPanelDimension = new Dimension(685,600);
	private final Dimension topRightTabbedPanelDimension = new Dimension(680, 330);
	private final Dimension unionTabbedPanelDimension = new Dimension(415, 285);
	private final Dimension kernelTabbedPanelDimension = new Dimension(250, 285);
	private final Dimension revisionResultsTabbedPanelDimension = new Dimension(680, 265);
	
	private final String colorOrderA = new String("#00ff00");
	private final String colorOrderB = new String("#ff0000");
	private final String colorKernel = new String("#0000ff");
	
	// Main Panels
	private JPanel leftMainPanel, rightMainPanel, bottomMainPanel;
	
	// Menu Elements
	private JMenuBar menuBar;
	private JMenu menuCredibilityOrder, menuOperators, menuHelp, menuOpen, menuRuningExample, menuAddResultingOrder;
	private JMenuItem menuItemDefaultBase, menuItemAddOrder, menuItemAddResultingOrderTSF, menuItemAddResultingOrderLSF, menuItemAddResultingOrderLCSFGLCSF, menuItemExport, menuItemExit, menuItemExample1, menuItemExample2, menuItemExample7, menuItemExample1Merge, menuItemExampleJobsMerge, menuItemLoadCredibilityBase;
	private JMenuItem menuItemRevisionOperator;
	private JMenuItem menuItemMergeOperator;
	private JMenuItem menuItemUserManual, menuItemAboutVersion, menuItemPoweredBy;
	
	// Main Tabbed Panels
	private JTabbedPane leftMainTabbedPane, orderATabbedPane, orderBTabbedPane;
	private JTabbedPane topRightMainTabbedPane, unionTabbedPanel, kernelTabbedPanel;
	private JTabbedPane bottomRightMainTabbedPane;
	
	// Main Tab Panels
	private JComponent ordersABTabPanel, topRightMainTabPanel;
	
	// Main Panels
	private JComponent orderAPanel, orderASelectInputPanel, orderAGraphPanel;
	private JComponent orderBPanel, orderBSelectInputPanel, orderBGraphPanel;
	private JComponent unionPanel, kernelPanel;
	private JComponent resultsByTSFPane, resultsByLSFPane, resultsBy_LCSF_GLCSF_Pane;
	
	// Labels Information
	private JLabel lblInformationLeft, lblInformationRight;
	
	private JComboBox<String> comboSelectOrderA, comboSelectOrderB, comboSelectOperationA, comboSelectOperationB;
		
	private JGraphXAdapter<Integer, DefaultEdge> adapterOrderA, adapterOrderB;
	private JGraphXAdapter<Integer, DefaultEdge> adapterUnion, adapterRevisionTSF, adapterRevisionLSF, adapterRevisionLCSF_GLCSF;
	
	private mxGraphComponent graphComponentOrderA, graphComponentOrderB;
	private mxGraphComponent graphComponentRevision, graphComponentRevisionTSF, graphComponentRevisionLSF, graphComponentRevisionLCSF_GLCSF;
	
	private mxHierarchicalLayout layoutOrderA, layoutOrderB;
	private mxHierarchicalLayout layoutRevision, layoutRevisionTSF, layoutRevisionRSF, layoutRevisionLCSF_GLCSF;

	private SimpleDirectedGraph<Integer, DefaultEdge> union, union_tsf, union_lsf, union_lcsf_glcsf;
	
	public SimpleAppView(AppController l) {
		appController = l;
		
		frame = new JFrame("Graphical user interface of multiple change on credibility orders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1100, 700));
        frame.setResizable(false);
        
        initComponent();
        
        frame.pack();
        frame.setVisible(true);
	}
	
	protected void initComponent() {
		// Menu Bar panel
		menuBar = new JMenuBar();
        frame.getContentPane().add(BorderLayout.NORTH, menuBar);
        
        // Information Panel
        bottomMainPanel = new JPanel();
        bottomMainPanel.setPreferredSize(new Dimension(0, 30));
        bottomMainPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        bottomMainPanel.setLayout(new BorderLayout());
        frame.getContentPane().add(BorderLayout.SOUTH, bottomMainPanel);
               
        // Credibility orders A and B Panel
        leftMainPanel = new JPanel();
        leftMainPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        leftMainPanel.setPreferredSize(leftMainPanelDimension);
		frame.getContentPane().add(BorderLayout.WEST, leftMainPanel);
        
		// Union and Revision Panel
		rightMainPanel = new JPanel();
		rightMainPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		rightMainPanel.setPreferredSize(rightMainPanelDimension);
        frame.getContentPane().add(BorderLayout.EAST, rightMainPanel);
		
		initMenu();
		initInformationPanel();
		initLeftMainPanel();
		initRightMainPanel();
		initListeners();
        
	}
	
	protected void initMenu() {     
		// General Menu Bar
		menuCredibilityOrder = new JMenu("Credibility Orders");
        menuOperators = new JMenu("Operators");  
        menuHelp = new JMenu("Help");
        
        menuBar.add(menuCredibilityOrder);
        menuBar.add(menuOperators);
        menuBar.add(menuHelp);
        
        // Credibility Base Menu Bar
        menuOpen = new JMenu("Open");
        menuItemDefaultBase = new JMenuItem("New Default Orders");
        menuItemDefaultBase.setToolTipText("Create two empty credibility orders.");
        menuItemAddOrder = new JMenuItem("Add Credibility Order...");
        menuItemAddOrder.setToolTipText("Create an empty credibility order.");
		// Modified code merge ---------------------------------
        menuAddResultingOrder = new JMenu("Add resulting credibility order");
		// ---------------------------------------------------
        menuItemExport = new JMenuItem("Export Orders...");
        menuItemExport.setToolTipText("Export the complete credibility base.");
        menuItemExit = new JMenuItem("Exit");
        
        menuRuningExample = new JMenu("Running examples");
        menuItemLoadCredibilityBase = new JMenuItem("Credibility Base from existing file...");
		menuItemExample1 = new JMenuItem("Example 1 (IJAR 2022)");
        menuItemExample2 = new JMenuItem("Example 2 (IJAR 2022)");
        menuItemExample7 = new JMenuItem("Example 7 (IJAR 2022)");
		
		menuItemExample1Merge = new JMenuItem("Example 1 (ESWA- Under revision)");
		menuItemExampleJobsMerge = new JMenuItem("Example Jobs (ESWA- Under revision)");

        menuOpen.add(menuRuningExample);
        menuOpen.add(menuItemLoadCredibilityBase);
        menuRuningExample.add(menuItemExample1);
        menuRuningExample.add(menuItemExample2);
        menuRuningExample.add(menuItemExample7);

		menuRuningExample.add(menuItemExample1Merge);
		menuRuningExample.add(menuItemExampleJobsMerge);
                
        menuCredibilityOrder.add(menuOpen);
        menuCredibilityOrder.add(new JSeparator());
        menuCredibilityOrder.add(menuItemDefaultBase);
        menuCredibilityOrder.add(menuItemAddOrder);
		// Modified code merge ---------------------------------
        menuCredibilityOrder.add(menuAddResultingOrder);
		// ---------------------------------------------------
        menuCredibilityOrder.add(menuItemExport);
        menuCredibilityOrder.add(new JSeparator());
        menuCredibilityOrder.add(menuItemExit);

		// Modified code merge ---------------------------------
		menuItemAddResultingOrderTSF = new JMenuItem("Add TSF resulting credibility order.");
        menuItemAddResultingOrderTSF.setToolTipText("Add to the credibility base the actual TSF resulting credibility order.");
        menuItemAddResultingOrderLSF = new JMenuItem("Add LSF resulting credibility order.");
        menuItemAddResultingOrderLSF.setToolTipText("Add to the credibility base the actual LSF resulting credibility order.");
        menuItemAddResultingOrderLCSFGLCSF = new JMenuItem("Add LCSF/GLCSF resulting credibility order.");
        menuItemAddResultingOrderLCSFGLCSF.setToolTipText("Add to the credibility base the actual LCSF/GLCSF resulting credibility order.");
        
        menuAddResultingOrder.add(menuItemAddResultingOrderTSF);
        menuAddResultingOrder.add(menuItemAddResultingOrderLSF);
        menuAddResultingOrder.add(menuItemAddResultingOrderLCSFGLCSF);
		// ---------------------------------------------------

        // Operators Menu Bar
        menuItemRevisionOperator = new JMenuItem("Revision ("+ aLabel + " " + revisionLabel + " " + bLabel + ")");
		menuItemMergeOperator = new JMenuItem("Merge ("+ aLabel + " " + mergeLabel + " " + bLabel + ")");
        
        menuOperators.add(menuItemRevisionOperator);
		menuOperators.add(menuItemMergeOperator);
        
        // Help Menu Bar
        menuItemUserManual = new JMenuItem("User Manual");
        menuItemAboutVersion = new JMenuItem("About this version");
        menuItemPoweredBy = new JMenuItem("Powered by");
        
        menuHelp.add(menuItemUserManual);
        menuHelp.add(menuItemAboutVersion);
        menuHelp.add(new JSeparator());
        menuHelp.add(menuItemPoweredBy);
        
        // Disabled options in this version
        menuItemUserManual.setEnabled(false);
        menuItemAboutVersion.setEnabled(false);
	}
	
	protected void initInformationPanel() {
		// Left and right labels to print information
		lblInformationLeft = new JLabel("");
        bottomMainPanel.add(lblInformationLeft, BorderLayout.WEST);
        lblInformationRight = new JLabel("");
        lblInformationRight.setHorizontalAlignment(SwingConstants.RIGHT);
        bottomMainPanel.add(lblInformationRight, BorderLayout.EAST);
	}
	
	protected void initLeftMainPanel() {
		JLabel label;
		
		// Left Main Tabbed Panel
		leftMainTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		leftMainTabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		leftMainTabbedPane.setPreferredSize(leftMainPanelDimension);
		
		// Credibility Orders A and B Tab Panel
		ordersABTabPanel = new JPanel();
		leftMainTabbedPane.addTab("Credibility orders", null, ordersABTabPanel, "Credibility Orders " + aLabel + " and " + bLabel + " visualizer");
		
		// Order A and B Tabbed panel
		orderATabbedPane = new JTabbedPane(JTabbedPane.TOP);
		orderBTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		orderATabbedPane.setPreferredSize(ordersABTabPanelDimension);
		orderBTabbedPane.setPreferredSize(ordersABTabPanelDimension);
				
		// Order A Panel
		orderAPanel = new JPanel();
		orderASelectInputPanel = new JPanel(); 
		label = new JLabel("Viewing credibility order:");
		comboSelectOrderA = new JComboBox<String>();
		comboSelectOrderA.setModel(new javax.swing.DefaultComboBoxModel<>(new String [] {"-"}));
		comboSelectOperationA  = new JComboBox<String>();
		comboSelectOperationA.setModel(new javax.swing.DefaultComboBoxModel<>(new String [] {"-", "Add A>B", "Delete A>B"}));
		orderASelectInputPanel.add(label);
		orderASelectInputPanel.add(comboSelectOrderA);
		orderASelectInputPanel.add(new JLabel("Operations:"));
		orderASelectInputPanel.add(comboSelectOperationA);
		orderAGraphPanel = makeEmptyPanel(waitingInitializationLabel);
		orderAGraphPanel.setPreferredSize(ordersABGraphsDimension);
		orderAPanel.add(orderASelectInputPanel);
		orderAPanel.add(orderAGraphPanel);
		orderATabbedPane.addTab("Order " + aLabel, null, orderAPanel, "Credibility Order " + aLabel);
        
		// Order B Panel
        orderBPanel = new JPanel(); 
        orderBSelectInputPanel = new JPanel(); 
        label = new JLabel("Viewing credibility order:");
        comboSelectOrderB = new JComboBox<String>();
        comboSelectOrderB.setModel(new javax.swing.DefaultComboBoxModel<>(new String [] {"-"}));
        comboSelectOperationB = new JComboBox<String>();
		comboSelectOperationB.setModel(new javax.swing.DefaultComboBoxModel<>(new String [] {"-", "Add A>B", "Delete A>B"}));
		orderBSelectInputPanel.add(label);
		orderBSelectInputPanel.add(comboSelectOrderB);
		orderBSelectInputPanel.add(new JLabel("Operations:"));
		orderBSelectInputPanel.add(comboSelectOperationB);
		orderBGraphPanel = makeEmptyPanel(waitingInitializationLabel);
		orderBGraphPanel.setPreferredSize(ordersABGraphsDimension);
		orderBPanel.add(orderBSelectInputPanel);
		orderBPanel.add(orderBGraphPanel);
		orderBTabbedPane.addTab("Order " + bLabel, null, orderBPanel, "Credibility Order " + bLabel);
        
        leftMainPanel.add(leftMainTabbedPane);
        ordersABTabPanel.add(orderATabbedPane);
        ordersABTabPanel.add(orderBTabbedPane);
	}
	
	protected void initRightMainPanel() {
		// Union of Credibility Orders Tabbed Panel 
		topRightMainTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		topRightMainTabbedPane.setPreferredSize(topRightTabbedPanelDimension);
		
        // Top Right Main Tab Panel 
		topRightMainTabPanel = new JPanel();
        topRightMainTabbedPane.addTab("Union of "+ aLabel + " and " + bLabel + " credibility orders", null, topRightMainTabPanel, "Union of "+ aLabel + " and " + bLabel + " credibility orders");
        
        // Union of credibility orders Tabbed Panel
        unionTabbedPanel = new JTabbedPane(JTabbedPane.TOP);
        unionTabbedPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        unionTabbedPanel.setPreferredSize(unionTabbedPanelDimension);
        
        // Union Tab Panel
        unionPanel = makeEmptyPanel(waitingOperationLabel);
        unionTabbedPanel.addTab("Graph representation", null, unionPanel, "Viewing the union of " + aLabel + " and " + bLabel + " credibility orders");

        // Kernels Tabbed Panel.        
        kernelTabbedPanel = new JTabbedPane(JTabbedPane.TOP);
        kernelTabbedPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        kernelTabbedPanel.setPreferredSize(kernelTabbedPanelDimension);
		
        // Kernels Tab Panel
        kernelPanel = makeEmptyPanel(waitingOperationLabel);
        // Modified code merge ---------------------------------
        kernelTabbedPanel.addTab("...", null, kernelPanel, "Defined when an operation is executed");
 		// ---------------------------------------------------        
 		
        topRightMainTabPanel.add(unionTabbedPanel);
        topRightMainTabPanel.add(kernelTabbedPanel);
        
        // Revision Results Tabbed Panel
 		bottomRightMainTabbedPane = new JTabbedPane(JTabbedPane.TOP);
 		bottomRightMainTabbedPane.setPreferredSize(revisionResultsTabbedPanelDimension);
 		
        // Revision Results Tab Panel
        resultsByTSFPane = makeEmptyPanel(waitingOperationLabel);
        resultsByLSFPane = makeEmptyPanel(waitingOperationLabel);
        resultsBy_LCSF_GLCSF_Pane = makeEmptyPanel(waitingOperationLabel);
        
        bottomRightMainTabbedPane.addTab("... TSF", null, resultsByTSFPane, "Operation applying Total Selection Function");
        bottomRightMainTabbedPane.addTab("... LSF", null, resultsByLSFPane, "Operation applying Lexicographic Selection Function");
		bottomRightMainTabbedPane.addTab("...", null, resultsBy_LCSF_GLCSF_Pane, "Operation applying a Selection Function to be determinate");

        rightMainPanel.add(topRightMainTabbedPane);
        rightMainPanel.add(bottomRightMainTabbedPane);
        
	}
	
	protected void initListeners() {
		
		menuItemDefaultBase.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				appController.newCredibilityBase();
			}
		});
		
		menuItemAddOrder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int contextNumber;
				String text = JOptionPane.showInputDialog("Type the number (<="+AppController.MAX_CONTEXT_ID+") of the credibility order to be added");
				
				if (text != null && text.length() > 0) {
					try{
						contextNumber = Integer.parseInt(text);
						if (contextNumber < AppController.MAX_CONTEXT_ID) {
							appController.addCredibilityOrder(contextNumber);
						}else {
							JOptionPane.showMessageDialog(null, "The number is not correct", "Failed Request", JOptionPane.ERROR_MESSAGE);
						}
					}catch(NumberFormatException ex){
						JOptionPane.showMessageDialog(null, "The number is not correct", "Failed Request", JOptionPane.ERROR_MESSAGE);
					}
				}
				
			}
		});
		
		// Modified code merge ---------------------------------
		menuItemAddResultingOrderTSF.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int contextNumber;
				String text = JOptionPane.showInputDialog("Type the number (<="+AppController.MAX_CONTEXT_ID+") of the credibility order to be added");
				if (text != null && text.length() > 0) {
					try{
						contextNumber = Integer.parseInt(text);
						if (contextNumber < AppController.MAX_CONTEXT_ID) {
							appController.addCredibilityOrder(contextNumber);
							for(DefaultEdge de : getUnionTSF().edgeSet()) {
								String edge = de.toString();
								String[] parts = edge.substring(1, edge.length() - 1).split(" : ");
								String source = parts[0].trim(); // value1
								String target = parts[1].trim(); // value2
								appController.addCredibilityElement(Integer.parseInt(target), Integer.parseInt(source), contextNumber, 0);
					        }
						}else {
							JOptionPane.showMessageDialog(null, "The number is not correct", "Failed Request", JOptionPane.ERROR_MESSAGE);
						}
					}catch(NumberFormatException ex){
						JOptionPane.showMessageDialog(null, "The number is not correct", "Failed Request", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		
		menuItemAddResultingOrderLSF.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int contextNumber;
				String text = JOptionPane.showInputDialog("Type the number (<="+AppController.MAX_CONTEXT_ID+") of the credibility order to be added");
				if (text != null && text.length() > 0) {
					try{
						contextNumber = Integer.parseInt(text);
						if (contextNumber < AppController.MAX_CONTEXT_ID) {
							appController.addCredibilityOrder(contextNumber);
							for(DefaultEdge de : getUnionLSF().edgeSet()) {
								String edge = de.toString();
								String[] parts = edge.substring(1, edge.length() - 1).split(" : ");
								String source = parts[0].trim(); // value1
								String target = parts[1].trim(); // value2
								appController.addCredibilityElement(Integer.parseInt(target), Integer.parseInt(source), contextNumber, 0);
					        }
						}else {
							JOptionPane.showMessageDialog(null, "The number is not correct", "Failed Request", JOptionPane.ERROR_MESSAGE);
						}
					}catch(NumberFormatException ex){
						JOptionPane.showMessageDialog(null, "The number is not correct", "Failed Request", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		
		menuItemAddResultingOrderLCSFGLCSF.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int contextNumber;
				String text = JOptionPane.showInputDialog("Type the number (<="+AppController.MAX_CONTEXT_ID+") of the credibility order to be added");
				if (text != null && text.length() > 0) {
					try{
						contextNumber = Integer.parseInt(text);
						if (contextNumber < AppController.MAX_CONTEXT_ID) {
							appController.addCredibilityOrder(contextNumber);
							for(DefaultEdge de : getUnionLCSF_GLCSF().edgeSet()) {
								String edge = de.toString();
								String[] parts = edge.substring(1, edge.length() - 1).split(" : ");
								String source = parts[0].trim(); // value1
								String target = parts[1].trim(); // value2
								appController.addCredibilityElement(Integer.parseInt(target), Integer.parseInt(source), contextNumber, 0);
					        }
						}else {
							JOptionPane.showMessageDialog(null, "The number is not correct", "Failed Request", JOptionPane.ERROR_MESSAGE);
						}
					}catch(NumberFormatException ex){
						JOptionPane.showMessageDialog(null, "The number is not correct", "Failed Request", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		// ---------------------------------------------------
		
		menuItemExample1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				appController.newCredibilityBase(AppController.RUNNING_EXAMPLE_1);
			}
		});
		
		menuItemExample2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				appController.newCredibilityBase(AppController.RUNNING_EXAMPLE_2);
			}
		});
		
		menuItemExample7.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				appController.newCredibilityBase(AppController.RUNNING_EXAMPLE_7);
			}
		});

		menuItemExample1Merge.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				appController.newCredibilityBase(AppController.RUNNING_EXAMPLE_1_MERGE);
			}
		});

		menuItemExampleJobsMerge.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				appController.newCredibilityBase(AppController.RUNNING_EXAMPLE_JOBS_MERGE);
			}
		});

		menuItemLoadCredibilityBase.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String path = FileSelector.showFileChooser();
				if(path != null)
					appController.readFromFileNewCredibilityBase(path, null);
				else
					JOptionPane.showMessageDialog(null, "Must select a file that contains a credibility base. ", "Failed Request", JOptionPane.ERROR_MESSAGE);
			}
		});

		menuItemExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog("Save file with the name:", "");
				String savePath = FileSelector.showFolderChooser();
				if(savePath != null){
					appController.saveCredibilityBaseDataIntoFiles(savePath, name);
				}else
					JOptionPane.showMessageDialog(null, "Must select a folder to save " + aLabel + " and " + bLabel + " credibility orders. ", "Failed Request", JOptionPane.ERROR_MESSAGE);
			}
		});

		menuItemExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);				
			}
		});
		
		menuItemRevisionOperator.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int contextA, contextB;
				if (comboSelectOrderA.getItemCount() > 0) {
					contextA = Integer.parseInt(comboSelectOrderA.getItemAt(comboSelectOrderA.getSelectedIndex()));
					contextB = Integer.parseInt(comboSelectOrderB.getItemAt(comboSelectOrderB.getSelectedIndex()));
					if (contextA != contextB) {
						appController.applySelectionFunctions(contextA, contextB, true);
					}else {
						JOptionPane.showMessageDialog(null, "The selected " + aLabel + " and " + bLabel + " credibility orders must be different. ", "Failed Request", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		menuItemMergeOperator.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int contextA, contextB;
				if (comboSelectOrderA.getItemCount() > 0) {
					contextA = Integer.parseInt(comboSelectOrderA.getItemAt(comboSelectOrderA.getSelectedIndex()));
					contextB = Integer.parseInt(comboSelectOrderB.getItemAt(comboSelectOrderB.getSelectedIndex()));
					if (contextA != contextB) {
						appController.applySelectionFunctions(contextA, contextB, false);
					}else {
						JOptionPane.showMessageDialog(null, "The selected " + aLabel + " and " + bLabel + " credibility orders must be different. ", "Failed Request", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		
		menuItemPoweredBy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new PoweredByDialog(Frame.getFrames()[0]);
			}
		});
		
		comboSelectOrderA.addItemListener(new ItemListener() {
		    @SuppressWarnings("static-access")
			public void itemStateChanged(ItemEvent evt) {
		        int newContext;
		    	if (evt.getStateChange() == ItemEvent.SELECTED) {
		        	newContext = Integer.parseInt(evt.getItem().toString());
		        	appController.changeActiveContextInOrderVisualizer(appController.CREDIBILITY_ORDER_A, newContext);
		        }
		    }
		});
		
		comboSelectOrderB.addItemListener(new ItemListener() {
		    @SuppressWarnings("static-access")
			public void itemStateChanged(ItemEvent evt) {
		        int newContext;
		    	if (evt.getStateChange() == ItemEvent.SELECTED) {
		        	newContext = Integer.parseInt(evt.getItem().toString());
		        	appController.changeActiveContextInOrderVisualizer(appController.CREDIBILITY_ORDER_B, newContext);
		        }
		    }
		});
		
		comboSelectOperationA.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
		    	if (evt.getStateChange() == ItemEvent.SELECTED && comboSelectOperationA.getSelectedIndex() != 0) {
		    		String text = comboSelectOrderA.getSelectedItem().toString();
		    		applyOperation(comboSelectOperationA.getSelectedIndex(), Integer.parseInt(text),AppController.CREDIBILITY_ORDER_A);
		    		comboSelectOperationA.setSelectedIndex(0);
		        }
		    }
		});
		
		comboSelectOperationB.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
		    	if (evt.getStateChange() == ItemEvent.SELECTED && comboSelectOperationB.getSelectedIndex() != 0) {
		    		String text = comboSelectOrderB.getSelectedItem().toString();
		    		applyOperation(comboSelectOperationB.getSelectedIndex(), Integer.parseInt(text), AppController.CREDIBILITY_ORDER_B);
		    		comboSelectOperationB.setSelectedIndex(0);
		        }
		    }
		});
	}
	
	protected void applyOperation(int indexComboOperation, int context, int operationInOrder) {
		Pair<Integer, Integer> p = requestCredibilityElementAgents();
		if (p != null) {
			switch(indexComboOperation) {
		    	case 1:{
		    		appController.addCredibilityElement(p.getFirst(), p.getSecond(), context, operationInOrder);
		    		break;
		    	}
		    	case 2:{
		    		appController.removeCredibilityElement(p.getFirst(), p.getSecond(), context, operationInOrder);
		    		break;
		    	}
			}
		}
		
	} 
	
	protected Pair<Integer, Integer> requestCredibilityElementAgents(){
		Pair<Integer, Integer> toReturn;
		String text;
		String [] parts;
		int agentA, agentB;
		
		toReturn = null;
		text = JOptionPane.showInputDialog("Type the credibility element A>B (agent identifier must be <="+AppController.MAX_AGENT_ID+")");
		
		if (text != null) {
			parts = text.split(">");
		
			try{
				agentA = Integer.parseInt(parts[0]);
				agentB = Integer.parseInt(parts[1]);
				if (agentA <= AppController.MAX_AGENT_ID && agentB <= AppController.MAX_AGENT_ID) {
					toReturn = new Pair<Integer, Integer>(agentA, agentB);
				}else {
					JOptionPane.showMessageDialog(null, "The credibility element entered is not correct", "Failed Request", JOptionPane.ERROR_MESSAGE);
				}
				
			}catch(NumberFormatException | ArrayIndexOutOfBoundsException ex){
				JOptionPane.showMessageDialog(null, "The credibility element entered is not correct", "Failed Request", JOptionPane.ERROR_MESSAGE);
			}
			
		}
		
		return toReturn;
	}
	
	protected JComponent makeEmptyPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }
	
	protected void disableEditToolGraph(JGraphXAdapter<Integer, DefaultEdge> adapter, mxGraphComponent graphComponent) {
		adapter.setConnectableEdges(false);
		adapter.setAllowDanglingEdges(false);
		adapter.setCellsBendable(false);
		adapter.setCellsCloneable(false);
		adapter.setCellsDeletable(false);
		adapter.setCellsDisconnectable(false);
		adapter.setCellsEditable(false);
		adapter.setCellsLocked(true);
		adapter.setCellsMovable(false);
		adapter.setCellsResizable(false);
		adapter.setCellsSelectable(true);
		adapter.setDisconnectOnMove(false);
		adapter.setConnectableEdges(false);
		adapter.setDropEnabled(false);
		adapter.setDisconnectOnMove(false);
		graphComponent.setDragEnabled(false);
        graphComponent.setConnectable(false);
	}
	
	protected void stylizeGraphComponent(int element) {
		JGraphXAdapter<Integer, DefaultEdge> adapter;
		mxGraphComponent graphComponent;
		Map<String, Object> vertexStyle, edgeStyle;
		String edgeColor, vertexShape = mxConstants.SHAPE_RECTANGLE;
		
		switch(element) {
			case AppController.CREDIBILITY_ORDER_A:{
				adapter = adapterOrderA;
				graphComponent = graphComponentOrderA;
				edgeColor = colorOrderA;
				break;
			}
			
			case AppController.CREDIBILITY_ORDER_B:{
				adapter = adapterOrderB;
				graphComponent = graphComponentOrderB;
				edgeColor = colorOrderB;
				break;
			}
			
			case AppController.CREDIBILITY_ORDER_REVISION:{
				adapter = adapterUnion;
				graphComponent = graphComponentRevision;
				edgeColor = colorOrderB;
				break;
			}
			
			case AppController.CREDIBILITY_ORDER_REVISION_TSF:{
				adapter = adapterRevisionTSF;
				graphComponent = graphComponentRevisionTSF;
				edgeColor = colorOrderB;
				break;
			}
			
			case AppController.CREDIBILITY_ORDER_REVISION_LSF:{
				adapter = adapterRevisionLSF;
				graphComponent = graphComponentRevisionLSF;
				edgeColor = colorOrderB;
				break;
			}
			
			case AppController.CREDIBILITY_ORDER_REVISION_LCSF_GLCSF:{
				adapter = adapterRevisionLCSF_GLCSF;
				graphComponent = graphComponentRevisionLCSF_GLCSF;
				edgeColor = colorOrderB;
				break;
			}
			
			default: { return; }
		}
		
		vertexStyle = adapter.getStylesheet().getDefaultVertexStyle();
		edgeStyle = adapter.getStylesheet().getDefaultEdgeStyle();
		
		vertexStyle.put(mxConstants.STYLE_SHAPE, vertexShape);
		
		edgeStyle.put(mxConstants.STYLE_FONTSIZE, 0);
		edgeStyle.put(mxConstants.STYLE_STROKECOLOR, edgeColor);
		
		graphComponent.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
	}
		
	public void setLeftInformation(String info, int format) {
		String prefix = "";
		
		switch(format) {
			case TEXT_ERROR:{
				prefix = "<font color='red'> ERROR: </font>"; 
				break;
			}
			case TEXT_SUCCESS:{
				prefix = "<font color='green'> OK: </font>";
				break;
			}
		}
		lblInformationLeft.setText("<html>" + prefix + info + "</html>");
	}
	
	public void setRightInformation(String info) {
		lblInformationRight.setText(info);
	}
	
	public void setEnabledMenuOption(int element, boolean enabled) {
		switch (element){
			case AppController.REVISION_OPERATOR:{
				menuItemRevisionOperator.setEnabled(enabled);
				menuItemMergeOperator.setEnabled(enabled);
				break;
			}
			
			case AppController.CREDIBILITY_ORDER_OPERATIONS:{
				comboSelectOperationA.setEnabled(enabled);
				comboSelectOperationB.setEnabled(enabled);
				break;
			}
		}
	}
		
	public void resetRevisionPanels() {        
		unionPanel.removeAll();
		kernelPanel.removeAll();
		resultsByTSFPane.removeAll();
		resultsByLSFPane.removeAll();
        resultsBy_LCSF_GLCSF_Pane.removeAll();
        		
		unionPanel.add(makeEmptyPanel(waitingOperationLabel));
		kernelPanel.add(makeEmptyPanel(waitingOperationLabel));
        resultsByTSFPane.add(makeEmptyPanel(waitingOperationLabel));        
        resultsByLSFPane.add(makeEmptyPanel(waitingOperationLabel));
        resultsBy_LCSF_GLCSF_Pane.add(makeEmptyPanel(waitingOperationLabel));
        
        bottomRightMainTabbedPane.updateUI();
	}
	
	public void drawGraphInVisualizer(int element, Graph<Integer, DefaultEdge> g) {
		ListenableGraph<Integer, DefaultEdge> graph = new DefaultListenableGraph<>(g);
		
		switch(element) {
		
			case AppController.CREDIBILITY_ORDER_A:{
				adapterOrderA = new JGraphXAdapter<Integer, DefaultEdge>(graph);
				graphComponentOrderA = new mxGraphComponent(adapterOrderA);
				disableEditToolGraph(adapterOrderA, graphComponentOrderA);
				stylizeGraphComponent(AppController.CREDIBILITY_ORDER_A);
				orderAGraphPanel.removeAll();
				orderAGraphPanel.add(graphComponentOrderA);
		        layoutOrderA = new mxHierarchicalLayout(adapterOrderA, SwingConstants.WEST);
		        layoutOrderA.setInterHierarchySpacing(15);
		        layoutOrderA.execute(adapterOrderA.getDefaultParent());
		        orderAGraphPanel.updateUI();
				break;
			}
			
			case AppController.CREDIBILITY_ORDER_B:{
				adapterOrderB = new JGraphXAdapter<Integer, DefaultEdge>(graph);
				graphComponentOrderB = new mxGraphComponent(adapterOrderB);
				disableEditToolGraph(adapterOrderB, graphComponentOrderB);
				stylizeGraphComponent(AppController.CREDIBILITY_ORDER_B);
				orderBGraphPanel.removeAll();
				orderBGraphPanel.add(graphComponentOrderB);	
		        layoutOrderB = new mxHierarchicalLayout(adapterOrderB, SwingConstants.WEST);
		        layoutOrderB.setInterHierarchySpacing(15);
		        layoutOrderB.execute(adapterOrderB.getDefaultParent());
		        orderBGraphPanel.updateUI();
				break;
			}
			
			default:{ return; }
		
		}
	}
	
	@SuppressWarnings("unchecked")
	public void drawSimpleRevision(
			Graph<Integer, DefaultEdge> gA,
			Graph<Integer, DefaultEdge> gB,
			List<List<CredibilityElement<Integer>>> kernels,
			List<CredibilityElement<Integer>> TSF,
			List<CredibilityElement<Integer>> LSF,
			List<CredibilityElement<Integer>> LCSF_GLCSF
			) {

		// Modified code merge ---------------------------------
		if(appController.isRevision()){
			bottomRightMainTabbedPane.setTitleAt(0, "Revision by TSF");
			bottomRightMainTabbedPane.setTitleAt(1, "Revision by LSF");

			bottomRightMainTabbedPane.setTitleAt(2, "Revision by LCSF");
			bottomRightMainTabbedPane.setToolTipTextAt(2, "Operation applying Least Credible Selection Function");
			
			kernelTabbedPanel.setTitleAt(0, bLabel+"-inconsistent kernels of "+aLabel);
 			kernelTabbedPanel.setToolTipText("Viewing the kernel set");
		}else{
			bottomRightMainTabbedPane.setTitleAt(0, "Merging by TSF");
			bottomRightMainTabbedPane.setTitleAt(1, "Merging by LSF");

			bottomRightMainTabbedPane.setTitleAt(2, "Merging by GLCSF");
			bottomRightMainTabbedPane.setToolTipTextAt(2, "Operation applying Generalized Least Credible Selection Function");
			
			kernelTabbedPanel.setTitleAt(0, "Conflict sets");
 			kernelTabbedPanel.setToolTipText("Viewing the conflict set");
		}
		// ---------------------------------------------------

		ListenableGraph<Integer, DefaultEdge> graph, graph_tsf, graph_lsf, graph_lcsf_glcsf;
		HashMap<DefaultEdge, mxICell> map_edge_to_cell_merge, map_edge_to_cell_tsf, map_edge_to_cell_lsf, map_edge_to_cell_lcsf_glcsf;
		Set<DefaultEdge> edgesGA;
		Iterator<DefaultEdge> iteratorEdgesGA;
		DefaultEdge actualEdge;
		ArrayList<mxICell> cellListMerge, cellListTSF, cellListLSF, cellListLCSF_GLCSF;
		mxICell actualCellMerge;
		JTextArea kernelsTextArea;
		String text;
		int index;

		// Compute merged order: order_2 + order_1
		// The order of the union is very important: all edges of gB prevail over the repeated edges of gA
		union = new SimpleDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);
        Graphs.addGraph(union, gB);
        Graphs.addGraph(union, gA);

        // Clone merged order
        union_tsf = (SimpleDirectedGraph<Integer, DefaultEdge>) union.clone();
        union_lsf = (SimpleDirectedGraph<Integer, DefaultEdge>) union.clone();
        union_lcsf_glcsf = (SimpleDirectedGraph<Integer, DefaultEdge>) union.clone();

        // Remove the selected credibility elements of each selection function
        for(CredibilityElement<Integer> ce : TSF) {
        	union_tsf.removeEdge(ce.getLessCredible(), ce.getMostCredible());
        	
        	if (union_lcsf_glcsf.containsVertex(ce.getLessCredible()) && union_tsf.edgesOf(ce.getLessCredible()).isEmpty())
        		union_tsf.removeVertex(ce.getLessCredible());

        	if (union_lcsf_glcsf.containsVertex(ce.getMostCredible()) && union_tsf.edgesOf(ce.getMostCredible()).isEmpty())
        		union_tsf.removeVertex(ce.getMostCredible());
        }

        for(CredibilityElement<Integer> ce : LSF) {
 			union_lsf.removeEdge(ce.getLessCredible(), ce.getMostCredible());

 			if (union_lcsf_glcsf.containsVertex(ce.getLessCredible()) && union_lsf.edgesOf(ce.getLessCredible()).isEmpty())
 				union_lsf.removeVertex(ce.getLessCredible());

        	if (union_lcsf_glcsf.containsVertex(ce.getMostCredible()) && union_lsf.edgesOf(ce.getMostCredible()).isEmpty())
        		union_lsf.removeVertex(ce.getMostCredible());

 		}

        for(CredibilityElement<Integer> ce : LCSF_GLCSF) {
 			union_lcsf_glcsf.removeEdge(ce.getLessCredible(), ce.getMostCredible());

 			if (union_lcsf_glcsf.containsVertex(ce.getLessCredible()) && union_lcsf_glcsf.edgesOf(ce.getLessCredible()).isEmpty())
 				union_lcsf_glcsf.removeVertex(ce.getLessCredible());

        	if (union_lcsf_glcsf.containsVertex(ce.getMostCredible()) && union_lcsf_glcsf.edgesOf(ce.getMostCredible()).isEmpty())
        		union_lcsf_glcsf.removeVertex(ce.getMostCredible());
 		}

		graph = new DefaultListenableGraph<Integer, DefaultEdge>(union);
		graph_tsf = new DefaultListenableGraph<Integer, DefaultEdge>(union_tsf);
		graph_lsf = new DefaultListenableGraph<Integer, DefaultEdge>(union_lsf);
		graph_lcsf_glcsf = new DefaultListenableGraph<Integer, DefaultEdge>(union_lcsf_glcsf);
		
		// Color all edges with GB edges color
		adapterUnion = new JGraphXAdapter<Integer, DefaultEdge>(graph);
		adapterRevisionTSF = new JGraphXAdapter<Integer, DefaultEdge>(graph_tsf);
		adapterRevisionLSF = new JGraphXAdapter<Integer, DefaultEdge>(graph_lsf);
		adapterRevisionLCSF_GLCSF = new JGraphXAdapter<Integer, DefaultEdge>(graph_lcsf_glcsf);

		graphComponentRevision = new mxGraphComponent(adapterUnion);
		graphComponentRevisionTSF = new mxGraphComponent(adapterRevisionTSF);
		graphComponentRevisionLSF = new mxGraphComponent(adapterRevisionLSF);
		graphComponentRevisionLCSF_GLCSF = new mxGraphComponent(adapterRevisionLCSF_GLCSF);

		disableEditToolGraph(adapterUnion, graphComponentRevision);
		disableEditToolGraph(adapterRevisionTSF, graphComponentRevisionTSF);
		disableEditToolGraph(adapterRevisionLSF, graphComponentRevisionLSF);
		disableEditToolGraph(adapterRevisionLCSF_GLCSF, graphComponentRevisionLCSF_GLCSF);

		stylizeGraphComponent(AppController.CREDIBILITY_ORDER_REVISION);
		stylizeGraphComponent(AppController.CREDIBILITY_ORDER_REVISION_TSF);
		stylizeGraphComponent(AppController.CREDIBILITY_ORDER_REVISION_LSF);
		stylizeGraphComponent(AppController.CREDIBILITY_ORDER_REVISION_LCSF_GLCSF);

		// Gets GA edge set and edge cell map, to color GA edges with GA edge color.
		map_edge_to_cell_merge = adapterUnion.getEdgeToCellMap();
		map_edge_to_cell_tsf = adapterRevisionTSF.getEdgeToCellMap();
		map_edge_to_cell_lsf = adapterRevisionLSF.getEdgeToCellMap();
		map_edge_to_cell_lcsf_glcsf = adapterRevisionLCSF_GLCSF.getEdgeToCellMap();

		edgesGA = gA.edgeSet();
		iteratorEdgesGA = edgesGA.iterator();
		cellListMerge = new ArrayList<mxICell>();
		cellListTSF = new ArrayList<mxICell>();
		cellListLSF = new ArrayList<mxICell>();
		cellListLCSF_GLCSF = new ArrayList<mxICell>();

		// cellListX contains all edges of GA that do not belong to GB.
		while(iteratorEdgesGA.hasNext()) {
			actualEdge = iteratorEdgesGA.next();
			actualCellMerge = map_edge_to_cell_merge.get(actualEdge);
			while(actualCellMerge == null && iteratorEdgesGA.hasNext()) {
				actualEdge = iteratorEdgesGA.next();
				actualCellMerge = map_edge_to_cell_merge.get(actualEdge);
			}

			if (actualCellMerge != null)
				cellListMerge.add(actualCellMerge);

			if ((actualCellMerge = map_edge_to_cell_tsf.get(actualEdge)) != null )
				cellListTSF.add(actualCellMerge);

			if ((actualCellMerge = map_edge_to_cell_lsf.get(actualEdge)) != null )
				cellListLSF.add(actualCellMerge);

			if ((actualCellMerge = map_edge_to_cell_lcsf_glcsf.get(actualEdge)) != null )
				cellListLCSF_GLCSF.add(actualCellMerge);

		}

		// Put the GB edge style to the cell that only belong to GB.
		adapterUnion.setCellStyle("defaultEdge;fontSize=0;strokeColor="+colorOrderA, cellListMerge.toArray());
		adapterRevisionTSF.setCellStyle("defaultEdge;fontSize=0;strokeColor="+colorOrderA, cellListTSF.toArray());
		adapterRevisionLSF.setCellStyle("defaultEdge;fontSize=0;strokeColor="+colorOrderA, cellListLSF.toArray());
		adapterRevisionLCSF_GLCSF.setCellStyle("defaultEdge;fontSize=0;strokeColor="+colorOrderA, cellListLCSF_GLCSF.toArray());

		// cellList contains all edges of gA that belong to a kernel.
		// print the kernels set in the kernelSetPane
		// remove from graph_tsf all the credibility elements of kernels

		kernelsTextArea = new JTextArea();
		new JScrollPane(kernelsTextArea);

		kernelsTextArea.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
		kernelsTextArea.setOpaque(false);
		kernelsTextArea.setEditable(false);

		index = 0;
		// Modified code merge ---------------------------------
		if(appController.isRevision()){
			text = new String("Kernels Set (K = { Ki | Ki is a Kernel}) \n \n");
		}else{
			text = new String("");
		}
		// ---------------------------------------------------
		cellListMerge = new ArrayList<mxICell>();

		for(List<CredibilityElement<Integer>> kernel : kernels) {
			// Texto for Ki kernel
			text += "K"+ index++ +" = { ";

			for (CredibilityElement<Integer> ce : kernel) {

				graph_tsf.removeEdge(ce.getLessCredible(), ce.getMostCredible());

				actualCellMerge = map_edge_to_cell_merge.get( gA.getEdge(ce.getLessCredible(), ce.getMostCredible()));

				// Modified code merge ---------------------------------
				if(!appController.isRevision() && actualCellMerge == null)
					actualCellMerge = map_edge_to_cell_merge.get( gB.getEdge(ce.getLessCredible(), ce.getMostCredible()));
				// ---------------------------------------------------

				cellListMerge.add(actualCellMerge);

				text += " " + ce.toString() + ",";
			}
			text += " } \n";
		}

		// Modified code merge ---------------------------------
		if (kernels.isEmpty()) {
			if(appController.isRevision()){
				text += "The kernel set is empty.";
			}else {
				text += "The set is empty.";
			}
		}
		// ---------------------------------------------------

		kernelsTextArea.setText(text);
		kernelPanel.removeAll();
		kernelPanel.add(kernelsTextArea);

		// Put the kernel edge style to the cell that belong to gB and kernel.
		adapterUnion.setCellStyle("defaultEdge;fontSize=0;strokeWidth=1.5;strokeColor="+colorKernel+";dashed=true", cellListMerge.toArray());

		// Draw the resulting graphs on the corresponding unionPanel
		unionPanel.removeAll();
		unionPanel.add(graphComponentRevision);
        layoutRevision = new mxHierarchicalLayout(adapterUnion, SwingConstants.WEST);
        layoutRevision.setInterHierarchySpacing(15);
        layoutRevision.execute(adapterUnion.getDefaultParent());

        resultsByTSFPane.removeAll();
        resultsByTSFPane.add(graphComponentRevisionTSF);
        layoutRevisionTSF = new mxHierarchicalLayout(adapterRevisionTSF, SwingConstants.WEST);
        layoutRevisionTSF.setInterHierarchySpacing(15);
        layoutRevisionTSF.execute(adapterRevisionTSF.getDefaultParent());

        resultsByLSFPane.removeAll();
        resultsByLSFPane.add(graphComponentRevisionLSF);
        layoutRevisionRSF = new mxHierarchicalLayout(adapterRevisionLSF, SwingConstants.WEST);
        layoutRevisionRSF.setInterHierarchySpacing(15);
        layoutRevisionRSF.execute(adapterRevisionLSF.getDefaultParent());

        resultsBy_LCSF_GLCSF_Pane.removeAll();
        resultsBy_LCSF_GLCSF_Pane.add(graphComponentRevisionLCSF_GLCSF);
        layoutRevisionLCSF_GLCSF = new mxHierarchicalLayout(adapterRevisionLCSF_GLCSF, SwingConstants.WEST);
        layoutRevisionLCSF_GLCSF.setInterHierarchySpacing(15);
        layoutRevisionLCSF_GLCSF.execute(adapterRevisionLCSF_GLCSF.getDefaultParent());

        bottomRightMainTabbedPane.updateUI();

	}

	public void setContextsList(String [] listItems) {
		comboSelectOrderA.removeAllItems();
		comboSelectOrderB.removeAllItems();
		for (String s : listItems) {
			comboSelectOrderA.addItem(s);
			comboSelectOrderB.addItem(s);
		}
		if (comboSelectOrderB.getItemCount() > 0)
			comboSelectOrderB.setSelectedIndex( (comboSelectOrderB.getItemCount() > 1) ? 1 : 0);
	}

	// Modified code merge ---------------------------------
	private SimpleDirectedGraph<Integer, DefaultEdge> getUnionTSF(){
		return this.union_tsf;
	}
	
	private SimpleDirectedGraph<Integer, DefaultEdge> getUnionLSF(){
		return this.union_lsf;
	}
	
	private SimpleDirectedGraph<Integer, DefaultEdge> getUnionLCSF_GLCSF(){
		return this.union_lcsf_glcsf;
	}
	// ---------------------------------------------------

}
