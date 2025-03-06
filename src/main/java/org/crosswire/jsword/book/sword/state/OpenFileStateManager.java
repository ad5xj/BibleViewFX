package org.crosswire.jsword.book.sword.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.sword.BlockType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public final class OpenFileStateManager {

    private final ScheduledFuture<?> monitoringThread;

    private final Map<BookMetaData, Queue<OpenFileState>> metaToStates;

    private volatile boolean shuttingDown;

    private static volatile OpenFileStateManager manager;

    private OpenFileStateManager(int cleanupIntervalSeconds, final int maxExpiry) {
        this.metaToStates = new HashMap<BookMetaData, Queue<OpenFileState>>();
        this.monitoringThread = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        }).scheduleWithFixedDelay(new Runnable() {
            public void run() {
                long currentTime = System.currentTimeMillis();
                for (Queue<OpenFileState> e : (Iterable<Queue<OpenFileState>>) OpenFileStateManager.this.metaToStates.values()) {
                    for (Iterator<OpenFileState> iterator = e.iterator(); iterator.hasNext();) {
                        OpenFileState state = iterator.next();
                        if (state.getLastAccess() + (maxExpiry * 1000) < currentTime) {
                            state.releaseResources();
                            iterator.remove();
                        }
                    }
                }
            }
        }, 0L, cleanupIntervalSeconds, TimeUnit.SECONDS);
    }

    public static synchronized void init(int cleanupIntervalSeconds, int maxExpiry) {
        if (manager == null) {
            manager = new OpenFileStateManager(cleanupIntervalSeconds, maxExpiry);
        } else {
            LOGGER.warn("The OpenFileStateManager has already been initialised, potentially with its default settings. The following values were ignored: cleanUpInterval [{}], maxExpiry=[{}]", Integer.toString(cleanupIntervalSeconds), Integer.toString(maxExpiry));
        }
    }

    public static OpenFileStateManager instance() {
        if (manager == null)
      synchronized (OpenFileStateManager.class) {
            init(60, 60);
        }
        return manager;
    }

    public RawBackendState getRawBackendState(BookMetaData metadata) throws BookException {
        ensureNotShuttingDown();
        RawBackendState state = getInstance(metadata);
        if (state == null) {
            LOGGER.trace("Initializing: {}", metadata.getInitials());
            return new RawBackendState(metadata);
        }
        LOGGER.trace("Reusing: {}", metadata.getInitials());
        return state;
    }

    public RawFileBackendState getRawFileBackendState(BookMetaData metadata) throws BookException {
        ensureNotShuttingDown();
        RawFileBackendState state = getInstance(metadata);
        if (state == null) {
            LOGGER.trace("Initializing: {}", metadata.getInitials());
            return new RawFileBackendState(metadata);
        }
        LOGGER.trace("Reusing: {}", metadata.getInitials());
        return state;
    }

    public GenBookBackendState getGenBookBackendState(BookMetaData metadata) throws BookException {
        ensureNotShuttingDown();
        GenBookBackendState state = getInstance(metadata);
        if (state == null) {
            LOGGER.trace("Initializing: {}", metadata.getInitials());
            return new GenBookBackendState(metadata);
        }
        LOGGER.trace("Reusing: {}", metadata.getInitials());
        return state;
    }

    public RawLDBackendState getRawLDBackendState(BookMetaData metadata) throws BookException {
        ensureNotShuttingDown();
        RawLDBackendState state = getInstance(metadata);
        if (state == null) {
            LOGGER.trace("Initializing: {}", metadata.getInitials());
            return new RawLDBackendState(metadata);
        }
        LOGGER.trace("Reusing: {}", metadata.getInitials());
        return state;
    }

    public ZLDBackendState getZLDBackendState(BookMetaData metadata) throws BookException {
        ensureNotShuttingDown();
        ZLDBackendState state = getInstance(metadata);
        if (state == null) {
            LOGGER.trace("Initializing: {}", metadata.getInitials());
            return new ZLDBackendState(metadata);
        }
        LOGGER.trace("Reusing: {}", metadata.getInitials());
        return state;
    }

    public ZVerseBackendState getZVerseBackendState(BookMetaData metadata, BlockType blockType) throws BookException {
        ensureNotShuttingDown();
        ZVerseBackendState state = getInstance(metadata);
        if (state == null) {
            LOGGER.trace("Initializing: {}", metadata.getInitials());
            return new ZVerseBackendState(metadata, blockType);
        }
        LOGGER.trace("Reusing: {}", metadata.getInitials());
        return state;
    }

    private <T extends OpenFileState> T getInstance(BookMetaData metadata) {
        Queue<OpenFileState> availableStates = getQueueForMeta(metadata);
        OpenFileState openFileState = availableStates.poll();
        if (openFileState != null) {
            openFileState.setLastAccess(System.currentTimeMillis());
        }
        return (T) openFileState;
    }

    private Queue<OpenFileState> getQueueForMeta(BookMetaData metadata) {
        Queue<OpenFileState> availableStates = this.metaToStates.get(metadata);
        if (availableStates == null)
      synchronized (OpenFileState.class) {
            availableStates = new ConcurrentLinkedQueue<OpenFileState>();
            this.metaToStates.put(metadata, availableStates);
        }
        return availableStates;
    }

    public void release(OpenFileState fileState) {
        if (fileState == null) {
            return;
        }
        fileState.setLastAccess(System.currentTimeMillis());
        BookMetaData bmd = fileState.getBookMetaData();
        Queue<OpenFileState> queueForMeta = getQueueForMeta(bmd);
        LOGGER.trace("Offering to releasing: {}", bmd.getInitials());
        boolean offered = queueForMeta.offer(fileState);
        if (!offered) {
            LOGGER.trace("Released: {}", bmd.getInitials());
            fileState.releaseResources();
        }
    }

    public void shutDown() {
        this.shuttingDown = true;
        this.monitoringThread.cancel(true);
        for (Queue<OpenFileState> e : this.metaToStates.values()) {
            OpenFileState state = null;
            while ((state = e.poll()) != null) {
                state.releaseResources();
            }
        }
    }

    private void ensureNotShuttingDown() throws BookException {
        if (this.shuttingDown) {
            throw new BookException("Unable to read book, application is shutting down.");
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenFileStateManager.class);
}
