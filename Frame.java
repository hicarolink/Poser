package main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Frame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Jpanels
	JPanel panel, bottomPanel, upperPanel, extraOptions;

	// Images
	BufferedImage image;
	Image photo;

	// Jbuttons
	JButton btnOpenFile = new JButton("Add dir");
	JButton btnAddImg = new JButton("Add img");
	JButton btnPlay = new JButton("Play");
	JButton btnStop = new JButton("Stop");
	JButton btnClear = new JButton("Clear");
	JButton btnExit = new JButton("Exit");
	JButton btnRemove = new JButton("Del img");
	JButton btnNext = new JButton("Next");

	// Jcheckboxes
	JCheckBox randomImg = new JCheckBox("Random");
	JCheckBox grayscale = new JCheckBox("Grayscale");
	JCheckBox upsidedown = new JCheckBox("Upside down");

	// Jradiobuttons
	JRadioButton sec30, sec60, sec90, min2, min5, min10;

	final DefaultListModel model = new DefaultListModel();
	JList listImages = new JList(model);

	JFileChooser chooser, chooser2;
	String choosertitle;
	ArrayList<String> images_names = new ArrayList<String>();
	int timePoser = Constants.SEC_30;
	int timeScreen;
	JLabel labelTime;
	int indexDisplay = 0;

	JFrame controlPanel = new JFrame();

	Timer timerScreen;

	static final List<String> EXTENSIONS = Arrays.asList("gif", "png", "bmp",
			"jpg");

	static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

		@Override
		public boolean accept(File dir, String name) {
			// TODO Auto-generated method stub
			for (final String ext : EXTENSIONS) {
				if (name.endsWith("." + ext)) {
					return true;
				}
			}
			return false;
		}
	};

	public Frame() {
		setTitle("Kiddo Poser");
		controlPanel.setTitle("Control Panel");

		setLayout(new BorderLayout());
		// labelTime.setHorizontalAlignment(SwingConstants.CENTER);

		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				// This is only called when the user releases the mouse button.
				System.out.println("componentResized");
				if (photo != null) {
					ImageDimensions(getWidth(), getHeight(), image.getWidth(),
							image.getHeight());
					panel.repaint();
				}
			}
		});

		initRadioButtons();

		initButtons();

		randomImg.setSelected(false);
		grayscale.setSelected(false);
		upsidedown.setSelected(false);

		grayscale.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				panel.repaint();
			}
		});

		upsidedown.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
				panel.repaint();
			}
		});

		bottomPanel = new JPanel();
		JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		exitPanel.add(btnExit);
		JPanel controlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		bottomPanel.setLayout(new BorderLayout());
		controlButtons.add(btnOpenFile);
		controlButtons.add(btnAddImg);
		controlButtons.add(btnRemove);
		controlButtons.add(btnPlay);
		controlButtons.add(btnNext);
		controlButtons.add(btnStop);
		controlButtons.add(btnClear);
		bottomPanel.add(controlButtons, BorderLayout.NORTH);
		bottomPanel.add(exitPanel, BorderLayout.SOUTH);

		upperPanel = new JPanel();
		upperPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		upperPanel.add(new JLabel("Time"));
		upperPanel.add(sec30);
		upperPanel.add(sec60);
		upperPanel.add(sec90);
		upperPanel.add(min2);
		upperPanel.add(min5);
		upperPanel.add(min10);

		extraOptions = new JPanel(new FlowLayout(FlowLayout.CENTER));
		extraOptions.add(randomImg);
		extraOptions.add(grayscale);
		extraOptions.add(upsidedown);

		panel = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				if (photo != null) {
					if (upsidedown.isSelected() || grayscale.isSelected()) {
						BufferedImage img = toBufferedImage(photo);
						if(upsidedown.isSelected()) {
							// Reverse the image
							AffineTransform tx = AffineTransform.getScaleInstance(
									1, -1);
							tx.translate(0, -photo.getHeight(null));
							AffineTransformOp op = new AffineTransformOp(tx,
									AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
							img = op.filter(img, null);
						}

						if (grayscale.isSelected()) {

							BufferedImage gray;
							gray = new BufferedImage(img.getWidth(),
									img.getHeight(),
									BufferedImage.TYPE_BYTE_GRAY);

							Graphics g2 = gray.getGraphics();
							g2.drawImage(img, 0, 0, null);
							g2.dispose();

							img = gray;
						}
						
						g.drawImage(img, 0, 0, null);
					} else {
						g.drawImage(photo, 0, 0, null);
					}
				} else {
					g.setColor(panel.getBackground());
					g.fillRect(0, 0, panel.getWidth(), panel.getHeight());
				}
			}
		};

		add(panel, BorderLayout.CENTER);
		// add(bottomPanel, BorderLayout.SOUTH);

		setVisible(true);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setSize(getWidth() / 2, getHeight() - 15);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocation(5, 0);
		JPanel p = new JPanel();
		labelTime = new JLabel("" + timeScreen, SwingConstants.CENTER);
		p.add(labelTime);
		add(p, BorderLayout.PAGE_END);

		controlPanel.setLocation((int) getSize().getWidth() + 5, 0);
		controlPanel.setSize(getWidth() - 20, getHeight() - 20);
		controlPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		controlPanel.setLayout(new BorderLayout());
		controlPanel.add(bottomPanel, BorderLayout.SOUTH);
		JPanel p1 = new JPanel(new BorderLayout());
		p1.add(upperPanel, BorderLayout.NORTH);
		p1.add(extraOptions, BorderLayout.SOUTH);
		controlPanel.add(p1, BorderLayout.NORTH);

		listImages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listImages.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				// TODO Auto-generated method stub
				if (listImages.getSelectedIndex() == -1) {
					// No selection, disable fire button.
				} else {
					// Selection, enable the fire button.
					// JOptionPane.showMessageDialog(null,
					// listImages.getSelectedIndex());
					showSelectedImage(listImages.getSelectedIndex());
					indexDisplay = listImages.getSelectedIndex();
				}
			}
		});
		// listImages.setLayoutOrientation(JList.VERTICAL_WRAP);
		// listImages.setVisibleRowCount(-1);

		JPanel p2 = new JPanel(new BorderLayout());
		p2.add(new JScrollPane(listImages,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
				BorderLayout.CENTER);
		controlPanel.add(p2, BorderLayout.CENTER);
		controlPanel.setVisible(true);

	}

	public void initRadioButtons() {
		sec30 = new JRadioButton("30 sec");
		sec30.setSelected(true);
		sec30.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				timePoser = Constants.SEC_30;
			}
		});

		sec60 = new JRadioButton("60 sec");
		sec60.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				timePoser = Constants.SEC_60;
			}
		});

		sec90 = new JRadioButton("90 sec");
		sec90.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				timePoser = Constants.SEC_90;
			}
		});

		min2 = new JRadioButton("2 min");
		min2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				timePoser = Constants.MIN_2;
			}
		});

		min5 = new JRadioButton("5 min");
		min5.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				timePoser = Constants.MIN_5;
			}
		});

		min10 = new JRadioButton("10 min");
		min10.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				timePoser = Constants.MIN_10;
			}
		});

		ButtonGroup group = new ButtonGroup();
		group.add(sec30);
		group.add(sec60);
		group.add(sec90);
		group.add(min2);
		group.add(min5);
		group.add(min10);
	}

	public void initButtons() {
		btnPlay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				deactivateRadioButtons();

				if (images_names.size() > 0) {
					btnPlay.setEnabled(false);
					btnOpenFile.setEnabled(false);
					btnAddImg.setEnabled(false);
					btnClear.setEnabled(false);
					btnRemove.setEnabled(false);
					btnStop.setEnabled(true);
					btnExit.setEnabled(false);
					btnNext.setEnabled(true);

					timerScreen = new Timer();
					timeScreen = timePoser;
					updateLabelScreen(timeScreen);
					showImage();

					timerScreen.schedule(new timeUpdate(), 0, 1000);
				} else {
					JOptionPane.showMessageDialog(null,
							"There are no loaded images.");
				}
			}
		});

		btnRemove.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				int indexRm = listImages.getSelectedIndex();
				if (indexRm != -1) {
					model.remove(indexRm);
					images_names.remove(indexRm);

					if (images_names.size() == 0) {
						btnPlay.setEnabled(false);
						btnRemove.setEnabled(false);
						btnClear.setEnabled(false);
						btnNext.setEnabled(false);
					}

					photo = null;
					panel.repaint();
				}
			}
		});

		btnRemove.setEnabled(false);

		btnPlay.setEnabled(false);

		btnStop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				timerScreen.purge();
				timerScreen.cancel();

				updateLabelScreen(0);

				btnStop.setEnabled(false);
				btnOpenFile.setEnabled(true);
				btnAddImg.setEnabled(true);
				btnPlay.setEnabled(true);
				btnRemove.setEnabled(true);
				btnClear.setEnabled(true);
				btnExit.setEnabled(true);
				btnNext.setEnabled(false);

				activateRadiobuttons();
			}
		});

		btnStop.setEnabled(false);

		btnOpenFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub

				openFiles(false);
			}
		});

		btnAddImg.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub

				openFiles(true);
			}
		});

		btnClear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				images_names = new ArrayList<String>();
				model.clear();
				photo = null;
				btnClear.setEnabled(false);
				btnPlay.setEnabled(false);
				btnRemove.setEnabled(false);
				btnNext.setEnabled(false);

				panel.repaint();
			}
		});

		btnExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});

		btnClear.setEnabled(false);

		btnNext.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				timerScreen.purge();
				timerScreen.cancel();

				timerScreen = new Timer();
				timeScreen = timePoser;
				updateLabelScreen(timeScreen);
				showImage();

				timerScreen.schedule(new timeUpdate(), 0, 1000);
			}
		});

		btnNext.setEnabled(false);
	}

	public void updateLabelScreen(int time) {
		labelTime.setText("" + time);
		System.out.println(time);
	}

	public void openFiles(boolean openImage) {
		chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle(choosertitle);
		if (openImage == false) {
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
		//
		// disable the "All files" option.
		//
		// chooser.setAcceptAllFileFilterUsed(false);
		//
		int result = chooser.showOpenDialog(Frame.this);
		if (result == JFileChooser.APPROVE_OPTION) {
			System.out.println("getCurrentDirectory(): "
					+ chooser.getCurrentDirectory());
			System.out.println("getSelectedFile() : "
					+ chooser.getSelectedFile());
			File dir = chooser.getSelectedFile();

			if (dir.isDirectory()) {
				for (final File f : dir.listFiles(IMAGE_FILTER)) {
					images_names.add(dir.getAbsoluteFile() + "/" + f.getName());
					if (model.contains(f.getName()) == false) {
						model.addElement(f.getName());
					}
				}
			} else {
				File fileImg = chooser.getSelectedFile();

				String ext = "";
				int i = fileImg.getName().lastIndexOf('.');
				if (i > 0) {
					ext = fileImg.getName().substring(i + 1);
				}
				boolean contains = EXTENSIONS.contains(ext);
				if (contains == true) {
					images_names.add("" + fileImg.getAbsoluteFile());
					if (model.contains(fileImg.getName()) == false) {
						model.addElement(fileImg.getName());
					}

					System.out.println("contem...");

				} else {
					System.out.println("nao...");
				}
			}
			if (images_names.size() > 0) {
				btnPlay.setEnabled(true);
				btnRemove.setEnabled(true);
				btnClear.setEnabled(true);

				activateRadiobuttons();
			}
		} else {
			System.out.println("No Selection ");
		}
	}

	public void deactivateRadioButtons() {
		sec30.setEnabled(false);
		sec60.setEnabled(false);
		sec90.setEnabled(false);
		min2.setEnabled(false);
		min5.setEnabled(false);
		min10.setEnabled(false);
	}

	public void activateRadiobuttons() {
		sec30.setEnabled(true);
		sec60.setEnabled(true);
		sec90.setEnabled(true);
		min2.setEnabled(true);
		min5.setEnabled(true);
		min10.setEnabled(true);
	}

	public void ImageDimensions(int wFrame, int hFrame, int wImage, int hImage) {
		double ratio, ratiopanel;
		if (wImage > hImage) {
			if (wImage > wFrame) {
				ratio = (double) wImage / (double) wFrame;

				hImage = (int) ((double) hImage / ratio);
			} else {
				ratio = (double) wFrame / (double) wImage;

				hImage = (int) ((double) hImage * ratio);
			}
			wImage = wFrame;

			if (hImage > hFrame) {
				ratiopanel = (double) hImage / (double) hFrame;
				hImage = hFrame;
				wImage = (int) ((double) wImage / ratiopanel);
			}
		} else {
			if (hImage > hFrame) {
				ratio = (double) hImage / (double) hFrame;

				wImage = (int) ((double) wImage / ratio);
			} else {
				ratio = (double) hFrame / (double) hImage;

				wImage = (int) ((double) wImage * ratio);
			}
			hImage = hFrame;
		}

		photo = image.getScaledInstance(wImage, hImage, Image.SCALE_SMOOTH);
	}

	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null),
				img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}

	public void showImage() {
		try {

			if (images_names.size() > 0) {
				if (randomImg.isSelected()) {
					Random generator = new Random();

					int i = generator.nextInt(images_names.size());

					listImages.setSelectedIndex(i);
					listImages.ensureIndexIsVisible(i);

					image = ImageIO.read(new File(images_names.get(i)));
				} else {
					if (indexDisplay >= images_names.size()) {
						indexDisplay = 0;
					}

					listImages.setSelectedIndex(indexDisplay);
					listImages.ensureIndexIsVisible(indexDisplay);

					image = ImageIO.read(new File(images_names
							.get(indexDisplay)));

					indexDisplay++;
				}

			} else {

				timerScreen.purge();
				timerScreen.cancel();

				updateLabelScreen(0);
			}

			ImageDimensions(getWidth(), getHeight(), image.getWidth(),
					image.getHeight());
			System.out.println(image.getHeight());

			panel.repaint();

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("PROBLEM..." + e.getMessage());
		}
	}

	public void showSelectedImage(int index) {
		if (index != -1) {
			try {
				image = ImageIO.read(new File(images_names.get(index)));

				timeScreen = timePoser;

				ImageDimensions(getWidth(), getHeight(), image.getWidth(),
						image.getHeight());

				panel.repaint();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class timeUpdate extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (timeScreen > 0) {
				timeScreen--;
			} else {
				showImage();
				timeScreen = timePoser;
			}
			updateLabelScreen(timeScreen);
		}

	}
}
