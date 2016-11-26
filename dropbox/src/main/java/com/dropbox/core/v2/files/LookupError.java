/* DO NOT EDIT */
/* This file was generated from files.stone */

package com.dropbox.core.v2.files;

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
 * This class is an open tagged union.  Tagged unions instances are always
 * associated to a specific tag.  This means only one of the {@code isAbc()}
 * methods will return {@code true}. You can use {@link #tag()} to determine the
 * tag associated with this instance.
 *
 * <p> Open unions may be extended in the future with additional tags. If a new
 * tag is introduced that this SDK does not recognized, the {@link #OTHER} value
 * will be used. </p>
 */
public final class LookupError {
    // union files.LookupError (files.stone)

    /**
     * Discriminating tag type for {@link LookupError}.
     */
    public enum Tag {
        MALFORMED_PATH, // String
        /**
         * There is nothing at the given path.
         */
        NOT_FOUND,
        /**
         * We were expecting a file, but the given path refers to something that
         * isn't a file.
         */
        NOT_FILE,
        /**
         * We were expecting a folder, but the given path refers to something
         * that isn't a folder.
         */
        NOT_FOLDER,
        /**
         * The file cannot be transferred because the content is restricted.
         * For example, sometimes there are legal restrictions due to copyright
         * claims.
         */
        RESTRICTED_CONTENT,
        /**
         * The path root parameter provided is invalid.
         */
        INVALID_PATH_ROOT, // PathRootError
        /**
         * Catch-all used for unknown tag values returned by the Dropbox
         * servers.
         *
         * <p> Receiving a catch-all value typically indicates this SDK version
         * is not up to date. Consider updating your SDK version to handle the
         * new tags. </p>
         */
        OTHER; // *catch_all
    }

    /**
     * There is nothing at the given path.
     */
    public static final LookupError NOT_FOUND = new LookupError(Tag.NOT_FOUND, null, null);
    /**
     * We were expecting a file, but the given path refers to something that
     * isn't a file.
     */
    public static final LookupError NOT_FILE = new LookupError(Tag.NOT_FILE, null, null);
    /**
     * We were expecting a folder, but the given path refers to something that
     * isn't a folder.
     */
    public static final LookupError NOT_FOLDER = new LookupError(Tag.NOT_FOLDER, null, null);
    /**
     * The file cannot be transferred because the content is restricted.  For
     * example, sometimes there are legal restrictions due to copyright claims.
     */
    public static final LookupError RESTRICTED_CONTENT = new LookupError(Tag.RESTRICTED_CONTENT, null, null);
    /**
     * Catch-all used for unknown tag values returned by the Dropbox servers.
     *
     * <p> Receiving a catch-all value typically indicates this SDK version is
     * not up to date. Consider updating your SDK version to handle the new
     * tags. </p>
     */
    public static final LookupError OTHER = new LookupError(Tag.OTHER, null, null);

    private final Tag _tag;
    private final String malformedPathValue;
    private final PathRootError invalidPathRootValue;

    /**
     *
     * @param invalidPathRootValue  The path root parameter provided is invalid.
     *     Must not be {@code null}.
     * @param _tag  Discriminating tag for this instance.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    private LookupError(Tag _tag, String malformedPathValue, PathRootError invalidPathRootValue) {
        this._tag = _tag;
        this.malformedPathValue = malformedPathValue;
        this.invalidPathRootValue = invalidPathRootValue;
    }

    /**
     * Returns the tag for this instance.
     *
     * <p> This class is a tagged union.  Tagged unions instances are always
     * associated to a specific tag.  This means only one of the {@code isXyz()}
     * methods will return {@code true}. Callers are recommended to use the tag
     * value in a {@code switch} statement to properly handle the different
     * values for this {@code LookupError}. </p>
     *
     * <p> If a tag returned by the server is unrecognized by this SDK, the
     * {@link Tag#OTHER} value will be used. </p>
     *
     * @return the tag for this instance.
     */
    public Tag tag() {
        return _tag;
    }

    /**
     * Returns {@code true} if this instance has the tag {@link
     * Tag#MALFORMED_PATH}, {@code false} otherwise.
     *
     * @return {@code true} if this instance is tagged as {@link
     *     Tag#MALFORMED_PATH}, {@code false} otherwise.
     */
    public boolean isMalformedPath() {
        return this._tag == Tag.MALFORMED_PATH;
    }

    /**
     * Returns an instance of {@code LookupError} that has its tag set to {@link
     * Tag#MALFORMED_PATH}.
     *
     * <p> None </p>
     *
     * @param value  value to assign to this instance.
     *
     * @return Instance of {@code LookupError} with its tag set to {@link
     *     Tag#MALFORMED_PATH}.
     */
    public static LookupError malformedPath(String value) {
        return new LookupError(Tag.MALFORMED_PATH, value, null);
    }

    /**
     * Returns an instance of {@code LookupError} that has its tag set to {@link
     * Tag#MALFORMED_PATH}.
     *
     * <p> None </p>
     *
     * @return Instance of {@code LookupError} with its tag set to {@link
     *     Tag#MALFORMED_PATH}.
     */
    public static LookupError malformedPath() {
        return malformedPath(null);
    }

    /**
     * This instance must be tagged as {@link Tag#MALFORMED_PATH}.
     *
     * @return The {@link String} value associated with this instance if {@link
     *     #isMalformedPath} is {@code true}.
     *
     * @throws IllegalStateException  If {@link #isMalformedPath} is {@code
     *     false}.
     */
    public String getMalformedPathValue() {
        if (this._tag != Tag.MALFORMED_PATH) {
            throw new IllegalStateException("Invalid tag: required Tag.MALFORMED_PATH, but was Tag." + this._tag.name());
        }
        return malformedPathValue;
    }

    /**
     * Returns {@code true} if this instance has the tag {@link Tag#NOT_FOUND},
     * {@code false} otherwise.
     *
     * @return {@code true} if this instance is tagged as {@link Tag#NOT_FOUND},
     *     {@code false} otherwise.
     */
    public boolean isNotFound() {
        return this._tag == Tag.NOT_FOUND;
    }

    /**
     * Returns {@code true} if this instance has the tag {@link Tag#NOT_FILE},
     * {@code false} otherwise.
     *
     * @return {@code true} if this instance is tagged as {@link Tag#NOT_FILE},
     *     {@code false} otherwise.
     */
    public boolean isNotFile() {
        return this._tag == Tag.NOT_FILE;
    }

    /**
     * Returns {@code true} if this instance has the tag {@link Tag#NOT_FOLDER},
     * {@code false} otherwise.
     *
     * @return {@code true} if this instance is tagged as {@link
     *     Tag#NOT_FOLDER}, {@code false} otherwise.
     */
    public boolean isNotFolder() {
        return this._tag == Tag.NOT_FOLDER;
    }

    /**
     * Returns {@code true} if this instance has the tag {@link
     * Tag#RESTRICTED_CONTENT}, {@code false} otherwise.
     *
     * @return {@code true} if this instance is tagged as {@link
     *     Tag#RESTRICTED_CONTENT}, {@code false} otherwise.
     */
    public boolean isRestrictedContent() {
        return this._tag == Tag.RESTRICTED_CONTENT;
    }

    /**
     * Returns {@code true} if this instance has the tag {@link
     * Tag#INVALID_PATH_ROOT}, {@code false} otherwise.
     *
     * @return {@code true} if this instance is tagged as {@link
     *     Tag#INVALID_PATH_ROOT}, {@code false} otherwise.
     */
    public boolean isInvalidPathRoot() {
        return this._tag == Tag.INVALID_PATH_ROOT;
    }

    /**
     * Returns an instance of {@code LookupError} that has its tag set to {@link
     * Tag#INVALID_PATH_ROOT}.
     *
     * <p> The path root parameter provided is invalid. </p>
     *
     * @param value  value to assign to this instance.
     *
     * @return Instance of {@code LookupError} with its tag set to {@link
     *     Tag#INVALID_PATH_ROOT}.
     *
     * @throws IllegalArgumentException  if {@code value} is {@code null}.
     */
    public static LookupError invalidPathRoot(PathRootError value) {
        if (value == null) {
            throw new IllegalArgumentException("Value is null");
        }
        return new LookupError(Tag.INVALID_PATH_ROOT, null, value);
    }

    /**
     * The path root parameter provided is invalid.
     *
     * <p> This instance must be tagged as {@link Tag#INVALID_PATH_ROOT}. </p>
     *
     * @return The {@link PathRootError} value associated with this instance if
     *     {@link #isInvalidPathRoot} is {@code true}.
     *
     * @throws IllegalStateException  If {@link #isInvalidPathRoot} is {@code
     *     false}.
     */
    public PathRootError getInvalidPathRootValue() {
        if (this._tag != Tag.INVALID_PATH_ROOT) {
            throw new IllegalStateException("Invalid tag: required Tag.INVALID_PATH_ROOT, but was Tag." + this._tag.name());
        }
        return invalidPathRootValue;
    }

    /**
     * Returns {@code true} if this instance has the tag {@link Tag#OTHER},
     * {@code false} otherwise.
     *
     * @return {@code true} if this instance is tagged as {@link Tag#OTHER},
     *     {@code false} otherwise.
     */
    public boolean isOther() {
        return this._tag == Tag.OTHER;
    }

    @Override
    public int hashCode() {
        int hash = Arrays.hashCode(new Object [] {
            _tag,
            malformedPathValue,
            invalidPathRootValue
        });
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        else if (obj instanceof LookupError) {
            LookupError other = (LookupError) obj;
            if (this._tag != other._tag) {
                return false;
            }
            switch (_tag) {
                case MALFORMED_PATH:
                    return (this.malformedPathValue == other.malformedPathValue) || (this.malformedPathValue != null && this.malformedPathValue.equals(other.malformedPathValue));
                case NOT_FOUND:
                    return true;
                case NOT_FILE:
                    return true;
                case NOT_FOLDER:
                    return true;
                case RESTRICTED_CONTENT:
                    return true;
                case INVALID_PATH_ROOT:
                    return (this.invalidPathRootValue == other.invalidPathRootValue) || (this.invalidPathRootValue.equals(other.invalidPathRootValue));
                case OTHER:
                    return true;
                default:
                    return false;
            }
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return Serializer.INSTANCE.serialize(this, false);
    }

    /**
     * Returns a String representation of this object formatted for easier
     * readability.
     *
     * <p> The returned String may contain newlines. </p>
     *
     * @return Formatted, multiline String representation of this object
     */
    public String toStringMultiline() {
        return Serializer.INSTANCE.serialize(this, true);
    }

    /**
     * For internal use only.
     */
    public static class Serializer extends UnionSerializer<LookupError> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void serialize(LookupError value, JsonGenerator g) throws IOException, JsonGenerationException {
            switch (value.tag()) {
                case MALFORMED_PATH: {
                    g.writeStartObject();
                    writeTag("malformed_path", g);
                    g.writeFieldName("malformed_path");
                    StoneSerializers.nullable(StoneSerializers.string()).serialize(value.malformedPathValue, g);
                    g.writeEndObject();
                    break;
                }
                case NOT_FOUND: {
                    g.writeString("not_found");
                    break;
                }
                case NOT_FILE: {
                    g.writeString("not_file");
                    break;
                }
                case NOT_FOLDER: {
                    g.writeString("not_folder");
                    break;
                }
                case RESTRICTED_CONTENT: {
                    g.writeString("restricted_content");
                    break;
                }
                case INVALID_PATH_ROOT: {
                    g.writeStartObject();
                    writeTag("invalid_path_root", g);
                    PathRootError.Serializer.INSTANCE.serialize(value.invalidPathRootValue, g, true);
                    g.writeEndObject();
                    break;
                }
                default: {
                    g.writeString("other");
                }
            }
        }

        @Override
        public LookupError deserialize(JsonParser p) throws IOException, JsonParseException {
            LookupError value;
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
            else if ("malformed_path".equals(tag)) {
                String fieldValue = null;
                if (p.getCurrentToken() != JsonToken.END_OBJECT) {
                    expectField("malformed_path", p);
                    fieldValue = StoneSerializers.nullable(StoneSerializers.string()).deserialize(p);
                }
                if (fieldValue == null) {
                    value = LookupError.malformedPath();
                }
                else {
                    value = LookupError.malformedPath(fieldValue);
                }
            }
            else if ("not_found".equals(tag)) {
                value = LookupError.NOT_FOUND;
            }
            else if ("not_file".equals(tag)) {
                value = LookupError.NOT_FILE;
            }
            else if ("not_folder".equals(tag)) {
                value = LookupError.NOT_FOLDER;
            }
            else if ("restricted_content".equals(tag)) {
                value = LookupError.RESTRICTED_CONTENT;
            }
            else if ("invalid_path_root".equals(tag)) {
                PathRootError fieldValue = null;
                fieldValue = PathRootError.Serializer.INSTANCE.deserialize(p, true);
                value = LookupError.invalidPathRoot(fieldValue);
            }
            else {
                value = LookupError.OTHER;
                skipFields(p);
            }
            if (!collapsed) {
                expectEndObject(p);
            }
            return value;
        }
    }
}
