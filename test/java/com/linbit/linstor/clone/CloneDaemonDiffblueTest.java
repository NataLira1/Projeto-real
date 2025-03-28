package com.linbit.linstor.clone;

import com.linbit.linstor.logging.StderrErrorReporter;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CloneDaemonDiffblueTest {
    /**
     * Test {@link CloneDaemon#CloneDaemon(ErrorReporter, ThreadGroup, String, String[], Consumer)}.
     * <ul>
     *   <li>When {@code Thread Name}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CloneDaemon#CloneDaemon(ErrorReporter, ThreadGroup, String, String[], Consumer)}
     */
    @Test
    public void testNewCloneDaemon_whenThreadName2() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     CloneDaemon.afterClone
        //     CloneDaemon.command
        //     CloneDaemon.deque
        //     CloneDaemon.errorReporter
        //     CloneDaemon.handler
        //     CloneDaemon.started
        //     CloneDaemon.thread

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");

        // Act
        new CloneDaemon(errorReporterRef, new ThreadGroup("foo"), "Thread Name", null, null);

    }

    /**
     * Test {@link CloneDaemon#CloneDaemon(ErrorReporter, ThreadGroup, String, String[], Consumer)}.
     * <ul>
     *   <li>When {@code Thread Name}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CloneDaemon#CloneDaemon(ErrorReporter, ThreadGroup, String, String[], Consumer)}
     */
    @Test
    public void testNewCloneDaemon_whenThreadName() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     CloneDaemon.afterClone
        //     CloneDaemon.command
        //     CloneDaemon.deque
        //     CloneDaemon.errorReporter
        //     CloneDaemon.handler
        //     CloneDaemon.started
        //     CloneDaemon.thread

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");

        // Act
        new CloneDaemon(errorReporterRef, new ThreadGroup("foo"), "Thread Name", new String[]{"Command Ref"}, null);

    }

    /**
     * Test {@link CloneDaemon#start()}.
     * <p>
     * Method under test: {@link CloneDaemon#start()}
     */
    @Test
    public void testStart2() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     CloneDaemon.afterClone
        //     CloneDaemon.command
        //     CloneDaemon.deque
        //     CloneDaemon.errorReporter
        //     CloneDaemon.handler
        //     CloneDaemon.started
        //     CloneDaemon.thread

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");

        // Act
        (new CloneDaemon(errorReporterRef, new ThreadGroup("foo"), "Thread Name", null, null)).start();
    }

    /**
     * Test {@link CloneDaemon#start()}.
     * <ul>
     *   <li>Given {@link StderrErrorReporter#StderrErrorReporter(String)} with moduleName is empty string.</li>
     * </ul>
     * <p>
     * Method under test: {@link CloneDaemon#start()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testStart_givenStderrErrorReporterWithModuleNameIsEmptyString() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Sandboxing policy violation.
        //   Diffblue Cover ran code in your project that tried
        //     to execute an external process.
        //   Diffblue Cover's default sandboxing policy disallows this in order to prevent
        //   your code from damaging your system environment.
        //   See https://diff.blue/R011 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("");

        // Act
        (new CloneDaemon(errorReporterRef, new ThreadGroup("foo"), "Thread Name", new String[]{"Command Ref"}, null))
                .start();
    }

    /**
     * Test {@link CloneDaemon#shutdown()}.
     * <p>
     * Method under test: {@link CloneDaemon#shutdown()}
     */
    @Test
    public void testShutdown() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     CloneDaemon.afterClone
        //     CloneDaemon.command
        //     CloneDaemon.deque
        //     CloneDaemon.errorReporter
        //     CloneDaemon.handler
        //     CloneDaemon.started
        //     CloneDaemon.thread

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");

        // Act
        (new CloneDaemon(errorReporterRef, new ThreadGroup("foo"), "Thread Name", new String[]{"Command Ref"}, null))
                .shutdown();
    }

    /**
     * Test {@link CloneDaemon#shutdown()}.
     * <p>
     * Method under test: {@link CloneDaemon#shutdown()}
     */
    @Test
    public void testShutdown2() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     CloneDaemon.afterClone
        //     CloneDaemon.command
        //     CloneDaemon.deque
        //     CloneDaemon.errorReporter
        //     CloneDaemon.handler
        //     CloneDaemon.started
        //     CloneDaemon.thread

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");

        // Act
        (new CloneDaemon(errorReporterRef, new ThreadGroup("foo"), "Thread Name", null, null)).shutdown();
    }

    /**
     * Test {@link CloneDaemon#awaitShutdown(long)}.
     * <ul>
     *   <li>When ten.</li>
     * </ul>
     * <p>
     * Method under test: {@link CloneDaemon#awaitShutdown(long)}
     */
    @Test
    public void testAwaitShutdown_whenTen() throws InterruptedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     CloneDaemon.afterClone
        //     CloneDaemon.command
        //     CloneDaemon.deque
        //     CloneDaemon.errorReporter
        //     CloneDaemon.handler
        //     CloneDaemon.started
        //     CloneDaemon.thread

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");

        // Act
        (new CloneDaemon(errorReporterRef, new ThreadGroup("foo"), "Thread Name", new String[]{"Command Ref"}, null))
                .awaitShutdown(10L);
    }
}
