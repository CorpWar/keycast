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

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.util.*;

/**
 * corpwar-keycast
 * Created by Daniel Ekedahl on 2015-07-12.
 */
public class DetectKeys implements NativeKeyListener, NativeMouseWheelListener {

    private Keycast keycast;
    private JLabel keyPressed, shiftLbl, ctrlLbl, altLbl;
    private boolean ctrl = false, alt = false, shift = false;
    private HashMap<String, ImageIcon> keyImages = new HashMap<>();
    private List<Integer> acceptedKeys = new ArrayList<>();
    private List<Integer> charKeys = new ArrayList<>();
    private StringBuilder sb;
    private String lastStringChars = "";
    private boolean isTyping = true;

    public DetectKeys(Keycast keycast, JLabel keyPressed, JLabel shiftLbl, JLabel ctrlLbl, JLabel altLbl) {
        this.keyPressed = keyPressed;
        this.shiftLbl = shiftLbl;
        this.ctrlLbl = ctrlLbl;
        this.altLbl = altLbl;
        this.keycast = keycast;
        sb = new StringBuilder();
        addAccptedKeys();
        addCharKeys();
        initKeyImages();
    }

    private void addAccptedKeys() {
        // 0-9
        acceptedKeys.add(2);
        acceptedKeys.add(3);
        acceptedKeys.add(4);
        acceptedKeys.add(5);
        acceptedKeys.add(6);
        acceptedKeys.add(7);
        acceptedKeys.add(8);
        acceptedKeys.add(9);
        acceptedKeys.add(10);
        acceptedKeys.add(11);

        // A-Z
        acceptedKeys.add(30);
        acceptedKeys.add(48);
        acceptedKeys.add(46);
        acceptedKeys.add(32);
        acceptedKeys.add(18);
        acceptedKeys.add(33);
        acceptedKeys.add(34);
        acceptedKeys.add(35);
        acceptedKeys.add(23);
        acceptedKeys.add(36);
        acceptedKeys.add(37);
        acceptedKeys.add(38);
        acceptedKeys.add(50);
        acceptedKeys.add(49);
        acceptedKeys.add(24);
        acceptedKeys.add(25);
        acceptedKeys.add(16);
        acceptedKeys.add(19);
        acceptedKeys.add(31);
        acceptedKeys.add(20);
        acceptedKeys.add(22);
        acceptedKeys.add(47);
        acceptedKeys.add(17);
        acceptedKeys.add(45);
        acceptedKeys.add(21);
        acceptedKeys.add(44);

        // F1-F10
        acceptedKeys.add(59);
        acceptedKeys.add(60);
        acceptedKeys.add(61);
        acceptedKeys.add(62);
        acceptedKeys.add(63);
        acceptedKeys.add(64);
        acceptedKeys.add(65);
        acceptedKeys.add(66);
        acceptedKeys.add(67);
        acceptedKeys.add(68);
        acceptedKeys.add(87);
        acceptedKeys.add(88);

        // Esc
        acceptedKeys.add(1);

        // Enter
        acceptedKeys.add(28);

        // NumPad keys
        acceptedKeys.add(71);
        acceptedKeys.add(72);
        acceptedKeys.add(73);
        acceptedKeys.add(75);
        acceptedKeys.add(76);
        acceptedKeys.add(77);
        acceptedKeys.add(79);
        acceptedKeys.add(80);
        acceptedKeys.add(81);
        acceptedKeys.add(82);

        // Extra keys
        acceptedKeys.add(NativeKeyEvent.VC_DELETE);
        acceptedKeys.add(NativeKeyEvent.VC_INSERT);
        acceptedKeys.add(NativeKeyEvent.VC_HOME);
        acceptedKeys.add(NativeKeyEvent.VC_END);
        acceptedKeys.add(NativeKeyEvent.VC_PAGE_UP);
        acceptedKeys.add(NativeKeyEvent.VC_PAGE_DOWN);
        acceptedKeys.add(NativeKeyEvent.VC_ESCAPE);
        acceptedKeys.add(NativeKeyEvent.VC_SPACE);
        acceptedKeys.add(NativeKeyEvent.VC_TAB);
        acceptedKeys.add(NativeKeyEvent.VC_BACKSPACE);
    }

    private void addCharKeys() {
        // 0-9
        charKeys.add(2);
        charKeys.add(3);
        charKeys.add(4);
        charKeys.add(5);
        charKeys.add(6);
        charKeys.add(7);
        charKeys.add(8);
        charKeys.add(9);
        charKeys.add(10);
        charKeys.add(11);

        // A-Z
        charKeys.add(30);
        charKeys.add(48);
        charKeys.add(46);
        charKeys.add(32);
        charKeys.add(18);
        charKeys.add(33);
        charKeys.add(34);
        charKeys.add(35);
        charKeys.add(23);
        charKeys.add(36);
        charKeys.add(37);
        charKeys.add(38);
        charKeys.add(50);
        charKeys.add(49);
        charKeys.add(24);
        charKeys.add(25);
        charKeys.add(16);
        charKeys.add(19);
        charKeys.add(31);
        charKeys.add(20);
        charKeys.add(22);
        charKeys.add(47);
        charKeys.add(17);
        charKeys.add(45);
        charKeys.add(21);
        charKeys.add(44);
    }

