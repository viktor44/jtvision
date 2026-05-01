package org.viktor44.jtvision.views;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.viktor44.jtvision.core.CommandCodes.cmCancel;
import static org.viktor44.jtvision.core.CommandCodes.cmError;
import static org.viktor44.jtvision.core.EventCodes.evCommand;
import static org.viktor44.jtvision.core.ViewFlags.sfModal;

import org.junit.jupiter.api.Test;
import org.viktor44.jtvision.core.JtvEvent;
import org.viktor44.jtvision.core.JtvRect;
import org.viktor44.jtvision.views.JtvGroup;

class JtvGroupExecuteTest {

    @Test
    void executeHandlesRuntimeExceptionsWithoutStoppingApp() {
        ThrowingGroup group = new ThrowingGroup();

        assertDoesNotThrow(group::execute);

        JtvEvent errorEvent = group.lastErrorEvent;
        assertNotNull(errorEvent);
        assertEquals(evCommand, errorEvent.getWhat());
        assertEquals(cmError, errorEvent.getMessage().getCommand());
        assertNotNull(errorEvent.getMessage().getInfoPtr());
    }

    private static final class ThrowingGroup extends JtvGroup {
        private int calls;
        private JtvEvent lastErrorEvent;

        private ThrowingGroup() {
            super(new JtvRect(0, 0, 10, 5));
            state |= sfModal;
        }

        @Override
        public JtvEvent getEvent(JtvEvent event) {
            event.setWhat(0);
            return event;
        }

        @Override
        public void handleEvent(JtvEvent event) {
            if (calls++ == 0) {
                throw new ArrayIndexOutOfBoundsException("boom");
            }
            endModal(cmCancel);
        }

        @Override
        public void eventError(JtvEvent event) {
            lastErrorEvent = new JtvEvent();
            lastErrorEvent.copyFrom(event);
        }
    }
}
