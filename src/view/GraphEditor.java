package view;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

public class GraphEditor implements FramesController {
	public final static String TITLE = "Graph Automaton Editor";
	public final static String DIALOG_QUIT_MESSAGE = "Do you really want to quit ?";
	public final static String DIALOG_QUIT_TITLE = "Quit ?";
	public final static int EDITOR_SIZE = 600;

	private static final List<JFrame> frames = new ArrayList<JFrame>();

	@Override
	public void quit() {
		if (JOptionPane.showConfirmDialog(null, DIALOG_QUIT_MESSAGE, DIALOG_QUIT_TITLE,
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}

	@Override
	public JFrame createFrame() {
		JFrame frame = new GraphFrame(this);
		frame.setTitle(TITLE);
		int pos = 30 * (frames.size() % 5);
		frame.setLocation(pos, pos);
		frame.setPreferredSize(new Dimension(EDITOR_SIZE, EDITOR_SIZE));
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		frames.add(frame);
		return frame;
	}

	@Override
	public void deleteFrame(JFrame frame) {
		if (frames.size() > 1) {
			frames.remove(frame);
			frame.dispose();
		} else
			quit();
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new GraphEditor().createFrame();
			}
		});
	}
}