    private void initKeyImages() {
        try {
            keyImages.put("shiftUp", new ImageIcon(ImageIO.read(getClass().getResource("/images/shiftUp.png"))));
            keyImages.put("shiftDown", new ImageIcon(ImageIO.read(getClass().getResource("/images/shiftDown.png"))));
            keyImages.put("ctrlUp", new ImageIcon(ImageIO.read(getClass().getResource("/images/ctrlUp.png"))));
            keyImages.put("ctrlDown", new ImageIcon(ImageIO.read(getClass().getResource("/images/ctrlDown.png"))));
            keyImages.put("altUp", new ImageIcon(ImageIO.read(getClass().getResource("/images/altUp.png"))));
            keyImages.put("altDown", new ImageIcon(ImageIO.read(getClass().getResource("/images/altDown.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        shiftLbl.setIcon(keyImages.get("shiftUp"));
        ctrlLbl.setIcon(keyImages.get("ctrlUp"));
        altLbl.setIcon(keyImages.get("altUp"));
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_SHIFT_L || nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_SHIFT_R) {
            shift = true;
        }
        if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_ALT_L || nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_ALT_R) {
            alt = true;
        }
        if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_CONTROL_L || nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_CONTROL_R) {
            ctrl = true;
        }

        if (isTyping && keycast.getPkf().isKeepAddingKeys() && !alt && !ctrl && charKeys.contains(nativeKeyEvent.getKeyCode())) {
            String keyPress = NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode());
            lastStringChars = lastStringChars + keyPress;
            keycast.getPkf().addCharToText("< " + lastStringChars + " >");
            keyPressed.setText(keyPress);
            return;
        }

        if (acceptedKeys.contains(nativeKeyEvent.getKeyCode())) {
            String keyPressShow = "<";
            String keyPress = NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode());
            keyPressed.setText(keyPress);
            if (shift) {
                keyPressShow = keyPressShow + " Shift +";
            }
            if (alt) {
                keyPressShow = keyPressShow + " Alt +";
            }
            if (ctrl) {
                keyPressShow = keyPressShow + " Ctrl +";
            }
            if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_DELETE) {
                keycast.getPkf().setKeyText(keyPressShow + " Del >");
            } else if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_INSERT) {
                keycast.getPkf().setKeyText(keyPressShow + " Insert >");
            } else if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_HOME) {
                keycast.getPkf().setKeyText(keyPressShow + " Home >");
            } else if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_END) {
                keycast.getPkf().setKeyText(keyPressShow + " End >");
            } else if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_PAGE_UP) {
                keycast.getPkf().setKeyText(keyPressShow + " Page Up >");
            } else if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_PAGE_DOWN) {
                keycast.getPkf().setKeyText(keyPressShow + " Page Down >");
            } else if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
                keycast.getPkf().setKeyText(keyPressShow + " Esc >");
            } else if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_SPACE) {
                keycast.getPkf().setKeyText(keyPressShow + " SpaceBar >");
            } else if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_TAB) {
                keycast.getPkf().setKeyText(keyPressShow + " Tab >");
            } else if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_BACKSPACE) {
                keycast.getPkf().setKeyText(keyPressShow + " Backspace >");
            } else {
                keycast.getPkf().setKeyText(keyPressShow + " " + keyPress + " >");
            }
            if (!shift && !alt && !ctrl && charKeys.contains(nativeKeyEvent.getKeyCode())) {
                lastStringChars = keyPress;
                isTyping = true;
            } else {
                lastStringChars = "";
                isTyping = false;
            }
        }

        changeActionKeys();
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

        if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_SHIFT_L || nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_SHIFT_R) {
            shift = false;
        }
        if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_ALT_L || nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_ALT_R) {
            alt = false;
        }
        if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_CONTROL_L || nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_CONTROL_R) {
            ctrl = false;
        }
        keyPressed.setText("");
        changeActionKeys();
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }

    private void changeActionKeys() {
        if (shift) {
            shiftLbl.setIcon(keyImages.get("shiftDown"));
        } else {
            shiftLbl.setIcon(keyImages.get("shiftUp"));
        }
        if (ctrl) {
            ctrlLbl.setIcon(keyImages.get("ctrlDown"));
        } else {
            ctrlLbl.setIcon(keyImages.get("ctrlUp"));
        }
        if (alt) {
            altLbl.setIcon(keyImages.get("altDown"));
        } else {
            altLbl.setIcon(keyImages.get("altUp"));
        }

    }

    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent nativeMouseWheelEvent) {
        String keyPressShow = "<";
        if (shift) {
            keyPressShow = keyPressShow + " Shift +";
        }
        if (alt) {
            keyPressShow = keyPressShow + " Alt +";
        }
        if (ctrl) {
            keyPressShow = keyPressShow + " Ctrl +";
        }
        if (nativeMouseWheelEvent.getWheelRotation() < 0) {
            keycast.getPkf().setKeyText(keyPressShow + " Wheel Up >");
        } else if (nativeMouseWheelEvent.getWheelRotation() > 0) {
            keycast.getPkf().setKeyText(keyPressShow + " Wheel Down >");
        }


    }
}
