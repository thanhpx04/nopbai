package control;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.event.MouseInputListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

import model.NotDeterministInitialStateException;
import model.NotDeterministTransitionException;
import model.NullTransitionsException;
import model.UnknownInitialStateException;
import model.State;
import model.Transition;
import model.FileXML;
import model.ObservableAutomaton;

public class GraphComponent extends JComponent implements MouseInputListener, KeyListener {
	private static final long serialVersionUID = 1L;
	private static final String DIALOG_REMOVEJP_MESSAGE = "If the last join point is removed, the transition will be removed\n\nDo you want to remove it?";
	private static final String DIALOG_REMOVEJP_TITLE = "Remove the last join point";
	private static final String INPUT_LABEL_TITLE = "Label transition";
	private static final String DIALOG_LABEL_MESSAGE = "Label must be only one character a-z or A-Z";
	private static final String INPUT_STRING_TITLE = "Label transition";
	private static final String DIALOG_STRING_MESSAGE = "String must be at least one character a-z or A-Z";
	private static final List<Color> colorList = new ArrayList<Color>();

	private static int n = 0;// label of state
	private RectangularShape shapeSample = new Ellipse2D.Double(0, 0, 10, 10);
	private List<State> states = new ArrayList<State>();
	private List<Color> colors = new ArrayList<>();
	private int dx = 0;// distance from mouse click to centre
	private int dy = 0;
	private RectangularShape currentJointPoint = null;
	private State currentState = null;
	private List<Transition<String>> transitions = new ArrayList<Transition<String>>();
	private Transition<String> currentTransition = null;
	private String stateType = "";
	private String inputString = null;

