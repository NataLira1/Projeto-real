package com.linbit.linstor.api.protobuf;

import com.linbit.linstor.api.protobuf.serializer.ProtoCommonSerializer;
import com.linbit.linstor.core.apicallhandler.ResponseSerializer;
import com.linbit.linstor.logging.StderrErrorReporter;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Ignore;
import org.junit.Test;
import reactor.core.publisher.Flux;

public class BackupShippingFinishedDiffblueTest {
    /**
     * Test {@link BackupShippingFinished#executeReactive(InputStream)}.
     * <p>
     * Method under test: {@link BackupShippingFinished#executeReactive(InputStream)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testExecuteReactive() throws IOException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Can't find a suitable constructor.
        //   Diffblue Cover was unable to construct an instance of BackupShippingFinished.
        //   No suitable constructor or factory method found. Please check that the class
        //   under test has a non-private constructor or factory method.
        //   See https://diff.blue/R083 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        BackupShippingFinished backupShippingFinished = null;
        InputStream msgDataInRef = null;

        // Act
        Flux<byte[]> actualExecuteReactiveResult = backupShippingFinished.executeReactive(msgDataInRef);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test {@link BackupShippingFinished#BackupShippingFinished(StltApiCallHandler, ResponseSerializer)}.
     * <p>
     * Method under test: {@link BackupShippingFinished#BackupShippingFinished(StltApiCallHandler, ResponseSerializer)}
     */
    @Test
    public void testNewBackupShippingFinished() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     BackupShippingFinished.apiCallHandler
        //     BackupShippingFinished.responseSerializer

        // Arrange and Act
        new BackupShippingFinished(null,
                new ResponseSerializer(new ProtoCommonSerializer(new StderrErrorReporter("Module Name"), null)));

    }
}
