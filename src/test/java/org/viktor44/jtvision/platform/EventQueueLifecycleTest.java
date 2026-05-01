package org.viktor44.jtvision.platform;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.viktor44.jtvision.platform.EventQueue;

class EventQueueLifecycleTest {

    @AfterEach
    void resetEventQueueStatics() throws Exception {
        InputStream inputStream = (InputStream) getStaticField("inputStream");
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException ignored) {
            }
        }

        Thread inputThread = (Thread) getStaticField("inputThread");
        Thread readerThread = (Thread) getStaticField("readerThread");

        if (inputThread != null) {
            inputThread.interrupt();
            inputThread.join(500);
        }
        if (readerThread != null) {
            readerThread.interrupt();
            readerThread.join(500);
        }

        setStaticField("inputThread", null);
        setStaticField("readerThread", null);
        setStaticField("inputStream", null);
        setStaticField("savedTerminalState", null);
        setBooleanField("running", false);
        setBooleanField("closeInputStreamOnStop", false);
        setBooleanField("rawTerminalEnabled", false);
        setBooleanField("windowsInputModeSaved", false);
        setIntField("savedWindowsInputMode", 0);
    }

    @Test
    void shutdownForceClosesBlockedInputAndStopsReaderThread() throws Exception {
        BlockingInputStream stream = new BlockingInputStream();
        Thread reader = startBlockedReader(stream);

        setStaticField("inputStream", stream);
        setStaticField("readerThread", reader);
        setBooleanField("closeInputStreamOnStop", false);
        setBooleanField("running", true);
        setBooleanField("rawTerminalEnabled", false);

        assertTrue(stream.awaitReadStarted(), "reader thread did not start blocking read");

        EventQueue.shutdown();

        assertTrue(stream.isClosed(), "shutdown should force-close input stream");
        assertTrue(waitUntilStopped(reader, 1200), "reader thread should stop after shutdown");
        assertNull(getStaticField("readerThread"), "readerThread field should be cleared");
        assertNull(getStaticField("inputStream"), "inputStream field should be cleared");
    }

    @Test
    void suspendClosesTtyInputAndStopsReaderThread() throws Exception {
        BlockingInputStream stream = new BlockingInputStream();
        Thread reader = startBlockedReader(stream);

        setStaticField("inputStream", stream);
        setStaticField("readerThread", reader);
        setBooleanField("closeInputStreamOnStop", true);
        setBooleanField("running", true);
        setBooleanField("rawTerminalEnabled", false);

        assertTrue(stream.awaitReadStarted(), "reader thread did not start blocking read");

        EventQueue.suspend();

        assertTrue(stream.isClosed(), "suspend should close tty input stream");
        assertTrue(waitUntilStopped(reader, 1200), "reader thread should stop after suspend");
        assertNull(getStaticField("readerThread"), "readerThread field should be cleared");
        assertNull(getStaticField("inputStream"), "inputStream field should be cleared");
    }

    private static Thread startBlockedReader(final BlockingInputStream stream) {
        Thread reader = new Thread(() -> {
            try {
                stream.read();
            } catch (IOException ignored) {
            }
        }, "jvision-reader-test");
        reader.start();
        return reader;
    }

    private static boolean waitUntilStopped(Thread thread, long timeoutMs) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (thread.isAlive() && System.currentTimeMillis() < deadline) {
            thread.join(25);
        }
        return !thread.isAlive();
    }

    private static Object getStaticField(String name) throws Exception {
        Field field = EventQueue.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(null);
    }

    private static void setStaticField(String name, Object value) throws Exception {
        Field field = EventQueue.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(null, value);
    }

    private static void setBooleanField(String name, boolean value) throws Exception {
        Field field = EventQueue.class.getDeclaredField(name);
        field.setAccessible(true);
        field.setBoolean(null, value);
    }

    private static void setIntField(String name, int value) throws Exception {
        Field field = EventQueue.class.getDeclaredField(name);
        field.setAccessible(true);
        field.setInt(null, value);
    }

    private static final class BlockingInputStream extends InputStream {
        private final CountDownLatch readStarted = new CountDownLatch(1);
        private boolean closed;

        @Override
        public int read() throws IOException {
            readStarted.countDown();
            synchronized (this) {
                while (!closed) {
                    try {
                        wait();
                    } catch (InterruptedException ignored) {
                        // Intentionally keep waiting until close() is called.
                    }
                }
            }
            throw new IOException("stream closed");
        }

        @Override
        public void close() {
            synchronized (this) {
                closed = true;
                notifyAll();
            }
        }

        public boolean awaitReadStarted() throws InterruptedException {
            return readStarted.await(1, TimeUnit.SECONDS);
        }

        public boolean isClosed() {
            synchronized (this) {
                return closed;
            }
        }
    }
}