	public GraphComponent() {
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		colorList.add(Color.BLACK);
		colorList.add(Color.MAGENTA);
		colorList.add(Color.GRAY);
		colorList.add(Color.BLUE);
		colorList.add(Color.RED);
	}

	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		for (Transition<String> t : transitions) {
			g.setColor(getForeground());
			t.draw(g2);
		}
		for (int i = 0; i < states.size(); i++) {
			State s = states.get(i);
			g.setColor(colors.get(i));
			s.draw(g2);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			State s = getState(e.getX(), e.getY());
			if (s != null) {
				removeState(s);
				return;
			}
			// this variable check transition has only one joinpoint
			Transition<String> transition = null;
			for (Transition<String> t : transitions) {
				RectangularShape jp = t.getJointPoint(e.getX(), e.getY());
				if (jp != null) {
					if (t.getListJointPoints().size() > 1) {
						t.removeJointPoint(jp);
						return;
					} else
						transition = t;
				}
			}
			// if transition has only one join point left then remove it
			if (transition != null && JOptionPane.showConfirmDialog(this, DIALOG_REMOVEJP_MESSAGE,
					DIALOG_REMOVEJP_TITLE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				transitions.remove(transition);
		}
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		requestFocusInWindow();
		if ((e.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) == InputEvent.BUTTON3_DOWN_MASK)
			return;
		State s = getState(e.getX(), e.getY());
		if (s == null)
			currentJointPoint = getJointPoint(e.getX(), e.getY());
		if (s == null && currentJointPoint == null)
			s = createState(e.getX(), e.getY());
		if (s != null)
			if (e.isAltDown())
				currentTransition = startTransition(s);
			else
				currentState = s;
		if (currentState != null) {
			switch (stateType) {
			case "initial":
				states.get(states.indexOf(currentState)).setInitial(true);
				break;
			case "terminal":
				states.get(states.indexOf(currentState)).setTerminal(true);
				break;
			case "normal":
				states.get(states.indexOf(currentState)).setInitial(false);
				states.get(states.indexOf(currentState)).setTerminal(false);
				break;
			}
			stateType = "";
		}
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (currentTransition != null) {
			// regular expression accept string has only 1 word a-z or A-Z
			String regex = "^[a-zA-Z]{1}$";
			String s = JOptionPane.showInputDialog(this, INPUT_LABEL_TITLE);
			try {
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(s);
				while (!matcher.find()) {
					JOptionPane.showMessageDialog(this, DIALOG_LABEL_MESSAGE, INPUT_LABEL_TITLE,
							JOptionPane.INFORMATION_MESSAGE);
					s = JOptionPane.showInputDialog(this, INPUT_LABEL_TITLE);
					matcher = pattern.matcher(s);
				}
			} catch (Exception ex) {
				ex.getMessage();
			}
			if (s != null) {
				endTransition(currentTransition, e.getX(), e.getY());
				currentTransition.setLabel(s);
			} else
				transitions.remove(currentTransition);
		}
		repaint();
		currentState = null;
		currentJointPoint = null;
		currentTransition = null;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (currentState != null) {
			moveShape(currentState.getShape(), e.getX() - dx, e.getY() - dy);
		} else if (currentTransition != null) {
			moveShape(currentTransition.target().getShape(), e.getX(), e.getY());
		} else if (currentJointPoint != null) {
			moveShape(currentJointPoint, e.getX(), e.getY());
		}
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE && currentTransition != null)
			currentTransition.addJointPoint();
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	public void setShapeType(RectangularShape sample) {
		shapeSample = sample;
	}

	private State getState(int x, int y) {
		for (int i = states.size() - 1; i >= 0; i--) {
			State s = states.get(i);
			if (s.contains(x, y)) {
				dx = (int) (x - s.getShape().getCenterX());
				dy = (int) (y - s.getShape().getCenterY());
				return s;
			}
		}
		return null;
	}

	private State createState(int x, int y) {
		RectangularShape rs = newShape(x, y);
		State s = new State(false, false, rs, Integer.toString(n++));
		states.add(s);
		return s;
	}

	private RectangularShape newShape(int x, int y) {
		RectangularShape rs = (RectangularShape) shapeSample.clone();
		moveShape(rs, x, y);
		Random r = new Random();
		colors.add(colorList.get(r.nextInt(colorList.size())));
		return rs;
	}

	private void moveShape(RectangularShape rs, int x, int y) {
		rs.setFrameFromCenter(x, y, x + rs.getWidth() / 2, y + rs.getHeight() / 2);
	}

	private RectangularShape getJointPoint(int x, int y) {
		for (Transition<String> t : transitions)
			if (t.getJointPoint(x, y) != null)
				return t.getJointPoint(x, y);
		return null;
	}

	private Transition<String> startTransition(State s) {
		RectangularShape rs2 = newShape(0, 0);
		RectangularShape rs = s.getShape();
		rs2.setFrameFromCenter((int) rs.getCenterX(), (int) rs.getCenterY(), (int) rs.getCenterX(),
				(int) rs.getCenterY());
		Transition<String> t = new Transition<String>(s, new State(false, false, rs2, null));
		transitions.add(t);
		return t;
	}

	private void endTransition(Transition<String> t, int x, int y) {
		State s = getState(x, y);
		if (s == null) {
			t.target().getShape().setFrameFromCenter(x, y, x + shapeSample.getHeight() / 2,
					y + shapeSample.getWidth() / 2);
			t.target().setLabel(Integer.toString(n++));
			states.add(t.target());
		} else
			t.setTarget(s);
		if (t.getListJointPoints().size() < 1)
			t.addDefaultJP();
	}

	private void removeState(State s) {
		List<Transition<String>> toRemove = new ArrayList<Transition<String>>();
		for (Transition<String> t : transitions)
			if (t.target() == s || t.source() == s)
				toRemove.add(t);
		for (Transition<String> t : toRemove)
			transitions.remove(t);
		colors.remove(states.indexOf(s));
		states.remove(s);
	}

	public void setInputString(JTextArea showInfor) {
		// regular expression string has at least 1 word a-z or A-Z
		String regex = "[a-zA-Z]+";
		inputString = JOptionPane.showInputDialog(this, INPUT_STRING_TITLE);
		if (inputString == null)
			return;
		try {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(inputString);
			while (!matcher.find()) {
				JOptionPane.showMessageDialog(this, DIALOG_STRING_MESSAGE, INPUT_STRING_TITLE,
						JOptionPane.INFORMATION_MESSAGE);
				inputString = JOptionPane.showInputDialog(this, INPUT_STRING_TITLE);
				if (inputString == null)
					return;
				matcher = pattern.matcher(inputString);
			}
			String[] listWord = inputString.split("(?!^)");
			ObservableAutomaton<String> observable = new ObservableAutomaton<String>(transitions);
			observable.addObserver(new Observer() {
				@SuppressWarnings("unchecked")
				public void update(Observable observable, Object transition) {
					((Transition<String>) transition).setObservableIsCalled(true);
				}
			});
			boolean check = observable.recognize(listWord);
			repaint();
			String message = "The string '" + inputString + (check == true ? "' is" : "' is not") + " accepted";
			JOptionPane.showMessageDialog(this, message);
			showInfor.append(message + "\n");
			// reset colour of list transitions
			for (Transition<String> transition : transitions)
				transition.setObservableIsCalled(false);
			repaint();
		} catch (NotDeterministInitialStateException | NotDeterministTransitionException | NullTransitionsException
				| UnknownInitialStateException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}

	public void setStateType(String type) {
		stateType = type;
	}

	public void saveToXML() {
		try {
			JFileChooser fileChooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("xml files (*.xml)", "xml");
			fileChooser.setFileFilter(filter);
			int selection = fileChooser.showSaveDialog(this);
			if (selection == JFileChooser.APPROVE_OPTION) {
				String filename = fileChooser.getSelectedFile().toString();
				// add file type
				if (!filename.endsWith(".xml"))
					filename += ".xml";
				File fileToSave = new File(filename);
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.newDocument();
				FileXML xmlFile = new FileXML();
				xmlFile.save(doc, fileToSave, states, colors, colorList, transitions);
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}

	public void openFromXML() {
		try {
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("xml files (*.xml)", "xml");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File inputFile = new File(chooser.getSelectedFile().getAbsolutePath());
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.parse(inputFile);
				// reset all list
				states.clear();
				colors.clear();
				transitions.clear();
				FileXML xmlFile = new FileXML();
				xmlFile.open(doc, states, colors, colorList, transitions);
				repaint();
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}
}
