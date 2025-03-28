package com.linbit.linstor.api.protobuf;

import com.linbit.linstor.api.protobuf.serializer.ProtoCommonSerializer;
import com.linbit.linstor.core.apicallhandler.ResponseSerializer;
import com.linbit.linstor.logging.StderrErrorReporter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ChangedControllerDiffblueTest {
    /**
     * Test {@link ChangedController#executeReactive(InputStream)}.
     * <p>
     * Method under test: {@link ChangedController#executeReactive(InputStream)}
     */
    @Test
    public void testExecuteReactive() throws IOException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.linbit.linstor.core.DeviceManager.getUpdateTracker()" because "this.deviceManager" is null
        //       at com.linbit.linstor.api.protobuf.ChangedController.executeReactive(ChangedController.java:42)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        ResponseSerializer serializer = new ResponseSerializer(
                new ProtoCommonSerializer(new StderrErrorReporter("Module Name"), null));
        ChangedController changedController = new ChangedController(null, serializer);

        try {
            // Act
            changedController.executeReactive(new ByteArrayInputStream("AXAXAXAX".getBytes(StandardCharsets.UTF_8)));
            fail("Expected NullPointerException");
        } catch (IOException ioEx) {
            fail("Unexpected IOException: " + ioEx.getMessage());
        } catch (NullPointerException npe) {
            // Assert
            assertEquals("Cannot invoke \"com.linbit.linstor.core.DeviceManager.getUpdateTracker()\" " +
                            "because \"this.deviceManager\" is null",
                    npe.getMessage());
        }
    }

    /**
     * Test {@link ChangedController#ChangedController(DeviceManager, ResponseSerializer)}.
     * <p>
     * Method under test: {@link ChangedController#ChangedController(DeviceManager, ResponseSerializer)}
     */
    @Test
    public void testNewChangedController() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     ChangedController.deviceManager
        //     ChangedController.responseSerializer

        // Arrange and Act
        new ChangedController(null,
                new ResponseSerializer(new ProtoCommonSerializer(new StderrErrorReporter("Module Name"), null)));

    }
}
