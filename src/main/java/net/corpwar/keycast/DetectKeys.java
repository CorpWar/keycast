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
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;
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
public class DetectKeys implements NativeKeyListener, NativeMouseWheelListener, NativeMouseListener {

    private Keycast keycast;
    private JLabel keyPressed, shiftLbl, ctrlLbl, altLbl;
    private boolean ctrl = false, alt = false, shift = false;
    private HashMap<String, ImageIcon> keyImages = new HashMap<>();
    private List<Integer> acceptedKeys = new ArrayList<>();
    private List<Integer> charKeys = new ArrayList<>();
    private List<Integer> extraKeys = new ArrayList<>();
    private StringBuilder sb;
    private String lastStringChars = "";
    private boolean isTyping = true;
    private boolean isShowRealChars = true;
    private boolean isExtraKeyPressed = true;

    // Mouse events
    private boolean isShowMouseEventText = true;
    private JLabel mouseIcon;
    private HashMap<String, ImageIcon> mouseImages;
    private boolean L = false, M = false, R = false;

    public DetectKeys(Keycast keycast, JLabel keyPressed, JLabel shiftLbl, JLabel ctrlLbl, JLabel altLbl, JLabel mouseIcon, HashMap<String, ImageIcon> mouseImages) {
        this.keyPressed = keyPressed;
        this.shiftLbl = shiftLbl;
        this.ctrlLbl = ctrlLbl;
        this.altLbl = altLbl;
        this.keycast = keycast;
        this.mouseIcon = mouseIcon;
        this.mouseImages = mouseImages;
        sb = new StringBuilder();
        addAccptedKeys();
        addCharKeys();
        addExtraKeys();
        initKeyImages();
    }

    public boolean isShowMouseEventText() {
        return isShowMouseEventText;
    }

    public void setIsShowMouseEventText(boolean isShowMouseEventText) {
        this.isShowMouseEventText = isShowMouseEventText;
    }

    public boolean isShowRealChars() {
        return isShowRealChars;
    }

    public void setShowRealChars(boolean showRealChars) {
        this.isShowRealChars = showRealChars;
    }

