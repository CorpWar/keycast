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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

public class Settings extends JDialog {
    private Keycast keycast;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField txtBeforeFade;
    private JTextField txtFading;
    private JCheckBox showMainWindowCheckBox;
    private JCheckBox showTypedKeyWindowCheckBox;
    private JSlider slidMainWin;
    private JSlider slidKeyWin;
    private JButton btnMainWinColor;
    private JButton btnKeyWinColor;

    public Settings(Keycast keycast) {
        this.keycast = keycast;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        populateData();

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        txtBeforeFade.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keycast.getPkf().setTimeBeforeFade(Long.valueOf(txtBeforeFade.getText()));
            }
        });
        txtFading.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keycast.getPkf().setTimeFading(Long.valueOf(txtFading.getText()));
            }
        });

        showMainWindowCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keycast.setVisible(!keycast.isVisible());
            }
        });
        showTypedKeyWindowCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keycast.getPkf().setVisible(!keycast.getPkf().isVisible());
            }
        });

        slidMainWin.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                if (!source.getValueIsAdjusting()) {
                    keycast.setOpacity(slidMainWin.getValue() / 100f);
                }
            }
        });
        slidKeyWin.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                if (!source.getValueIsAdjusting()) {
                    keycast.getPkf().opacityValue  = (slidKeyWin.getValue() / 100f);
                }
            }
        });


        btnMainWinColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color c = JColorChooser.showDialog(null, "Choose a Color", getForeground());
                if (c != null) {
                    keycast.getContentPane().setBackground(c);
                }
            }
        });

        btnKeyWinColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color c = JColorChooser.showDialog(null, "Choose a Color", getForeground());
                if (c != null) {
                    keycast.getPkf().getContentPane().setBackground(c);
                }
            }
        });
    }



    private void populateData() {
        txtBeforeFade.setText(Long.toString(keycast.getPkf().getTimeBeforeFade()));
        txtFading.setText(Long.toString(keycast.getPkf().getTimeFading()));
        showMainWindowCheckBox.setEnabled(keycast.isVisible());
        showTypedKeyWindowCheckBox.setEnabled(keycast.getPkf().isVisible());
        if (keycast.isTranslucencySupported()) {
            slidMainWin.setEnabled(true);
            slidMainWin.setValue(Math.round(keycast.getOpacity() * 100));
        } else {
            slidMainWin.setEnabled(false);
        }
        if (keycast.getPkf().isTranslucencySupported) {
            slidKeyWin.setEnabled(true);
            slidKeyWin.setValue(Math.round(keycast.getPkf().opacityValue * 100));
        } else {
            slidKeyWin.setEnabled(false);
        }
    }

    private void onOK() {
        keycast.saveSettings();
        dispose();
    }

    private void onCancel() {
        keycast.loadSettings();
        dispose();
    }
}
