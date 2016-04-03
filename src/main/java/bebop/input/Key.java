/*
 * Copyright (C) 2016 ZenFiler Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package bebop.input;

import static org.eclipse.swt.SWT.*;
import static org.eclipse.swt.internal.win32.OS.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import kiss.Table;
import kiss.model.ClassUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import bebop.util.SWTUtil;

/**
 * @version 2011/11/02 15:52:39
 */
public enum Key {
    /** Virtual Key Code */
    N1('1', '1'),

    /** Virtual Key Code */
    N2('2', '2'),

    /** Virtual Key Code */
    N3('3', '3'),

    /** Virtual Key Code */
    N4('4', '4'),

    /** Virtual Key Code */
    N5('5', '5'),

    /** Virtual Key Code */
    N6('6', '6'),

    /** Virtual Key Code */
    N7('7', '7'),

    /** Virtual Key Code */
    N8('8', '8'),

    /** Vitual Key Code */
    N9('9', '9'),

    /** Virtual Key Code */
    N0('0', '0'),

    /** Virtual Key Code */
    A('a'),

    /** Virtual Key Code */
    B('b'),

    /** Virtual Key Code */
    C('c'),

    /** Virtual Key Code */
    D('d'),

    /** Virtual Key Code */
    E('e'),

    /** Virtual Key Code */
    F('f'),

    /** Virtual Key Code */
    G('g'),

    /** Virtual Key Code */
    H('h'),

    /** Virtual Key Code */
    I('i'),

    /** Virtual Key Code */
    J('j'),

    /** Virtual Key Code */
    K('k'),

    /** Virtual Key Code */
    L('l'),

    /** Virtual Key Code */
    M('m'),

    /** Virtual Key Code */
    N('n'),

    /** Virtual Key Code */
    O('o'),

    /** Virtual Key Code */
    P('p'),

    /** Virtual Key Code */
    Q('q'),

    /** Virtual Key Code */
    R('r'),

    /** Virtual Key Code */
    S('s'),

    /** Virtual Key Code */
    T('t'),

    /** Virtual Key Code */
    U('u'),

    /** Virtual Key Code */
    V('v'),

    /** Virtual Key Code */
    W('w'),

    /** Virtual Key Code */
    X('x'),

    /** Virtual Key Code */
    Y('y'),

    /** Virtual Key Code */
    Z('z'),

    /** Virtual Key Code */
    Up(ARROW_UP, VK_UP, false, true),

    /** Virtual Key Code */
    Down(ARROW_DOWN, VK_DOWN, false, true),

    /** Virtual Key Code */
    Right(ARROW_RIGHT, VK_RIGHT, false, true),

    /** Virtual Key Code */
    Left(ARROW_LEFT, VK_LEFT, false, true),

    /** Virtual Key Code */
    Space(SPACE, VK_SPACE),

    /** Virtual Key Code */
    Backspace(BS, VK_BACK),

    /** Virtual Key Code */
    Enter(CR, VK_RETURN),

    /** Virtual Key Code */
    EnterInTenKey(CR, VK_SEPARATOR),

    /** Virtual Key Code */
    Delete(DEL, VK_DELETE),

    /** Virtual Key Code */
    Escape(ESC, VK_ESCAPE),

    /** Virtual Key Code */
    Insert(INSERT, VK_INSERT),

    /** Virtual Key Code */
    Tab(TAB, VK_TAB),

    /** Virtual Key Code */
    Home(HOME, VK_HOME),

    /** Virtual Key Code */
    End(END, VK_END),

    /** Virtual Key Code */
    PageUp(PAGE_UP, VK_PRIOR),

    /** Virtual Key Code */
    PageDown(PAGE_DOWN, VK_NEXT),

    /** Virtual Key Code */
    ControlRight(CTRL, VK_RCONTROL, false, true),

    /** Virtual Key Code */
    ControlLeft(CTRL, VK_LCONTROL, false, true),

    /** Virtual Key Code */
    ShiftRight(SHIFT, VK_RSHIFT, false, false),

    /** Virtual Key Code */
    ShiftLeft(SHIFT, VK_LSHIFT, false, false),

    /** Virtual Key Code */
    AltRight(ALT, VK_RMENU, true),

    /** Virtual Key Code */
    AltLeft(ALT, VK_LMENU, true),

    /** Virtual Key Code */
    F1(SWT.F1, VK_F1, true),

    /** Virtual Key Code */
    F2(SWT.F2, VK_F2, true),

    /** Virtual Key Code */
    F3(SWT.F3, VK_F3, true),

    /** Virtual Key Code */
    F4(SWT.F4, VK_F4, true),

    /** Virtual Key Code */
    F5(SWT.F5, VK_F5, true),

