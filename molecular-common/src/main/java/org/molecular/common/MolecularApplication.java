/*
 * This file ("MolecularApplication.java") is part of the molecular-project by Louis.
 * Copyright © 2017 Louis
 *
 * The molecular-project is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The molecular-project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with molecular-project.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.molecular.common;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.molecular.api.Constants;
import org.molecular.api.base.Semantic;
import org.molecular.api.concurrent.ThreadTickable;
import org.molecular.api.concurrent.TickExecution;
import org.molecular.api.platform.PlatformApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import static org.molecular.api.base.NumberParser.asInt;

/**
 * @author Louis
 */

public abstract class MolecularApplication implements PlatformApplication, Runnable {

    protected final Logger logger = LoggerFactory.getLogger("org.molecular");

    protected final int version;
    protected final int protocol;
    protected final Semantic semantic;

    protected final Path directory;
    protected final SocketAddress address;

    private final Queue<FutureTask<?>> scheduled = new ArrayDeque<>();
    private final List<ThreadTickable> repeating = new ArrayList<>();

    protected int counter = 0;

    protected Thread heartbeat;

    protected volatile boolean running = false;

    private int timeTick = 1000 / Constants.TICK_PER_SECONDS;
    private long lastWarn = 0L;
    private long lastTick = 0L;

    protected MolecularApplication(Class<?> clazz, Path directory, SocketAddress address) {
        this.version = asInt(clazz.getPackage().getImplementationVersion(), -1);
        this.semantic = new Semantic(this.version);
        this.protocol = 1;
        this.directory = directory;
        this.address = address;
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    //<editor-fold desc="Application Ticking">
    @Override
    public void run() {
        try {
            this.lastTick = System.currentTimeMillis();

            long tickingTime = 0L;
            if (this.doFirstTick()) {
                while (this.running) {
                    long current = System.currentTimeMillis();
                    long delta = current - this.lastTick;

                    if (delta < 0L) {
                        this.logger.warn("Running {}ms ahead, waiting {} tick(s)", delta, (delta / this.timeTick) * -1);
                        Thread.sleep(delta * -1);
                    }

                    if (delta > Constants.TICK_MAX_LATENCY && this.lastTick - this.lastWarn >= Constants.TICK_WARN_TIME) {
                        this.logger.warn("Running {}ms behind, skipping {} tick(s)", delta, (delta / this.timeTick));
                        delta = Constants.TICK_MAX_LATENCY;
                        this.lastWarn = this.lastTick;
                    }

                    this.lastTick = current;
                    tickingTime += delta;

                    while (tickingTime > this.timeTick) {
                        tickingTime -= this.timeTick;
                        this.doRunningTick();
                        this.counter++;
                    }

                    Thread.sleep(Math.max(1L, this.timeTick - tickingTime));
                }
            } else {
                this.logger.error("Encountered an unexpected return while starting the application");
            }
        } catch (Throwable throwable) {
            this.logger.error("Encountered an unexpected exception while starting/running the application", throwable);
        } finally {
            try {
                this.doLastTick();
            } catch (Throwable throwable) {
                this.logger.error("Encountered an unexpected exception while stopping the application", throwable);
            } finally {
                Runtime.getRuntime().exit(0);
            }
        }
    }

    @Override
    public void startup() {
        if (!this.running) {
            this.running = true;
            (this.heartbeat = new Thread(this, "Application Heartbeat")).start();
        } else {
            logger.warn("The application is already running and can not be started again");
        }
    }
    //</editor-fold>

    @Override
    public void shutdown() {
        if (this.running) {
            this.running = false;
        } else {
            logger.warn("The application is already stopped and can not be stopped again");
        }
    }

    @Override
    public Semantic getSemantic() {
        return this.semantic;
    }

    @Override
    public int getVersion() {
        return this.version;
    }

    @Override
    public int getProtocol() {
        return this.protocol;
    }

    @Override
    public Path getRootPath() {
        return this.directory;
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    protected abstract boolean doFirstTick();

    protected abstract boolean doLastTick();

    protected abstract void doRunningTick();

    @TickExecution
    protected void execute() {
        while (!this.scheduled.isEmpty()) {
            FutureTask<?> poll = this.scheduled.poll();
            try {
                poll.run();
                poll.get();
            } catch (Throwable throwable) {
                logger.error("Error while executing sync scheduled task {}", poll.getClass(), throwable);
            }
        }
        for (ThreadTickable tickable : this.repeating) {
            try {
                tickable.doTick();
            } catch (Throwable throwable) {
                this.repeating.remove(tickable);
                logger.error("Error while executing tickable {}", tickable.getClass(), throwable);
            }
        }
    }

    @Override
    public void scheduleTickable(@Nonnull ThreadTickable tickable) {
        this.repeating.add(tickable);
    }

    @CanIgnoreReturnValue
    @Override
    public <E> ListenableFuture<E> scheduleSyncDelayedTask(@Nonnull Callable<E> callable) {
        if (Thread.currentThread() == this.heartbeat) {
            try {
                return Futures.immediateFuture(callable.call());
            } catch (Throwable throwable) {
                return Futures.immediateFailedFuture(throwable);
            }
        } else {
            return this.scheduleDelayedTask(callable);
        }
    }

    @CanIgnoreReturnValue
    @Override
    public <E> ListenableFuture<E> scheduleDelayedTask(@Nonnull Callable<E> callable) {
        ListenableFutureTask<E> task = ListenableFutureTask.create(callable);
        this.scheduled.offer(task);
        return task;
    }

}
