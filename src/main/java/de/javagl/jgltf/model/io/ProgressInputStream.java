/*
 * www.javagl.de - JglTF
 *
 * Copyright 2015-2016 Marco Hutter - http://www.javagl.de
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.javagl.jgltf.model.io;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.LongConsumer;

/**
 * An input stream that informs property change listeners and consumers
 * about the number of bytes that are read.
 */
public final class ProgressInputStream extends FilterInputStream {
    // Originally based on http://stackoverflow.com/a/1339589, heavily modified

    /**
     * The property change support
     */
    private final PropertyChangeSupport propertyChangeSupport;

    /**
     * The consumers that will be informed about the total number of
     * bytes that have been read
     */
    private final List<LongConsumer> totalNumBytesReadConsumers;

    /**
     * The total number of bytes that have been read
     */
    private volatile long totalNumBytesRead;

    /**
     * Creates a new progress input stream that reads from the given input
     * stream
     * 
     * @param inputStream The input stream
     */
    public ProgressInputStream(final InputStream inputStream) {
        super(inputStream);
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.totalNumBytesReadConsumers = new CopyOnWriteArrayList<LongConsumer>();
    }

    /**
     * Returns the total number of bytes that already have been read
     * from the stream
     * 
     * @return The number of bytes read
     */
    long getTotalNumBytesRead() {
        return this.totalNumBytesRead;
    }

    /**
     * Add the given listener to be informed when the number of bytes
     * that have been read from this stream changes
     * 
     * @param listener The listener to add
     */
    void addPropertyChangeListener(final PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove the given listener from this stream
     * 
     * @param listener The listener to remove
     */
    void removePropertyChangeListener(final PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Add the given consumer to be informed about the total number of
     * bytes that have been read
     * 
     * @param consumer The consumer
     */
    public void addTotalNumBytesReadConsumer(final LongConsumer consumer) {
        this.totalNumBytesReadConsumers.add(consumer);
    }

    /**
     * Remove the given consumer
     * 
     * @param consumer The consumer
     */
    public void removeTotalNumBytesReadConsumer(final LongConsumer consumer) {
        this.totalNumBytesReadConsumers.remove(consumer);
    }

    @Override
    public int read() throws IOException {
        final int b = super.read();
        if (b != -1) {
            this.updateProgress(1);
        }
        return b;
    }

    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int read = super.read(b, off, len);
        if (read == -1) {
            return -1;
        }
        return (int) this.updateProgress(read);
    }

    @Override
    public long skip(final long n) throws IOException {
        return this.updateProgress(super.skip(n));
    }

    @Override
    public void mark(final int readlimit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    /**
     * Update the progress of this stream, based on the given number of
     * bytes that have been read, and inform all registered listeners
     * 
     * @param numBytesRead The number of bytes that have been read
     * @return The number of bytes read
     */
    private long updateProgress(final long numBytesRead) {
        if (numBytesRead > 0) {
            final long oldTotalNumBytesRead = this.totalNumBytesRead;
            this.totalNumBytesRead += numBytesRead;
            this.propertyChangeSupport.firePropertyChange("totalNumBytesRead", oldTotalNumBytesRead,
                    this.totalNumBytesRead);
            this.fireTotalNumBytesRead();
        }
        return numBytesRead;
    }

    /**
     * Forward the information about the total number of bytes that have
     * been read to the consumers
     */
    private void fireTotalNumBytesRead() {
        for (final LongConsumer consumer : this.totalNumBytesReadConsumers) {
            consumer.accept(this.totalNumBytesRead);
        }
    }
}
