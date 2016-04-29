package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.RectangularShape;

public class State implements StateInterface {
	private static final int LENGTH = 40;
	private boolean initial, terminal;
	private RectangularShape shape;
	private String label;

	public State(boolean initial, boolean terminal, RectangularShape rs, String label) {
		this.initial = initial;
		this.terminal = terminal;
		this.shape = rs;
		this.label = label;
	}

	public void setInitial(boolean initial) {
		this.initial = initial;
	}

	public void setTerminal(boolean terminal) {
		this.terminal = terminal;
	}

	public RectangularShape getShape() {
		return shape;
	}

	public void setShape(RectangularShape shape) {
		this.shape = shape;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public boolean initial() {
		return initial;
	}

	@Override
	public boolean terminal() {
		return terminal;
	}

	public void draw(Graphics2D g2) {
		Paint bg = g2.getPaint();
		if (terminal)
			g2.drawOval((int) shape.getMinX() - LENGTH / 10, (int) shape.getMinY() - LENGTH / 10,
					(int) shape.getWidth() + LENGTH / 5, (int) shape.getHeight() + LENGTH / 5);
		g2.setPaint(Color.BLACK);
		if (initial) {
			// draw arrow to initial state
			g2.drawLine((int) shape.getCenterX(), (int) shape.getCenterY(), (int) shape.getCenterX() - LENGTH * 2,
					(int) shape.getCenterY());
			int x = (int) shape.getCenterX() - LENGTH;
			g2.drawLine(x, (int) shape.getCenterY(), x - LENGTH / 5, (int) shape.getCenterY() + LENGTH / 5);
			g2.drawLine(x, (int) shape.getCenterY(), x - LENGTH / 5, (int) shape.getCenterY() - LENGTH / 5);
		}
		g2.setPaint(bg);
		g2.fill(shape);
		g2.setColor(Color.WHITE);
		if (label != null)
			g2.drawString(label, (int) shape.getCenterX(), (int) shape.getCenterY());
	}

	public boolean contains(int x, int y) {
		return shape.contains(x, y);
	}
}