    private void addAccptedKeys() {
        // 0-9
        acceptedKeys.add(NativeKeyEvent.VC_0);
        acceptedKeys.add(NativeKeyEvent.VC_1);
        acceptedKeys.add(NativeKeyEvent.VC_2);
        acceptedKeys.add(NativeKeyEvent.VC_3);
        acceptedKeys.add(NativeKeyEvent.VC_4);
        acceptedKeys.add(NativeKeyEvent.VC_5);
        acceptedKeys.add(NativeKeyEvent.VC_6);
        acceptedKeys.add(NativeKeyEvent.VC_7);
        acceptedKeys.add(NativeKeyEvent.VC_8);
        acceptedKeys.add(NativeKeyEvent.VC_9);

        // A-Z
        acceptedKeys.add(NativeKeyEvent.VC_A);
        acceptedKeys.add(NativeKeyEvent.VC_B);
        acceptedKeys.add(NativeKeyEvent.VC_C);
        acceptedKeys.add(NativeKeyEvent.VC_D);
        acceptedKeys.add(NativeKeyEvent.VC_E);
        acceptedKeys.add(NativeKeyEvent.VC_F);
        acceptedKeys.add(NativeKeyEvent.VC_G);
        acceptedKeys.add(NativeKeyEvent.VC_H);
        acceptedKeys.add(NativeKeyEvent.VC_I);
        acceptedKeys.add(NativeKeyEvent.VC_J);
        acceptedKeys.add(NativeKeyEvent.VC_K);
        acceptedKeys.add(NativeKeyEvent.VC_L);
        acceptedKeys.add(NativeKeyEvent.VC_M);
        acceptedKeys.add(NativeKeyEvent.VC_N);
        acceptedKeys.add(NativeKeyEvent.VC_O);
        acceptedKeys.add(NativeKeyEvent.VC_P);
        acceptedKeys.add(NativeKeyEvent.VC_Q);
        acceptedKeys.add(NativeKeyEvent.VC_R);
        acceptedKeys.add(NativeKeyEvent.VC_S);
        acceptedKeys.add(NativeKeyEvent.VC_T);
        acceptedKeys.add(NativeKeyEvent.VC_U);
        acceptedKeys.add(NativeKeyEvent.VC_V);
        acceptedKeys.add(NativeKeyEvent.VC_W);
        acceptedKeys.add(NativeKeyEvent.VC_X);
        acceptedKeys.add(NativeKeyEvent.VC_Y);
        acceptedKeys.add(NativeKeyEvent.VC_Z);

        // F1-F10
        acceptedKeys.add(NativeKeyEvent.VC_F1);
        acceptedKeys.add(NativeKeyEvent.VC_F2);
        acceptedKeys.add(NativeKeyEvent.VC_F3);
        acceptedKeys.add(NativeKeyEvent.VC_F4);
        acceptedKeys.add(NativeKeyEvent.VC_F5);
        acceptedKeys.add(NativeKeyEvent.VC_F6);
        acceptedKeys.add(NativeKeyEvent.VC_F7);
        acceptedKeys.add(NativeKeyEvent.VC_F8);
        acceptedKeys.add(NativeKeyEvent.VC_F9);
        acceptedKeys.add(NativeKeyEvent.VC_F10);
        acceptedKeys.add(NativeKeyEvent.VC_F11);
        acceptedKeys.add(NativeKeyEvent.VC_F12);

        // NumPad keys
        acceptedKeys.add(NativeKeyEvent.VC_KP_0);
        acceptedKeys.add(NativeKeyEvent.VC_KP_1);
        acceptedKeys.add(NativeKeyEvent.VC_KP_2);
        acceptedKeys.add(NativeKeyEvent.VC_KP_3);
        acceptedKeys.add(NativeKeyEvent.VC_KP_4);
        acceptedKeys.add(NativeKeyEvent.VC_KP_5);
        acceptedKeys.add(NativeKeyEvent.VC_KP_6);
        acceptedKeys.add(NativeKeyEvent.VC_KP_7);
        acceptedKeys.add(NativeKeyEvent.VC_KP_8);
        acceptedKeys.add(NativeKeyEvent.VC_KP_9);

        // Minus, plus, divided, multiplication
        acceptedKeys.add(NativeKeyEvent.VC_KP_SUBTRACT);
        acceptedKeys.add(NativeKeyEvent.VC_KP_ADD);
        acceptedKeys.add(NativeKeyEvent.VC_KP_DIVIDE);
        acceptedKeys.add(NativeKeyEvent.VC_KP_MULTIPLY);

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
        // Esc
        acceptedKeys.add(NativeKeyEvent.VC_ESCAPE);

        // Enter
        acceptedKeys.add(NativeKeyEvent.VC_ENTER);
    }

    private void addCharKeys() {
        // 0-9
        charKeys.add(NativeKeyEvent.VC_0);
        charKeys.add(NativeKeyEvent.VC_1);
        charKeys.add(NativeKeyEvent.VC_2);
        charKeys.add(NativeKeyEvent.VC_3);
        charKeys.add(NativeKeyEvent.VC_4);
        charKeys.add(NativeKeyEvent.VC_5);
        charKeys.add(NativeKeyEvent.VC_6);
        charKeys.add(NativeKeyEvent.VC_7);
        charKeys.add(NativeKeyEvent.VC_8);
        charKeys.add(NativeKeyEvent.VC_9);

        // A-Z
        charKeys.add(NativeKeyEvent.VC_A);
        charKeys.add(NativeKeyEvent.VC_B);
        charKeys.add(NativeKeyEvent.VC_C);
        charKeys.add(NativeKeyEvent.VC_D);
        charKeys.add(NativeKeyEvent.VC_E);
        charKeys.add(NativeKeyEvent.VC_F);
        charKeys.add(NativeKeyEvent.VC_G);
        charKeys.add(NativeKeyEvent.VC_H);
        charKeys.add(NativeKeyEvent.VC_I);
        charKeys.add(NativeKeyEvent.VC_J);
        charKeys.add(NativeKeyEvent.VC_K);
        charKeys.add(NativeKeyEvent.VC_L);
        charKeys.add(NativeKeyEvent.VC_M);
        charKeys.add(NativeKeyEvent.VC_N);
        charKeys.add(NativeKeyEvent.VC_O);
        charKeys.add(NativeKeyEvent.VC_P);
        charKeys.add(NativeKeyEvent.VC_Q);
        charKeys.add(NativeKeyEvent.VC_R);
        charKeys.add(NativeKeyEvent.VC_S);
        charKeys.add(NativeKeyEvent.VC_T);
        charKeys.add(NativeKeyEvent.VC_U);
        charKeys.add(NativeKeyEvent.VC_V);
        charKeys.add(NativeKeyEvent.VC_W);
        charKeys.add(NativeKeyEvent.VC_X);
        charKeys.add(NativeKeyEvent.VC_Y);
        charKeys.add(NativeKeyEvent.VC_Z);
    }

