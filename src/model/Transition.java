package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.List;

public class Transition<T> implements TransitionInterface<T> {
	private static final int JOINT_POINT_SIZE = 10;
	private static final int ARROW_SIZE = 15;
	private static final double ARROW_ANGLE = Math.PI / 6;

	private State source;
	private State target;
	private T label = null;
	private List<RectangularShape> jointPoints = new ArrayList<RectangularShape>();
	// this variable to visualise the acknowledgement when observable is called
	private boolean observableIsCalled;

	public Transition(State source, State target) {
		this.source = source;
		this.target = target;
		observableIsCalled = false;
	}

	public void setObservableIsCalled(boolean observableIsCalled) {
		this.observableIsCalled = observableIsCalled;
	}

	@Override
	public State source() {
		return source;
	}

	@Override
	public State target() {
		return target;
	}

	@Override
	public T label() {
		return label;
	}

	@Override
	public void setLabel(T label) {
		this.label = label;
	}

	public void setSource(State source) {
		this.source = source;
	}

	public void setTarget(State target) {
		this.target = target;
	}

	public List<RectangularShape> getListJointPoints() {
		return jointPoints;
	}

	public void addJointPoint() {
		jointPoints.add(new Ellipse2D.Double((int) target.getShape().getCenterX(), (int) target.getShape().getCenterY(),
				JOINT_POINT_SIZE, JOINT_POINT_SIZE));
	}

	public void addDefaultJP() {
		if (source == target) {
			jointPoints.add(new Ellipse2D.Double((int) source.getShape().getMaxX() + source.getShape().getWidth(),
					(int) source.getShape().getMinY(), JOINT_POINT_SIZE, JOINT_POINT_SIZE));
			jointPoints.add(new Ellipse2D.Double((int) source.getShape().getMaxX() + source.getShape().getWidth(),
					(int) source.getShape().getMaxY(), JOINT_POINT_SIZE, JOINT_POINT_SIZE));
		} else
			jointPoints.add(new Ellipse2D.Double((int) (target.getShape().getCenterX() + source.getShape().getCenterX()) / 2,
					(int) (target.getShape().getCenterY() + source.getShape().getCenterY()) / 2, JOINT_POINT_SIZE,
					JOINT_POINT_SIZE));
	}

	public void removeJointPoint(RectangularShape jp) {
		jointPoints.remove(jp);
	}

	public RectangularShape getJointPoint(int x, int y) {
		for (RectangularShape p : jointPoints) {
			if (p.contains(x, y))
				return p;
		}
		return null;
	}

	public void draw(Graphics2D g2) {
		Paint bg = g2.getPaint();
		g2.setPaint(Color.RED);
		for (RectangularShape jp : jointPoints)
			g2.fill(jp);
		if (observableIsCalled)
			g2.setPaint(Color.RED);
		else
			g2.setPaint(bg);
		int x1 = (int) source.getShape().getCenterX();
		int y1 = (int) source.getShape().getCenterY();
		for (RectangularShape jp : jointPoints) {
			int x2 = (int) jp.getCenterX();
			int y2 = (int) jp.getCenterY();
			g2.drawLine(x1, y1, x2, y2);
			drawArrow(g2, x1, y1, (3 * x1 + x2) / 4, (3 * y1 + y2) / 4);
			x1 = x2;
			y1 = y2;
		}
		int x2 = (int) target.getShape().getCenterX();
		int y2 = (int) target.getShape().getCenterY();
		g2.drawLine(x1, y1, x2, y2);
		drawArrow(g2, x1, y1, (3 * x1 + x2) / 4, (3 * y1 + y2) / 4);
		if (label != null)
			g2.drawString((String) label, (int) labelPosition().getX(), (int) labelPosition().getY());
	}

	private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
		double alpha = x1 == x2 ? Math.PI / 2. : Math.atan(((y1 - y2) / ((double) (x1 - x2))));
		if (x2 - x1 < 0 || (x1 == x2 && y1 > y2))
			alpha = Math.PI + alpha;

		int x3 = (int) (x2 - ARROW_SIZE * Math.cos(alpha + ARROW_ANGLE));
		int y3 = (int) (y2 - ARROW_SIZE * Math.sin(alpha + ARROW_ANGLE));
		int x4 = (int) (x2 - ARROW_SIZE * Math.cos(alpha - ARROW_ANGLE));
		int y4 = (int) (y2 - ARROW_SIZE * Math.sin(alpha - ARROW_ANGLE));

		g2.drawLine(x2, y2, x3, y3);
		g2.drawLine(x2, y2, x4, y4);
	}

	public Point labelPosition() {
		int x1, x2, y1, y2;
		int jps = jointPoints.size();
		if (jps <= 1) {
			x1 = (int) source.getShape().getMaxX();
			y1 = (int) source.getShape().getMaxY();
			if (jps == 0) {
				x2 = (int) target.getShape().getMaxX();
				y2 = (int) target.getShape().getMaxY();
			} else {
				RectangularShape rs = jointPoints.get(0);
				x2 = (int) rs.getMaxX();
				y2 = (int) rs.getMaxY();
			}
		} else {
			int index = jointPoints.size() / 2;
			RectangularShape rs1 = jointPoints.get(index - 1);
			RectangularShape rs2 = jointPoints.get(index);
			x1 = (int) rs1.getMaxX();
			y1 = (int) rs1.getMaxY();
			x2 = (int) rs2.getMaxX();
			y2 = (int) rs2.getMaxY();
		}
		return new Point((x1 + x2) / 2, (y1 + y2) / 2);
	}
}
