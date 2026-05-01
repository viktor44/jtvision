/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2026-present Viktor44
 */
package org.viktor44.jtvision.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Payload of a mouse-family event ({@code evMouseDown}, {@code evMouseUp},
 * {@code evMouseMove}, {@code evMouseAuto}, or {@code evMouseWheel}).
 *
 * <p>This class corresponds to the {@code evMouse} variant of the Pascal
 * {@code TEvent} record.  It is active when {@link JtvEvent#getWhat()} matches
 * the {@link EventCodes#evMouse} mask.
 *
 * <p>The original Pascal record defined three fields: {@code Buttons} (byte),
 * {@code Double} (Boolean), and {@code Where} (TPoint).  This Java port expands
 * that with {@link #eventFlags} (which subsumes {@code Double}), plus
 * {@link #modifiers} and {@link #wheel} as jtvision extensions.
 *
 * @see JtvEvent
 * @see EventCodes#evMouseDown
 * @see EventCodes#evMouseUp
 * @see EventCodes#evMouseMove
 * @see EventCodes#evMouseAuto
 * @see EventCodes#evMouseWheel
 */
@NoArgsConstructor
@AllArgsConstructor
public class MouseEvent {

	/**
     * Mouse position in global (application-level) coordinates ({@code Where} in the original Pascal record).
     * <p>
     * The position is expressed in the coordinate system of the top-level
     * application view.  Use {@code TView.makeLocal} to convert to view-local
     * coordinates before comparing against view bounds.
     */
	@Getter
    private JtvPoint where = new JtvPoint();

	/**
     * Flags describing the nature of this mouse event (jtvision extension,
     * replaces the Pascal {@code Double} boolean field).
     * <p>
     * Bit values are defined in {@link EventCodes}:
     * <ul>
     *   <li>{@link EventCodes#meMouseMoved} — pointer moved during the current
     *       interaction sequence</li>
     *   <li>{@link EventCodes#meDoubleClick} — event is a double click</li>
     *   <li>{@link EventCodes#meTripleClick} — event is a triple click</li>
     * </ul>
     * In the original Pascal record only a single {@code Double: Boolean} field
     * was available; check {@link EventCodes#meDoubleClick} to replicate that
     * behaviour.
     */
	@Getter
    private int eventFlags;

    /**
     * State of modifier keys at the time of the mouse event.
     * <p>
     * Holds platform-specific modifier bits (Shift, Ctrl, Alt, etc.).
     */
	@Getter
    private int modifiers;

    /**
     * State of the mouse buttons at the time of the event
     * <p>
     * Bit values are defined in {@link EventCodes}:
     * <ul>
     *   <li>{@link EventCodes#mbLeftButton} ({@code $01}) — left button pressed</li>
     *   <li>{@link EventCodes#mbRightButton} ({@code $02}) — right button pressed</li>
     *   <li>{@link EventCodes#mbMiddleButton} ({@code $04}) — middle button pressed
     *       (jtvision extension)</li>
     * </ul>
     * Example: {@code (event.getMouse().getButtons() == mbLeftButton)}
     */
	@Getter
    private int buttons;

    /**
     * Mouse wheel direction and magnitude
     * <p>
     * Non-zero only for {@link EventCodes#evMouseWheel} events.  Direction
     * bits are defined in {@link EventCodes}: {@link EventCodes#mwUp},
     * {@link EventCodes#mwDown}, {@link EventCodes#mwLeft},
     * {@link EventCodes#mwRight}.
     * Pascal record.
     */
	@Getter
    private int wheel;
}
