package com.linbit.linstor.api.protobuf;

import com.linbit.linstor.logging.StderrErrorReporter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Ignore;
import org.junit.Test;

public class ArchiveLogsDiffblueTest {
    /**
     * Test {@link ArchiveLogs#execute(InputStream)}.
     * <ul>
     *   <li>Given {@link ArchiveLogs#ArchiveLogs(ErrorReporter)} with errorReporterRef is {@code null}.</li>
     * </ul>
     * <p>
     * Method under test: {@link ArchiveLogs#execute(InputStream)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testExecute_givenArchiveLogsWithErrorReporterRefIsNull() throws IOException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.logging.ErrorReporter.archiveLogDirectory()" because "this.errorReporter" is null
        //       at com.linbit.linstor.api.protobuf.ArchiveLogs.execute(ArchiveLogs.java:33)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        ArchiveLogs archiveLogs = new ArchiveLogs(null);

        // Act
        archiveLogs.execute(new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8")));
    }

    /**
     * Test {@link ArchiveLogs#execute(InputStream)}.
     * <ul>
     *   <li>Given {@link StderrErrorReporter#StderrErrorReporter(String)} with {@code Module Name}.</li>
     * </ul>
     * <p>
     * Method under test: {@link ArchiveLogs#execute(InputStream)}
     */
    @Test
    public void testExecute_givenStderrErrorReporterWithModuleName() throws IOException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     ArchiveLogs.errorReporter

        // Arrange
        ArchiveLogs archiveLogs = new ArchiveLogs(new StderrErrorReporter("Module Name"));

        // Act
        archiveLogs.execute(new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8")));
    }

    /**
     * Test {@link ArchiveLogs#ArchiveLogs(ErrorReporter)}.
     * <p>
     * Method under test: {@link ArchiveLogs#ArchiveLogs(ErrorReporter)}
     */
    @Test
    public void testNewArchiveLogs() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     ArchiveLogs.errorReporter

        // Arrange and Act
        new ArchiveLogs(new StderrErrorReporter("Module Name"));
    }
}
