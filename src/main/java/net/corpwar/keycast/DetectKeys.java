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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * corpwar-keycast
 * Created by Daniel Ekedahl on 2015-07-12.
 */
public class DetectKeys implements NativeKeyListener {

    private JLabel keyPressed, shiftLbl, ctrlLbl, altLbl;
    private boolean ctrl = false, alt = false, shift = false;
    private HashMap<String, ImageIcon> keyImages = new HashMap<>();
    private List<Integer> acceptedKeys = new ArrayList<>();

    public DetectKeys(JLabel keyPressed, JLabel shiftLbl, JLabel ctrlLbl, JLabel altLbl) {
        this.keyPressed = keyPressed;
        this.shiftLbl = shiftLbl;
        this.ctrlLbl = ctrlLbl;
        this.altLbl = altLbl;
        addAccptedKeys();
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
        if (acceptedKeys.contains(nativeKeyEvent.getKeyCode())) {
            keyPressed.setText(NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()));
        }

        if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_SHIFT_L || nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_SHIFT_R) {
            shift = true;
        }
        if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_ALT_L || nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_ALT_R) {
            alt = true;
        }
        if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_CONTROL_L || nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_CONTROL_R) {
            ctrl = true;
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
        changeActionKeys();
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
        // not use
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
}
