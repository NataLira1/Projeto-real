package com.linbit.linstor.backupshipping;

import com.linbit.linstor.logging.StderrErrorReporter;
import org.junit.Ignore;
import org.junit.Test;

public class BackupShippingL2LDaemonDiffblueTest {
    /**
     * Test {@link BackupShippingL2LDaemon#BackupShippingL2LDaemon(ErrorReporter, ThreadGroup, String, String[], Integer, BiConsumer, Long)}.
     * <ul>
     *   <li>When {@code Thread Name}.</li>
     * </ul>
     * <p>
     * Method under test: {@link BackupShippingL2LDaemon#BackupShippingL2LDaemon(ErrorReporter, ThreadGroup, String, String[], Integer, BiConsumer, Long)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNewBackupShippingL2LDaemon_whenThreadName3() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot read the array length because "command" is null
        //       at java.base/java.lang.ProcessBuilder.<init>(ProcessBuilder.java:229)
        //       at com.linbit.extproc.DaemonHandler.<init>(DaemonHandler.java:30)
        //       at com.linbit.linstor.backupshipping.BackupShippingL2LDaemon.<init>(BackupShippingL2LDaemon.java:66)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");

        // Act
        new BackupShippingL2LDaemon(errorReporterRef, new ThreadGroup("foo"), "Thread Name", null, 1, null, 10L);

    }

    /**
     * Test {@link BackupShippingL2LDaemon#BackupShippingL2LDaemon(ErrorReporter, ThreadGroup, String, String[], Integer, BiConsumer, Long)}.
     * <ul>
     *   <li>When {@code Thread Name}.</li>
     * </ul>
     * <p>
     * Method under test: {@link BackupShippingL2LDaemon#BackupShippingL2LDaemon(ErrorReporter, ThreadGroup, String, String[], Integer, BiConsumer, Long)}
     */
    @Test
    public void testNewBackupShippingL2LDaemon_whenThreadName2() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     BackupShippingL2LDaemon.afterTermination
        //     BackupShippingL2LDaemon.alreadyInUse
        //     BackupShippingL2LDaemon.command
        //     BackupShippingL2LDaemon.deque
        //     BackupShippingL2LDaemon.errorReporter
        //     BackupShippingL2LDaemon.handler
        //     BackupShippingL2LDaemon.port
        //     BackupShippingL2LDaemon.prepareAbort
        //     BackupShippingL2LDaemon.runAfterTermination
        //     BackupShippingL2LDaemon.shutdown
        //     BackupShippingL2LDaemon.started
        //     BackupShippingL2LDaemon.syncObj
        //     BackupShippingL2LDaemon.thread
        //     BackupShippingL2LDaemon.timeoutInMs
        //     BackupShippingL2LDaemon.waitForConnDeque

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");

