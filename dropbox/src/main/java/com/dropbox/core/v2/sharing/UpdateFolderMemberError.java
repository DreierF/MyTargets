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
 * This class is an open tagged union.  Tagged unions instances are always
 * associated to a specific tag.  This means only one of the {@code isAbc()}
 * methods will return {@code true}. You can use {@link #tag()} to determine the
 * tag associated with this instance.
 *
 * <p> Open unions may be extended in the future with additional tags. If a new
 * tag is introduced that this SDK does not recognized, the {@link #OTHER} value
 * will be used. </p>
 */
public final class UpdateFolderMemberError {
    // union sharing.UpdateFolderMemberError (sharing_folders.stone)

    /**
     * Discriminating tag type for {@link UpdateFolderMemberError}.
     */
    public enum Tag {
        ACCESS_ERROR, // SharedFolderAccessError
        MEMBER_ERROR, // SharedFolderMemberError
        /**
         * If updating the access type required the member to be added to the
         * shared folder and there was an error when adding the member.
         */
        NO_EXPLICIT_ACCESS, // AddFolderMemberError
        /**
         * The current user's account doesn't support this action. An example of
         * this is when downgrading a member from editor to viewer. This action
         * can only be performed by users that have upgraded to a Pro or
         * Business plan.
         */
        INSUFFICIENT_PLAN,
        /**
         * The current user does not have permission to perform this action.
         */
        NO_PERMISSION,
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
     * The current user's account doesn't support this action. An example of
     * this is when downgrading a member from editor to viewer. This action can
     * only be performed by users that have upgraded to a Pro or Business plan.
     */
    public static final UpdateFolderMemberError INSUFFICIENT_PLAN = new UpdateFolderMemberError(Tag.INSUFFICIENT_PLAN, null, null, null);
    /**
     * The current user does not have permission to perform this action.
     */
    public static final UpdateFolderMemberError NO_PERMISSION = new UpdateFolderMemberError(Tag.NO_PERMISSION, null, null, null);
    /**
     * Catch-all used for unknown tag values returned by the Dropbox servers.
     *
     * <p> Receiving a catch-all value typically indicates this SDK version is
     * not up to date. Consider updating your SDK version to handle the new
     * tags. </p>
     */
    public static final UpdateFolderMemberError OTHER = new UpdateFolderMemberError(Tag.OTHER, null, null, null);

    private final Tag _tag;
    private final SharedFolderAccessError accessErrorValue;
    private final SharedFolderMemberError memberErrorValue;
    private final AddFolderMemberError noExplicitAccessValue;

    /**
     *
     * @param accessErrorValue  Must not be {@code null}.
     * @param memberErrorValue  Must not be {@code null}.
     * @param noExplicitAccessValue  If updating the access type required the
     *     member to be added to the shared folder and there was an error when
     *     adding the member. Must not be {@code null}.
     * @param _tag  Discriminating tag for this instance.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    private UpdateFolderMemberError(Tag _tag, SharedFolderAccessError accessErrorValue, SharedFolderMemberError memberErrorValue, AddFolderMemberError noExplicitAccessValue) {
        this._tag = _tag;
        this.accessErrorValue = accessErrorValue;
        this.memberErrorValue = memberErrorValue;
        this.noExplicitAccessValue = noExplicitAccessValue;
    }

    /**
     * Returns the tag for this instance.
     *
     * <p> This class is a tagged union.  Tagged unions instances are always
     * associated to a specific tag.  This means only one of the {@code isXyz()}
     * methods will return {@code true}. Callers are recommended to use the tag
     * value in a {@code switch} statement to properly handle the different
     * values for this {@code UpdateFolderMemberError}. </p>
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
     * Tag#ACCESS_ERROR}, {@code false} otherwise.
     *
     * @return {@code true} if this instance is tagged as {@link
     *     Tag#ACCESS_ERROR}, {@code false} otherwise.
     */
    public boolean isAccessError() {
        return this._tag == Tag.ACCESS_ERROR;
    }

    /**
     * Returns an instance of {@code UpdateFolderMemberError} that has its tag
     * set to {@link Tag#ACCESS_ERROR}.
     *
     * <p> None </p>
     *
     * @param value  value to assign to this instance.
     *
     * @return Instance of {@code UpdateFolderMemberError} with its tag set to
     *     {@link Tag#ACCESS_ERROR}.
     *
     * @throws IllegalArgumentException  if {@code value} is {@code null}.
     */
    public static UpdateFolderMemberError accessError(SharedFolderAccessError value) {
        if (value == null) {
            throw new IllegalArgumentException("Value is null");
        }
        return new UpdateFolderMemberError(Tag.ACCESS_ERROR, value, null, null);
    }

    /**
     * This instance must be tagged as {@link Tag#ACCESS_ERROR}.
     *
     * @return The {@link SharedFolderAccessError} value associated with this
     *     instance if {@link #isAccessError} is {@code true}.
     *
     * @throws IllegalStateException  If {@link #isAccessError} is {@code
     *     false}.
     */
    public SharedFolderAccessError getAccessErrorValue() {
        if (this._tag != Tag.ACCESS_ERROR) {
            throw new IllegalStateException("Invalid tag: required Tag.ACCESS_ERROR, but was Tag." + this._tag.name());
        }
        return accessErrorValue;
    }

    /**
     * Returns {@code true} if this instance has the tag {@link
     * Tag#MEMBER_ERROR}, {@code false} otherwise.
     *
     * @return {@code true} if this instance is tagged as {@link
     *     Tag#MEMBER_ERROR}, {@code false} otherwise.
     */
    public boolean isMemberError() {
        return this._tag == Tag.MEMBER_ERROR;
    }

    /**
     * Returns an instance of {@code UpdateFolderMemberError} that has its tag
     * set to {@link Tag#MEMBER_ERROR}.
     *
     * <p> None </p>
     *
     * @param value  value to assign to this instance.
     *
     * @return Instance of {@code UpdateFolderMemberError} with its tag set to
     *     {@link Tag#MEMBER_ERROR}.
     *
     * @throws IllegalArgumentException  if {@code value} is {@code null}.
     */
    public static UpdateFolderMemberError memberError(SharedFolderMemberError value) {
        if (value == null) {
            throw new IllegalArgumentException("Value is null");
        }
        return new UpdateFolderMemberError(Tag.MEMBER_ERROR, null, value, null);
    }

    /**
     * This instance must be tagged as {@link Tag#MEMBER_ERROR}.
     *
     * @return The {@link SharedFolderMemberError} value associated with this
     *     instance if {@link #isMemberError} is {@code true}.
     *
     * @throws IllegalStateException  If {@link #isMemberError} is {@code
     *     false}.
     */
    public SharedFolderMemberError getMemberErrorValue() {
        if (this._tag != Tag.MEMBER_ERROR) {
            throw new IllegalStateException("Invalid tag: required Tag.MEMBER_ERROR, but was Tag." + this._tag.name());
        }
        return memberErrorValue;
    }

    /**
     * Returns {@code true} if this instance has the tag {@link
     * Tag#NO_EXPLICIT_ACCESS}, {@code false} otherwise.
     *
     * @return {@code true} if this instance is tagged as {@link
     *     Tag#NO_EXPLICIT_ACCESS}, {@code false} otherwise.
     */
    public boolean isNoExplicitAccess() {
        return this._tag == Tag.NO_EXPLICIT_ACCESS;
    }

    /**
     * Returns an instance of {@code UpdateFolderMemberError} that has its tag
     * set to {@link Tag#NO_EXPLICIT_ACCESS}.
     *
     * <p> If updating the access type required the member to be added to the
     * shared folder and there was an error when adding the member. </p>
     *
     * @param value  value to assign to this instance.
     *
     * @return Instance of {@code UpdateFolderMemberError} with its tag set to
     *     {@link Tag#NO_EXPLICIT_ACCESS}.
     *
     * @throws IllegalArgumentException  if {@code value} is {@code null}.
     */
    public static UpdateFolderMemberError noExplicitAccess(AddFolderMemberError value) {
        if (value == null) {
            throw new IllegalArgumentException("Value is null");
        }
        return new UpdateFolderMemberError(Tag.NO_EXPLICIT_ACCESS, null, null, value);
    }

    /**
     * If updating the access type required the member to be added to the shared
     * folder and there was an error when adding the member.
     *
     * <p> This instance must be tagged as {@link Tag#NO_EXPLICIT_ACCESS}. </p>
     *
     * @return The {@link AddFolderMemberError} value associated with this
     *     instance if {@link #isNoExplicitAccess} is {@code true}.
     *
     * @throws IllegalStateException  If {@link #isNoExplicitAccess} is {@code
     *     false}.
     */
    public AddFolderMemberError getNoExplicitAccessValue() {
        if (this._tag != Tag.NO_EXPLICIT_ACCESS) {
            throw new IllegalStateException("Invalid tag: required Tag.NO_EXPLICIT_ACCESS, but was Tag." + this._tag.name());
        }
        return noExplicitAccessValue;
    }

    /**
     * Returns {@code true} if this instance has the tag {@link
     * Tag#INSUFFICIENT_PLAN}, {@code false} otherwise.
     *
     * @return {@code true} if this instance is tagged as {@link
     *     Tag#INSUFFICIENT_PLAN}, {@code false} otherwise.
     */
    public boolean isInsufficientPlan() {
        return this._tag == Tag.INSUFFICIENT_PLAN;
    }

    /**
     * Returns {@code true} if this instance has the tag {@link
     * Tag#NO_PERMISSION}, {@code false} otherwise.
     *
     * @return {@code true} if this instance is tagged as {@link
     *     Tag#NO_PERMISSION}, {@code false} otherwise.
     */
    public boolean isNoPermission() {
        return this._tag == Tag.NO_PERMISSION;
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
            accessErrorValue,
            memberErrorValue,
            noExplicitAccessValue
        });
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        else if (obj instanceof UpdateFolderMemberError) {
            UpdateFolderMemberError other = (UpdateFolderMemberError) obj;
            if (this._tag != other._tag) {
                return false;
            }
            switch (_tag) {
                case ACCESS_ERROR:
                    return (this.accessErrorValue == other.accessErrorValue) || (this.accessErrorValue.equals(other.accessErrorValue));
                case MEMBER_ERROR:
                    return (this.memberErrorValue == other.memberErrorValue) || (this.memberErrorValue.equals(other.memberErrorValue));
                case NO_EXPLICIT_ACCESS:
                    return (this.noExplicitAccessValue == other.noExplicitAccessValue) || (this.noExplicitAccessValue.equals(other.noExplicitAccessValue));
                case INSUFFICIENT_PLAN:
                    return true;
                case NO_PERMISSION:
                    return true;
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
    static class Serializer extends UnionSerializer<UpdateFolderMemberError> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void serialize(UpdateFolderMemberError value, JsonGenerator g) throws IOException, JsonGenerationException {
            switch (value.tag()) {
                case ACCESS_ERROR: {
                    g.writeStartObject();
                    writeTag("access_error", g);
                    g.writeFieldName("access_error");
                    SharedFolderAccessError.Serializer.INSTANCE.serialize(value.accessErrorValue, g);
                    g.writeEndObject();
                    break;
                }
                case MEMBER_ERROR: {
                    g.writeStartObject();
                    writeTag("member_error", g);
                    g.writeFieldName("member_error");
                    SharedFolderMemberError.Serializer.INSTANCE.serialize(value.memberErrorValue, g);
                    g.writeEndObject();
                    break;
                }
                case NO_EXPLICIT_ACCESS: {
                    g.writeStartObject();
                    writeTag("no_explicit_access", g);
                    g.writeFieldName("no_explicit_access");
                    AddFolderMemberError.Serializer.INSTANCE.serialize(value.noExplicitAccessValue, g);
                    g.writeEndObject();
                    break;
                }
                case INSUFFICIENT_PLAN: {
                    g.writeString("insufficient_plan");
                    break;
                }
                case NO_PERMISSION: {
                    g.writeString("no_permission");
                    break;
                }
                default: {
                    g.writeString("other");
                }
            }
        }

        @Override
        public UpdateFolderMemberError deserialize(JsonParser p) throws IOException, JsonParseException {
            UpdateFolderMemberError value;
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
            else if ("access_error".equals(tag)) {
                SharedFolderAccessError fieldValue = null;
                expectField("access_error", p);
                fieldValue = SharedFolderAccessError.Serializer.INSTANCE.deserialize(p);
                value = UpdateFolderMemberError.accessError(fieldValue);
            }
            else if ("member_error".equals(tag)) {
                SharedFolderMemberError fieldValue = null;
                expectField("member_error", p);
                fieldValue = SharedFolderMemberError.Serializer.INSTANCE.deserialize(p);
                value = UpdateFolderMemberError.memberError(fieldValue);
            }
            else if ("no_explicit_access".equals(tag)) {
                AddFolderMemberError fieldValue = null;
                expectField("no_explicit_access", p);
                fieldValue = AddFolderMemberError.Serializer.INSTANCE.deserialize(p);
                value = UpdateFolderMemberError.noExplicitAccess(fieldValue);
            }
            else if ("insufficient_plan".equals(tag)) {
                value = UpdateFolderMemberError.INSUFFICIENT_PLAN;
            }
            else if ("no_permission".equals(tag)) {
                value = UpdateFolderMemberError.NO_PERMISSION;
            }
            else {
                value = UpdateFolderMemberError.OTHER;
                skipFields(p);
            }
            if (!collapsed) {
                expectEndObject(p);
            }
            return value;
        }
    }
}
