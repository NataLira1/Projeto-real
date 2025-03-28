package com.linbit.linstor.api.protobuf;

import com.linbit.linstor.api.protobuf.serializer.ProtoCtrlStltSerializer;
import com.linbit.linstor.core.CtrlSecurityObjects;
import com.linbit.linstor.core.apicallhandler.StltSosReportApiCallHandler;
import com.linbit.linstor.core.cfg.StltConfig;
import com.linbit.linstor.logging.StderrErrorReporter;
import com.linbit.linstor.propscon.ReadOnlyPropsImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertThrows;

public class ReqSosReportCleanupDiffblueTest {
    /**
     * Test {@link ReqSosReportCleanup#execute(InputStream)}.
     * <ul>
     *   <li>When {@link ByteArrayInputStream#ByteArrayInputStream(byte[])} with {@code AXAXAXAX} Bytes is {@code UTF-8}.</li>
     * </ul>
     * <p>
     * Method under test: {@link ReqSosReportCleanup#execute(InputStream)}
     */
    @Test
    public void testExecute_whenByteArrayInputStreamWithAxaxaxaxBytesIsUtf8() throws IOException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.google.protobuf.InvalidProtocolBufferException: While parsing a protocol message, the input ended unexpectedly in the middle of a field.  This could mean either that the input has been truncated or that an embedded message misreported its own length.
        //       at com.google.protobuf.InvalidProtocolBufferException.truncatedMessage(InvalidProtocolBufferException.java:92)
        //       at com.google.protobuf.CodedInputStream$StreamDecoder.refillBuffer(CodedInputStream.java:2748)
        //       at com.google.protobuf.CodedInputStream$StreamDecoder.readRawByte(CodedInputStream.java:2824)
        //       at com.google.protobuf.CodedInputStream$StreamDecoder.readRawVarint64SlowPath(CodedInputStream.java:2613)
        //       at com.google.protobuf.CodedInputStream$StreamDecoder.readRawVarint64(CodedInputStream.java:2606)
        //       at com.google.protobuf.CodedInputStream$StreamDecoder.readInt64(CodedInputStream.java:2243)
        //       at com.google.protobuf.UnknownFieldSet$Builder.mergeFieldFrom(UnknownFieldSet.java:484)
        //       at com.google.protobuf.GeneratedMessageV3$Builder.parseUnknownField(GeneratedMessageV3.java:864)
        //       at com.linbit.linstor.proto.responses.MsgReqSosCleanupOuterClass$MsgReqSosCleanup$Builder.mergeFrom(MsgReqSosCleanupOuterClass.java:459)
        //       at com.linbit.linstor.proto.responses.MsgReqSosCleanupOuterClass$MsgReqSosCleanup$1.parsePartialFrom(MsgReqSosCleanupOuterClass.java:589)
        //       at com.linbit.linstor.proto.responses.MsgReqSosCleanupOuterClass$MsgReqSosCleanup$1.parsePartialFrom(MsgReqSosCleanupOuterClass.java:581)
        //       at com.google.protobuf.AbstractParser.parsePartialFrom(AbstractParser.java:192)
        //       at com.google.protobuf.AbstractParser.parsePartialDelimitedFrom(AbstractParser.java:232)
        //       at com.google.protobuf.AbstractParser.parseDelimitedFrom(AbstractParser.java:244)
        //       at com.google.protobuf.AbstractParser.parseDelimitedFrom(AbstractParser.java:249)
        //       at com.google.protobuf.AbstractParser.parseDelimitedFrom(AbstractParser.java:25)
        //       at com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(GeneratedMessageV3.java:375)
        //       at com.linbit.linstor.proto.responses.MsgReqSosCleanupOuterClass$MsgReqSosCleanup.parseDelimitedFrom(MsgReqSosCleanupOuterClass.java:250)
        //       at com.linbit.linstor.api.protobuf.ReqSosReportCleanup.execute(ReqSosReportCleanup.java:50)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        StltSosReportApiCallHandler sosApiCallHandlerRef = new StltSosReportApiCallHandler(errorReporterRef,
                new StltConfig());

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ReqSosReportCleanup reqSosReportCleanup = new ReqSosReportCleanup(sosApiCallHandlerRef, null, null,
                new ProtoCtrlStltSerializer(errReporter, null, secObjsRef, ReadOnlyPropsImpl.emptyRoProps()));

        // Act
        ReqSosReportCleanup req = new ReqSosReportCleanup(
                null, // sosApiCallHandlerRef
                null,
                null,
                null
        );

        // Act & Assert
        assertThrows(Exception.class, () ->
                req.execute(new ByteArrayInputStream("AX".getBytes()))
        );
    }

    /**
     * Test {@link ReqSosReportCleanup#ReqSosReportCleanup(StltSosReportApiCallHandler, Provider, Provider, CtrlStltSerializer)}.
     * <p>
     * Method under test: {@link ReqSosReportCleanup#ReqSosReportCleanup(StltSosReportApiCallHandler, Provider, Provider, CtrlStltSerializer)}
     */
    @Test
    public void testNewReqSosReportCleanup() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     ReqSosReportCleanup.apiCallId
        //     ReqSosReportCleanup.ctrlStltSerializer
        //     ReqSosReportCleanup.peerProvider
        //     ReqSosReportCleanup.sosApiCallHandler

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        StltSosReportApiCallHandler sosApiCallHandlerRef = new StltSosReportApiCallHandler(errorReporterRef,
                new StltConfig());

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();

        // Act
        new ReqSosReportCleanup(sosApiCallHandlerRef, null, null,
                new ProtoCtrlStltSerializer(errReporter, null, secObjsRef, ReadOnlyPropsImpl.emptyRoProps()));

    }
}
