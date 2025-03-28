package com.linbit.linstor.core.apicallhandler;

import com.linbit.drbd.DrbdVersion;
import com.linbit.extproc.ExtCmdFactory;
import com.linbit.linstor.core.cfg.StltConfig;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdEventService;
import com.linbit.linstor.layer.drbd.drbdstate.DrbdStateTracker;
import com.linbit.linstor.logging.StderrErrorReporter;
import com.linbit.linstor.storage.kinds.ExtTools;
import com.linbit.linstor.timer.CoreTimerImpl;
import org.junit.Ignore;
import org.junit.Test;

public class StltExtToolsCheckerDiffblueTest {
    /**
     * Test {@link StltExtToolsChecker#getExternalTools(boolean)}.
     * <p>
     * Method under test: {@link StltExtToolsChecker#getExternalTools(boolean)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testGetExternalTools() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.linbit.ImplementationError: Attempt to create an instance of class com.linbit.extproc.ChildProcessHandler with a null com.linbit.timer.Timer reference
        //       at com.linbit.ErrorCheck.ctorNotNull(ErrorCheck.java:20)
        //       at com.linbit.extproc.ChildProcessHandler.<init>(ChildProcessHandler.java:50)
        //       at com.linbit.extproc.ExtCmd.<init>(ExtCmd.java:47)
        //       at com.linbit.drbd.DrbdVersion.checkVersions(DrbdVersion.java:92)
        //       at com.linbit.linstor.core.apicallhandler.StltExtToolsChecker.getExternalTools(StltExtToolsChecker.java:205)
        //   java.lang.NullPointerException
        //       at com.linbit.ErrorCheck.ctorNotNull(ErrorCheck.java:20)
        //       at com.linbit.extproc.ChildProcessHandler.<init>(ChildProcessHandler.java:50)
        //       at com.linbit.extproc.ExtCmd.<init>(ExtCmd.java:47)
        //       at com.linbit.drbd.DrbdVersion.checkVersions(DrbdVersion.java:92)
        //       at com.linbit.linstor.core.apicallhandler.StltExtToolsChecker.getExternalTools(StltExtToolsChecker.java:205)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(null, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StltConfig stltCfgRef = new StltConfig();
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();

        // Act
        (new StltExtToolsChecker(errorReporterRef, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef, new DrbdEventService(
                errorReporterRef2, trackerRef, null, new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name")))))
                .getExternalTools(true);
    }

    /**
     * Test {@link StltExtToolsChecker#getExternalTools(boolean)}.
     * <p>
     * Method under test: {@link StltExtToolsChecker#getExternalTools(boolean)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testGetExternalTools2() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Sandboxing policy violation.
        //   Diffblue Cover ran code in your project that tried
        //     to execute an external process.
        //   Diffblue Cover's default sandboxing policy disallows this in order to prevent
        //   your code from damaging your system environment.
        //   See https://diff.blue/R011 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StltConfig stltCfgRef = new StltConfig();
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer2 = new CoreTimerImpl();

        // Act
        (new StltExtToolsChecker(errorReporterRef, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef, new DrbdEventService(
                errorReporterRef2, trackerRef, null, new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name")))))
                .getExternalTools(true);
    }

    /**
     * Test {@link StltExtToolsChecker#areSupported(boolean, ExtTools[])}.
     * <p>
     * Method under test: {@link StltExtToolsChecker#areSupported(boolean, ExtTools[])}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testAreSupported() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.linbit.ImplementationError: Attempt to create an instance of class com.linbit.extproc.ChildProcessHandler with a null com.linbit.timer.Timer reference
        //       at com.linbit.ErrorCheck.ctorNotNull(ErrorCheck.java:20)
        //       at com.linbit.extproc.ChildProcessHandler.<init>(ChildProcessHandler.java:50)
        //       at com.linbit.extproc.ExtCmd.<init>(ExtCmd.java:47)
        //       at com.linbit.drbd.DrbdVersion.checkVersions(DrbdVersion.java:92)
        //       at com.linbit.linstor.core.apicallhandler.StltExtToolsChecker.getExternalTools(StltExtToolsChecker.java:205)
        //       at com.linbit.linstor.core.apicallhandler.StltExtToolsChecker.areSupported(StltExtToolsChecker.java:276)
        //   java.lang.NullPointerException
        //       at com.linbit.ErrorCheck.ctorNotNull(ErrorCheck.java:20)
        //       at com.linbit.extproc.ChildProcessHandler.<init>(ChildProcessHandler.java:50)
        //       at com.linbit.extproc.ExtCmd.<init>(ExtCmd.java:47)
        //       at com.linbit.drbd.DrbdVersion.checkVersions(DrbdVersion.java:92)
        //       at com.linbit.linstor.core.apicallhandler.StltExtToolsChecker.getExternalTools(StltExtToolsChecker.java:205)
        //       at com.linbit.linstor.core.apicallhandler.StltExtToolsChecker.areSupported(StltExtToolsChecker.java:276)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(null, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StltConfig stltCfgRef = new StltConfig();
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer = new CoreTimerImpl();

        // Act
        (new StltExtToolsChecker(errorReporterRef, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                new DrbdEventService(errorReporterRef2, trackerRef, null,
                        new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name"))))).areSupported(true,
                ExtTools.DRBD9_KERNEL);
    }

    /**
     * Test {@link StltExtToolsChecker#areSupported(boolean, ExtTools[])}.
     * <p>
     * Method under test: {@link StltExtToolsChecker#areSupported(boolean, ExtTools[])}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testAreSupported2() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Sandboxing policy violation.
        //   Diffblue Cover ran code in your project that tried
        //     to execute an external process.
        //   Diffblue Cover's default sandboxing policy disallows this in order to prevent
        //   your code from damaging your system environment.
        //   See https://diff.blue/R011 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StltConfig stltCfgRef = new StltConfig();
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer2 = new CoreTimerImpl();

        // Act
        (new StltExtToolsChecker(errorReporterRef, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef,
                new DrbdEventService(errorReporterRef2, trackerRef, null,
                        new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"))))).areSupported(true,
                ExtTools.DRBD9_KERNEL);


    }

    /**
     * Test {@link StltExtToolsChecker#StltExtToolsChecker(ErrorReporter, DrbdVersion, ExtCmdFactory, StltConfig, DrbdEventService)}.
     * <p>
     * Method under test: {@link StltExtToolsChecker#StltExtToolsChecker(ErrorReporter, DrbdVersion, ExtCmdFactory, StltConfig, DrbdEventService)}
     */
    @Test
    public void testNewStltExtToolsChecker() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     StltExtToolsChecker.cache
        //     StltExtToolsChecker.drbdEventService
        //     StltExtToolsChecker.drbdVersionCheck
        //     StltExtToolsChecker.errorReporter
        //     StltExtToolsChecker.extCmdFactory
        //     StltExtToolsChecker.platformPredicate
        //     StltExtToolsChecker.stltCfg
        //     ExtCmdFactory.errlog
        //     ExtCmdFactory.timer

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        CoreTimerImpl coreTimer = new CoreTimerImpl();
        DrbdVersion drbdVersionCheckRef = new DrbdVersion(coreTimer, new StderrErrorReporter("Module Name"));

        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StltConfig stltCfgRef = new StltConfig();
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        DrbdStateTracker trackerRef = new DrbdStateTracker();
        CoreTimerImpl coreTimer2 = new CoreTimerImpl();

        // Act
        new StltExtToolsChecker(errorReporterRef, drbdVersionCheckRef, extCmdFactoryRef, stltCfgRef, new DrbdEventService(
                errorReporterRef2, trackerRef, null, new DrbdVersion(coreTimer2, new StderrErrorReporter("Module Name"))));

    }
}