        // Act
        new BackupShippingL2LDaemon(errorReporterRef, new ThreadGroup("foo"), "Thread Name", new String[]{"Command Ref"}, 1,
                null, null);

    }

    /**
     * Test {@link BackupShippingL2LDaemon#BackupShippingL2LDaemon(ErrorReporter, ThreadGroup, String, String[], Integer, BiConsumer, Long)}.
     * <ul>
     *   <li>When array of {@link String} with {@code Command Ref}.</li>
     * </ul>
     * <p>
     * Method under test: {@link BackupShippingL2LDaemon#BackupShippingL2LDaemon(ErrorReporter, ThreadGroup, String, String[], Integer, BiConsumer, Long)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNewBackupShippingL2LDaemon_whenArrayOfStringWithCommandRef() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: name cannot be null
        //       at java.base/java.lang.Thread.<init>(Thread.java:404)
        //       at java.base/java.lang.Thread.<init>(Thread.java:715)
        //       at java.base/java.lang.Thread.<init>(Thread.java:636)
        //       at com.linbit.linstor.backupshipping.BackupShippingL2LDaemon.<init>(BackupShippingL2LDaemon.java:70)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");

        // Act
        new BackupShippingL2LDaemon(errorReporterRef, new ThreadGroup("foo"), null, new String[]{"Command Ref"}, 1, null,
                10L);

    }

    /**
     * Test {@link BackupShippingL2LDaemon#start()}.
     * <p>
     * Method under test: {@link BackupShippingL2LDaemon#start()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testStart() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Sandboxing policy violation.
        //   Diffblue Cover ran code in your project that tried
        //     to execute an external process.
        //   Diffblue Cover's default sandboxing policy disallows this in order to prevent
        //   your code from damaging your system environment.
        //   See https://diff.blue/R011 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");

        // Act
        (new BackupShippingL2LDaemon(errorReporterRef, new ThreadGroup("foo"), "Thread Name",
                new String[]{"com.linbit.extproc.OutputProxy$Event"}, 1, null, 10L)).start();
    }

    /**
     * Test {@link BackupShippingL2LDaemon#start()}.
     * <p>
     * Method under test: {@link BackupShippingL2LDaemon#start()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testStart2() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Sandboxing policy violation.
        //   Diffblue Cover ran code in your project that tried
        //     to execute an external process.
        //   Diffblue Cover's default sandboxing policy disallows this in order to prevent
        //   your code from damaging your system environment.
        //   See https://diff.blue/R011 to resolve this issue.

        // Arrange and Act
        (new BackupShippingL2LDaemon(null, new ThreadGroup("foo"), "Thread Name", new String[]{"Command Ref"}, 1, null,
                10L)).start();
    }

    /**
     * Test {@link BackupShippingL2LDaemon#start()}.
     * <p>
     * Method under test: {@link BackupShippingL2LDaemon#start()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testStart3() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Sandboxing policy violation.
        //   Diffblue Cover ran code in your project that tried
        //     to execute an external process.
        //   Diffblue Cover's default sandboxing policy disallows this in order to prevent
        //   your code from damaging your system environment.
        //   See https://diff.blue/R011 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");

        // Act
        (new BackupShippingL2LDaemon(errorReporterRef, new ThreadGroup("foo"), "Thread Name", new String[]{"Command Ref"},
                1, null, null)).start();
    }

    /**
     * Test {@link BackupShippingL2LDaemon#start()}.
     * <p>
     * Method under test: {@link BackupShippingL2LDaemon#start()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testStart4() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Sandboxing policy violation.
        //   Diffblue Cover ran code in your project that tried
        //     to execute an external process.
        //   Diffblue Cover's default sandboxing policy disallows this in order to prevent
        //   your code from damaging your system environment.
        //   See https://diff.blue/R011 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");

        // Act
        (new BackupShippingL2LDaemon(errorReporterRef, new ThreadGroup("foo"), "Thread Name", new String[]{"Command Ref"},
                1, null, 10L)).start();
    }

    /**
     * Test {@link BackupShippingL2LDaemon#start()}.
     * <p>
     * Method under test: {@link BackupShippingL2LDaemon#start()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testStart5() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Sandboxing policy violation.
        //   Diffblue Cover ran code in your project that tried
        //     to execute an external process.
        //   Diffblue Cover's default sandboxing policy disallows this in order to prevent
        //   your code from damaging your system environment.
        //   See https://diff.blue/R011 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");

        // Act
        (new BackupShippingL2LDaemon(errorReporterRef, new ThreadGroup("foo"), "Thread Name", new String[]{"Command Ref"},
                1, null, Long.MIN_VALUE)).start();
    }

    /**
     * Test {@link BackupShippingL2LDaemon#start()}.
     * <ul>
     *   <li>Given {@link StderrErrorReporter#StderrErrorReporter(String)} with moduleName is {@code 42}.</li>
     * </ul>
     * <p>
     * Method under test: {@link BackupShippingL2LDaemon#start()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testStart_givenStderrErrorReporterWithModuleNameIs42() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Sandboxing policy violation.
        //   Diffblue Cover ran code in your project that tried
        //     to execute an external process.
        //   Diffblue Cover's default sandboxing policy disallows this in order to prevent
        //   your code from damaging your system environment.
        //   See https://diff.blue/R011 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("42");

        // Act
        (new BackupShippingL2LDaemon(errorReporterRef, new ThreadGroup("foo"), "Thread Name", new String[]{"Command Ref"},
                1, null, null)).start();
    }

    /**
     * Test {@link BackupShippingL2LDaemon#BackupShippingL2LDaemon(ErrorReporter, ThreadGroup, String, String[], Integer, BiConsumer, Long)}.
     * <ul>
     *   <li>When {@code Thread Name}.</li>
     * </ul>
     * <p>
     * Method under test: {@link BackupShippingL2LDaemon#BackupShippingL2LDaemon(ErrorReporter, ThreadGroup, String, String[], Integer, BiConsumer, Long)}
     */
    @Test
    public void testNewBackupShippingL2LDaemon_whenThreadName() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     BackupShippingL2LDaemon.afterTermination
        //     BackupShippingL2LDaemon.alreadyInUse
        //     BackupShippingL2LDaemon.command
        //     BackupShippingL2LDaemon.deque
        //     BackupShippingL2LDaemon.errorReporter
        //     BackupShippingL2LDaemon.handler
        //     BackupShippingL2LDaemon.port
        //     BackupShippingL2LDaemon.prepareAbort
        //     BackupShippingL2LDaemon.runAfterTermination
        //     BackupShippingL2LDaemon.shutdown
        //     BackupShippingL2LDaemon.started
        //     BackupShippingL2LDaemon.syncObj
        //     BackupShippingL2LDaemon.thread
        //     BackupShippingL2LDaemon.timeoutInMs
        //     BackupShippingL2LDaemon.waitForConnDeque

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");

        // Act
        new BackupShippingL2LDaemon(errorReporterRef, new ThreadGroup("foo"), "Thread Name", new String[]{"Command Ref"}, 1,
                null, 10L);

    }

    /**
     * Test {@link BackupShippingL2LDaemon#run()}.
     * <p>
     * Method under test: {@link BackupShippingL2LDaemon#run()}
     */
    @Test
    public void testRun() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     BackupShippingL2LDaemon.afterTermination
        //     BackupShippingL2LDaemon.alreadyInUse
        //     BackupShippingL2LDaemon.command
        //     BackupShippingL2LDaemon.deque
        //     BackupShippingL2LDaemon.errorReporter
        //     BackupShippingL2LDaemon.handler
        //     BackupShippingL2LDaemon.port
        //     BackupShippingL2LDaemon.prepareAbort
        //     BackupShippingL2LDaemon.runAfterTermination
        //     BackupShippingL2LDaemon.shutdown
        //     BackupShippingL2LDaemon.started
        //     BackupShippingL2LDaemon.syncObj
        //     BackupShippingL2LDaemon.thread
        //     BackupShippingL2LDaemon.timeoutInMs
        //     BackupShippingL2LDaemon.waitForConnDeque

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");

        // Act
        (new BackupShippingL2LDaemon(errorReporterRef, new ThreadGroup("foo"), "Thread Name", new String[]{"Command Ref"},
                1, null, 10L)).run();
    }

    /**
     * Test {@link BackupShippingL2LDaemon#run()}.
     * <p>
     * Method under test: {@link BackupShippingL2LDaemon#run()}
     */
    @Test
    public void testRun2() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     BackupShippingL2LDaemon.afterTermination
        //     BackupShippingL2LDaemon.alreadyInUse
        //     BackupShippingL2LDaemon.command
        //     BackupShippingL2LDaemon.deque
        //     BackupShippingL2LDaemon.errorReporter
        //     BackupShippingL2LDaemon.handler
        //     BackupShippingL2LDaemon.port
        //     BackupShippingL2LDaemon.prepareAbort
        //     BackupShippingL2LDaemon.runAfterTermination
        //     BackupShippingL2LDaemon.shutdown
        //     BackupShippingL2LDaemon.started
        //     BackupShippingL2LDaemon.syncObj
        //     BackupShippingL2LDaemon.thread
        //     BackupShippingL2LDaemon.timeoutInMs
        //     BackupShippingL2LDaemon.waitForConnDeque

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");

        // Act
        (new BackupShippingL2LDaemon(errorReporterRef, new ThreadGroup("foo"), "Thread Name", new String[]{"Command Ref"},
                1, null, null)).run();
    }

    /**
     * Test {@link BackupShippingL2LDaemon#run()}.
     * <p>
     * Method under test: {@link BackupShippingL2LDaemon#run()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testRun3() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.logging.ErrorReporter.logTrace(String, Object[])" because "this.errorReporter" is null
        //       at com.linbit.linstor.backupshipping.BackupShippingL2LDaemon.run(BackupShippingL2LDaemon.java:168)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange and Act
        (new BackupShippingL2LDaemon(null, new ThreadGroup("foo"), "Thread Name", new String[]{"Command Ref"}, 1, null,
                10L)).run();
    }

    /**
     * Test {@link BackupShippingL2LDaemon#shutdown(boolean)}.
     * <p>
     * Method under test: {@link BackupShippingL2LDaemon#shutdown(boolean)}
     */
    @Test
    public void testShutdown() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     BackupShippingL2LDaemon.afterTermination
        //     BackupShippingL2LDaemon.alreadyInUse
        //     BackupShippingL2LDaemon.command
        //     BackupShippingL2LDaemon.deque
        //     BackupShippingL2LDaemon.errorReporter
        //     BackupShippingL2LDaemon.handler
        //     BackupShippingL2LDaemon.port
        //     BackupShippingL2LDaemon.prepareAbort
        //     BackupShippingL2LDaemon.runAfterTermination
        //     BackupShippingL2LDaemon.shutdown
        //     BackupShippingL2LDaemon.started
        //     BackupShippingL2LDaemon.syncObj
        //     BackupShippingL2LDaemon.thread
        //     BackupShippingL2LDaemon.timeoutInMs
        //     BackupShippingL2LDaemon.waitForConnDeque

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");

        // Act
        (new BackupShippingL2LDaemon(errorReporterRef, new ThreadGroup("foo"), "Thread Name", new String[]{"Command Ref"},
                1, null, 10L)).shutdown(true);
    }

    /**
     * Test {@link BackupShippingL2LDaemon#shutdown(boolean)}.
     * <p>
     * Method under test: {@link BackupShippingL2LDaemon#shutdown(boolean)}
     */
    @Test
    public void testShutdown2() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     BackupShippingL2LDaemon.afterTermination
        //     BackupShippingL2LDaemon.alreadyInUse
        //     BackupShippingL2LDaemon.command
        //     BackupShippingL2LDaemon.deque
        //     BackupShippingL2LDaemon.errorReporter
        //     BackupShippingL2LDaemon.handler
        //     BackupShippingL2LDaemon.port
        //     BackupShippingL2LDaemon.prepareAbort
        //     BackupShippingL2LDaemon.runAfterTermination
        //     BackupShippingL2LDaemon.shutdown
        //     BackupShippingL2LDaemon.started
        //     BackupShippingL2LDaemon.syncObj
        //     BackupShippingL2LDaemon.thread
        //     BackupShippingL2LDaemon.timeoutInMs
        //     BackupShippingL2LDaemon.waitForConnDeque

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");

        // Act
        (new BackupShippingL2LDaemon(errorReporterRef, new ThreadGroup("foo"), "Thread Name", new String[]{"Command Ref"},
                1, null, null)).shutdown(true);
    }

    /**
     * Test {@link BackupShippingL2LDaemon#awaitShutdown(long)}.
     * <ul>
     *   <li>When minus one.</li>
     * </ul>
     * <p>
     * Method under test: {@link BackupShippingL2LDaemon#awaitShutdown(long)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testAwaitShutdown_whenMinusOne() throws InterruptedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.IllegalArgumentException: timeout value is negative
        //       at java.base/java.lang.Thread.join(Thread.java:1316)
        //       at com.linbit.linstor.backupshipping.BackupShippingL2LDaemon.awaitShutdown(BackupShippingL2LDaemon.java:291)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");

        // Act
        (new BackupShippingL2LDaemon(errorReporterRef, new ThreadGroup("foo"), "Thread Name", new String[]{"Command Ref"},
                1, null, 10L)).awaitShutdown(-1L);
    }

    /**
     * Test {@link BackupShippingL2LDaemon#awaitShutdown(long)}.
     * <ul>
     *   <li>When ten.</li>
     * </ul>
     * <p>
     * Method under test: {@link BackupShippingL2LDaemon#awaitShutdown(long)}
     */
    @Test
    public void testAwaitShutdown_whenTen() throws InterruptedException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     BackupShippingL2LDaemon.afterTermination
        //     BackupShippingL2LDaemon.alreadyInUse
        //     BackupShippingL2LDaemon.command
        //     BackupShippingL2LDaemon.deque
        //     BackupShippingL2LDaemon.errorReporter
        //     BackupShippingL2LDaemon.handler
        //     BackupShippingL2LDaemon.port
        //     BackupShippingL2LDaemon.prepareAbort
        //     BackupShippingL2LDaemon.runAfterTermination
        //     BackupShippingL2LDaemon.shutdown
        //     BackupShippingL2LDaemon.started
        //     BackupShippingL2LDaemon.syncObj
        //     BackupShippingL2LDaemon.thread
        //     BackupShippingL2LDaemon.timeoutInMs
        //     BackupShippingL2LDaemon.waitForConnDeque

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");

        // Act
        (new BackupShippingL2LDaemon(errorReporterRef, new ThreadGroup("foo"), "Thread Name", new String[]{"Command Ref"},
                1, null, 10L)).awaitShutdown(10L);
    }

    /**
     * Test {@link BackupShippingL2LDaemon#setPrepareAbort()}.
     * <p>
     * Method under test: {@link BackupShippingL2LDaemon#setPrepareAbort()}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testSetPrepareAbort() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "java.util.function.BiConsumer.accept(Object, Object)" because "this.afterTermination" is null
        //       at com.linbit.linstor.backupshipping.BackupShippingL2LDaemon.setPrepareAbort(BackupShippingL2LDaemon.java:304)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");

        // Act
        (new BackupShippingL2LDaemon(errorReporterRef, new ThreadGroup("foo"), "Thread Name", new String[]{"Command Ref"},
                1, null, 10L)).setPrepareAbort();
    }
}
