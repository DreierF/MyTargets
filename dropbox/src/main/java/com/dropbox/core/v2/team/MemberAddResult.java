/* DO NOT EDIT */
/* This file was generated from team_members.stone */

package com.dropbox.core.v2.team;

import com.dropbox.core.stone.StoneSerializers;
import com.dropbox.core.stone.UnionSerializer;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Describes the result of attempting to add a single user to the team.
 * 'success' is the only value indicating that a user was indeed added to the
 * team - the other values explain the type of failure that occurred, and
 * include the email of the user for which the operation has failed.
 *
 * <p> This class is a tagged union.  Tagged unions instances are always
 * associated to a specific tag.  This means only one of the {@code isAbc()}
 * methods will return {@code true}. You can use {@link #tag()} to determine the
 * tag associated with this instance. </p>
 */
public final class MemberAddResult {
    // union team.MemberAddResult (team_members.stone)

    /**
     * Discriminating tag type for {@link MemberAddResult}.
     */
    public enum Tag {
        /**
         * Describes a user that was successfully added to the team.
         */
        SUCCESS, // TeamMemberInfo
        /**
         * Team is already full. The organization has no available licenses.
         */
        TEAM_LICENSE_LIMIT, // String
        /**
         * Team is already full. The free team member limit has been reached.
         */
        FREE_TEAM_MEMBER_LIMIT_REACHED, // String
        /**
         * User is already on this team. The provided email address is
         * associated with a user who is already a member of (including in
         * recoverable state) or invited to the team.
         */
        USER_ALREADY_ON_TEAM, // String
        /**
         * User is already on another team. The provided email address is
         * associated with a user that is already a member or invited to another
         * team.
         */
        USER_ON_ANOTHER_TEAM, // String
        /**
         * User is already paired.
         */
        USER_ALREADY_PAIRED, // String
        /**
         * User migration has failed.
         */
        USER_MIGRATION_FAILED, // String
        /**
         * A user with the given external member ID already exists on the team
         * (including in recoverable state).
         */
        DUPLICATE_EXTERNAL_MEMBER_ID, // String
        /**
         * User creation has failed.
         */
        USER_CREATION_FAILED; // String
    }

    private final Tag _tag;
    private final TeamMemberInfo successValue;
    private final String teamLicenseLimitValue;
    private final String freeTeamMemberLimitReachedValue;
    private final String userAlreadyOnTeamValue;
    private final String userOnAnotherTeamValue;
    private final String userAlreadyPairedValue;
    private final String userMigrationFailedValue;
    private final String duplicateExternalMemberIdValue;
    private final String userCreationFailedValue;

    /**
     * Describes the result of attempting to add a single user to the team.
     * 'success' is the only value indicating that a user was indeed added to
     * the team - the other values explain the type of failure that occurred,
     * and include the email of the user for which the operation has failed.
     *
     * @param successValue  Describes a user that was successfully added to the
     *     team. Must not be {@code null}.
     * @param teamLicenseLimitValue  Team is already full. The organization has
     *     no available licenses. Must have length of at most 255, match pattern
     *     "{@code
     *     ^['&A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$}",
     *     and not be {@code null}.
     * @param freeTeamMemberLimitReachedValue  Team is already full. The free
     *     team member limit has been reached. Must have length of at most 255,
     *     match pattern "{@code
     *     ^['&A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$}",
     *     and not be {@code null}.
     * @param userAlreadyOnTeamValue  User is already on this team. The provided
     *     email address is associated with a user who is already a member of
     *     (including in recoverable state) or invited to the team. Must have
     *     length of at most 255, match pattern "{@code
     *     ^['&A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$}",
     *     and not be {@code null}.
     * @param userOnAnotherTeamValue  User is already on another team. The
     *     provided email address is associated with a user that is already a
     *     member or invited to another team. Must have length of at most 255,
     *     match pattern "{@code
     *     ^['&A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$}",
     *     and not be {@code null}.
     * @param userAlreadyPairedValue  User is already paired. Must have length
     *     of at most 255, match pattern "{@code
     *     ^['&A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$}",
     *     and not be {@code null}.
     * @param userMigrationFailedValue  User migration has failed. Must have
     *     length of at most 255, match pattern "{@code
     *     ^['&A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$}",
     *     and not be {@code null}.
     * @param duplicateExternalMemberIdValue  A user with the given external
     *     member ID already exists on the team (including in recoverable
     *     state). Must have length of at most 255, match pattern "{@code
     *     ^['&A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$}",
     *     and not be {@code null}.
     * @param userCreationFailedValue  User creation has failed. Must have
     *     length of at most 255, match pattern "{@code
     *     ^['&A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$}",
     *     and not be {@code null}.
     * @param _tag  Discriminating tag for this instance.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    private MemberAddResult(Tag _tag, TeamMemberInfo successValue, String teamLicenseLimitValue, String freeTeamMemberLimitReachedValue, String userAlreadyOnTeamValue, String userOnAnotherTeamValue, String userAlreadyPairedValue, String userMigrationFailedValue, String duplicateExternalMemberIdValue, String userCreationFailedValue) {
        this._tag = _tag;
        this.successValue = successValue;
        this.teamLicenseLimitValue = teamLicenseLimitValue;
        this.freeTeamMemberLimitReachedValue = freeTeamMemberLimitReachedValue;
        this.userAlreadyOnTeamValue = userAlreadyOnTeamValue;
        this.userOnAnotherTeamValue = userOnAnotherTeamValue;
        this.userAlreadyPairedValue = userAlreadyPairedValue;
        this.userMigrationFailedValue = userMigrationFailedValue;
        this.duplicateExternalMemberIdValue = duplicateExternalMemberIdValue;
        this.userCreationFailedValue = userCreationFailedValue;
    }

    /**
     * Returns the tag for this instance.
     *
     * <p> This class is a tagged union.  Tagged unions instances are always
     * associated to a specific tag.  This means only one of the {@code isXyz()}
     * methods will return {@code true}. Callers are recommended to use the tag
     * value in a {@code switch} statement to properly handle the different
     * values for this {@code MemberAddResult}. </p>
     *
     * @return the tag for this instance.
     */
    public Tag tag() {
        return _tag;
    }

    /**
     * Returns {@code true} if this instance has the tag {@link Tag#SUCCESS},
     * {@code false} otherwise.
     *
     * @return {@code true} if this instance is tagged as {@link Tag#SUCCESS},
     *     {@code false} otherwise.
     */
    public boolean isSuccess() {
        return this._tag == Tag.SUCCESS;
    }

    /**
     * Returns an instance of {@code MemberAddResult} that has its tag set to
     * {@link Tag#SUCCESS}.
     *
     * <p> Describes a user that was successfully added to the team. </p>
     *
     * @param value  value to assign to this instance.
     *
     * @return Instance of {@code MemberAddResult} with its tag set to {@link
     *     Tag#SUCCESS}.
     *
     * @throws IllegalArgumentException  if {@code value} is {@code null}.
     */
    public static MemberAddResult success(TeamMemberInfo value) {
        if (value == null) {
            throw new IllegalArgumentException("Value is null");
        }
        return new MemberAddResult(Tag.SUCCESS, value, null, null, null, null, null, null, null, null);
    }

    /**
     * Describes a user that was successfully added to the team.
     *
     * <p> This instance must be tagged as {@link Tag#SUCCESS}. </p>
     *
     * @return The {@link TeamMemberInfo} value associated with this instance if
     *     {@link #isSuccess} is {@code true}.
     *
     * @throws IllegalStateException  If {@link #isSuccess} is {@code false}.
     */
    public TeamMemberInfo getSuccessValue() {
        if (this._tag != Tag.SUCCESS) {
            throw new IllegalStateException("Invalid tag: required Tag.SUCCESS, but was Tag." + this._tag.name());
        }
        return successValue;
    }

    /**
     * Returns {@code true} if this instance has the tag {@link
     * Tag#TEAM_LICENSE_LIMIT}, {@code false} otherwise.
     *
     * @return {@code true} if this instance is tagged as {@link
     *     Tag#TEAM_LICENSE_LIMIT}, {@code false} otherwise.
     */
    public boolean isTeamLicenseLimit() {
        return this._tag == Tag.TEAM_LICENSE_LIMIT;
    }

    /**
     * Returns an instance of {@code MemberAddResult} that has its tag set to
     * {@link Tag#TEAM_LICENSE_LIMIT}.
     *
     * <p> Team is already full. The organization has no available licenses.
     * </p>
     *
     * @param value  value to assign to this instance.
     *
     * @return Instance of {@code MemberAddResult} with its tag set to {@link
     *     Tag#TEAM_LICENSE_LIMIT}.
     *
     * @throws IllegalArgumentException  if {@code value} is longer than 255,
     *     does not match pattern "{@code
     *     ^['&amp;A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$}",
     *     or is {@code null}.
     */
    public static MemberAddResult teamLicenseLimit(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value is null");
        }
        if (value.length() > 255) {
            throw new IllegalArgumentException("String is longer than 255");
        }
        if (!Pattern.matches("^['&A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$", value)) {
            throw new IllegalArgumentException("String does not match pattern");
        }
        return new MemberAddResult(Tag.TEAM_LICENSE_LIMIT, null, value, null, null, null, null, null, null, null);
    }

    /**
     * Team is already full. The organization has no available licenses.
     *
     * <p> This instance must be tagged as {@link Tag#TEAM_LICENSE_LIMIT}. </p>
     *
     * @return The {@link String} value associated with this instance if {@link
     *     #isTeamLicenseLimit} is {@code true}.
     *
     * @throws IllegalStateException  If {@link #isTeamLicenseLimit} is {@code
     *     false}.
     */
    public String getTeamLicenseLimitValue() {
        if (this._tag != Tag.TEAM_LICENSE_LIMIT) {
            throw new IllegalStateException("Invalid tag: required Tag.TEAM_LICENSE_LIMIT, but was Tag." + this._tag.name());
        }
        return teamLicenseLimitValue;
    }

    /**
     * Returns {@code true} if this instance has the tag {@link
     * Tag#FREE_TEAM_MEMBER_LIMIT_REACHED}, {@code false} otherwise.
     *
     * @return {@code true} if this instance is tagged as {@link
     *     Tag#FREE_TEAM_MEMBER_LIMIT_REACHED}, {@code false} otherwise.
     */
    public boolean isFreeTeamMemberLimitReached() {
        return this._tag == Tag.FREE_TEAM_MEMBER_LIMIT_REACHED;
    }

    /**
     * Returns an instance of {@code MemberAddResult} that has its tag set to
     * {@link Tag#FREE_TEAM_MEMBER_LIMIT_REACHED}.
     *
     * <p> Team is already full. The free team member limit has been reached.
     * </p>
     *
     * @param value  value to assign to this instance.
     *
     * @return Instance of {@code MemberAddResult} with its tag set to {@link
     *     Tag#FREE_TEAM_MEMBER_LIMIT_REACHED}.
     *
     * @throws IllegalArgumentException  if {@code value} is longer than 255,
     *     does not match pattern "{@code
     *     ^['&amp;A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$}",
     *     or is {@code null}.
     */
    public static MemberAddResult freeTeamMemberLimitReached(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value is null");
        }
        if (value.length() > 255) {
            throw new IllegalArgumentException("String is longer than 255");
        }
        if (!Pattern.matches("^['&A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$", value)) {
            throw new IllegalArgumentException("String does not match pattern");
        }
        return new MemberAddResult(Tag.FREE_TEAM_MEMBER_LIMIT_REACHED, null, null, value, null, null, null, null, null, null);
    }

    /**
     * Team is already full. The free team member limit has been reached.
     *
     * <p> This instance must be tagged as {@link
     * Tag#FREE_TEAM_MEMBER_LIMIT_REACHED}. </p>
     *
     * @return The {@link String} value associated with this instance if {@link
     *     #isFreeTeamMemberLimitReached} is {@code true}.
     *
     * @throws IllegalStateException  If {@link #isFreeTeamMemberLimitReached}
     *     is {@code false}.
     */
    public String getFreeTeamMemberLimitReachedValue() {
        if (this._tag != Tag.FREE_TEAM_MEMBER_LIMIT_REACHED) {
            throw new IllegalStateException("Invalid tag: required Tag.FREE_TEAM_MEMBER_LIMIT_REACHED, but was Tag." + this._tag.name());
        }
        return freeTeamMemberLimitReachedValue;
    }

    /**
     * Returns {@code true} if this instance has the tag {@link
     * Tag#USER_ALREADY_ON_TEAM}, {@code false} otherwise.
     *
     * @return {@code true} if this instance is tagged as {@link
     *     Tag#USER_ALREADY_ON_TEAM}, {@code false} otherwise.
     */
    public boolean isUserAlreadyOnTeam() {
        return this._tag == Tag.USER_ALREADY_ON_TEAM;
    }

    /**
     * Returns an instance of {@code MemberAddResult} that has its tag set to
     * {@link Tag#USER_ALREADY_ON_TEAM}.
     *
     * <p> User is already on this team. The provided email address is
     * associated with a user who is already a member of (including in
     * recoverable state) or invited to the team. </p>
     *
     * @param value  value to assign to this instance.
     *
     * @return Instance of {@code MemberAddResult} with its tag set to {@link
     *     Tag#USER_ALREADY_ON_TEAM}.
     *
     * @throws IllegalArgumentException  if {@code value} is longer than 255,
     *     does not match pattern "{@code
     *     ^['&amp;A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$}",
     *     or is {@code null}.
     */
    public static MemberAddResult userAlreadyOnTeam(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value is null");
        }
        if (value.length() > 255) {
            throw new IllegalArgumentException("String is longer than 255");
        }
        if (!Pattern.matches("^['&A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$", value)) {
            throw new IllegalArgumentException("String does not match pattern");
        }
        return new MemberAddResult(Tag.USER_ALREADY_ON_TEAM, null, null, null, value, null, null, null, null, null);
    }

    /**
     * User is already on this team. The provided email address is associated
     * with a user who is already a member of (including in recoverable state)
     * or invited to the team.
     *
     * <p> This instance must be tagged as {@link Tag#USER_ALREADY_ON_TEAM}.
     * </p>
     *
     * @return The {@link String} value associated with this instance if {@link
     *     #isUserAlreadyOnTeam} is {@code true}.
     *
     * @throws IllegalStateException  If {@link #isUserAlreadyOnTeam} is {@code
     *     false}.
     */
    public String getUserAlreadyOnTeamValue() {
        if (this._tag != Tag.USER_ALREADY_ON_TEAM) {
            throw new IllegalStateException("Invalid tag: required Tag.USER_ALREADY_ON_TEAM, but was Tag." + this._tag.name());
        }
        return userAlreadyOnTeamValue;
    }

    /**
     * Returns {@code true} if this instance has the tag {@link
     * Tag#USER_ON_ANOTHER_TEAM}, {@code false} otherwise.
     *
     * @return {@code true} if this instance is tagged as {@link
     *     Tag#USER_ON_ANOTHER_TEAM}, {@code false} otherwise.
     */
    public boolean isUserOnAnotherTeam() {
        return this._tag == Tag.USER_ON_ANOTHER_TEAM;
    }

    /**
     * Returns an instance of {@code MemberAddResult} that has its tag set to
     * {@link Tag#USER_ON_ANOTHER_TEAM}.
     *
     * <p> User is already on another team. The provided email address is
     * associated with a user that is already a member or invited to another
     * team. </p>
     *
     * @param value  value to assign to this instance.
     *
     * @return Instance of {@code MemberAddResult} with its tag set to {@link
     *     Tag#USER_ON_ANOTHER_TEAM}.
     *
     * @throws IllegalArgumentException  if {@code value} is longer than 255,
     *     does not match pattern "{@code
     *     ^['&amp;A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$}",
     *     or is {@code null}.
     */
    public static MemberAddResult userOnAnotherTeam(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value is null");
        }
        if (value.length() > 255) {
            throw new IllegalArgumentException("String is longer than 255");
        }
        if (!Pattern.matches("^['&A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$", value)) {
            throw new IllegalArgumentException("String does not match pattern");
        }
        return new MemberAddResult(Tag.USER_ON_ANOTHER_TEAM, null, null, null, null, value, null, null, null, null);
    }

    /**
     * User is already on another team. The provided email address is associated
     * with a user that is already a member or invited to another team.
     *
     * <p> This instance must be tagged as {@link Tag#USER_ON_ANOTHER_TEAM}.
     * </p>
     *
     * @return The {@link String} value associated with this instance if {@link
     *     #isUserOnAnotherTeam} is {@code true}.
     *
     * @throws IllegalStateException  If {@link #isUserOnAnotherTeam} is {@code
     *     false}.
     */
    public String getUserOnAnotherTeamValue() {
        if (this._tag != Tag.USER_ON_ANOTHER_TEAM) {
            throw new IllegalStateException("Invalid tag: required Tag.USER_ON_ANOTHER_TEAM, but was Tag." + this._tag.name());
        }
        return userOnAnotherTeamValue;
    }

    /**
     * Returns {@code true} if this instance has the tag {@link
     * Tag#USER_ALREADY_PAIRED}, {@code false} otherwise.
     *
     * @return {@code true} if this instance is tagged as {@link
     *     Tag#USER_ALREADY_PAIRED}, {@code false} otherwise.
     */
    public boolean isUserAlreadyPaired() {
        return this._tag == Tag.USER_ALREADY_PAIRED;
    }

    /**
     * Returns an instance of {@code MemberAddResult} that has its tag set to
     * {@link Tag#USER_ALREADY_PAIRED}.
     *
     * <p> User is already paired. </p>
     *
     * @param value  value to assign to this instance.
     *
     * @return Instance of {@code MemberAddResult} with its tag set to {@link
     *     Tag#USER_ALREADY_PAIRED}.
     *
     * @throws IllegalArgumentException  if {@code value} is longer than 255,
     *     does not match pattern "{@code
     *     ^['&amp;A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$}",
     *     or is {@code null}.
     */
    public static MemberAddResult userAlreadyPaired(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value is null");
        }
        if (value.length() > 255) {
            throw new IllegalArgumentException("String is longer than 255");
        }
        if (!Pattern.matches("^['&A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$", value)) {
            throw new IllegalArgumentException("String does not match pattern");
        }
        return new MemberAddResult(Tag.USER_ALREADY_PAIRED, null, null, null, null, null, value, null, null, null);
    }

    /**
     * User is already paired.
     *
     * <p> This instance must be tagged as {@link Tag#USER_ALREADY_PAIRED}. </p>
     *
     * @return The {@link String} value associated with this instance if {@link
     *     #isUserAlreadyPaired} is {@code true}.
     *
     * @throws IllegalStateException  If {@link #isUserAlreadyPaired} is {@code
     *     false}.
     */
    public String getUserAlreadyPairedValue() {
        if (this._tag != Tag.USER_ALREADY_PAIRED) {
            throw new IllegalStateException("Invalid tag: required Tag.USER_ALREADY_PAIRED, but was Tag." + this._tag.name());
        }
        return userAlreadyPairedValue;
    }

    /**
     * Returns {@code true} if this instance has the tag {@link
     * Tag#USER_MIGRATION_FAILED}, {@code false} otherwise.
     *
     * @return {@code true} if this instance is tagged as {@link
     *     Tag#USER_MIGRATION_FAILED}, {@code false} otherwise.
     */
    public boolean isUserMigrationFailed() {
        return this._tag == Tag.USER_MIGRATION_FAILED;
    }

    /**
     * Returns an instance of {@code MemberAddResult} that has its tag set to
     * {@link Tag#USER_MIGRATION_FAILED}.
     *
     * <p> User migration has failed. </p>
     *
     * @param value  value to assign to this instance.
     *
     * @return Instance of {@code MemberAddResult} with its tag set to {@link
     *     Tag#USER_MIGRATION_FAILED}.
     *
     * @throws IllegalArgumentException  if {@code value} is longer than 255,
     *     does not match pattern "{@code
     *     ^['&amp;A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$}",
     *     or is {@code null}.
     */
    public static MemberAddResult userMigrationFailed(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value is null");
        }
        if (value.length() > 255) {
            throw new IllegalArgumentException("String is longer than 255");
        }
        if (!Pattern.matches("^['&A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$", value)) {
            throw new IllegalArgumentException("String does not match pattern");
        }
        return new MemberAddResult(Tag.USER_MIGRATION_FAILED, null, null, null, null, null, null, value, null, null);
    }

    /**
     * User migration has failed.
     *
     * <p> This instance must be tagged as {@link Tag#USER_MIGRATION_FAILED}.
     * </p>
     *
     * @return The {@link String} value associated with this instance if {@link
     *     #isUserMigrationFailed} is {@code true}.
     *
     * @throws IllegalStateException  If {@link #isUserMigrationFailed} is
     *     {@code false}.
     */
    public String getUserMigrationFailedValue() {
        if (this._tag != Tag.USER_MIGRATION_FAILED) {
            throw new IllegalStateException("Invalid tag: required Tag.USER_MIGRATION_FAILED, but was Tag." + this._tag.name());
        }
        return userMigrationFailedValue;
    }

    /**
     * Returns {@code true} if this instance has the tag {@link
     * Tag#DUPLICATE_EXTERNAL_MEMBER_ID}, {@code false} otherwise.
     *
     * @return {@code true} if this instance is tagged as {@link
     *     Tag#DUPLICATE_EXTERNAL_MEMBER_ID}, {@code false} otherwise.
     */
    public boolean isDuplicateExternalMemberId() {
        return this._tag == Tag.DUPLICATE_EXTERNAL_MEMBER_ID;
    }

    /**
     * Returns an instance of {@code MemberAddResult} that has its tag set to
     * {@link Tag#DUPLICATE_EXTERNAL_MEMBER_ID}.
     *
     * <p> A user with the given external member ID already exists on the team
     * (including in recoverable state). </p>
     *
     * @param value  value to assign to this instance.
     *
     * @return Instance of {@code MemberAddResult} with its tag set to {@link
     *     Tag#DUPLICATE_EXTERNAL_MEMBER_ID}.
     *
     * @throws IllegalArgumentException  if {@code value} is longer than 255,
     *     does not match pattern "{@code
     *     ^['&amp;A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$}",
     *     or is {@code null}.
     */
    public static MemberAddResult duplicateExternalMemberId(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value is null");
        }
        if (value.length() > 255) {
            throw new IllegalArgumentException("String is longer than 255");
        }
        if (!Pattern.matches("^['&A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$", value)) {
            throw new IllegalArgumentException("String does not match pattern");
        }
        return new MemberAddResult(Tag.DUPLICATE_EXTERNAL_MEMBER_ID, null, null, null, null, null, null, null, value, null);
    }

    /**
     * A user with the given external member ID already exists on the team
     * (including in recoverable state).
     *
     * <p> This instance must be tagged as {@link
     * Tag#DUPLICATE_EXTERNAL_MEMBER_ID}. </p>
     *
     * @return The {@link String} value associated with this instance if {@link
     *     #isDuplicateExternalMemberId} is {@code true}.
     *
     * @throws IllegalStateException  If {@link #isDuplicateExternalMemberId} is
     *     {@code false}.
     */
    public String getDuplicateExternalMemberIdValue() {
        if (this._tag != Tag.DUPLICATE_EXTERNAL_MEMBER_ID) {
            throw new IllegalStateException("Invalid tag: required Tag.DUPLICATE_EXTERNAL_MEMBER_ID, but was Tag." + this._tag.name());
        }
        return duplicateExternalMemberIdValue;
    }

    /**
     * Returns {@code true} if this instance has the tag {@link
     * Tag#USER_CREATION_FAILED}, {@code false} otherwise.
     *
     * @return {@code true} if this instance is tagged as {@link
     *     Tag#USER_CREATION_FAILED}, {@code false} otherwise.
     */
    public boolean isUserCreationFailed() {
        return this._tag == Tag.USER_CREATION_FAILED;
    }

    /**
     * Returns an instance of {@code MemberAddResult} that has its tag set to
     * {@link Tag#USER_CREATION_FAILED}.
     *
     * <p> User creation has failed. </p>
     *
     * @param value  value to assign to this instance.
     *
     * @return Instance of {@code MemberAddResult} with its tag set to {@link
     *     Tag#USER_CREATION_FAILED}.
     *
     * @throws IllegalArgumentException  if {@code value} is longer than 255,
     *     does not match pattern "{@code
     *     ^['&amp;A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$}",
     *     or is {@code null}.
     */
    public static MemberAddResult userCreationFailed(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value is null");
        }
        if (value.length() > 255) {
            throw new IllegalArgumentException("String is longer than 255");
        }
        if (!Pattern.matches("^['&A-Za-z0-9._%+-]+@[A-Za-z0-9-][A-Za-z0-9.-]*.[A-Za-z]{2,15}$", value)) {
            throw new IllegalArgumentException("String does not match pattern");
        }
        return new MemberAddResult(Tag.USER_CREATION_FAILED, null, null, null, null, null, null, null, null, value);
    }

    /**
     * User creation has failed.
     *
     * <p> This instance must be tagged as {@link Tag#USER_CREATION_FAILED}.
     * </p>
     *
     * @return The {@link String} value associated with this instance if {@link
     *     #isUserCreationFailed} is {@code true}.
     *
     * @throws IllegalStateException  If {@link #isUserCreationFailed} is {@code
     *     false}.
     */
    public String getUserCreationFailedValue() {
        if (this._tag != Tag.USER_CREATION_FAILED) {
            throw new IllegalStateException("Invalid tag: required Tag.USER_CREATION_FAILED, but was Tag." + this._tag.name());
        }
        return userCreationFailedValue;
    }

    @Override
    public int hashCode() {
        int hash = Arrays.hashCode(new Object [] {
            _tag,
            successValue,
            teamLicenseLimitValue,
            freeTeamMemberLimitReachedValue,
            userAlreadyOnTeamValue,
            userOnAnotherTeamValue,
            userAlreadyPairedValue,
            userMigrationFailedValue,
            duplicateExternalMemberIdValue,
            userCreationFailedValue
        });
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        else if (obj instanceof MemberAddResult) {
            MemberAddResult other = (MemberAddResult) obj;
            if (this._tag != other._tag) {
                return false;
            }
            switch (_tag) {
                case SUCCESS:
                    return (this.successValue == other.successValue) || (this.successValue.equals(other.successValue));
                case TEAM_LICENSE_LIMIT:
                    return (this.teamLicenseLimitValue == other.teamLicenseLimitValue) || (this.teamLicenseLimitValue.equals(other.teamLicenseLimitValue));
                case FREE_TEAM_MEMBER_LIMIT_REACHED:
                    return (this.freeTeamMemberLimitReachedValue == other.freeTeamMemberLimitReachedValue) || (this.freeTeamMemberLimitReachedValue.equals(other.freeTeamMemberLimitReachedValue));
                case USER_ALREADY_ON_TEAM:
                    return (this.userAlreadyOnTeamValue == other.userAlreadyOnTeamValue) || (this.userAlreadyOnTeamValue.equals(other.userAlreadyOnTeamValue));
                case USER_ON_ANOTHER_TEAM:
                    return (this.userOnAnotherTeamValue == other.userOnAnotherTeamValue) || (this.userOnAnotherTeamValue.equals(other.userOnAnotherTeamValue));
                case USER_ALREADY_PAIRED:
                    return (this.userAlreadyPairedValue == other.userAlreadyPairedValue) || (this.userAlreadyPairedValue.equals(other.userAlreadyPairedValue));
                case USER_MIGRATION_FAILED:
                    return (this.userMigrationFailedValue == other.userMigrationFailedValue) || (this.userMigrationFailedValue.equals(other.userMigrationFailedValue));
                case DUPLICATE_EXTERNAL_MEMBER_ID:
                    return (this.duplicateExternalMemberIdValue == other.duplicateExternalMemberIdValue) || (this.duplicateExternalMemberIdValue.equals(other.duplicateExternalMemberIdValue));
                case USER_CREATION_FAILED:
                    return (this.userCreationFailedValue == other.userCreationFailedValue) || (this.userCreationFailedValue.equals(other.userCreationFailedValue));
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
    static class Serializer extends UnionSerializer<MemberAddResult> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void serialize(MemberAddResult value, JsonGenerator g) throws IOException, JsonGenerationException {
            switch (value.tag()) {
                case SUCCESS: {
                    g.writeStartObject();
                    writeTag("success", g);
                    TeamMemberInfo.Serializer.INSTANCE.serialize(value.successValue, g, true);
                    g.writeEndObject();
                    break;
                }
                case TEAM_LICENSE_LIMIT: {
                    g.writeStartObject();
                    writeTag("team_license_limit", g);
                    g.writeFieldName("team_license_limit");
                    StoneSerializers.string().serialize(value.teamLicenseLimitValue, g);
                    g.writeEndObject();
                    break;
                }
                case FREE_TEAM_MEMBER_LIMIT_REACHED: {
                    g.writeStartObject();
                    writeTag("free_team_member_limit_reached", g);
                    g.writeFieldName("free_team_member_limit_reached");
                    StoneSerializers.string().serialize(value.freeTeamMemberLimitReachedValue, g);
                    g.writeEndObject();
                    break;
                }
                case USER_ALREADY_ON_TEAM: {
                    g.writeStartObject();
                    writeTag("user_already_on_team", g);
                    g.writeFieldName("user_already_on_team");
                    StoneSerializers.string().serialize(value.userAlreadyOnTeamValue, g);
                    g.writeEndObject();
                    break;
                }
                case USER_ON_ANOTHER_TEAM: {
                    g.writeStartObject();
                    writeTag("user_on_another_team", g);
                    g.writeFieldName("user_on_another_team");
                    StoneSerializers.string().serialize(value.userOnAnotherTeamValue, g);
                    g.writeEndObject();
                    break;
                }
                case USER_ALREADY_PAIRED: {
                    g.writeStartObject();
                    writeTag("user_already_paired", g);
                    g.writeFieldName("user_already_paired");
                    StoneSerializers.string().serialize(value.userAlreadyPairedValue, g);
                    g.writeEndObject();
                    break;
                }
                case USER_MIGRATION_FAILED: {
                    g.writeStartObject();
                    writeTag("user_migration_failed", g);
                    g.writeFieldName("user_migration_failed");
                    StoneSerializers.string().serialize(value.userMigrationFailedValue, g);
                    g.writeEndObject();
                    break;
                }
                case DUPLICATE_EXTERNAL_MEMBER_ID: {
                    g.writeStartObject();
                    writeTag("duplicate_external_member_id", g);
                    g.writeFieldName("duplicate_external_member_id");
                    StoneSerializers.string().serialize(value.duplicateExternalMemberIdValue, g);
                    g.writeEndObject();
                    break;
                }
                case USER_CREATION_FAILED: {
                    g.writeStartObject();
                    writeTag("user_creation_failed", g);
                    g.writeFieldName("user_creation_failed");
                    StoneSerializers.string().serialize(value.userCreationFailedValue, g);
                    g.writeEndObject();
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unrecognized tag: " + value.tag());
                }
            }
        }

        @Override
        public MemberAddResult deserialize(JsonParser p) throws IOException, JsonParseException {
            MemberAddResult value;
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
            else if ("success".equals(tag)) {
                TeamMemberInfo fieldValue = null;
                fieldValue = TeamMemberInfo.Serializer.INSTANCE.deserialize(p, true);
                value = MemberAddResult.success(fieldValue);
            }
            else if ("team_license_limit".equals(tag)) {
                String fieldValue = null;
                expectField("team_license_limit", p);
                fieldValue = StoneSerializers.string().deserialize(p);
                value = MemberAddResult.teamLicenseLimit(fieldValue);
            }
            else if ("free_team_member_limit_reached".equals(tag)) {
                String fieldValue = null;
                expectField("free_team_member_limit_reached", p);
                fieldValue = StoneSerializers.string().deserialize(p);
                value = MemberAddResult.freeTeamMemberLimitReached(fieldValue);
            }
            else if ("user_already_on_team".equals(tag)) {
                String fieldValue = null;
                expectField("user_already_on_team", p);
                fieldValue = StoneSerializers.string().deserialize(p);
                value = MemberAddResult.userAlreadyOnTeam(fieldValue);
            }
            else if ("user_on_another_team".equals(tag)) {
                String fieldValue = null;
                expectField("user_on_another_team", p);
                fieldValue = StoneSerializers.string().deserialize(p);
                value = MemberAddResult.userOnAnotherTeam(fieldValue);
            }
            else if ("user_already_paired".equals(tag)) {
                String fieldValue = null;
                expectField("user_already_paired", p);
                fieldValue = StoneSerializers.string().deserialize(p);
                value = MemberAddResult.userAlreadyPaired(fieldValue);
            }
            else if ("user_migration_failed".equals(tag)) {
                String fieldValue = null;
                expectField("user_migration_failed", p);
                fieldValue = StoneSerializers.string().deserialize(p);
                value = MemberAddResult.userMigrationFailed(fieldValue);
            }
            else if ("duplicate_external_member_id".equals(tag)) {
                String fieldValue = null;
                expectField("duplicate_external_member_id", p);
                fieldValue = StoneSerializers.string().deserialize(p);
                value = MemberAddResult.duplicateExternalMemberId(fieldValue);
            }
            else if ("user_creation_failed".equals(tag)) {
                String fieldValue = null;
                expectField("user_creation_failed", p);
                fieldValue = StoneSerializers.string().deserialize(p);
                value = MemberAddResult.userCreationFailed(fieldValue);
            }
            else {
                throw new JsonParseException(p, "Unknown tag: " + tag);
            }
            if (!collapsed) {
                expectEndObject(p);
            }
            return value;
        }
    }
}
