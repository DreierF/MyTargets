/* DO NOT EDIT */
/* This file was generated from sharing_folders.stone */

package com.dropbox.core.v2.sharing;

import com.dropbox.core.stone.StoneSerializers;
import com.dropbox.core.stone.UnionSerializer;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.util.Arrays;

/**
 * Policy governing who can view shared links.
 */
public enum SharedLinkPolicy {
    // union sharing.SharedLinkPolicy (sharing_folders.stone)
    /**
     * Links can be shared with anyone.
     */
    ANYONE,
    /**
     * Links can only be shared among members of the shared folder.
     */
    MEMBERS,
    /**
     * Catch-all used for unknown tag values returned by the Dropbox servers.
     *
     * <p> Receiving a catch-all value typically indicates this SDK version is
     * not up to date. Consider updating your SDK version to handle the new
     * tags. </p>
     */
    OTHER; // *catch_all

    /**
     * For internal use only.
     */
    static class Serializer extends UnionSerializer<SharedLinkPolicy> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void serialize(SharedLinkPolicy value, JsonGenerator g) throws IOException, JsonGenerationException {
            switch (value) {
                case ANYONE: {
                    g.writeString("anyone");
                    break;
                }
                case MEMBERS: {
                    g.writeString("members");
                    break;
                }
                default: {
                    g.writeString("other");
                }
            }
        }

        @Override
        public SharedLinkPolicy deserialize(JsonParser p) throws IOException, JsonParseException {
            SharedLinkPolicy value;
            boolean collapsed;
            String tag;
            if (p.getCurrentToken() == JsonToken.VALUE_STRING) {
                collapsed = true;
                tag = getStringValue(p);
                p.nextToken();
            }
            else {
                collapsed = false;
                expectStartObject(p);
                tag = readTag(p);
            }
            if (tag == null) {
                throw new JsonParseException(p, "Required field missing: " + TAG_FIELD);
            }
            else if ("anyone".equals(tag)) {
                value = SharedLinkPolicy.ANYONE;
            }
            else if ("members".equals(tag)) {
                value = SharedLinkPolicy.MEMBERS;
            }
            else {
                value = SharedLinkPolicy.OTHER;
                skipFields(p);
            }
            if (!collapsed) {
                expectEndObject(p);
            }
            return value;
        }
    }
}
