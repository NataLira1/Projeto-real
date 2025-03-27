package com.linbit.linstor.api.protobuf;

import com.linbit.extproc.ExtCmdFactory;
import com.linbit.linstor.api.protobuf.serializer.ProtoCtrlStltSerializer;
import com.linbit.linstor.core.CtrlSecurityObjects;
import com.linbit.linstor.logging.StderrErrorReporter;
import com.linbit.linstor.propscon.ReadOnlyPropsImpl;
import com.linbit.linstor.timer.CoreTimerImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Ignore;
import org.junit.Test;

public class ListPhysicalDevicesDiffblueTest {
    /**
     * Test {@link ListPhysicalDevices#execute(InputStream)}.
     * <ul>
     *   <li>When {@link ByteArrayInputStream#ByteArrayInputStream(byte[])} with array of {@code byte} with zero and {@code X}.</li>
     * </ul>
     * <p>
     * Method under test: {@link ListPhysicalDevices#execute(InputStream)}
     */
    @Test
    @Ignore("TODO: Complete this test")
    public void testExecute_whenByteArrayInputStreamWithArrayOfByteWithZeroAndX() throws IOException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Sandboxing policy violation.
        //   Diffblue Cover ran code in your project that tried
        //     to execute an external process.
        //   Diffblue Cover's default sandboxing policy disallows this in order to prevent
        //   your code from damaging your system environment.
        //   See https://diff.blue/R011 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ListPhysicalDevices listPhysicalDevices = new ListPhysicalDevices(errorReporterRef, extCmdFactoryRef, null, null,
                new ProtoCtrlStltSerializer(errReporter, null, secObjsRef, ReadOnlyPropsImpl.emptyRoProps()));

        // Act
        listPhysicalDevices.execute(new ByteArrayInputStream(new byte[]{0, 'X', 'A', 'X', 'A', 'X', 'A', 'X'}));
    }

    /**
     * Test {@link ListPhysicalDevices#execute(InputStream)}.
     * <ul>
     *   <li>When {@link ByteArrayInputStream#ByteArrayInputStream(byte[])} with {@code AXAXAXAX} Bytes is {@code UTF-8}.</li>
     * </ul>
     * <p>
     * Method under test: {@link ListPhysicalDevices#execute(InputStream)}
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
        //       at com.linbit.linstor.proto.javainternal.c2s.MsgReqPhysicalDevicesOuterClass$MsgReqPhysicalDevices$Builder.mergeFrom(MsgReqPhysicalDevicesOuterClass.java:383)
        //       at com.linbit.linstor.proto.javainternal.c2s.MsgReqPhysicalDevicesOuterClass$MsgReqPhysicalDevices$1.parsePartialFrom(MsgReqPhysicalDevicesOuterClass.java:465)
        //       at com.linbit.linstor.proto.javainternal.c2s.MsgReqPhysicalDevicesOuterClass$MsgReqPhysicalDevices$1.parsePartialFrom(MsgReqPhysicalDevicesOuterClass.java:457)
        //       at com.google.protobuf.AbstractParser.parsePartialFrom(AbstractParser.java:192)
        //       at com.google.protobuf.AbstractParser.parsePartialDelimitedFrom(AbstractParser.java:232)
        //       at com.google.protobuf.AbstractParser.parseDelimitedFrom(AbstractParser.java:244)
        //       at com.google.protobuf.AbstractParser.parseDelimitedFrom(AbstractParser.java:249)
        //       at com.google.protobuf.AbstractParser.parseDelimitedFrom(AbstractParser.java:25)
        //       at com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(GeneratedMessageV3.java:375)
        //       at com.linbit.linstor.proto.javainternal.c2s.MsgReqPhysicalDevicesOuterClass$MsgReqPhysicalDevices.parseDelimitedFrom(MsgReqPhysicalDevicesOuterClass.java:187)
        //       at com.linbit.linstor.api.protobuf.ListPhysicalDevices.execute(ListPhysicalDevices.java:59)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();
        ListPhysicalDevices listPhysicalDevices = new ListPhysicalDevices(errorReporterRef, extCmdFactoryRef, null, null,
                new ProtoCtrlStltSerializer(errReporter, null, secObjsRef, ReadOnlyPropsImpl.emptyRoProps()));

        // Act
        listPhysicalDevices.execute(new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8")));
    }

    /**
     * Test {@link ListPhysicalDevices#ListPhysicalDevices(ErrorReporter, ExtCmdFactory, Provider, Provider, CtrlStltSerializer)}.
     * <p>
     * Method under test: {@link ListPhysicalDevices#ListPhysicalDevices(ErrorReporter, ExtCmdFactory, Provider, Provider, CtrlStltSerializer)}
     */
    @Test
    public void testNewListPhysicalDevices() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     ListPhysicalDevices.apiCallId
        //     ListPhysicalDevices.ctrlStltSerializer
        //     ListPhysicalDevices.errorReporter
        //     ListPhysicalDevices.extCmdFactory
        //     ListPhysicalDevices.peerProvider

        // Arrange
        StderrErrorReporter errorReporterRef = new StderrErrorReporter("Module Name");
        CoreTimerImpl timerRef = new CoreTimerImpl();
        ExtCmdFactory extCmdFactoryRef = new ExtCmdFactory(timerRef, new StderrErrorReporter("Module Name"));

        StderrErrorReporter errReporter = new StderrErrorReporter("Module Name");
        CtrlSecurityObjects secObjsRef = new CtrlSecurityObjects();

        // Act
        new ListPhysicalDevices(errorReporterRef, extCmdFactoryRef, null, null,
                new ProtoCtrlStltSerializer(errReporter, null, secObjsRef, ReadOnlyPropsImpl.emptyRoProps()));

    }
}
