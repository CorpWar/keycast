package net.corpwar.keycast;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;

/**
 * corpwar-keycast
 * Created by Ghost on 2015-07-18.
 */
public class PressedKeysFrame extends JFrame implements Runnable {

    private Thread frame;
    private long deltaTime;
    private long lastTime;
    private boolean running = true;
    private long sleepTime = 20;
    private final boolean isTranslucencySupported;
    private GridBagConstraints gbc = new GridBagConstraints();

    private JLabel text;

    public PressedKeysFrame() {
        super();
        setSize(450, 60);
        setLayout(new GridBagLayout());

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setShape(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
            }
        });
        setUndecorated(true);
        setAlwaysOnTop(true);

        // Determine what the GraphicsDevice can support.
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        isTranslucencySupported = gd.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT);
        // Set the window to 70% translucency, if supported.
        if (isTranslucencySupported) {
            this.setOpacity(0.7f);
        }

        text = new JLabel();
        text.setFont(new Font("Serif", Font.PLAIN, 40));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(text,gbc);

        // Display the window.
        setVisible(true);

        frame = new Thread(this);
        frame.start();
    }

    public void setKeyText(String keyText) {
        text.setText(keyText);
        setOpacity(0.7f);
    }

    public void dispose() {
        running = false;
        frame.interrupt();
    }

    @Override
    public void run() {
        lastTime = System.currentTimeMillis();

        while (running) {
            deltaTime = System.currentTimeMillis() - lastTime;

            if (deltaTime >= sleepTime) {
                doUpdate(deltaTime);
            }

            lastTime = System.currentTimeMillis();
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void doUpdate(long deltaTime) {
        if (isTranslucencySupported) {
            if (getOpacity() > 0) {
                float opacity = getOpacity() - (deltaTime / 5000f);

                if (opacity < 0) {
                    setOpacity(0);
                } else {
                    setOpacity(opacity);
                }
            }
        }

    }
}
