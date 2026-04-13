package org.asamk.signal.manager.util;

import org.whispersystems.signalservice.api.messages.SignalServiceAttachmentStream;
import org.whispersystems.signalservice.api.push.exceptions.ResumeLocationInvalidException;
import org.whispersystems.signalservice.api.util.StreamDetails;
import org.whispersystems.signalservice.internal.push.http.ResumableUploadSpec;

import java.util.Optional;
import java.util.UUID;

public class AttachmentUtils {

    public static SignalServiceAttachmentStream createAttachmentStream(
            StreamDetails streamDetails,
            Optional<String> name,
            boolean voiceNote,
            ResumableUploadSpec resumableUploadSpec
    ) throws ResumeLocationInvalidException {
        final var uploadTimestamp = System.currentTimeMillis();
        return SignalServiceAttachmentStream.newStreamBuilder()
                .withStream(streamDetails.getStream())
                .withContentType(streamDetails.getContentType())
                .withLength(streamDetails.getLength())
                .withFileName(name.orElse(null))
                .withVoiceNote(voiceNote)
                .withUploadTimestamp(uploadTimestamp)
                .withResumableUploadSpec(resumableUploadSpec)
                .withUuid(UUID.randomUUID())
                .build();
    }

    public static SignalServiceAttachmentStream createAttachmentStream(
            StreamDetails streamDetails,
            Optional<String> name,
            ResumableUploadSpec resumableUploadSpec
    ) throws ResumeLocationInvalidException {
        return createAttachmentStream(streamDetails, name, false, resumableUploadSpec);
    }
}
