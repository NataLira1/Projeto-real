package com.linbit.linstor.api.protobuf;

import static org.junit.Assert.assertEquals;

import com.linbit.linstor.api.LinStorScope;
import com.linbit.linstor.api.interfaces.serializer.CommonSerializer;
import com.linbit.linstor.api.protobuf.serializer.ProtoCommonSerializer;
import com.linbit.linstor.core.StltExternalFileHandler;
import com.linbit.linstor.core.apicallhandler.ScopeRunner;
import com.linbit.linstor.core.apicallhandler.StltApiCallHandlerUtils;
import com.linbit.linstor.core.cfg.StltConfig;
import com.linbit.linstor.logging.StderrErrorReporter;
import com.linbit.linstor.transaction.manager.SatelliteTransactionMgrGenerator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import javax.inject.Provider;

import org.junit.Ignore;
import org.junit.Test;
import reactor.core.publisher.Flux;

public class ReqFreeSpaceDiffblueTest {
    /**
     * Test {@link ReqFreeSpace#executeReactive(InputStream)}.
     * <ul>
     *   <li>Then return Prefetch is minus one.</li>
     * </ul>
     * <p>
     * Method under test: {@link ReqFreeSpace#executeReactive(InputStream)}
     */
    @Test
    public void testExecuteReactive_thenReturnPrefetchIsMinusOne() throws UnsupportedEncodingException {
        // Arrange
        StderrErrorReporter errorLogRef = new StderrErrorReporter("Module Name");
        SatelliteTransactionMgrGenerator transactionMgrGeneratorRef = new SatelliteTransactionMgrGenerator();
        ScopeRunner scopeRunnerRef = new ScopeRunner(errorLogRef, transactionMgrGeneratorRef, new LinStorScope());

        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = new StltApiCallHandlerUtils(errorReporterRef, null, null,
                new StltExternalFileHandler(errorReporterRef2, null, null, null, new StltConfig()), null, null, null, null,
                null, null, null, null, null, null);

        ReqFreeSpace reqFreeSpace = new ReqFreeSpace(scopeRunnerRef, apiCallHandlerUtilsRef,
                new ProtoCommonSerializer(new StderrErrorReporter("Module Name"), null), null);

        // Act
        Flux<byte[]> actualExecuteReactiveResult = reqFreeSpace
                .executeReactive(new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8")));

        // Assert
        assertEquals(-1, actualExecuteReactiveResult.getPrefetch());
        assertEquals(-1, actualExecuteReactiveResult.buffer().getPrefetch());
        assertEquals(-1, actualExecuteReactiveResult.checkpoint().getPrefetch());
        assertEquals(-1, actualExecuteReactiveResult.elapsed().getPrefetch());
        assertEquals(-1, actualExecuteReactiveResult.timestamp().getPrefetch());
        assertEquals(256, actualExecuteReactiveResult.parallel().getPrefetch());
        assertEquals(Integer.MAX_VALUE, actualExecuteReactiveResult.cache().getPrefetch());
    }

    /**
     * Test {@link ReqFreeSpace#ReqFreeSpace(ScopeRunner, StltApiCallHandlerUtils, CommonSerializer, Provider)}.
     * <p>
     * Method under test: {@link ReqFreeSpace#ReqFreeSpace(ScopeRunner, StltApiCallHandlerUtils, CommonSerializer, Provider)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testNewReqFreeSpace() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Exception in arrange section.
        //   Diffblue Cover was unable to construct an instance of the class under test using
        //   com.linbit.linstor.api.protobuf.ReqFreeSpace.<init>(ScopeRunner, StltApiCallHandlerUtils, CommonSerializer, Provider).
        //   The arrange section threw
        //   java.lang.NullPointerException: Cannot read field "errorReporter" because "initRef" is null
        //       at com.linbit.linstor.layer.storage.AbsStorageProvider.<init>(AbsStorageProvider.java:187)
        //       at com.linbit.linstor.layer.storage.ebs.AbsEbsProvider.<init>(AbsEbsProvider.java:135)
        //       at com.linbit.linstor.layer.storage.ebs.EbsInitiatorProvider.<init>(EbsInitiatorProvider.java:110)
        //   See https://diff.blue/R081 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ScopeRunner scopeRunnerRef = null;
        StltApiCallHandlerUtils apiCallHandlerUtilsRef = null;
        CommonSerializer commonSerializerRef = null;
        Provider<Long> apiCallIdProviderRef = null;

        // Act
        ReqFreeSpace actualReqFreeSpace = new ReqFreeSpace(scopeRunnerRef, apiCallHandlerUtilsRef, commonSerializerRef,
                apiCallIdProviderRef);

        // Assert
        // TODO: Add assertions on result
    }
}
