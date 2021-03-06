package debback2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import debback2.Backgammon.MenuItemPainter;

public class AnnouncementPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> menuItems;
	private String selectMenuItem;
	private String focusedItem;
	private BufferedImage image;

	private MenuItemPainter painter;
	private Map<String, Rectangle> menuBounds;

	public AnnouncementPane(JFrame frame, JFrame frame2, JFrame frame3) {
		setBackground(Color.BLACK);
		frame.setVisible(false);
		repaint();
		revalidate();
		frame3.setVisible(false);
		repaint();
		revalidate();
		try {
			image = ImageIO.read(this.getClass().getResource("backgammon3.jpg"));

		} catch (IOException ex) {
			System.out.println("Could not find the image file " + ex.toString());
		}
		painter = new SimpleMenuItemPainter();
		menuItems = new ArrayList<>(25);
		menuItems.add("WELCOME");
		menuItems.add("Start");
		menuItems.add("Exit");
		selectMenuItem = menuItems.get(0);

		MouseAdapter ma = new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				String newItem = null;
				for (String text : menuItems) {
					Rectangle bounds = menuBounds.get(text);
					if (bounds.contains(e.getPoint())) {
						newItem = text;
						break;
					}
				}

				if (newItem != null && !newItem.equals(selectMenuItem)) {
					selectMenuItem = newItem;
					repaint();
				}

				if (selectMenuItem.equals("Start")) {
					System.out.println("START");
					frame3.repaint();
					frame3.revalidate();
					frame3.setVisible(true);
					frame3.repaint();
					frame3.revalidate();
					frame2.setVisible(false);
					frame2.repaint();
					frame2.revalidate();

				}

				if (selectMenuItem.equals("Exit")) {
					frame.dispose();
					System.exit(0);
				}

			}

			@Override
			public void mouseMoved(MouseEvent e) {
				focusedItem = null;
				for (String text : menuItems) {
					Rectangle bounds = menuBounds.get(text);
					if (bounds.contains(e.getPoint())) {
						focusedItem = text;
						repaint();
						break;
					}
				}
			}

		};

		addMouseListener(ma);
		addMouseMotionListener(ma);

		InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
		ActionMap am = getActionMap();

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "arrowDown");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "arrowUp");

		am.put("arrowDown", new MenuAction(1));
		am.put("arrowUp", new MenuAction(-1));

	}

	@Override
	public void invalidate() {
		menuBounds = null;
		super.invalidate();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(900, 530);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, 900, 550, this);
		Graphics2D g2d = (Graphics2D) g.create();
		if (menuBounds == null) {
			menuBounds = new HashMap<>(menuItems.size());
			int width = 0;
			int height = 0;
			for (String text : menuItems) {
				Dimension dim = painter.getPreferredSize(g2d, text);
				width = Math.max(width, dim.width);
				height = Math.max(height, dim.height);
			}

			int x = (getWidth() - (width + 10)) / 2;

			int totalHeight = (height + 10) * menuItems.size();
			totalHeight += 5 * (menuItems.size() - 1);

			int y = (getHeight() - totalHeight) / 2;

			for (String text : menuItems) {
				menuBounds.put(text, new Rectangle(x, y, width + 10, height + 10));
				y += height + 10 + 5;
			}

		}
		for (String text : menuItems) {
			Rectangle bounds = menuBounds.get(text);
			boolean isSelected = text.equals(selectMenuItem);
			boolean isFocused = text.equals(focusedItem);
			painter.paint(g2d, text, bounds, isSelected, isFocused);
		}
		g2d.dispose();
	}

	public class MenuAction extends AbstractAction {

		private final int delta;

		public MenuAction(int delta) {
			this.delta = delta;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int index = menuItems.indexOf(selectMenuItem);
			if (index < 0) {
				selectMenuItem = menuItems.get(0);
			}
			index += delta;
			if (index < 0) {
				selectMenuItem = menuItems.get(menuItems.size() - 1);
			} else if (index >= menuItems.size()) {
				selectMenuItem = menuItems.get(0);
			} else {
				selectMenuItem = menuItems.get(index);
			}
			repaint();
		}

	}

}
