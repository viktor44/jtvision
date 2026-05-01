package org.viktor44.jtvision.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Payload of a message-family event ({@code evCommand}, {@code evBroadcast}, or a
 * user-defined message).
 * <p>
 * This class corresponds to the {@code evMessage} variant of the Pascal
 * {@code TEvent} record.  It is active when {@link JtvEvent#getWhat()} is in the
 * {@link EventCodes#evMessage} mask.
 * <p>
 * The original Pascal record stored the extra payload as an overlay of several
 * types ({@code InfoPtr}, {@code InfoLong}, {@code InfoWord}, …).  Here the most
 * commonly used overlays are exposed as separate fields.
 *
 * @see JtvEvent
 * @see EventCodes#evCommand
 * @see EventCodes#evBroadcast
 */
@NoArgsConstructor
@AllArgsConstructor
public class MessageEvent {

	/**
     * The command code carried by this event.
     * <p>
     * For {@code evCommand} events this is the integer constant bound to the
     * menu item, status key, or hot key that generated the command.  For
     * {@code evBroadcast} events it identifies the broadcast type (for example
     * {@code cmCommandSetChanged}).  Standard command codes are defined in
     * {@link CommandCodes}.
     */
	@Getter
	@Setter
    private int command;

	/**
     * Arbitrary object payload
     * <p>
     * Views that generate events often place a reference to themselves or to
     * some related object here so that the recipient can identify the source.
     * {@link JtvEvent#clearEvent} sets this field to the view that consumed the
     * event, which is used by {@code Message()} to return that view to the caller.
     */
	@Getter
	@Setter
    private Object infoPtr;

    /**
     * Integer payload
     * <p>
     * Alternative numeric interpretation of the message payload, used when a
     * signed integer value needs to be passed alongside the command code.
     */
	@Getter
	@Setter
    private int infoInt;

    /**
     * Long-integer payload
     *
     * <p>Alternative numeric interpretation of the message payload for cases that
     * require a wider integer value than {@link #infoInt} can hold.
     */
	@Getter
	@Setter
    private long infoLong;

    /**
     * Constructs a message event with a command and an object payload.
     *
     * @param command the command code
     * @param infoPtr the object payload (may be {@code null})
     */
    public MessageEvent(int command, Object infoPtr) {
        this(command, infoPtr, 0, 0);
    }
}
