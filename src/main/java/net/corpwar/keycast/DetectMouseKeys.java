package net.corpwar.keycast;

import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

import javax.swing.*;
import java.util.HashMap;

/**
 * corpwar-keycast
 * Created by Ghost on 2015-07-12.
 */
public class DetectMouseKeys implements NativeMouseListener {

    private JLabel mouseIcon;
    private HashMap<String, ImageIcon> mouseImages;
    private boolean L = false, M = false, R = false;

    public DetectMouseKeys(JLabel mouseIcon, HashMap<String, ImageIcon> mouseImages) {
        this.mouseIcon = mouseIcon;
        this.mouseImages = mouseImages;
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {
        // Not used
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {
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
