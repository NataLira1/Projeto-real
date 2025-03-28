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
