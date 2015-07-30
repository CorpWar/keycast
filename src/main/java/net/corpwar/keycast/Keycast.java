/**************************************************************************
 * CorpNet
 * Copyright (C) 2015 Daniel Ekedahl
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 **************************************************************************/
package net.corpwar.keycast;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.SwingDispatchService;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * corpwar-keycast
 * Created by Daniel Ekedahl on 2015-07-12.
 */
public class Keycast extends JFrame{


    private Settings settings;
    private DetectKeys detectKeys;
    private PressedKeysFrame pkf;
    private TrayIcon trayIcon;
    private SystemTray tray;
    private int posX=0,posY=0;
    private HashMap<String, ImageIcon> mouseImages = new HashMap<>();
    private JLabel mouseLabel;
    private JLabel keyLabel;
    private JLabel shift, ctrl, alt;
    private GridBagConstraints gbc = new GridBagConstraints();
    private Color backgroundColor = Color.LIGHT_GRAY;
    private final boolean isTranslucencySupported;

    public Keycast() {
        super("Corpwar Keycast");
        settings = new Settings(this);

        pkf = new PressedKeysFrame();
        // Set the event dispatcher to a swing safe executor service.
        GlobalScreen.setEventDispatcher(new SwingDispatchService());

        tray = SystemTray.getSystemTray();
        setLayout(new GridBagLayout());

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setShape(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.out.println("windowClosed");
                saveSettings();
                if (trayIcon != null) {
                    tray.remove(trayIcon);
                }
                e.getWindow().dispose();

                //Clean up the native hook.
                try {
                    GlobalScreen.unregisterNativeHook();
                } catch (NativeHookException e1) {
                    e1.printStackTrace();
                }
                System.runFinalization();
                System.exit(0);
            }
        });



        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                posX = e.getX();
                posY = e.getY();
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent evt) {
                //sets frame position when mouse dragged
                setLocation(evt.getXOnScreen() - posX, evt.getYOnScreen() - posY);
                pkf.setLocation(evt.getXOnScreen() - posX, evt.getYOnScreen() - posY - pkf.getHeight());
            }
        });

        addTrayIcon();
        initAllImages();
        initTextLabel();
        initGlobalListeners();

        setLocationRelativeTo(null);
        setUndecorated(true);
        setAlwaysOnTop(true);
        setSize(450, 90);
        this.getContentPane().setBackground(backgroundColor);
        pkf.setLocation(getX(), getY() - pkf.getHeight());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Determine what the GraphicsDevice can support.
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        isTranslucencySupported = gd.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT);
        setTransparent();

        loadSettings();

        // Display the window.
        setVisible(true);
    }

    public boolean isTranslucencySupported() {
        return isTranslucencySupported;
    }

    private void setTransparent() {
        //If shaped windows aren't supported, exit.
        // DON'T USING THIS AT THE MOMENT
//        if (!gd.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT)) {
//            System.err.println("Shaped windows are not supported");
//            System.exit(0);
//        }

        // Set the window default to 70% translucency, if supported.
        if (isTranslucencySupported) {
            setOpacity(0.7f);
        }
    }

    public PressedKeysFrame getPkf() {
        return pkf;
    }

    public DetectKeys getDetectKeys() {
        return detectKeys;
    }

    private void initTextLabel() {
        // Shift key
        shift = new JLabel();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.ipadx = 0;
        add(shift, gbc);

        // Ctrl key
        ctrl = new JLabel();
        gbc.gridx = 2;
        gbc.gridy = 0;
        add(ctrl, gbc);

        // Alt key
        alt = new JLabel();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.ipadx = 10;
        add(alt, gbc);

        keyLabel = new JLabel();
        keyLabel.setFont(new Font("Serif", Font.PLAIN, 40));
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(keyLabel, gbc);

    }

    private void initAllImages() {
        try {
            mouseImages.put("none", new ImageIcon(ImageIO.read(getClass().getResource("/images/mouse.png"))));
            mouseImages.put("L", new ImageIcon(ImageIO.read(getClass().getResource("/images/mouseL.png"))));
            mouseImages.put("M", new ImageIcon(ImageIO.read(getClass().getResource("/images/mouseM.png"))));
            mouseImages.put("R", new ImageIcon(ImageIO.read(getClass().getResource("/images/mouseR.png"))));
            mouseImages.put("LM", new ImageIcon(ImageIO.read(getClass().getResource("/images/mouseLM.png"))));
            mouseImages.put("LR", new ImageIcon(ImageIO.read(getClass().getResource("/images/mouseLR.png"))));
            mouseImages.put("MR", new ImageIcon(ImageIO.read(getClass().getResource("/images/mouseMR.png"))));
            mouseImages.put("LMR", new ImageIcon(ImageIO.read(getClass().getResource("/images/mouseLMR.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mouseLabel = new JLabel(mouseImages.get("none"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipadx = 30;
        add(mouseLabel, gbc);
    }

    private void initGlobalListeners() {
        // Initialze native hook.
        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
        detectKeys = new DetectKeys(this, keyLabel, shift, ctrl, alt, mouseLabel, mouseImages);
        GlobalScreen.addNativeKeyListener(detectKeys);
        GlobalScreen.addNativeMouseWheelListener(detectKeys);
        GlobalScreen.addNativeMouseListener(detectKeys);
    }

    private void addTrayIcon() {

        try {
            Image image = ImageIO.read(getClass().getResource("/images/mouseIcon.png"));

            if (SystemTray.isSupported()) {

                PopupMenu trayMenu = new PopupMenu();

                MenuItem settingsMenu = new MenuItem("Settings");
                settingsMenu.addActionListener(e1 -> {
                    settings.setSize(200, 200);
                    settings.setLocationRelativeTo(null);
                    settings.pack();
                    settings.setModalityType(Dialog.ModalityType.MODELESS);
                    settings.populateData();
                    settings.setVisible(true);
                });
                trayMenu.add(settingsMenu);
                MenuItem resetMenu = new MenuItem("Reset");
                resetMenu.addActionListener(e1 -> {
                    this.setVisible(true);
                    getPkf().setVisible(true);
                    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                    setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
                    pkf.setLocation((int) getLocation().getX(), (int) getLocation().getY() - pkf.getHeight());
                    if (isTranslucencySupported) {
                        setOpacity(0.7f);
                        getPkf().setOpacity(0.7f);
                    }
                    getContentPane().setBackground(Color.LIGHT_GRAY);
                    getPkf().setBackgroundColor(Color.LIGHT_GRAY);
                    getPkf().setTextColor(Color.BLACK);
                    getPkf().setTimeBeforeFade(2000);
                    getPkf().setTimeFading(1000);
                    saveSettings();
                });
                trayMenu.add(resetMenu);

                trayMenu.addSeparator();

                MenuItem closeItem = new MenuItem("Close");
                closeItem.addActionListener(e -> dispose());
                trayMenu.add(closeItem);

                trayIcon = new TrayIcon(image, "Corpwar keycast", trayMenu);
                trayIcon.setImageAutoSize(true);
                tray.add(trayIcon);

            }

            // Add icon on taskbar
            setIconImage(image);

        } catch (IOException e) {
            e.printStackTrace();
        }  catch (AWTException e) {
            System.err.println("TrayIcon could not be added.");
        }


    }

    public void loadSettings() {
        Properties props = new Properties();
        InputStream is = null;
        try {
            File f = new File("keycast.properties");
            if (f.exists()) {
                is = new FileInputStream(f);
                props.load(is);
                setLocation(new Integer(props.getProperty("windowX", "0")), new Integer(props.getProperty("windowY", "0")));
                pkf.setLocation((int) getLocation().getX(), (int) getLocation().getY() - pkf.getHeight());
                if (isTranslucencySupported) {
                    setOpacity(new Float(props.getProperty("opacity", "0.7")));
                    getPkf().opacityValue = new Float(props.getProperty("keyWinOpacity", "0.7"));
                }
                getPkf().setTimeBeforeFade(new Long(props.getProperty("timeBeforeFade", "2000")));
                getPkf().setTimeFading(new Long(props.getProperty("timeFading", "1000")));
                getContentPane().setBackground(new Color(new Integer(props.getProperty("mainWinColor", "" + Color.LIGHT_GRAY))));
                getPkf().setBackgroundColor(new Color(new Integer(props.getProperty("keyWinColor", "" + Color.LIGHT_GRAY))));
                getPkf().setTextColor(new Color(new Integer(props.getProperty("textColor", "" + Color.BLACK.getRGB()))));
                getPkf().setAmountWindows(Integer.valueOf(props.getProperty("amountHistoryWindows", "3")));
                getDetectKeys().setIsShowMouseEventText(Boolean.valueOf(props.getProperty("showMouseEventText", "false")));
                getDetectKeys().setShowRealChars(Boolean.valueOf(props.getProperty("showRealChars", "false")));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {

                }
            }
        }
    }

    public void saveSettings() {
        try {
            Properties props = new Properties();
            props.setProperty("windowX", "" + this.getLocation().x);
            props.setProperty("windowY", "" + this.getLocation().y);
            props.setProperty("opacity", "" + this.getOpacity());
            props.setProperty("keyWinOpacity", "" + this.getPkf().opacityValue);
            props.setProperty("timeBeforeFade", "" + this.getPkf().getTimeBeforeFade());
            props.setProperty("timeFading", "" + this.getPkf().getTimeFading());
            props.setProperty("mainWinColor", "" + this.getContentPane().getBackground().getRGB());
            props.setProperty("keyWinColor", "" + this.getPkf().getContentPane().getBackground().getRGB());
            props.setProperty("textColor", "" + this.getPkf().getTextColor().getRGB());
            props.setProperty("amountHistoryWindows", "" + this.getPkf().getAmountWindows());
            props.setProperty("showMouseEventText", "" + this.getDetectKeys().isShowMouseEventText());
            props.setProperty("showRealChars", "" + this.getDetectKeys().isShowRealChars());
            File f = new File("keycast.properties");
            OutputStream out = new FileOutputStream( f );
            props.store(out, "Settings for Corpwar Keycast");
            out.close();
        }
        catch (Exception e ) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);

        // Create the GUI on the event-dispatching thread
        SwingUtilities.invokeLater(Keycast::new);
    }
}
