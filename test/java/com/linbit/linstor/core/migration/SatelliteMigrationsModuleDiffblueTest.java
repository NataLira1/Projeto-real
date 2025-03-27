package com.linbit.linstor.core.migration;

import com.google.inject.Injector;
import com.linbit.linstor.logging.ErrorReporter;
import com.linbit.linstor.logging.StderrErrorReporter;
import org.junit.Ignore;
import org.junit.Test;

public class SatelliteMigrationsModuleDiffblueTest {
    /**
     * Test {@link SatelliteMigrationsModule#getAllStltMigrations(Injector, ErrorReporter)}.
     * <ul>
     *   <li>When {@link StderrErrorReporter#StderrErrorReporter(String)} with {@code Module Name}.</li>
     * </ul>
     * <p>
     * Method under test: {@link SatelliteMigrationsModule#getAllStltMigrations(Injector, ErrorReporter)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testGetAllStltMigrations_whenStderrErrorReporterWithModuleName() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.google.inject.Injector.getInstance(java.lang.Class)" because "injector" is null
        //       at com.linbit.linstor.core.migration.SatelliteMigrationsModule.getAllStltMigrations(SatelliteMigrationsModule.java:53)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        SatelliteMigrationsModule satelliteMigrationsModule = new SatelliteMigrationsModule();

        // Act
        satelliteMigrationsModule.getAllStltMigrations(null, new StderrErrorReporter("Module Name"));
    }

    /**
     * Test new {@link SatelliteMigrationsModule} (default constructor).
     * <p>
     * Method under test: default or parameterless constructor of {@link SatelliteMigrationsModule}
     */
    @Test
    public void testNewSatelliteMigrationsModule() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     AbstractModule.binder

        // Arrange and Act
        new SatelliteMigrationsModule();
    }
}
