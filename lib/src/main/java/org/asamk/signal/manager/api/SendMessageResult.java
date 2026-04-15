package org.asamk.signal.manager.api;

import org.asamk.signal.manager.helper.RecipientAddressResolver;
import org.asamk.signal.manager.storage.recipients.RecipientResolver;

public record SendMessageResult(
        RecipientAddress address,
        boolean isSuccess,
        boolean isNetworkFailure,
        boolean isUnregisteredFailure,
        boolean isIdentityFailure,
        RateLimitException rateLimitException,
        ProofRequiredException proofRequiredFailure,
        boolean isInvalidPreKeyFailure
) {

    public static SendMessageResult unregisteredFailure(RecipientAddress address) {
        return new SendMessageResult(address, false, false, true, false, null, null, false);
    }

    public static SendMessageResult from(
            final org.whispersystems.signalservice.api.messages.SendMessageResult sendMessageResult,
            RecipientResolver recipientResolver,
            RecipientAddressResolver addressResolver
    ) {
        final var rateLimitFailure = sendMessageResult.getRateLimitFailure();
        final var proofRequiredFailure = sendMessageResult.getProofRequiredFailure();
        return new SendMessageResult(addressResolver.resolveRecipientAddress(recipientResolver.resolveRecipient(
                sendMessageResult.getAddress())).toApiRecipientAddress(),
                sendMessageResult.isSuccess(),
                sendMessageResult.isNetworkFailure(),
                sendMessageResult.isUnregisteredFailure(),
                sendMessageResult.getIdentityFailure() != null,
                rateLimitFailure == null ? null : RateLimitException.from(rateLimitFailure),
                proofRequiredFailure == null ? null : ProofRequiredException.from(proofRequiredFailure),
                sendMessageResult.isInvalidPreKeyFailure());
    }

    public boolean isRateLimitFailure() {
        return this.rateLimitException != null || this.proofRequiredFailure != null;
    }

    public Long rateLimitRetryAfterMilliseconds() {
        if (proofRequiredFailure != null) {
            return proofRequiredFailure.getRetryAfterMilliseconds();
        } else if (rateLimitException != null) {
            return rateLimitException.getRetryAfterMilliseconds();
        } else {
            return null;
        }
    }
}