    /** Virtual Key Code */
    F6(SWT.F6, VK_F6, true),

    /** Virtual Key Code */
    F7(SWT.F7, VK_F7, true),

    /** Virtual Key Code */
    F8(SWT.F8, VK_F8, true),

    /** Virtual Key Code */
    F9(SWT.F9, VK_F9, true),

    /** Virtual Key Code */
    F10(SWT.F10, VK_F10, true),

    /** Virtual Key Code */
    F11(SWT.F11, VK_F11, true),

    /** Virtual Key Code */
    F12(SWT.F12, VK_F12, true);

    /** The SWT key code. */
    public final int code;

    /** The native virtual key code. */
    public final short nativeCode;

    /** Is this key is system related? */
    final boolean system;

    /** Is this key is extended key? */
    final boolean extended;

    /**
     * <p>
     * Native key.
     * </p>
     * 
     * @param code
     */
    private Key(int code) {
        this(code, code - 32, false);
    }

    /**
     * <p>
     * Native key.
     * </p>
     * 
     * @param nativeCode
     */
    private Key(int code, int nativeCode) {
        this(code, nativeCode, false);
    }

    /**
     * <p>
     * Native key.
     * </p>
     * 
     * @param nativeCode
     */
    private Key(int code, int nativeCode, boolean system) {
        this(code, nativeCode, system, false);
    }

    /**
     * <p>
     * Native key.
     * </p>
     * 
     * @param nativeCode
     */
    private Key(int code, int nativeCode, boolean system, boolean extended) {
        this.code = code;
        this.nativeCode = (short) nativeCode;
        this.system = system;
        this.extended = extended;
    }

    /**
     * @param keies A owner of declaring key bindings.
     * @param control A target widget to bind.
     */
    public static void bind(Object keies, Control control) {
        KeyBindings bindings = SWTUtil.get(control, KeyBindings.class);

        if (bindings == null) {
            bindings = new KeyBindings(control);

            // register
            SWTUtil.set(control, bindings);

            // register as event listner
            if (control instanceof StyledText) {
                ((StyledText) control).addVerifyKeyListener(bindings);
            } else {
                control.addListener(KeyDown, bindings);
            }
            control.addTraverseListener(bindings);
        }

        // Collect key binding methods.
        Table<Method, Annotation> methods = ClassUtil.getAnnotations(keies.getClass());

        for (Entry<Method, List<Annotation>> entry : methods.entrySet()) {
            Method method = entry.getKey();

            for (Annotation annotation : entry.getValue()) {
                if (annotation instanceof KeyBind) {
                    method.setAccessible(true);
                    bindings.put(new KeyStroke((KeyBind) annotation), new KeyBinding(keies, method));
                }
            }
        }
    }

    /**
     * @version 2012/03/03 21:07:59
     */
    @SuppressWarnings("serial")
    private static class KeyBindings extends HashMap<KeyStroke, KeyBinding>
            implements Listener, TraverseListener, VerifyKeyListener {

        /** The associated widget. */
        private final Control control;

        /**
         * @param control
         */
        private KeyBindings(Control control) {
            this.control = control;
        }

        /**
         * <p>
         * Handle key event.
         * <p>
         * 
         * @param stroke
         */
        private boolean handle(KeyStroke stroke) {
            KeyBinding binding = get(stroke);

            if (binding != null) {
                return binding.invoke();
            } else {
                // event bubbling
                Composite parent = control.getParent();

                if (parent == null) {
                    return true;
                } else {
                    KeyBindings bindings = SWTUtil.get(parent, KeyBindings.class);

                    if (bindings == null) {
                        return true;
                    } else {
                        return bindings.handle(stroke);
                    }
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void handleEvent(Event event) {
            if (event.doit) {
                event.doit = handle(new KeyStroke(event));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void verifyKey(VerifyEvent event) {
            if (event.doit) {
                event.doit = handle(new KeyStroke(event));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void keyTraversed(TraverseEvent event) {
            if (event.doit && (event.detail == TRAVERSE_TAB_NEXT || event.detail == TRAVERSE_TAB_PREVIOUS)) {
                event.doit = false; // drop traversal event to throw key event for TAB key
            }
        }
    }

    /**
     * @version 2012/03/03 21:13:54
     */
    private static class KeyBinding {

        /** The binding method owner. */
        private final Object owner;

        /** The binding method. */
        private final Method binder;

        /**
         * @param owner
         * @param binder
         */
        private KeyBinding(Object owner, Method binder) {
            this.owner = owner;
            this.binder = binder;
        }

        /**
         * <p>
         * Invoke key binding.
         * </p>
         * 
         * @return
         */
        private boolean invoke() {
            try {
                binder.invoke(owner);

                return false;
            } catch (Exception e) {
                return true;
            }
        }
    }
}
