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
import java.util.*;

/**
 * corpwar-keycast
 * Created by Ghost on 2015-07-18.
 */
public class PressedKeysFrame extends JDialog implements Runnable {

    public final boolean isTranslucencySupported;
    public float opacityValue = 0.7f;

    private final long sleepTime = 20;

    private Thread frame;
    private long deltaTime;
    private long lastTime;
    private boolean running = true;

    private GridBagConstraints gbc = new GridBagConstraints();

    // Time in milliseconds
    private long timeBeforeFade = 2000;

    // Time in milliseconds
    private long timeFading = 1000;
    private long timeSinceLastChange = 0;

    // If keep adding chars to string, if starting to fade new string will start
    private boolean keepAddingChars = true;

    private Color backgroundColor = Color.LIGHT_GRAY;

    private JLabel text;
    private String lastText;
    private Integer sameText = 1;

    // How many windows to show
    private int amountWindows = 3;

    private Queue<OldTextDialog> oldTextDialogList = new LinkedList<>();

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
            this.setOpacity(opacityValue);
        }

        text = new JLabel();
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

    public long getTimeBeforeFade() {
        return timeBeforeFade;
    }

    public void setTimeBeforeFade(long timeBeforeFade) {
        this.timeBeforeFade = timeBeforeFade;
    }

    public long getTimeFading() {
        return timeFading;
    }

    public void setTimeFading(long timeFading) {
        this.timeFading = timeFading;
    }

    public void setTextColor(Color textColor) {
        text.setForeground(textColor);
    }

    public Color getTextColor() {
        return text.getForeground();
    }

    public int getAmountWindows() {
        return amountWindows;
    }

    public void setAmountWindows(int amountWindows) {
        this.amountWindows = amountWindows;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        this.getContentPane().setBackground(backgroundColor);
    }

    public void setKeyText(String keyText) {
        if (keyText.equals(lastText) && isKeepAddingKeys()) {
            sameText++;
            text.setText(keyText + " x" + sameText);
        } else {
            if (isKeepAddingKeys() && isVisible()) {
                OldTextDialog oldTextDialog = new OldTextDialog(this, sameText > 1 ? lastText + " x" + sameText : lastText, timeBeforeFade, timeFading, opacityValue, backgroundColor, text.getForeground());
                oldTextDialog.setLocation(getX(), getY() - oldTextDialog.getHeight());
                if (oldTextDialogList.size() >= amountWindows) {
                    OldTextDialog tempOldDialog = oldTextDialogList.poll();
                    tempOldDialog.dispose();
                }
                for (OldTextDialog tempOldTextDialog : oldTextDialogList) {
                    tempOldTextDialog.setLocation(tempOldTextDialog.getX(), tempOldTextDialog.getY() - tempOldTextDialog.getHeight());
                }
                oldTextDialogList.add(oldTextDialog);
            }
            lastText = keyText;
            sameText = 1;
            text.setText(keyText);
        }
        setOpacity(opacityValue);
        timeSinceLastChange = 0;
    }

    public void addCharToText(String charToAdd) {
        lastText = charToAdd;
        sameText = 1;
        text.setText(charToAdd);
        setOpacity(opacityValue);
        timeSinceLastChange = 0;
    }

    public void dispose() {
        running = false;
        frame.interrupt();
    }

    public boolean isKeepAddingKeys() {
        return timeSinceLastChange < timeBeforeFade;
    }

    public void removeOldWindowFromList(OldTextDialog oldTextDialog) {
        if (oldTextDialogList.contains(oldTextDialog)) {
            oldTextDialogList.remove(oldTextDialog);
        }
    }

    @Override
    public void run() {
        lastTime = System.currentTimeMillis();

        while (running) {
            deltaTime = System.currentTimeMillis() - lastTime;

            if (deltaTime >= sleepTime) {
                timeSinceLastChange += deltaTime;
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
                if (timeSinceLastChange > timeBeforeFade) {
                    float opacity = getOpacity() - (deltaTime / (float)timeFading);
                    if (opacity < 0) {
                        setOpacity(0);
                    } else {
                        setOpacity(opacity);
                    }
                }
            }
        }
    }
}
