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

import static org.eclipse.swt.internal.win32.OS.*;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;

import org.eclipse.swt.internal.win32.INPUT;
import org.eclipse.swt.internal.win32.KEYBDINPUT;
import org.eclipse.swt.internal.win32.TCHAR;

import kiss.I;

/**
 * @version 2011/10/18 12:11:37
 */
public class Window {

    /** The date format. */
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

    /** The window handle. */
    private final long windowHandle;

    /** The current window handle. */
    private long currentHandle;

    /** The global delay time (ms). */
    private int time = 50;

    /**
     * 
     */
    private Window(long windowHandle) {
        this.windowHandle = windowHandle;
        this.currentHandle = windowHandle;
    }

    /**
     * <p>
     * Get foreground window.
     * </p>
     * 
     * @return
     */
    public static final Window getWindow() {
        return new Window(GetForegroundWindow());
    }

    /**
     * <p>
     * Get specified window.
     * </p>
     * 
     * @param title
     * @return
     */
    public static final Window getWindow(String title) {
        long id = FindWindow(null, new TCHAR(CP_INSTALLED, title, true));

        if (id == 0) {
            throw new IllegalAccessError("The specified title window is not found.");
        }
        return new Window(id);
    }

    /**
     * <p>
     * Get current window title.
     * </p>
     * 
     * @return
     */
    public String getTitle() {
        TCHAR buffer = new TCHAR(CP_INSTALLED, 1000);

        GetWindowText(windowHandle, buffer, buffer.length());

        return buffer.toString(0, GetWindowTextLength(windowHandle));
    }

    /**
     * <p>
     * Show up this window.
     * </p>
     * 
     * @return
     */
    public Window show() {
        SetForegroundWindow(windowHandle);

        // API definition
        return delay(time);
    }

    /**
     * <p>
     * Delay automatic operation.
     * </p>
     * 
     * @param millseconds A delay time.
     * @return
     */
    public Window delay(int millseconds) {
        try {
            Thread.sleep(millseconds);
        } catch (InterruptedException e) {
            throw I.quiet(e);
        }

        // API definition
        return this;
    }

    /**
     * <p>
     * Input the specified character.
     * </p>
     * 
     * @param value Your input.
     * @return
     */
    public Window input(int value) {
        return input(String.valueOf(value));
    }

    /**
     * <p>
     * Input the specified character.
     * </p>
     * 
     * @param value Your input.
     * @return
     */
    public Window input(Date value) {
        return input(formatter.format(value));
    }

    /**
     * <p>
     * Input the specified character.
     * </p>
     * 
     * @param message Your input.
     * @return
     */
    public Window input(CharSequence message) {
        if (message == null) {
            return delay(time);
        }

        for (int i = 0; i < message.length(); i++) {
            PostMessage(currentHandle, WM_CHAR, message.charAt(i), 0);
            delay(30);
        }

        // API definition
        return delay(time);
    }

    /**
     * <p>
     * Input the specified key.
     * </p>
     * 
     * @param message Your key input.
     * @return
     */
    public Window input(Key code, Key... modifiers) {
        for (int i = 0; i < modifiers.length; i++) {
            input(modifiers[i], true);
        }

        input(code, true);
        input(code, false);

        for (int i = modifiers.length - 1; 0 <= i; i--) {
            input(modifiers[i], false);
        }

        // API definition
        return delay(time);
    }

    /**
     * <p>
     * Send key event.
     * </p>
     * 
     * @param code A key code.
     * @return
     */
    public void input(Key code, boolean down) {
        KEYBDINPUT input = new KEYBDINPUT();
        input.wVk = code.nativeCode;
        input.dwFlags = down ? 0 : KEYEVENTF_KEYUP;
        if (code.extended) input.dwFlags = input.dwFlags | KEYEVENTF_EXTENDEDKEY;

        long heap = GetProcessHeap();
        long pointer = HeapAlloc(heap, HEAP_ZERO_MEMORY, INPUT.sizeof);
        MoveMemory(pointer, new int[] {INPUT_KEYBOARD}, 4);
        MoveMemory(pointer + 4, input, KEYBDINPUT.sizeof);

        SendInput(1, pointer, INPUT.sizeof);

        HeapFree(heap, 0, pointer);
    }

    /**
     * <p>
     * Find window by various options.
     * </p>
     * 
     * @param className
     * @param index
     */
    public Window find(String className, int position) {
        int counter = 1;

        Deque<Long> widgets = new ArrayDeque();
        widgets.add(GetWindow(windowHandle, GW_CHILD));

        while (widgets.size() > 0) {
            long candidate = widgets.pollLast();

            if (getClassName(candidate).equals(className) && counter++ == position) {
                currentHandle = candidate;

                // API definition
                return delay(time);
            }

            long next = GetWindow(candidate, GW_HWNDNEXT);

            if (next != 0) {
                widgets.add(next);
            }

            long child = GetWindow(candidate, GW_CHILD);

            if (child != 0) {
                widgets.add(child);
            }
        }

        // NOT FOUND
        throw new IllegalAccessError("The specified window is not found.");
    }

    /**
     * <p>
     * Find window by various options.
     * </p>
     * 
     * @param className
     * @param index
     */
    public Window find() {
        Deque<Long> widgets = new ArrayDeque();
        widgets.add(GetWindow(windowHandle, GW_CHILD));

        while (widgets.size() > 0) {
            long candidate = widgets.pollLast();

            System.out.println(getClassName(candidate));

            long next = GetWindow(candidate, GW_HWNDNEXT);

            if (next != 0) {
                widgets.add(next);
            }

            long child = GetWindow(candidate, GW_CHILD);

            if (child != 0) {
                widgets.add(child);
            }
        }
        return delay(time);
    }

    /**
     * <p>
     * Retrieve class name.
     * </p>
     * 
     * @param id A window handle id.
     * @return A class name.
     */
    private String getClassName(long id) {
        // prepare character buffer
        TCHAR buffer = new TCHAR(CP_INSTALLED, 256);

        // read data
        GetClassName(id, buffer, 256);

        // API definition
        return buffer.toString(0, buffer.strlen());
    }
}
