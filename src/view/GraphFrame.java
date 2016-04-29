package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;

import control.GraphComponent;

public class GraphFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	public static final int FRAME_SIZE = 700;
	public static final int RADIUS = 50;
	public final static String MENU_FILE = "File";
	public final static String MENU_ITEM_NEW = "New";
	public final static String MENU_ITEM_CLOSE = "Close";
	public final static String MENU_ITEM_SAVE = "Save";
	public final static String MENU_ITEM_OPEN = "Open";
	public final static String MENU_ITEM_QUIT = "Quit";
	public final static String MENU_AUTOMATON = "Automaton";
	public final static String MENU_ITEM_INITIAL = "Set Initial Sate";
	public final static String MENU_ITEM_TERMINAL = "Set Terminal State";
	public final static String MENU_ITEM_NORMAL = "Set Normal State";
	public final static String MENU_ITEM_INPUTSTR = "Check String";
	final JTextArea showInfor;

	private GraphComponent component;
	private FramesController controller;

	public GraphFrame(FramesController controller) {
		this.controller = controller;
		component = new GraphComponent();
		component.setPreferredSize(new Dimension(FRAME_SIZE, FRAME_SIZE));
		JScrollPane srcollPane = new JScrollPane(component);
		JToolBar toolbar = new JToolBar();
		toolbar.setLayout(new GridLayout(0, 1));
		JButton b = addShapeButton(toolbar, new Ellipse2D.Double(0, 0, RADIUS, RADIUS), "Normal Circle");
		b.doClick();
		addShapeButton(toolbar, new Ellipse2D.Double(0, 0, RADIUS*1.5, RADIUS*1.5), "Big Circle");
		addShapeButton(toolbar, new Ellipse2D.Double(0, 0, RADIUS/1.5, RADIUS/1.5), "Small Circle");
		showInfor = new JTextArea(5, FRAME_SIZE);
		showInfor.setEditable(false); // set textArea non-editable
		JScrollPane scrollText = new JScrollPane(showInfor);
		scrollText.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		getContentPane().add(toolbar,BorderLayout.WEST);
		getContentPane().add(srcollPane, BorderLayout.CENTER);
		getContentPane().add(scrollText, BorderLayout.SOUTH);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				GraphFrame.this.controller.deleteFrame(GraphFrame.this);
			}
		});
		// menu File
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu menuFile = new JMenu(MENU_FILE);
		menuBar.add(menuFile);
		createMenuItem(menuFile, MENU_ITEM_NEW, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame.this.controller.createFrame();
			}
		});
		createMenuItem(menuFile, MENU_ITEM_CLOSE, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame.this.controller.deleteFrame(GraphFrame.this);
			}
		});
		menuFile.addSeparator();
		createMenuItem(menuFile, MENU_ITEM_SAVE, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				component.saveToXML();
			}
		});
		createMenuItem(menuFile, MENU_ITEM_OPEN, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				component.openFromXML();
			}
		});
		menuFile.addSeparator();
		createMenuItem(menuFile, MENU_ITEM_QUIT, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame.this.controller.quit();
			}
		});
		// menu Automaton
		JMenu menuAutomaton = new JMenu(MENU_AUTOMATON);
		menuBar.add(menuAutomaton);
		createMenuItem(menuAutomaton, MENU_ITEM_INITIAL, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				component.setStateType("initial");
			}
		});
		createMenuItem(menuAutomaton, MENU_ITEM_TERMINAL, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				component.setStateType("terminal");
			}
		});
		createMenuItem(menuAutomaton, MENU_ITEM_NORMAL, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				component.setStateType("normal");
			}
		});
		menuAutomaton.addSeparator();
		createMenuItem(menuAutomaton, MENU_ITEM_INPUTSTR, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				component.setInputString(showInfor);
			}
		});
	}

	private JButton addShapeButton(JToolBar toolbar, final RectangularShape sample, String name) {
		JButton button = new JButton(name);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				component.setShapeType(sample);
			}
		});
		toolbar.add(button);
		return button;
	}
	
	private void createMenuItem(JMenu menu, String name, ActionListener action) {
		JMenuItem menuItem = new JMenuItem(name);
		menuItem.addActionListener(action);
		menu.add(menuItem);
	}

}
