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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;

/**
 * corpwar-keycast
 * Created by Ghost on 2015-07-24.
 */
public class OldTextDialog extends JDialog implements Runnable {

    public final boolean isTranslucencySupported;

    private GridBagConstraints gbc = new GridBagConstraints();
    private JLabel text;

    private PressedKeysFrame pkf;

    private Thread frame;
    private long deltaTime;
    private long totalTime = 0;
    private long lastTime;
    private long sleepTime = 20;

    // Time in milliseconds
    private long timeBeforeFade = 2000;

    // Time in milliseconds
    private long timeFading = 1000;

    public OldTextDialog(PressedKeysFrame pkf, String oldText, long timeBeforeFade, long timeFading, float opacityValue, Color backgroundColor, Color textColor) {
        super();
        this.pkf = pkf;
        this.timeBeforeFade = timeBeforeFade;
        this.timeFading = timeFading;

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
            this.setOpacity(opacityValue);
        }

        text = new JLabel(oldText);
        text.setForeground(textColor);
        text.setFont(new Font("Serif", Font.PLAIN, 40));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(text,gbc);

        this.getContentPane().setBackground(backgroundColor);

        // Display the window.
        setVisible(true);

        frame = new Thread(this);
        frame.start();
    }

    @Override
    public void run() {
        lastTime = System.currentTimeMillis();

        while (true) {
            deltaTime = System.currentTimeMillis() - lastTime;

            if (deltaTime >= sleepTime) {
                totalTime += deltaTime;
                if (isTranslucencySupported) {
                    if (getOpacity() > 0) {
                        if (totalTime > timeBeforeFade) {
                            float opacity = getOpacity() - (deltaTime / (float)timeFading);
                            if (opacity < 0) {
                                break;
                            } else {
                                setOpacity(opacity);
                            }
                        }
                    }
                }
            }

            lastTime = System.currentTimeMillis();
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        pkf.removeOldWindowFromList(this);
        dispose();
    }
}
