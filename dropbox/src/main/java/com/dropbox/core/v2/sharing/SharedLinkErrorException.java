/* DO NOT EDIT */
/* This file was generated by Stone */

package com.dropbox.core.v2.sharing;

import com.dropbox.core.DbxApiException;
import com.dropbox.core.LocalizedText;

/**
 * Exception thrown when the server responds with a {@link SharedLinkError}
 * error.
 *
 * <p> This exception is raised by {@link
 * DbxUserSharingRequests#getSharedLinkMetadata(String)}. </p>
 */
public class SharedLinkErrorException extends DbxApiException {
    // exception for routes:
    //     2/sharing/get_shared_link_metadata

    private static final long serialVersionUID = 0L;

    /**
     * The error reported by {@link
     * DbxUserSharingRequests#getSharedLinkMetadata(String)}.
     */
    public final SharedLinkError errorValue;

    public SharedLinkErrorException(String routeName, String requestId, LocalizedText userMessage, SharedLinkError errorValue) {
        super(requestId, userMessage, buildMessage(routeName, userMessage, errorValue));
        if (errorValue == null) {
            throw new NullPointerException("errorValue");
        }
        this.errorValue = errorValue;
    }
}
