/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.core;

/**
 * Event type codes and masks used in {@code JtvEvent.what}.
 * Additional constants are jtvision extensions
 * (mouse wheel directions and richer click flags).
 */
public final class EventCodes {

    private EventCodes() {}

    /**
     * Mouse button pressed.
     */
    public static final int evMouseDown = 0x0001;

    /**
     * Mouse button released.
     */
    public static final int evMouseUp = 0x0002;

    /**
     * Mouse changed location.
     */
    public static final int evMouseMove = 0x0004;

    /**
     * Periodic event while a mouse button is held.
     */
    public static final int evMouseAuto = 0x0008;

    /**
     * jtvision extension for mouse wheel input.
     */
    public static final int evMouseWheel = 0x0020;

    /**
     * Key pressed.
     */
    public static final int evKeyDown = 0x0010;

    /**
     * Command event.
     */
    public static final int evCommand = 0x0100;

    /**
     * Broadcast event.
     */
    public static final int evBroadcast = 0x0200;

    /**
     * Event has already been handled; no further action is required.
     */
    public static final int evNothing = 0x0000;

    /**
     * Mask for mouse-family events.
     *
     * <p>The base mouse mask is {@code 0x000F}. jtvision extends it
     * with {@link #evMouseWheel}, resulting in {@code 0x002F}.
     */
    public static final int evMouse = 0x002F;

    /**
     * Keyboard event mask.
     */
    public static final int evKeyboard = 0x0010;

    /**
     * Message-family event mask ({@code 0xFF00}): command, broadcast, or user-defined message.
     */
    public static final int evMessage = 0xFF00;

    /**
     * Left mouse button state bit.
     */
    public static final int mbLeftButton = 0x01;

    /**
     * Right mouse button state bit.
     */
    public static final int mbRightButton = 0x02;

    /**
     * Middle mouse button state bit.
     */
    public static final int mbMiddleButton = 0x04;

    /**
     * Wheel direction: up.
     */
    public static final int mwUp = 0x01;

    /**
     * Wheel direction: down.
     */
    public static final int mwDown = 0x02;

    /**
     * Wheel direction: left.
     */
    public static final int mwLeft = 0x04;

    /**
     * Wheel direction: right.
     */
    public static final int mwRight = 0x08;

    /**
     * Pointer moved during the current interaction sequence.
     */
    public static final int meMouseMoved = 0x01;

    /**
     * Mouse event is a double click.
     */
    public static final int meDoubleClick = 0x02;

    /**
     * Mouse event is a triple click.
     */
    public static final int meTripleClick = 0x04;

    /**
     * Events routed by pointer position.
     *
     * <p>This is the classic positional mouse set ({@link #evMouseDown},
     * {@link #evMouseUp}, {@link #evMouseMove}, {@link #evMouseAuto}) and
     * intentionally excludes {@link #evMouseWheel}.
     */
    public static final int positionalEvents = evMouse & ~evMouseWheel;

    /**
     * Events routed through focused-event processing.
     *
     * <p>The default focused routing contains keyboard bits;
     * jtvision also includes {@link #evCommand} for command-chain handling.
     */
    public static final int focusedEvents = evKeyboard | evCommand;
}
