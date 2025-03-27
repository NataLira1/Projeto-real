package com.linbit.linstor.api.protobuf;

import com.linbit.extproc.ExtCmdFactory;
import com.linbit.linstor.api.protobuf.serializer.ProtoCtrlStltSerializer;
import com.linbit.linstor.core.CtrlSecurityObjects;
import com.linbit.linstor.layer.storage.DevicePoolHandler;
import com.linbit.linstor.logging.StderrErrorReporter;
import com.linbit.linstor.propscon.ReadOnlyPropsImpl;
import com.linbit.linstor.timer.CoreTimerImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Ignore;
import org.junit.Test;

public class CreateDevicePoolDiffblueTest {
    /**
     * Test {@link CreateDevicePool#execute(InputStream)}.
     * <ul>
     *   <li>When {@link ByteArrayInputStream#ByteArrayInputStream(byte[])} with {@code AXAXAXAX} Bytes is {@code UTF-8}.</li>
     * </ul>
     * <p>
     * Method under test: {@link CreateDevicePool#execute(InputStream)}
     */
    @Test
    @Ignore("TODO: Complete this test")
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
        //       at com.linbit.linstor.proto.javainternal.c2s.MsgCreateDevicePoolOuterClass$MsgCreateDevicePool$Builder.mergeFrom(MsgCreateDevicePoolOuterClass.java:1463)
        //       at com.linbit.linstor.proto.javainternal.c2s.MsgCreateDevicePoolOuterClass$MsgCreateDevicePool$1.parsePartialFrom(MsgCreateDevicePoolOuterClass.java:2096)
        //       at com.linbit.linstor.proto.javainternal.c2s.MsgCreateDevicePoolOuterClass$MsgCreateDevicePool$1.parsePartialFrom(MsgCreateDevicePoolOuterClass.java:2088)
        //       at com.google.protobuf.AbstractParser.parsePartialFrom(AbstractParser.java:192)
        //       at com.google.protobuf.AbstractParser.parsePartialDelimitedFrom(AbstractParser.java:232)
        //       at com.google.protobuf.AbstractParser.parseDelimitedFrom(AbstractParser.java:244)
        //       at com.google.protobuf.AbstractParser.parseDelimitedFrom(AbstractParser.java:249)
        //       at com.google.protobuf.AbstractParser.parseDelimitedFrom(AbstractParser.java:25)
        //       at com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(GeneratedMessageV3.java:375)
        //       at com.linbit.linstor.proto.javainternal.c2s.MsgCreateDevicePoolOuterClass$MsgCreateDevicePool.parseDelimitedFrom(MsgCreateDevicePoolOuterClass.java:1153)
        //       at com.linbit.linstor.api.protobuf.CreateDevicePool.execute(CreateDevicePool.java:64)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer ctrlStltSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef = new CoreTimerImpl();
        DevicePoolHandler devicePoolHandlerRef = new DevicePoolHandler(errorReporterRef,
                new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef2 = new CoreTimerImpl();
        CreateDevicePool createDevicePool = new CreateDevicePool(null, null, ctrlStltSerializerRef, devicePoolHandlerRef,
                errorReporterRef2, new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name")));

        // Act
        createDevicePool.execute(new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8")));
    }

    /**
     * Test {@link CreateDevicePool#CreateDevicePool(Provider, Provider, CtrlStltSerializer, DevicePoolHandler, ErrorReporter, ExtCmdFactory)}.
     * <p>
     * Method under test: {@link CreateDevicePool#CreateDevicePool(Provider, Provider, CtrlStltSerializer, DevicePoolHandler, ErrorReporter, ExtCmdFactory)}
     */
    @Test
    public void testNewCreateDevicePool() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     CreateDevicePool.apiCallId
        //     CreateDevicePool.ctrlStltSerializer
        //     CreateDevicePool.devicePoolHandler
        //     CreateDevicePool.errorReporter
        //     CreateDevicePool.extCmdFactory
        //     CreateDevicePool.peerProvider

        // Arrange
        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ProtoCtrlStltSerializer ctrlStltSerializerRef = new ProtoCtrlStltSerializer(errReporter, null, secObjsRef,
                ReadOnlyPropsImpl.emptyRoProps());

        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef = new CoreTimerImpl();
        DevicePoolHandler devicePoolHandlerRef = new DevicePoolHandler(errorReporterRef,
                new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name")));

        StderrErrorReporter errorReporterRef2 = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef2 = new CoreTimerImpl();

        // Act
        new CreateDevicePool(null, null, ctrlStltSerializerRef, devicePoolHandlerRef, errorReporterRef2,
                new ExtCmdFactory(timerRef2, new StderrErrorReporter("Module Name")));

    }
}
