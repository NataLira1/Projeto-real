package com.linbit.linstor.api.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;
import com.linbit.linstor.api.protobuf.serializer.ProtoCommonSerializer;
import com.linbit.linstor.core.apicallhandler.ResponseSerializer;
import com.linbit.linstor.logging.StderrErrorReporter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ChangedExternalFileDiffblueTest {
    /**
     * Test {@link ChangedExternalFile#executeReactive(InputStream)}.
     * <ul>
     *   <li>When {@link ByteArrayInputStream#ByteArrayInputStream(byte[])} with {@code AXAXAXAX} Bytes is {@code UTF-8}.</li>
     * </ul>
     * <p>
     * Method under test: {@link ChangedExternalFile#executeReactive(InputStream)}
     */
    @Test
    public void testExecuteReactive_whenByteArrayInputStreamWithAxaxaxaxBytesIsUtf8() throws IOException {
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
        //       at com.linbit.linstor.proto.javainternal.IntObjectIdOuterClass$IntObjectId$Builder.mergeFrom(IntObjectIdOuterClass.java:534)
        //       at com.linbit.linstor.proto.javainternal.IntObjectIdOuterClass$IntObjectId$1.parsePartialFrom(IntObjectIdOuterClass.java:768)
        //       at com.linbit.linstor.proto.javainternal.IntObjectIdOuterClass$IntObjectId$1.parsePartialFrom(IntObjectIdOuterClass.java:760)
        //       at com.google.protobuf.AbstractParser.parsePartialFrom(AbstractParser.java:192)
        //       at com.google.protobuf.AbstractParser.parsePartialDelimitedFrom(AbstractParser.java:232)
        //       at com.google.protobuf.AbstractParser.parseDelimitedFrom(AbstractParser.java:244)
        //       at com.google.protobuf.AbstractParser.parseDelimitedFrom(AbstractParser.java:249)
        //       at com.google.protobuf.AbstractParser.parseDelimitedFrom(AbstractParser.java:25)
        //       at com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(GeneratedMessageV3.java:375)
        //       at com.linbit.linstor.proto.javainternal.IntObjectIdOuterClass$IntObjectId.parseDelimitedFrom(IntObjectIdOuterClass.java:318)
        //       at com.linbit.linstor.api.protobuf.ChangedExternalFile.executeReactive(ChangedExternalFile.java:45)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        ChangedExternalFile changedExternalFile = new ChangedExternalFile(null,
                new ResponseSerializer(new ProtoCommonSerializer(new StderrErrorReporter("Module Name"), null)));

        // Act

        try {
            // Act
            changedExternalFile.executeReactive(new ByteArrayInputStream("AXAXAXAX".getBytes(StandardCharsets.UTF_8)));
            fail("Expected InvalidProtocolBufferException");
        } catch (IOException ex) {
            // Assert
            assertTrue(ex instanceof InvalidProtocolBufferException);
            assertTrue(ex.getMessage().contains("ended unexpectedly"));
        }
    }

    /**
     * Test {@link ChangedExternalFile#ChangedExternalFile(DeviceManager, ResponseSerializer)}.
     * <p>
     * Method under test: {@link ChangedExternalFile#ChangedExternalFile(DeviceManager, ResponseSerializer)}
     */
    @Test
    public void testNewChangedExternalFile() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     ChangedExternalFile.deviceManager
        //     ChangedExternalFile.responseSerializer

        // Arrange and Act
        new ChangedExternalFile(null,
                new ResponseSerializer(new ProtoCommonSerializer(new StderrErrorReporter("Module Name"), null)));

    }
}
