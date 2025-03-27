package com.linbit.linstor.core.apicallhandler;

import com.linbit.linstor.api.pojo.FileInfoPojo;
import com.linbit.linstor.core.cfg.StltConfig;
import com.linbit.linstor.logging.StderrErrorReporter;
import com.linbit.utils.Pair;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

public class StltSosReportApiCallHandlerDiffblueTest {
    /**
     * Test {@link StltSosReportApiCallHandler#handleSosReportRequestFileList(String, LocalDateTime)}.
     * <p>
     * Method under test: {@link StltSosReportApiCallHandler#handleSosReportRequestFileList(String, LocalDateTime)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testHandleSosReportRequestFileList() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "Object.getClass()" because "obj" is null
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        StltSosReportApiCallHandler stltSosReportApiCallHandler = new StltSosReportApiCallHandler(errorReporterRef,
                new StltConfig());

        // Act
        Pair<List<FileInfoPojo>, String> actualHandleSosReportRequestFileListResult = stltSosReportApiCallHandler
                .handleSosReportRequestFileList("Sos Report Name", LocalDate.of(1970, 1, 1).atStartOfDay());
        actualHandleSosReportRequestFileListResult.compareTo(new Pair<>());
    }

    /**
     * Test {@link StltSosReportApiCallHandler#getRequestedSosReportFiles(List)}.
     * <ul>
     *   <li>When {@link ArrayList#ArrayList()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link StltSosReportApiCallHandler#getRequestedSosReportFiles(List)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testGetRequestedSosReportFiles_whenArrayList() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "java.nio.file.Path.toString()" because the return value of "com.linbit.linstor.logging.ErrorReporter.getLogDirectory()" is null
        //       at com.linbit.linstor.core.apicallhandler.StltSosReportApiCallHandler.getRequestedSosReportFiles(StltSosReportApiCallHandler.java:288)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        StltSosReportApiCallHandler stltSosReportApiCallHandler = new StltSosReportApiCallHandler(errorReporterRef,
                new StltConfig());

        // Act
        stltSosReportApiCallHandler.getRequestedSosReportFiles(new ArrayList<>());
    }

    /**
     * Test {@link StltSosReportApiCallHandler#handleSosReportCleanup(String)}.
     * <p>
     * Method under test: {@link StltSosReportApiCallHandler#handleSosReportCleanup(String)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testHandleSosReportCleanup() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Sandboxing policy violation.
        //   Diffblue Cover ran code in your project that tried
        //     to access files (file '/var/lib/linstor.d/sos-reports/Sos Report Name Ref', permission 'delete').
        //   Diffblue Cover's default sandboxing policy disallows this in order to prevent
        //   your code from damaging your system environment.
        //   See https://diff.blue/R011 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");

        // Act
        (new StltSosReportApiCallHandler(errorReporterRef, new StltConfig())).handleSosReportCleanup("Sos Report Name Ref");
    }

    /**
     * Test {@link StltSosReportApiCallHandler#StltSosReportApiCallHandler(ErrorReporter, StltConfig)}.
     * <p>
     * Method under test: {@link StltSosReportApiCallHandler#StltSosReportApiCallHandler(ErrorReporter, StltConfig)}
     */
    @Test
    public void testNewStltSosReportApiCallHandler() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     StltSosReportApiCallHandler.errorReporter
        //     StltSosReportApiCallHandler.stltCfg

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");

        // Act
        new StltSosReportApiCallHandler(errorReporterRef, new StltConfig());

    }
}
