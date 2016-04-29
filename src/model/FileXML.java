package model;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.io.File;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FileXML {
	
	public FileXML(){}

	public boolean save(Document doc, File fileToSave, List<State> states, List<Color> colors, List<Color> colorList,
			List<Transition<String>> transitions) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.newDocument();
			// root Automaton
			Element root = doc.createElement("Automaton");
			doc.appendChild(root);
			// element ListStates
			for (int i = 0; i < states.size(); i++) {
				Element state = doc.createElement("State");
				root.appendChild(state);
				Attr initial = doc.createAttribute("initial");
				initial.setValue(states.get(i).initial() + "");
				state.setAttributeNode(initial);
				Attr terminal = doc.createAttribute("terminal");
				terminal.setValue(states.get(i).terminal() + "");
				state.setAttributeNode(terminal);
				Attr size = doc.createAttribute("size");
				size.setValue(states.get(i).getShape().getWidth() + "");
				state.setAttributeNode(size);
				Attr x = doc.createAttribute("x");
				x.setValue(states.get(i).getShape().getCenterX() + "");
				state.setAttributeNode(x);
				Attr y = doc.createAttribute("y");
				y.setValue(states.get(i).getShape().getCenterY() + "");
				state.setAttributeNode(y);
				state.appendChild(doc.createTextNode(states.get(i).getLabel()));
			}
			// element ListColors
			for (int i = 0; i < colors.size(); i++) {
				Element color = doc.createElement("Color");
				root.appendChild(color);
				color.appendChild(doc.createTextNode(colorList.indexOf(colors.get(i)) + ""));
			}
			// element ListTransitions
			for (int i = 0; i < transitions.size(); i++) {
				Element transition = doc.createElement("Transition");
				root.appendChild(transition);
				Attr souce = doc.createAttribute("souce");
				souce.setValue(states.indexOf(transitions.get(i).source()) + "");
				transition.setAttributeNode(souce);
				Attr target = doc.createAttribute("target");
				target.setValue(states.indexOf(transitions.get(i).target()) + "");
				transition.setAttributeNode(target);
				Attr label = doc.createAttribute("label");
				label.setValue(transitions.get(i).label());
				transition.setAttributeNode(label);
				// element List join point
				Transition<String> trans = (Transition<String>)transitions.get(i);
				for (int j = 0; j < trans.getListJointPoints().size(); j++) {
					Element joinPoint = doc.createElement("JoinPoint");
					transition.appendChild(joinPoint);
					Attr x = doc.createAttribute("x");
					x.setValue(trans.getListJointPoints().get(j).getCenterX() + "");
					joinPoint.setAttributeNode(x);
					Attr y = doc.createAttribute("y");
					y.setValue(trans.getListJointPoints().get(j).getCenterY() + "");
					joinPoint.setAttributeNode(y);
				}
			}
			// write to xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(fileToSave);
			transformer.transform(source, result);
			return true;
		} catch (Exception e) {
			e.getMessage();
			return false;
		}
	}

	public boolean open(Document doc, List<State> states, List<Color> colors, List<Color> colorList,
			List<Transition<String>> transitions) {
		try {
			// state nodes
			NodeList listStates = doc.getElementsByTagName("State");
			for (int i = 0; i < listStates.getLength(); i++) {
				Node state = listStates.item(i);
				boolean initial = false, terminal = false;
				double size = 0, x = 0, y = 0;
				// Attributes of state
				NamedNodeMap attrs = state.getAttributes();
				for (int j = 0; j < attrs.getLength(); j++) {
					Node attr = attrs.item(j);
					switch (attr.getNodeName()) {
					case "initial":
						initial = Boolean.parseBoolean(attr.getNodeValue());
						break;
					case "terminal":
						terminal = Boolean.parseBoolean(attr.getNodeValue());
						break;
					case "size":
						size = Double.parseDouble(attr.getNodeValue());
						break;
					case "x":
						x = Double.parseDouble(attr.getNodeValue());
						break;
					case "y":
						y = Double.parseDouble(attr.getNodeValue());
						break;
					}
				}
				RectangularShape rs = new Ellipse2D.Double(x, y, size, size);
				rs.setFrameFromCenter(x, y, x + rs.getWidth() / 2, y + rs.getHeight() / 2);
				State s = new State(initial, terminal, rs, state.getTextContent());
				states.add(s);
			}
			// colour nodes
			NodeList listColors = doc.getElementsByTagName("Color");
			for (int i = 0; i < listColors.getLength(); i++) {
				Node color = listColors.item(i);
				colors.add(colorList.get(Integer.parseInt(color.getTextContent())));
			}
			// transition nodes
			NodeList listTransitions = doc.getElementsByTagName("Transition");
			for (int i = 0; i < listTransitions.getLength(); i++) {
				Node transition = listTransitions.item(i);
				// Attributes of transition
				Transition<String> t = new Transition<String>(null, null);
				String label = null;
				NamedNodeMap attrsTransition = transition.getAttributes();
				for (int j = 0; j < attrsTransition.getLength(); j++) {
					Node attr = attrsTransition.item(j);
					switch (attr.getNodeName()) {
					case "label":
						label = attr.getNodeValue();
						break;
					case "souce":
						t.setSource(states.get(Integer.parseInt(attr.getNodeValue())));
						break;
					case "target":
						t.setTarget(states.get(Integer.parseInt(attr.getNodeValue())));
						break;
					}
				}
				// Children nodes of transition
				NodeList children = transition.getChildNodes();
				for (int j = 0; j < children.getLength(); j++) {
					Node child = children.item(j);
					// Attributes of the child (join point)
					double x = 0, y = 0;
					NamedNodeMap attrsJP = child.getAttributes();
					for (int k = 0; k < attrsJP.getLength(); k++) {
						Node attr = attrsJP.item(k);
						switch (attr.getNodeName()) {
						case "x":
							x = Double.parseDouble(attr.getNodeValue());
							break;
						case "y":
							y = Double.parseDouble(attr.getNodeValue());
							break;
						}
					}
					RectangularShape rs = new Ellipse2D.Double(x, y, 10, 10);
					rs.setFrameFromCenter(x, y, x + rs.getWidth() / 2, y + rs.getHeight() / 2);
					t.getListJointPoints().add(rs);
				}
				// set label
				t.setLabel(label);
				transitions.add(t);
			}
			return true;
		} catch (Exception e) {
			e.getMessage();
			return false;
		}
	}
}