    private void addExtraKeys() {
        // Extra keys
        extraKeys.add(NativeKeyEvent.VC_DELETE);
        extraKeys.add(NativeKeyEvent.VC_INSERT);
        extraKeys.add(NativeKeyEvent.VC_HOME);
        extraKeys.add(NativeKeyEvent.VC_END);
        extraKeys.add(NativeKeyEvent.VC_PAGE_UP);
        extraKeys.add(NativeKeyEvent.VC_PAGE_DOWN);
        extraKeys.add(NativeKeyEvent.VC_ESCAPE);
        extraKeys.add(NativeKeyEvent.VC_SPACE);
        extraKeys.add(NativeKeyEvent.VC_TAB);
        extraKeys.add(NativeKeyEvent.VC_BACKSPACE);
        // Esc
        extraKeys.add(NativeKeyEvent.VC_ESCAPE);

        // Enter
        extraKeys.add(NativeKeyEvent.VC_ENTER);

        // Minus, plus, divided, multiplication
        extraKeys.add(NativeKeyEvent.VC_KP_SUBTRACT);
        extraKeys.add(NativeKeyEvent.VC_KP_ADD);
        extraKeys.add(NativeKeyEvent.VC_KP_DIVIDE);
        extraKeys.add(NativeKeyEvent.VC_KP_MULTIPLY);

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

        String keyPressShow = "<";
        if (!isShowRealChars || extraKeys.contains(nativeKeyEvent.getKeyCode())) {
            if (isTyping && keycast.getPkf().isKeepAddingKeys() && !shift && !alt && !ctrl && charKeys.contains(nativeKeyEvent.getKeyCode())) {
                String keyPress = NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode());
                lastStringChars = lastStringChars + keyPress;
                keycast.getPkf().addCharToText("< " + lastStringChars + " >");
                keyPressed.setText(keyPress);
                return;
            }

            String keyPress = NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode());
            if (!shift && !alt && !ctrl) {
                keyPressed.setText(keyPress);
            }
            if (shift) {
                keyPressShow = keyPressShow + " Shift +";
            }
            if (alt) {
                keyPressShow = keyPressShow + " Alt +";
            }
            if (ctrl) {
                keyPressShow = keyPressShow + " Ctrl +";
            }
            if (acceptedKeys.contains(nativeKeyEvent.getKeyCode()) && !extraKeys.contains(nativeKeyEvent.getKeyCode())) {
                keycast.getPkf().setKeepAddingChars(false);
                keycast.getPkf().setKeyText(keyPressShow + " " + keyPress + " >");
                if (!shift && !alt && !ctrl && charKeys.contains(nativeKeyEvent.getKeyCode())) {
                    lastStringChars = keyPress;
                    isTyping = true;
                    keycast.getPkf().setKeepAddingChars(true);
                } else {
                    lastStringChars = "";
                    isTyping = false;
                }
            }
        }
        if (extraKeys.contains(nativeKeyEvent.getKeyCode())) {
            keycast.getPkf().setKeepAddingChars(false);
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
            } else if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_ENTER) {
                keycast.getPkf().setKeyText(keyPressShow + " Enter >");
            } else if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_KP_SUBTRACT) {
                keycast.getPkf().setKeyText(keyPressShow + " - >");
            } else if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_KP_ADD) {
                keycast.getPkf().setKeyText(keyPressShow + " + >");
            } else if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_KP_DIVIDE) {
                keycast.getPkf().setKeyText(keyPressShow + " / >");
            } else if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_KP_MULTIPLY) {
                keycast.getPkf().setKeyText(keyPressShow + " * >");
            }
            isExtraKeyPressed = true;
        } else {
            isExtraKeyPressed = false;
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
        if (isShowRealChars && !isExtraKeyPressed) {
            String keyPressShow = "<";
            if (alt) {
                keyPressShow = keyPressShow + " Alt +";
            }
            if (ctrl) {
                keyPressShow = keyPressShow + " Ctrl +";
            }
            String keyPress = "" + nativeKeyEvent.getKeyChar();
            keyPressed.setText(keyPress);
            lastStringChars = lastStringChars + keyPress;
            if (!alt && !ctrl) {
                if (keycast.getPkf().isKeepAddingKeys()) {
                    keycast.getPkf().addCharToText("< " + lastStringChars + " >");
                } else {
                    lastStringChars = "" + keyPress;
                    keycast.getPkf().setKeyText("< " + lastStringChars + " >");
                }
                keyPressed.setText(keyPress);
                keycast.getPkf().setKeepAddingChars(true);
            } else {
                keycast.getPkf().setKeepAddingChars(false);
                keycast.getPkf().setKeyText(keyPressShow + " " + keyPress + " >");
            }
        }
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
        keycast.getPkf().setKeepAddingChars(false);
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

    @Override
    public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {
        // Not used
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {
        if (isShowMouseEventText) {
            keycast.getPkf().setKeepAddingChars(false);
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
            if (nativeMouseEvent.getButton() == NativeMouseEvent.BUTTON1) {
                keycast.getPkf().setKeyText(keyPressShow + " LMouseDown >");
            }
            if (nativeMouseEvent.getButton() == NativeMouseEvent.BUTTON2) {
                keycast.getPkf().setKeyText(keyPressShow + " RMouseDown >");
            }
            if (nativeMouseEvent.getButton() == NativeMouseEvent.BUTTON3) {
                keycast.getPkf().setKeyText(keyPressShow + " MMouseDown >");
            }
        }
        if (nativeMouseEvent.getButton() == NativeMouseEvent.BUTTON1) {
            L = true;
        }
        if (nativeMouseEvent.getButton() == NativeMouseEvent.BUTTON2) {
            R = true;
        }
        if (nativeMouseEvent.getButton() == NativeMouseEvent.BUTTON3) {
            M = true;
        }
        changeLabel();
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {
        if (isShowMouseEventText) {
            keycast.getPkf().setKeepAddingChars(false);
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
            if (nativeMouseEvent.getButton() == NativeMouseEvent.BUTTON1) {
                keycast.getPkf().setKeyText(keyPressShow + " LMouseUp >");
            }
            if (nativeMouseEvent.getButton() == NativeMouseEvent.BUTTON2) {
                keycast.getPkf().setKeyText(keyPressShow + " RMouseUp >");
            }
            if (nativeMouseEvent.getButton() == NativeMouseEvent.BUTTON3) {
                keycast.getPkf().setKeyText(keyPressShow + " MMouseUp >");
            }
        }
        if (nativeMouseEvent.getButton() == NativeMouseEvent.BUTTON1) {
            L = false;
        }
        if (nativeMouseEvent.getButton() == NativeMouseEvent.BUTTON2) {
            R = false;
        }
        if (nativeMouseEvent.getButton() == NativeMouseEvent.BUTTON3) {
            M = false;
        }
        changeLabel();
    }

    private void changeLabel() {
        if (L && M && R) {
            mouseIcon.setIcon(mouseImages.get("LMR"));
        } else if (L && M) {
            mouseIcon.setIcon(mouseImages.get("LM"));
        } else if (L && R) {
            mouseIcon.setIcon(mouseImages.get("LR"));
        } else if (M && R) {
            mouseIcon.setIcon(mouseImages.get("MR"));
        } else if (L) {
            mouseIcon.setIcon(mouseImages.get("L"));
        } else if (M) {
            mouseIcon.setIcon(mouseImages.get("M"));
        } else if (R) {
            mouseIcon.setIcon(mouseImages.get("R"));
        } else {
            mouseIcon.setIcon(mouseImages.get("none"));
        }
    }
}
