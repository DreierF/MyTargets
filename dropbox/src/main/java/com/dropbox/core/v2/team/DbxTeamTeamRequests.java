/* DO NOT EDIT */
/* This file was generated from team_devices.stone, team_property_templates.stone, team_members.stone, team_linked_apps.stone, team_reports.stone, team_groups.stone, team.stone */

package com.dropbox.core.v2.team;

import com.dropbox.core.DbxApiException;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWrappedException;
import com.dropbox.core.http.HttpRequestor;
import com.dropbox.core.v2.DbxRawClientV2;
import com.dropbox.core.v2.async.LaunchEmptyResult;
import com.dropbox.core.v2.async.PollArg;
import com.dropbox.core.v2.async.PollEmptyResult;
import com.dropbox.core.v2.async.PollError;
import com.dropbox.core.v2.async.PollErrorException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Routes in namespace "team".
 */
public class DbxTeamTeamRequests {
    // namespace team (team_devices.stone, team_property_templates.stone, team_members.stone, team_linked_apps.stone, team_reports.stone, team_groups.stone, team.stone)

    private final DbxRawClientV2 client;

    public DbxTeamTeamRequests(DbxRawClientV2 client) {
        this.client = client;
    }

    //
    // route 2/team/devices/list_member_devices
    //

    /**
     * List all device sessions of a team's member.
     *
     */
    ListMemberDevicesResult devicesListMemberDevices(ListMemberDevicesArg arg) throws ListMemberDevicesErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/devices/list_member_devices",
                                        arg,
                                        false,
                                        ListMemberDevicesArg.Serializer.INSTANCE,
                                        ListMemberDevicesResult.Serializer.INSTANCE,
                                        ListMemberDevicesError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new ListMemberDevicesErrorException("2/team/devices/list_member_devices", ex.getRequestId(), ex.getUserMessage(), (ListMemberDevicesError) ex.getErrorValue());
        }
    }

    /**
     * List all device sessions of a team's member.
     *
     * <p> The default values for the optional request parameters will be used.
     * See {@link DevicesListMemberDevicesBuilder} for more details. </p>
     *
     * @param teamMemberId  The team's member id. Must not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public ListMemberDevicesResult devicesListMemberDevices(String teamMemberId) throws ListMemberDevicesErrorException, DbxException {
        ListMemberDevicesArg _arg = new ListMemberDevicesArg(teamMemberId);
        return devicesListMemberDevices(_arg);
    }

    /**
     * List all device sessions of a team's member.
     *
     * @param teamMemberId  The team's member id. Must not be {@code null}.
     *
     * @return Request builder for configuring request parameters and completing
     *     the request.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public DevicesListMemberDevicesBuilder devicesListMemberDevicesBuilder(String teamMemberId) {
        ListMemberDevicesArg.Builder argBuilder_ = ListMemberDevicesArg.newBuilder(teamMemberId);
        return new DevicesListMemberDevicesBuilder(this, argBuilder_);
    }

    //
    // route 2/team/devices/list_members_devices
    //

    /**
     * List all device sessions of a team.
     *
     */
    ListMembersDevicesResult devicesListMembersDevices(ListMembersDevicesArg arg) throws ListMembersDevicesErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/devices/list_members_devices",
                                        arg,
                                        false,
                                        ListMembersDevicesArg.Serializer.INSTANCE,
                                        ListMembersDevicesResult.Serializer.INSTANCE,
                                        ListMembersDevicesError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new ListMembersDevicesErrorException("2/team/devices/list_members_devices", ex.getRequestId(), ex.getUserMessage(), (ListMembersDevicesError) ex.getErrorValue());
        }
    }

    /**
     * List all device sessions of a team.
     *
     * <p> The default values for the optional request parameters will be used.
     * See {@link DevicesListMembersDevicesBuilder} for more details. </p>
     */
    public ListMembersDevicesResult devicesListMembersDevices() throws ListMembersDevicesErrorException, DbxException {
        ListMembersDevicesArg _arg = new ListMembersDevicesArg();
        return devicesListMembersDevices(_arg);
    }

    /**
     * List all device sessions of a team.
     *
     * @return Request builder for configuring request parameters and completing
     *     the request.
     */
    public DevicesListMembersDevicesBuilder devicesListMembersDevicesBuilder() {
        ListMembersDevicesArg.Builder argBuilder_ = ListMembersDevicesArg.newBuilder();
        return new DevicesListMembersDevicesBuilder(this, argBuilder_);
    }

    //
    // route 2/team/devices/list_team_devices
    //

    /**
     * List all device sessions of a team.
     *
     */
    ListTeamDevicesResult devicesListTeamDevices(ListTeamDevicesArg arg) throws ListTeamDevicesErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/devices/list_team_devices",
                                        arg,
                                        false,
                                        ListTeamDevicesArg.Serializer.INSTANCE,
                                        ListTeamDevicesResult.Serializer.INSTANCE,
                                        ListTeamDevicesError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new ListTeamDevicesErrorException("2/team/devices/list_team_devices", ex.getRequestId(), ex.getUserMessage(), (ListTeamDevicesError) ex.getErrorValue());
        }
    }

    /**
     * List all device sessions of a team.
     *
     * <p> The default values for the optional request parameters will be used.
     * See {@link DevicesListTeamDevicesBuilder} for more details. </p>
     *
     * @deprecated use {@link DbxTeamTeamRequests#devicesListMembersDevices}
     *     instead.
     */
    @Deprecated
    public ListTeamDevicesResult devicesListTeamDevices() throws ListTeamDevicesErrorException, DbxException {
        ListTeamDevicesArg _arg = new ListTeamDevicesArg();
        return devicesListTeamDevices(_arg);
    }

    /**
     * List all device sessions of a team.
     *
     * @return Request builder for configuring request parameters and completing
     *     the request.
     *
     * @deprecated use {@link DbxTeamTeamRequests#devicesListMembersDevices}
     *     instead.
     */
    @Deprecated
    public DevicesListTeamDevicesBuilder devicesListTeamDevicesBuilder() {
        ListTeamDevicesArg.Builder argBuilder_ = ListTeamDevicesArg.newBuilder();
        return new DevicesListTeamDevicesBuilder(this, argBuilder_);
    }

    //
    // route 2/team/devices/revoke_device_session
    //

    /**
     * Revoke a device session of a team's member
     *
     */
    public void devicesRevokeDeviceSession(RevokeDeviceSessionArg arg) throws RevokeDeviceSessionErrorException, DbxException {
        try {
            this.client.rpcStyle(this.client.getHost().getApi(),
                                 "2/team/devices/revoke_device_session",
                                 arg,
                                 false,
                                 RevokeDeviceSessionArg.Serializer.INSTANCE,
                                 com.dropbox.core.stone.StoneSerializers.void_(),
                                 RevokeDeviceSessionError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new RevokeDeviceSessionErrorException("2/team/devices/revoke_device_session", ex.getRequestId(), ex.getUserMessage(), (RevokeDeviceSessionError) ex.getErrorValue());
        }
    }

    //
    // route 2/team/devices/revoke_device_session_batch
    //

    /**
     * Revoke a list of device sessions of team members
     *
     */
    RevokeDeviceSessionBatchResult devicesRevokeDeviceSessionBatch(RevokeDeviceSessionBatchArg arg) throws RevokeDeviceSessionBatchErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/devices/revoke_device_session_batch",
                                        arg,
                                        false,
                                        RevokeDeviceSessionBatchArg.Serializer.INSTANCE,
                                        RevokeDeviceSessionBatchResult.Serializer.INSTANCE,
                                        RevokeDeviceSessionBatchError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new RevokeDeviceSessionBatchErrorException("2/team/devices/revoke_device_session_batch", ex.getRequestId(), ex.getUserMessage(), (RevokeDeviceSessionBatchError) ex.getErrorValue());
        }
    }

    /**
     * Revoke a list of device sessions of team members
     *
     * @param revokeDevices  Must not contain a {@code null} item and not be
     *     {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public RevokeDeviceSessionBatchResult devicesRevokeDeviceSessionBatch(List<RevokeDeviceSessionArg> revokeDevices) throws RevokeDeviceSessionBatchErrorException, DbxException {
        RevokeDeviceSessionBatchArg _arg = new RevokeDeviceSessionBatchArg(revokeDevices);
        return devicesRevokeDeviceSessionBatch(_arg);
    }

    //
    // route 2/team/get_info
    //

    /**
     * Retrieves information about a team.
     */
    public TeamGetInfoResult getInfo() throws DbxApiException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/get_info",
                                        null,
                                        false,
                                        com.dropbox.core.stone.StoneSerializers.void_(),
                                        TeamGetInfoResult.Serializer.INSTANCE,
                                        com.dropbox.core.stone.StoneSerializers.void_());
        }
        catch (DbxWrappedException ex) {
            throw new DbxApiException(ex.getRequestId(), ex.getUserMessage(), "Unexpected error response for \"get_info\":" + ex.getErrorValue());
        }
    }

    //
    // route 2/team/groups/create
    //

    /**
     * Creates a new, empty group, with a requested name. Permission : Team
     * member management
     *
     *
     * @return Full description of a group.
     */
    GroupFullInfo groupsCreate(GroupCreateArg arg) throws GroupCreateErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/groups/create",
                                        arg,
                                        false,
                                        GroupCreateArg.Serializer.INSTANCE,
                                        GroupFullInfo.Serializer.INSTANCE,
                                        GroupCreateError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new GroupCreateErrorException("2/team/groups/create", ex.getRequestId(), ex.getUserMessage(), (GroupCreateError) ex.getErrorValue());
        }
    }

    /**
     * Creates a new, empty group, with a requested name. Permission : Team
     * member management
     *
     * @param groupName  Group name. Must not be {@code null}.
     *
     * @return Full description of a group.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public GroupFullInfo groupsCreate(String groupName) throws GroupCreateErrorException, DbxException {
        GroupCreateArg _arg = new GroupCreateArg(groupName);
        return groupsCreate(_arg);
    }

    /**
     * Creates a new, empty group, with a requested name. Permission : Team
     * member management
     *
     * @param groupName  Group name. Must not be {@code null}.
     *
     * @return Request builder for configuring request parameters and completing
     *     the request.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public GroupsCreateBuilder groupsCreateBuilder(String groupName) {
        GroupCreateArg.Builder argBuilder_ = GroupCreateArg.newBuilder(groupName);
        return new GroupsCreateBuilder(this, argBuilder_);
    }

    //
    // route 2/team/groups/delete
    //

    /**
     * Deletes a group. The group is deleted immediately. However the revoking
     * of group-owned resources may take additional time. Use the {@link
     * DbxTeamTeamRequests#groupsJobStatusGet(String)} to determine whether this
     * process has completed. Permission : Team member management
     *
     * @param arg  Argument for selecting a single group, either by group_id or
     *     by external group ID.
     *
     * @return Result returned by methods that may either launch an asynchronous
     *     job or complete synchronously. Upon synchronous completion of the
     *     job, no additional information is returned.
     */
    public LaunchEmptyResult groupsDelete(GroupSelector arg) throws GroupDeleteErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/groups/delete",
                                        arg,
                                        false,
                                        GroupSelector.Serializer.INSTANCE,
                                        LaunchEmptyResult.Serializer.INSTANCE,
                                        GroupDeleteError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new GroupDeleteErrorException("2/team/groups/delete", ex.getRequestId(), ex.getUserMessage(), (GroupDeleteError) ex.getErrorValue());
        }
    }

    //
    // route 2/team/groups/get_info
    //

    /**
     * Retrieves information about one or more groups. Permission : Team
     * Information
     *
     * @param arg  Argument for selecting a list of groups, either by group_ids,
     *     or external group IDs.
     */
    public List<GroupsGetInfoItem> groupsGetInfo(GroupsSelector arg) throws GroupsGetInfoErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/groups/get_info",
                                        arg,
                                        false,
                                        GroupsSelector.Serializer.INSTANCE,
                                        com.dropbox.core.stone.StoneSerializers.list(GroupsGetInfoItem.Serializer.INSTANCE),
                                        GroupsGetInfoError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new GroupsGetInfoErrorException("2/team/groups/get_info", ex.getRequestId(), ex.getUserMessage(), (GroupsGetInfoError) ex.getErrorValue());
        }
    }

    //
    // route 2/team/groups/job_status/get
    //

    /**
     * Once an async_job_id is returned from {@link
     * DbxTeamTeamRequests#groupsDelete}, {@link
     * DbxTeamTeamRequests#groupsMembersAdd(GroupSelector,List,boolean)} , or
     * {@link
     * DbxTeamTeamRequests#groupsMembersRemove(GroupSelector,List,boolean)} use
     * this method to poll the status of granting/revoking group members' access
     * to group-owned resources. Permission : Team member management
     *
     * @param arg  Arguments for methods that poll the status of an asynchronous
     *     job.
     *
     * @return Result returned by methods that poll for the status of an
     *     asynchronous job. Upon completion of the job, no additional
     *     information is returned.
     */
    PollEmptyResult groupsJobStatusGet(PollArg arg) throws GroupsPollErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/groups/job_status/get",
                                        arg,
                                        false,
                                        PollArg.Serializer.INSTANCE,
                                        PollEmptyResult.Serializer.INSTANCE,
                                        GroupsPollError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new GroupsPollErrorException("2/team/groups/job_status/get", ex.getRequestId(), ex.getUserMessage(), (GroupsPollError) ex.getErrorValue());
        }
    }

    /**
     * Once an async_job_id is returned from {@link
     * DbxTeamTeamRequests#groupsDelete}, {@link
     * DbxTeamTeamRequests#groupsMembersAdd(GroupSelector,List,boolean)} , or
     * {@link
     * DbxTeamTeamRequests#groupsMembersRemove(GroupSelector,List,boolean)} use
     * this method to poll the status of granting/revoking group members' access
     * to group-owned resources. Permission : Team member management
     *
     * @param asyncJobId  Id of the asynchronous job. This is the value of a
     *     response returned from the method that launched the job. Must have
     *     length of at least 1 and not be {@code null}.
     *
     * @return Result returned by methods that poll for the status of an
     *     asynchronous job. Upon completion of the job, no additional
     *     information is returned.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public PollEmptyResult groupsJobStatusGet(String asyncJobId) throws GroupsPollErrorException, DbxException {
        PollArg _arg = new PollArg(asyncJobId);
        return groupsJobStatusGet(_arg);
    }

    //
    // route 2/team/groups/list
    //

    /**
     * Lists groups on a team. Permission : Team Information
     *
     */
    GroupsListResult groupsList(GroupsListArg arg) throws DbxApiException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/groups/list",
                                        arg,
                                        false,
                                        GroupsListArg.Serializer.INSTANCE,
                                        GroupsListResult.Serializer.INSTANCE,
                                        com.dropbox.core.stone.StoneSerializers.void_());
        }
        catch (DbxWrappedException ex) {
            throw new DbxApiException(ex.getRequestId(), ex.getUserMessage(), "Unexpected error response for \"groups/list\":" + ex.getErrorValue());
        }
    }

    /**
     * Lists groups on a team. Permission : Team Information
     *
     * <p> The {@code limit} request parameter will default to {@code 1000L}
     * (see {@link #groupsList(long)}). </p>
     */
    public GroupsListResult groupsList() throws DbxApiException, DbxException {
        GroupsListArg _arg = new GroupsListArg();
        return groupsList(_arg);
    }

    /**
     * Lists groups on a team. Permission : Team Information
     *
     * @param limit  Number of results to return per call. Must be greater than
     *     or equal to 1 and be less than or equal to 1000.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public GroupsListResult groupsList(long limit) throws DbxApiException, DbxException {
        if (limit < 1L) {
            throw new IllegalArgumentException("Number 'limit' is smaller than 1L");
        }
        if (limit > 1000L) {
            throw new IllegalArgumentException("Number 'limit' is larger than 1000L");
        }
        GroupsListArg _arg = new GroupsListArg(limit);
        return groupsList(_arg);
    }

    //
    // route 2/team/groups/list/continue
    //

    /**
     * Once a cursor has been retrieved from {@link
     * DbxTeamTeamRequests#groupsList(long)}, use this to paginate through all
     * groups. Permission : Team information
     *
     */
    GroupsListResult groupsListContinue(GroupsListContinueArg arg) throws GroupsListContinueErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/groups/list/continue",
                                        arg,
                                        false,
                                        GroupsListContinueArg.Serializer.INSTANCE,
                                        GroupsListResult.Serializer.INSTANCE,
                                        GroupsListContinueError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new GroupsListContinueErrorException("2/team/groups/list/continue", ex.getRequestId(), ex.getUserMessage(), (GroupsListContinueError) ex.getErrorValue());
        }
    }

    /**
     * Once a cursor has been retrieved from {@link
     * DbxTeamTeamRequests#groupsList(long)}, use this to paginate through all
     * groups. Permission : Team information
     *
     * @param cursor  Indicates from what point to get the next set of groups.
     *     Must not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public GroupsListResult groupsListContinue(String cursor) throws GroupsListContinueErrorException, DbxException {
        GroupsListContinueArg _arg = new GroupsListContinueArg(cursor);
        return groupsListContinue(_arg);
    }

    //
    // route 2/team/groups/members/add
    //

    /**
     * Adds members to a group. The members are added immediately. However the
     * granting of group-owned resources may take additional time. Use the
     * {@link DbxTeamTeamRequests#groupsJobStatusGet(String)} to determine
     * whether this process has completed. Permission : Team member management
     *
     *
     * @return Result returned by {@link
     *     DbxTeamTeamRequests#groupsMembersAdd(GroupSelector,List,boolean)} and
     *     {@link
     *     DbxTeamTeamRequests#groupsMembersRemove(GroupSelector,List,boolean)}.
     */
    GroupMembersChangeResult groupsMembersAdd(GroupMembersAddArg arg) throws GroupMembersAddErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/groups/members/add",
                                        arg,
                                        false,
                                        GroupMembersAddArg.Serializer.INSTANCE,
                                        GroupMembersChangeResult.Serializer.INSTANCE,
                                        GroupMembersAddError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new GroupMembersAddErrorException("2/team/groups/members/add", ex.getRequestId(), ex.getUserMessage(), (GroupMembersAddError) ex.getErrorValue());
        }
    }

    /**
     * Adds members to a group. The members are added immediately. However the
     * granting of group-owned resources may take additional time. Use the
     * {@link DbxTeamTeamRequests#groupsJobStatusGet(String)} to determine
     * whether this process has completed. Permission : Team member management
     *
     * <p> The {@code returnMembers} request parameter will default to {@code
     * true} (see {@link #groupsMembersAdd(GroupSelector,List,boolean)}). </p>
     *
     * @param group  Group to which users will be added. Must not be {@code
     *     null}.
     * @param members  List of users to be added to the group. Must not contain
     *     a {@code null} item and not be {@code null}.
     *
     * @return Result returned by {@link
     *     DbxTeamTeamRequests#groupsMembersAdd(GroupSelector,List,boolean)} and
     *     {@link
     *     DbxTeamTeamRequests#groupsMembersRemove(GroupSelector,List,boolean)}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public GroupMembersChangeResult groupsMembersAdd(GroupSelector group, List<MemberAccess> members) throws GroupMembersAddErrorException, DbxException {
        GroupMembersAddArg _arg = new GroupMembersAddArg(group, members);
        return groupsMembersAdd(_arg);
    }

    /**
     * Adds members to a group. The members are added immediately. However the
     * granting of group-owned resources may take additional time. Use the
     * {@link DbxTeamTeamRequests#groupsJobStatusGet(String)} to determine
     * whether this process has completed. Permission : Team member management
     *
     * @param group  Group to which users will be added. Must not be {@code
     *     null}.
     * @param members  List of users to be added to the group. Must not contain
     *     a {@code null} item and not be {@code null}.
     * @param returnMembers  Whether to return the list of members in the group.
     *     Note that the default value will cause all the group members  to be
     *     returned in the response. This may take a long time for large groups.
     *
     * @return Result returned by {@link
     *     DbxTeamTeamRequests#groupsMembersAdd(GroupSelector,List,boolean)} and
     *     {@link
     *     DbxTeamTeamRequests#groupsMembersRemove(GroupSelector,List,boolean)}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public GroupMembersChangeResult groupsMembersAdd(GroupSelector group, List<MemberAccess> members, boolean returnMembers) throws GroupMembersAddErrorException, DbxException {
        GroupMembersAddArg _arg = new GroupMembersAddArg(group, members, returnMembers);
        return groupsMembersAdd(_arg);
    }

    //
    // route 2/team/groups/members/list
    //

    /**
     * Lists members of a group. Permission : Team Information
     *
     */
    GroupsMembersListResult groupsMembersList(GroupsMembersListArg arg) throws GroupSelectorErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/groups/members/list",
                                        arg,
                                        false,
                                        GroupsMembersListArg.Serializer.INSTANCE,
                                        GroupsMembersListResult.Serializer.INSTANCE,
                                        GroupSelectorError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new GroupSelectorErrorException("2/team/groups/members/list", ex.getRequestId(), ex.getUserMessage(), (GroupSelectorError) ex.getErrorValue());
        }
    }

    /**
     * Lists members of a group. Permission : Team Information
     *
     * <p> The {@code limit} request parameter will default to {@code 1000L}
     * (see {@link #groupsMembersList(GroupSelector,long)}). </p>
     *
     * @param group  The group whose members are to be listed. Must not be
     *     {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public GroupsMembersListResult groupsMembersList(GroupSelector group) throws GroupSelectorErrorException, DbxException {
        GroupsMembersListArg _arg = new GroupsMembersListArg(group);
        return groupsMembersList(_arg);
    }

    /**
     * Lists members of a group. Permission : Team Information
     *
     * @param group  The group whose members are to be listed. Must not be
     *     {@code null}.
     * @param limit  Number of results to return per call. Must be greater than
     *     or equal to 1 and be less than or equal to 1000.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public GroupsMembersListResult groupsMembersList(GroupSelector group, long limit) throws GroupSelectorErrorException, DbxException {
        if (limit < 1L) {
            throw new IllegalArgumentException("Number 'limit' is smaller than 1L");
        }
        if (limit > 1000L) {
            throw new IllegalArgumentException("Number 'limit' is larger than 1000L");
        }
        GroupsMembersListArg _arg = new GroupsMembersListArg(group, limit);
        return groupsMembersList(_arg);
    }

    //
    // route 2/team/groups/members/list/continue
    //

    /**
     * Once a cursor has been retrieved from {@link
     * DbxTeamTeamRequests#groupsMembersList(GroupSelector,long)}, use this to
     * paginate through all members of the group. Permission : Team information
     *
     */
    GroupsMembersListResult groupsMembersListContinue(GroupsMembersListContinueArg arg) throws GroupsMembersListContinueErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/groups/members/list/continue",
                                        arg,
                                        false,
                                        GroupsMembersListContinueArg.Serializer.INSTANCE,
                                        GroupsMembersListResult.Serializer.INSTANCE,
                                        GroupsMembersListContinueError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new GroupsMembersListContinueErrorException("2/team/groups/members/list/continue", ex.getRequestId(), ex.getUserMessage(), (GroupsMembersListContinueError) ex.getErrorValue());
        }
    }

    /**
     * Once a cursor has been retrieved from {@link
     * DbxTeamTeamRequests#groupsMembersList(GroupSelector,long)}, use this to
     * paginate through all members of the group. Permission : Team information
     *
     * @param cursor  Indicates from what point to get the next set of groups.
     *     Must not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public GroupsMembersListResult groupsMembersListContinue(String cursor) throws GroupsMembersListContinueErrorException, DbxException {
        GroupsMembersListContinueArg _arg = new GroupsMembersListContinueArg(cursor);
        return groupsMembersListContinue(_arg);
    }

    //
    // route 2/team/groups/members/remove
    //

    /**
     * Removes members from a group. The members are removed immediately.
     * However the revoking of group-owned resources may take additional time.
     * Use the {@link DbxTeamTeamRequests#groupsJobStatusGet(String)} to
     * determine whether this process has completed. This method permits
     * removing the only owner of a group, even in cases where this is not
     * possible via the web client. Permission : Team member management
     *
     *
     * @return Result returned by {@link
     *     DbxTeamTeamRequests#groupsMembersAdd(GroupSelector,List,boolean)} and
     *     {@link
     *     DbxTeamTeamRequests#groupsMembersRemove(GroupSelector,List,boolean)}.
     */
    GroupMembersChangeResult groupsMembersRemove(GroupMembersRemoveArg arg) throws GroupMembersRemoveErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/groups/members/remove",
                                        arg,
                                        false,
                                        GroupMembersRemoveArg.Serializer.INSTANCE,
                                        GroupMembersChangeResult.Serializer.INSTANCE,
                                        GroupMembersRemoveError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new GroupMembersRemoveErrorException("2/team/groups/members/remove", ex.getRequestId(), ex.getUserMessage(), (GroupMembersRemoveError) ex.getErrorValue());
        }
    }

    /**
     * Removes members from a group. The members are removed immediately.
     * However the revoking of group-owned resources may take additional time.
     * Use the {@link DbxTeamTeamRequests#groupsJobStatusGet(String)} to
     * determine whether this process has completed. This method permits
     * removing the only owner of a group, even in cases where this is not
     * possible via the web client. Permission : Team member management
     *
     * <p> The {@code returnMembers} request parameter will default to {@code
     * true} (see {@link #groupsMembersRemove(GroupSelector,List,boolean)}).
     * </p>
     *
     * @param group  Group from which users will be removed. Must not be {@code
     *     null}.
     * @param users  List of users to be removed from the group. Must not
     *     contain a {@code null} item and not be {@code null}.
     *
     * @return Result returned by {@link
     *     DbxTeamTeamRequests#groupsMembersAdd(GroupSelector,List,boolean)} and
     *     {@link
     *     DbxTeamTeamRequests#groupsMembersRemove(GroupSelector,List,boolean)}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public GroupMembersChangeResult groupsMembersRemove(GroupSelector group, List<UserSelectorArg> users) throws GroupMembersRemoveErrorException, DbxException {
        GroupMembersRemoveArg _arg = new GroupMembersRemoveArg(group, users);
        return groupsMembersRemove(_arg);
    }

    /**
     * Removes members from a group. The members are removed immediately.
     * However the revoking of group-owned resources may take additional time.
     * Use the {@link DbxTeamTeamRequests#groupsJobStatusGet(String)} to
     * determine whether this process has completed. This method permits
     * removing the only owner of a group, even in cases where this is not
     * possible via the web client. Permission : Team member management
     *
     * @param group  Group from which users will be removed. Must not be {@code
     *     null}.
     * @param users  List of users to be removed from the group. Must not
     *     contain a {@code null} item and not be {@code null}.
     * @param returnMembers  Whether to return the list of members in the group.
     *     Note that the default value will cause all the group members  to be
     *     returned in the response. This may take a long time for large groups.
     *
     * @return Result returned by {@link
     *     DbxTeamTeamRequests#groupsMembersAdd(GroupSelector,List,boolean)} and
     *     {@link
     *     DbxTeamTeamRequests#groupsMembersRemove(GroupSelector,List,boolean)}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public GroupMembersChangeResult groupsMembersRemove(GroupSelector group, List<UserSelectorArg> users, boolean returnMembers) throws GroupMembersRemoveErrorException, DbxException {
        GroupMembersRemoveArg _arg = new GroupMembersRemoveArg(group, users, returnMembers);
        return groupsMembersRemove(_arg);
    }

    //
    // route 2/team/groups/members/set_access_type
    //

    /**
     * Sets a member's access type in a group. Permission : Team member
     * management
     *
     */
    List<GroupsGetInfoItem> groupsMembersSetAccessType(GroupMembersSetAccessTypeArg arg) throws GroupMemberSetAccessTypeErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/groups/members/set_access_type",
                                        arg,
                                        false,
                                        GroupMembersSetAccessTypeArg.Serializer.INSTANCE,
                                        com.dropbox.core.stone.StoneSerializers.list(GroupsGetInfoItem.Serializer.INSTANCE),
                                        GroupMemberSetAccessTypeError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new GroupMemberSetAccessTypeErrorException("2/team/groups/members/set_access_type", ex.getRequestId(), ex.getUserMessage(), (GroupMemberSetAccessTypeError) ex.getErrorValue());
        }
    }

    /**
     * Sets a member's access type in a group. Permission : Team member
     * management
     *
     * <p> The {@code returnMembers} request parameter will default to {@code
     * true} (see {@link
     * #groupsMembersSetAccessType(GroupSelector,UserSelectorArg,GroupAccessType,boolean)}).
     * </p>
     *
     * @param group  Specify a group. Must not be {@code null}.
     * @param user  Identity of a user that is a member of the {@code group}
     *     argument to {@link
     *     DbxTeamTeamRequests#groupsMembersSetAccessType(GroupSelector,UserSelectorArg,GroupAccessType,boolean)}.
     *     Must not be {@code null}.
     * @param accessType  New group access type the user will have. Must not be
     *     {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public List<GroupsGetInfoItem> groupsMembersSetAccessType(GroupSelector group, UserSelectorArg user, GroupAccessType accessType) throws GroupMemberSetAccessTypeErrorException, DbxException {
        GroupMembersSetAccessTypeArg _arg = new GroupMembersSetAccessTypeArg(group, user, accessType);
        return groupsMembersSetAccessType(_arg);
    }

    /**
     * Sets a member's access type in a group. Permission : Team member
     * management
     *
     * @param group  Specify a group. Must not be {@code null}.
     * @param user  Identity of a user that is a member of the {@code group}
     *     argument to {@link
     *     DbxTeamTeamRequests#groupsMembersSetAccessType(GroupSelector,UserSelectorArg,GroupAccessType,boolean)}.
     *     Must not be {@code null}.
     * @param accessType  New group access type the user will have. Must not be
     *     {@code null}.
     * @param returnMembers  Whether to return the list of members in the group.
     *     Note that the default value will cause all the group members  to be
     *     returned in the response. This may take a long time for large groups.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public List<GroupsGetInfoItem> groupsMembersSetAccessType(GroupSelector group, UserSelectorArg user, GroupAccessType accessType, boolean returnMembers) throws GroupMemberSetAccessTypeErrorException, DbxException {
        GroupMembersSetAccessTypeArg _arg = new GroupMembersSetAccessTypeArg(group, user, accessType, returnMembers);
        return groupsMembersSetAccessType(_arg);
    }

    //
    // route 2/team/groups/update
    //

    /**
     * Updates a group's name and/or external ID. Permission : Team member
     * management
     *
     *
     * @return Full description of a group.
     */
    GroupFullInfo groupsUpdate(GroupUpdateArgs arg) throws GroupUpdateErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/groups/update",
                                        arg,
                                        false,
                                        GroupUpdateArgs.Serializer.INSTANCE,
                                        GroupFullInfo.Serializer.INSTANCE,
                                        GroupUpdateError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new GroupUpdateErrorException("2/team/groups/update", ex.getRequestId(), ex.getUserMessage(), (GroupUpdateError) ex.getErrorValue());
        }
    }

    /**
     * Updates a group's name and/or external ID. Permission : Team member
     * management
     *
     * <p> The default values for the optional request parameters will be used.
     * See {@link GroupsUpdateBuilder} for more details. </p>
     *
     * @param group  Specify a group. Must not be {@code null}.
     *
     * @return Full description of a group.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public GroupFullInfo groupsUpdate(GroupSelector group) throws GroupUpdateErrorException, DbxException {
        GroupUpdateArgs _arg = new GroupUpdateArgs(group);
        return groupsUpdate(_arg);
    }

    /**
     * Updates a group's name and/or external ID. Permission : Team member
     * management
     *
     * @param group  Specify a group. Must not be {@code null}.
     *
     * @return Request builder for configuring request parameters and completing
     *     the request.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public GroupsUpdateBuilder groupsUpdateBuilder(GroupSelector group) {
        GroupUpdateArgs.Builder argBuilder_ = GroupUpdateArgs.newBuilder(group);
        return new GroupsUpdateBuilder(this, argBuilder_);
    }

    //
    // route 2/team/linked_apps/list_member_linked_apps
    //

    /**
     * List all linked applications of the team member. Note, this endpoint does
     * not list any team-linked applications.
     *
     */
    ListMemberAppsResult linkedAppsListMemberLinkedApps(ListMemberAppsArg arg) throws ListMemberAppsErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/linked_apps/list_member_linked_apps",
                                        arg,
                                        false,
                                        ListMemberAppsArg.Serializer.INSTANCE,
                                        ListMemberAppsResult.Serializer.INSTANCE,
                                        ListMemberAppsError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new ListMemberAppsErrorException("2/team/linked_apps/list_member_linked_apps", ex.getRequestId(), ex.getUserMessage(), (ListMemberAppsError) ex.getErrorValue());
        }
    }

    /**
     * List all linked applications of the team member. Note, this endpoint does
     * not list any team-linked applications.
     *
     * @param teamMemberId  The team member id. Must not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public ListMemberAppsResult linkedAppsListMemberLinkedApps(String teamMemberId) throws ListMemberAppsErrorException, DbxException {
        ListMemberAppsArg _arg = new ListMemberAppsArg(teamMemberId);
        return linkedAppsListMemberLinkedApps(_arg);
    }

    //
    // route 2/team/linked_apps/list_members_linked_apps
    //

    /**
     * List all applications linked to the team members' accounts. Note, this
     * endpoint does not list any team-linked applications.
     *
     * @param arg  Arguments for {@link
     *     DbxTeamTeamRequests#linkedAppsListMembersLinkedApps(String)}.
     *
     * @return Information returned by {@link
     *     DbxTeamTeamRequests#linkedAppsListMembersLinkedApps(String)}.
     */
    ListMembersAppsResult linkedAppsListMembersLinkedApps(ListMembersAppsArg arg) throws ListMembersAppsErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/linked_apps/list_members_linked_apps",
                                        arg,
                                        false,
                                        ListMembersAppsArg.Serializer.INSTANCE,
                                        ListMembersAppsResult.Serializer.INSTANCE,
                                        ListMembersAppsError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new ListMembersAppsErrorException("2/team/linked_apps/list_members_linked_apps", ex.getRequestId(), ex.getUserMessage(), (ListMembersAppsError) ex.getErrorValue());
        }
    }

    /**
     * List all applications linked to the team members' accounts. Note, this
     * endpoint does not list any team-linked applications.
     *
     * @return Information returned by {@link
     *     DbxTeamTeamRequests#linkedAppsListMembersLinkedApps(String)}.
     */
    public ListMembersAppsResult linkedAppsListMembersLinkedApps() throws ListMembersAppsErrorException, DbxException {
        ListMembersAppsArg _arg = new ListMembersAppsArg();
        return linkedAppsListMembersLinkedApps(_arg);
    }

    /**
     * List all applications linked to the team members' accounts. Note, this
     * endpoint does not list any team-linked applications.
     *
     * @param cursor  At the first call to the {@link
     *     DbxTeamTeamRequests#linkedAppsListMembersLinkedApps(String)} the
     *     cursor shouldn't be passed. Then, if the result of the call includes
     *     a cursor, the following requests should include the received cursors
     *     in order to receive the next sub list of the team applications.
     *
     * @return Information returned by {@link
     *     DbxTeamTeamRequests#linkedAppsListMembersLinkedApps(String)}.
     */
    public ListMembersAppsResult linkedAppsListMembersLinkedApps(String cursor) throws ListMembersAppsErrorException, DbxException {
        ListMembersAppsArg _arg = new ListMembersAppsArg(cursor);
        return linkedAppsListMembersLinkedApps(_arg);
    }

    //
    // route 2/team/linked_apps/list_team_linked_apps
    //

    /**
     * List all applications linked to the team members' accounts. Note, this
     * endpoint doesn't list any team-linked applications.
     *
     * @param arg  Arguments for {@link
     *     DbxTeamTeamRequests#linkedAppsListTeamLinkedApps(String)}.
     *
     * @return Information returned by {@link
     *     DbxTeamTeamRequests#linkedAppsListTeamLinkedApps(String)}.
     */
    ListTeamAppsResult linkedAppsListTeamLinkedApps(ListTeamAppsArg arg) throws ListTeamAppsErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/linked_apps/list_team_linked_apps",
                                        arg,
                                        false,
                                        ListTeamAppsArg.Serializer.INSTANCE,
                                        ListTeamAppsResult.Serializer.INSTANCE,
                                        ListTeamAppsError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new ListTeamAppsErrorException("2/team/linked_apps/list_team_linked_apps", ex.getRequestId(), ex.getUserMessage(), (ListTeamAppsError) ex.getErrorValue());
        }
    }

    /**
     * List all applications linked to the team members' accounts. Note, this
     * endpoint doesn't list any team-linked applications.
     *
     * @return Information returned by {@link
     *     DbxTeamTeamRequests#linkedAppsListTeamLinkedApps(String)}.
     *
     * @deprecated use {@link
     *     DbxTeamTeamRequests#linkedAppsListMembersLinkedApps(String)} instead.
     */
    @Deprecated
    public ListTeamAppsResult linkedAppsListTeamLinkedApps() throws ListTeamAppsErrorException, DbxException {
        ListTeamAppsArg _arg = new ListTeamAppsArg();
        return linkedAppsListTeamLinkedApps(_arg);
    }

    /**
     * List all applications linked to the team members' accounts. Note, this
     * endpoint doesn't list any team-linked applications.
     *
     * @param cursor  At the first call to the {@link
     *     DbxTeamTeamRequests#linkedAppsListTeamLinkedApps(String)} the cursor
     *     shouldn't be passed. Then, if the result of the call includes a
     *     cursor, the following requests should include the received cursors in
     *     order to receive the next sub list of the team applications.
     *
     * @return Information returned by {@link
     *     DbxTeamTeamRequests#linkedAppsListTeamLinkedApps(String)}.
     *
     * @deprecated use {@link
     *     DbxTeamTeamRequests#linkedAppsListMembersLinkedApps(String)} instead.
     */
    @Deprecated
    public ListTeamAppsResult linkedAppsListTeamLinkedApps(String cursor) throws ListTeamAppsErrorException, DbxException {
        ListTeamAppsArg _arg = new ListTeamAppsArg(cursor);
        return linkedAppsListTeamLinkedApps(_arg);
    }

    //
    // route 2/team/linked_apps/revoke_linked_app
    //

    /**
     * Revoke a linked application of the team member
     *
     */
    void linkedAppsRevokeLinkedApp(RevokeLinkedApiAppArg arg) throws RevokeLinkedAppErrorException, DbxException {
        try {
            this.client.rpcStyle(this.client.getHost().getApi(),
                                 "2/team/linked_apps/revoke_linked_app",
                                 arg,
                                 false,
                                 RevokeLinkedApiAppArg.Serializer.INSTANCE,
                                 com.dropbox.core.stone.StoneSerializers.void_(),
                                 RevokeLinkedAppError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new RevokeLinkedAppErrorException("2/team/linked_apps/revoke_linked_app", ex.getRequestId(), ex.getUserMessage(), (RevokeLinkedAppError) ex.getErrorValue());
        }
    }

    /**
     * Revoke a linked application of the team member
     *
     * <p> The {@code keepAppFolder} request parameter will default to {@code
     * true} (see {@link #linkedAppsRevokeLinkedApp(String,String,boolean)}).
     * </p>
     *
     * @param appId  The application's unique id. Must not be {@code null}.
     * @param teamMemberId  The unique id of the member owning the device. Must
     *     not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public void linkedAppsRevokeLinkedApp(String appId, String teamMemberId) throws RevokeLinkedAppErrorException, DbxException {
        RevokeLinkedApiAppArg _arg = new RevokeLinkedApiAppArg(appId, teamMemberId);
        linkedAppsRevokeLinkedApp(_arg);
    }

    /**
     * Revoke a linked application of the team member
     *
     * @param appId  The application's unique id. Must not be {@code null}.
     * @param teamMemberId  The unique id of the member owning the device. Must
     *     not be {@code null}.
     * @param keepAppFolder  Whether to keep the application dedicated folder
     *     (in case the application uses  one).
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public void linkedAppsRevokeLinkedApp(String appId, String teamMemberId, boolean keepAppFolder) throws RevokeLinkedAppErrorException, DbxException {
        RevokeLinkedApiAppArg _arg = new RevokeLinkedApiAppArg(appId, teamMemberId, keepAppFolder);
        linkedAppsRevokeLinkedApp(_arg);
    }

    //
    // route 2/team/linked_apps/revoke_linked_app_batch
    //

    /**
     * Revoke a list of linked applications of the team members
     *
     */
    RevokeLinkedAppBatchResult linkedAppsRevokeLinkedAppBatch(RevokeLinkedApiAppBatchArg arg) throws RevokeLinkedAppBatchErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/linked_apps/revoke_linked_app_batch",
                                        arg,
                                        false,
                                        RevokeLinkedApiAppBatchArg.Serializer.INSTANCE,
                                        RevokeLinkedAppBatchResult.Serializer.INSTANCE,
                                        RevokeLinkedAppBatchError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new RevokeLinkedAppBatchErrorException("2/team/linked_apps/revoke_linked_app_batch", ex.getRequestId(), ex.getUserMessage(), (RevokeLinkedAppBatchError) ex.getErrorValue());
        }
    }

    /**
     * Revoke a list of linked applications of the team members
     *
     * @param revokeLinkedApp  Must not contain a {@code null} item and not be
     *     {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public RevokeLinkedAppBatchResult linkedAppsRevokeLinkedAppBatch(List<RevokeLinkedApiAppArg> revokeLinkedApp) throws RevokeLinkedAppBatchErrorException, DbxException {
        RevokeLinkedApiAppBatchArg _arg = new RevokeLinkedApiAppBatchArg(revokeLinkedApp);
        return linkedAppsRevokeLinkedAppBatch(_arg);
    }

    //
    // route 2/team/members/add
    //

    /**
     * Adds members to a team. Permission : Team member management A maximum of
     * 20 members can be specified in a single call. If no Dropbox account
     * exists with the email address specified, a new Dropbox account will be
     * created with the given email address, and that account will be invited to
     * the team. If a personal Dropbox account exists with the email address
     * specified in the call, this call will create a placeholder Dropbox
     * account for the user on the team and send an email inviting the user to
     * migrate their existing personal account onto the team. Team member
     * management apps are required to set an initial given_name and surname for
     * a user to use in the team invitation and for 'Perform as team member'
     * actions taken on the user before they become 'active'.
     *
     */
    MembersAddLaunch membersAdd(MembersAddArg arg) throws DbxApiException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/members/add",
                                        arg,
                                        false,
                                        MembersAddArg.Serializer.INSTANCE,
                                        MembersAddLaunch.Serializer.INSTANCE,
                                        com.dropbox.core.stone.StoneSerializers.void_());
        }
        catch (DbxWrappedException ex) {
            throw new DbxApiException(ex.getRequestId(), ex.getUserMessage(), "Unexpected error response for \"members/add\":" + ex.getErrorValue());
        }
    }

    /**
     * Adds members to a team. Permission : Team member management A maximum of
     * 20 members can be specified in a single call. If no Dropbox account
     * exists with the email address specified, a new Dropbox account will be
     * created with the given email address, and that account will be invited to
     * the team. If a personal Dropbox account exists with the email address
     * specified in the call, this call will create a placeholder Dropbox
     * account for the user on the team and send an email inviting the user to
     * migrate their existing personal account onto the team. Team member
     * management apps are required to set an initial given_name and surname for
     * a user to use in the team invitation and for 'Perform as team member'
     * actions taken on the user before they become 'active'.
     *
     * <p> The {@code forceAsync} request parameter will default to {@code
     * false} (see {@link #membersAdd(List,boolean)}). </p>
     *
     * @param newMembers  Details of new members to be added to the team. Must
     *     not contain a {@code null} item and not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public MembersAddLaunch membersAdd(List<MemberAddArg> newMembers) throws DbxApiException, DbxException {
        MembersAddArg _arg = new MembersAddArg(newMembers);
        return membersAdd(_arg);
    }

    /**
     * Adds members to a team. Permission : Team member management A maximum of
     * 20 members can be specified in a single call. If no Dropbox account
     * exists with the email address specified, a new Dropbox account will be
     * created with the given email address, and that account will be invited to
     * the team. If a personal Dropbox account exists with the email address
     * specified in the call, this call will create a placeholder Dropbox
     * account for the user on the team and send an email inviting the user to
     * migrate their existing personal account onto the team. Team member
     * management apps are required to set an initial given_name and surname for
     * a user to use in the team invitation and for 'Perform as team member'
     * actions taken on the user before they become 'active'.
     *
     * @param newMembers  Details of new members to be added to the team. Must
     *     not contain a {@code null} item and not be {@code null}.
     * @param forceAsync  Whether to force the add to happen asynchronously.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public MembersAddLaunch membersAdd(List<MemberAddArg> newMembers, boolean forceAsync) throws DbxApiException, DbxException {
        MembersAddArg _arg = new MembersAddArg(newMembers, forceAsync);
        return membersAdd(_arg);
    }

    //
    // route 2/team/members/add/job_status/get
    //

    /**
     * Once an async_job_id is returned from {@link
     * DbxTeamTeamRequests#membersAdd(List,boolean)} , use this to poll the
     * status of the asynchronous request. Permission : Team member management
     *
     * @param arg  Arguments for methods that poll the status of an asynchronous
     *     job.
     */
    MembersAddJobStatus membersAddJobStatusGet(PollArg arg) throws PollErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/members/add/job_status/get",
                                        arg,
                                        false,
                                        PollArg.Serializer.INSTANCE,
                                        MembersAddJobStatus.Serializer.INSTANCE,
                                        PollError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new PollErrorException("2/team/members/add/job_status/get", ex.getRequestId(), ex.getUserMessage(), (PollError) ex.getErrorValue());
        }
    }

    /**
     * Once an async_job_id is returned from {@link
     * DbxTeamTeamRequests#membersAdd(List,boolean)} , use this to poll the
     * status of the asynchronous request. Permission : Team member management
     *
     * @param asyncJobId  Id of the asynchronous job. This is the value of a
     *     response returned from the method that launched the job. Must have
     *     length of at least 1 and not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public MembersAddJobStatus membersAddJobStatusGet(String asyncJobId) throws PollErrorException, DbxException {
        PollArg _arg = new PollArg(asyncJobId);
        return membersAddJobStatusGet(_arg);
    }

    //
    // route 2/team/members/get_info
    //

    /**
     * Returns information about multiple team members. Permission : Team
     * information This endpoint will return {@link
     * MembersGetInfoItem#getIdNotFoundValue}, for IDs (or emails) that cannot
     * be matched to a valid team member.
     *
     */
    List<MembersGetInfoItem> membersGetInfo(MembersGetInfoArgs arg) throws MembersGetInfoErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/members/get_info",
                                        arg,
                                        false,
                                        MembersGetInfoArgs.Serializer.INSTANCE,
                                        com.dropbox.core.stone.StoneSerializers.list(MembersGetInfoItem.Serializer.INSTANCE),
                                        MembersGetInfoError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new MembersGetInfoErrorException("2/team/members/get_info", ex.getRequestId(), ex.getUserMessage(), (MembersGetInfoError) ex.getErrorValue());
        }
    }

    /**
     * Returns information about multiple team members. Permission : Team
     * information This endpoint will return {@link
     * MembersGetInfoItem#getIdNotFoundValue}, for IDs (or emails) that cannot
     * be matched to a valid team member.
     *
     * @param members  List of team members. Must not contain a {@code null}
     *     item and not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public List<MembersGetInfoItem> membersGetInfo(List<UserSelectorArg> members) throws MembersGetInfoErrorException, DbxException {
        MembersGetInfoArgs _arg = new MembersGetInfoArgs(members);
        return membersGetInfo(_arg);
    }

    //
    // route 2/team/members/list
    //

    /**
     * Lists members of a team. Permission : Team information
     *
     */
    MembersListResult membersList(MembersListArg arg) throws MembersListErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/members/list",
                                        arg,
                                        false,
                                        MembersListArg.Serializer.INSTANCE,
                                        MembersListResult.Serializer.INSTANCE,
                                        MembersListError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new MembersListErrorException("2/team/members/list", ex.getRequestId(), ex.getUserMessage(), (MembersListError) ex.getErrorValue());
        }
    }

    /**
     * Lists members of a team. Permission : Team information
     *
     * <p> The default values for the optional request parameters will be used.
     * See {@link MembersListBuilder} for more details. </p>
     */
    public MembersListResult membersList() throws MembersListErrorException, DbxException {
        MembersListArg _arg = new MembersListArg();
        return membersList(_arg);
    }

    /**
     * Lists members of a team. Permission : Team information
     *
     * @return Request builder for configuring request parameters and completing
     *     the request.
     */
    public MembersListBuilder membersListBuilder() {
        MembersListArg.Builder argBuilder_ = MembersListArg.newBuilder();
        return new MembersListBuilder(this, argBuilder_);
    }

    //
    // route 2/team/members/list/continue
    //

    /**
     * Once a cursor has been retrieved from {@link
     * DbxTeamTeamRequests#membersList}, use this to paginate through all team
     * members. Permission : Team information
     *
     */
    MembersListResult membersListContinue(MembersListContinueArg arg) throws MembersListContinueErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/members/list/continue",
                                        arg,
                                        false,
                                        MembersListContinueArg.Serializer.INSTANCE,
                                        MembersListResult.Serializer.INSTANCE,
                                        MembersListContinueError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new MembersListContinueErrorException("2/team/members/list/continue", ex.getRequestId(), ex.getUserMessage(), (MembersListContinueError) ex.getErrorValue());
        }
    }

    /**
     * Once a cursor has been retrieved from {@link
     * DbxTeamTeamRequests#membersList}, use this to paginate through all team
     * members. Permission : Team information
     *
     * @param cursor  Indicates from what point to get the next set of members.
     *     Must not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public MembersListResult membersListContinue(String cursor) throws MembersListContinueErrorException, DbxException {
        MembersListContinueArg _arg = new MembersListContinueArg(cursor);
        return membersListContinue(_arg);
    }

    //
    // route 2/team/members/recover
    //

    /**
     * Recover a deleted member. Permission : Team member management Exactly one
     * of team_member_id, email, or external_id must be provided to identify the
     * user account.
     *
     * @param arg  Exactly one of team_member_id, email, or external_id must be
     *     provided to identify the user account.
     */
    void membersRecover(MembersRecoverArg arg) throws MembersRecoverErrorException, DbxException {
        try {
            this.client.rpcStyle(this.client.getHost().getApi(),
                                 "2/team/members/recover",
                                 arg,
                                 false,
                                 MembersRecoverArg.Serializer.INSTANCE,
                                 com.dropbox.core.stone.StoneSerializers.void_(),
                                 MembersRecoverError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new MembersRecoverErrorException("2/team/members/recover", ex.getRequestId(), ex.getUserMessage(), (MembersRecoverError) ex.getErrorValue());
        }
    }

    /**
     * Recover a deleted member. Permission : Team member management Exactly one
     * of team_member_id, email, or external_id must be provided to identify the
     * user account.
     *
     * @param user  Identity of user to recover. Must not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public void membersRecover(UserSelectorArg user) throws MembersRecoverErrorException, DbxException {
        MembersRecoverArg _arg = new MembersRecoverArg(user);
        membersRecover(_arg);
    }

    //
    // route 2/team/members/remove
    //

    /**
     * Removes a member from a team. Permission : Team member management Exactly
     * one of team_member_id, email, or external_id must be provided to identify
     * the user account. This is not a deactivation where the account can be
     * re-activated again. Calling {@link
     * DbxTeamTeamRequests#membersAdd(List,boolean)} with the removed user's
     * email address will create a new account with a new team_member_id that
     * will not have access to any content that was shared with the initial
     * account. This endpoint may initiate an asynchronous job. To obtain the
     * final result of the job, the client should periodically poll {@link
     * DbxTeamTeamRequests#membersRemoveJobStatusGet(String)}.
     *
     *
     * @return Result returned by methods that may either launch an asynchronous
     *     job or complete synchronously. Upon synchronous completion of the
     *     job, no additional information is returned.
     */
    LaunchEmptyResult membersRemove(MembersRemoveArg arg) throws MembersRemoveErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/members/remove",
                                        arg,
                                        false,
                                        MembersRemoveArg.Serializer.INSTANCE,
                                        LaunchEmptyResult.Serializer.INSTANCE,
                                        MembersRemoveError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new MembersRemoveErrorException("2/team/members/remove", ex.getRequestId(), ex.getUserMessage(), (MembersRemoveError) ex.getErrorValue());
        }
    }

    /**
     * Removes a member from a team. Permission : Team member management Exactly
     * one of team_member_id, email, or external_id must be provided to identify
     * the user account. This is not a deactivation where the account can be
     * re-activated again. Calling {@link
     * DbxTeamTeamRequests#membersAdd(List,boolean)} with the removed user's
     * email address will create a new account with a new team_member_id that
     * will not have access to any content that was shared with the initial
     * account. This endpoint may initiate an asynchronous job. To obtain the
     * final result of the job, the client should periodically poll {@link
     * DbxTeamTeamRequests#membersRemoveJobStatusGet(String)}.
     *
     * <p> The default values for the optional request parameters will be used.
     * See {@link MembersRemoveBuilder} for more details. </p>
     *
     * @param user  Identity of user to remove/suspend. Must not be {@code
     *     null}.
     *
     * @return Result returned by methods that may either launch an asynchronous
     *     job or complete synchronously. Upon synchronous completion of the
     *     job, no additional information is returned.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public LaunchEmptyResult membersRemove(UserSelectorArg user) throws MembersRemoveErrorException, DbxException {
        MembersRemoveArg _arg = new MembersRemoveArg(user);
        return membersRemove(_arg);
    }

    /**
     * Removes a member from a team. Permission : Team member management Exactly
     * one of team_member_id, email, or external_id must be provided to identify
     * the user account. This is not a deactivation where the account can be
     * re-activated again. Calling {@link
     * DbxTeamTeamRequests#membersAdd(List,boolean)} with the removed user's
     * email address will create a new account with a new team_member_id that
     * will not have access to any content that was shared with the initial
     * account. This endpoint may initiate an asynchronous job. To obtain the
     * final result of the job, the client should periodically poll {@link
     * DbxTeamTeamRequests#membersRemoveJobStatusGet(String)}.
     *
     * @param user  Identity of user to remove/suspend. Must not be {@code
     *     null}.
     *
     * @return Request builder for configuring request parameters and completing
     *     the request.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public MembersRemoveBuilder membersRemoveBuilder(UserSelectorArg user) {
        MembersRemoveArg.Builder argBuilder_ = MembersRemoveArg.newBuilder(user);
        return new MembersRemoveBuilder(this, argBuilder_);
    }

    //
    // route 2/team/members/remove/job_status/get
    //

    /**
     * Once an async_job_id is returned from {@link
     * DbxTeamTeamRequests#membersRemove(UserSelectorArg)} , use this to poll
     * the status of the asynchronous request. Permission : Team member
     * management
     *
     * @param arg  Arguments for methods that poll the status of an asynchronous
     *     job.
     *
     * @return Result returned by methods that poll for the status of an
     *     asynchronous job. Upon completion of the job, no additional
     *     information is returned.
     */
    PollEmptyResult membersRemoveJobStatusGet(PollArg arg) throws PollErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/members/remove/job_status/get",
                                        arg,
                                        false,
                                        PollArg.Serializer.INSTANCE,
                                        PollEmptyResult.Serializer.INSTANCE,
                                        PollError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new PollErrorException("2/team/members/remove/job_status/get", ex.getRequestId(), ex.getUserMessage(), (PollError) ex.getErrorValue());
        }
    }

    /**
     * Once an async_job_id is returned from {@link
     * DbxTeamTeamRequests#membersRemove(UserSelectorArg)} , use this to poll
     * the status of the asynchronous request. Permission : Team member
     * management
     *
     * @param asyncJobId  Id of the asynchronous job. This is the value of a
     *     response returned from the method that launched the job. Must have
     *     length of at least 1 and not be {@code null}.
     *
     * @return Result returned by methods that poll for the status of an
     *     asynchronous job. Upon completion of the job, no additional
     *     information is returned.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public PollEmptyResult membersRemoveJobStatusGet(String asyncJobId) throws PollErrorException, DbxException {
        PollArg _arg = new PollArg(asyncJobId);
        return membersRemoveJobStatusGet(_arg);
    }

    //
    // route 2/team/members/send_welcome_email
    //

    /**
     * Sends welcome email to pending team member. Permission : Team member
     * management Exactly one of team_member_id, email, or external_id must be
     * provided to identify the user account. No-op if team member is not
     * pending.
     *
     * @param arg  Argument for selecting a single user, either by
     *     team_member_id, external_id or email.
     */
    public void membersSendWelcomeEmail(UserSelectorArg arg) throws MembersSendWelcomeErrorException, DbxException {
        try {
            this.client.rpcStyle(this.client.getHost().getApi(),
                                 "2/team/members/send_welcome_email",
                                 arg,
                                 false,
                                 UserSelectorArg.Serializer.INSTANCE,
                                 com.dropbox.core.stone.StoneSerializers.void_(),
                                 MembersSendWelcomeError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new MembersSendWelcomeErrorException("2/team/members/send_welcome_email", ex.getRequestId(), ex.getUserMessage(), (MembersSendWelcomeError) ex.getErrorValue());
        }
    }

    //
    // route 2/team/members/set_admin_permissions
    //

    /**
     * Updates a team member's permissions. Permission : Team member management
     *
     * @param arg  Exactly one of team_member_id, email, or external_id must be
     *     provided to identify the user account.
     */
    MembersSetPermissionsResult membersSetAdminPermissions(MembersSetPermissionsArg arg) throws MembersSetPermissionsErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/members/set_admin_permissions",
                                        arg,
                                        false,
                                        MembersSetPermissionsArg.Serializer.INSTANCE,
                                        MembersSetPermissionsResult.Serializer.INSTANCE,
                                        MembersSetPermissionsError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new MembersSetPermissionsErrorException("2/team/members/set_admin_permissions", ex.getRequestId(), ex.getUserMessage(), (MembersSetPermissionsError) ex.getErrorValue());
        }
    }

    /**
     * Updates a team member's permissions. Permission : Team member management
     *
     * @param user  Identity of user whose role will be set. Must not be {@code
     *     null}.
     * @param newRole  The new role of the member. Must not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public MembersSetPermissionsResult membersSetAdminPermissions(UserSelectorArg user, AdminTier newRole) throws MembersSetPermissionsErrorException, DbxException {
        MembersSetPermissionsArg _arg = new MembersSetPermissionsArg(user, newRole);
        return membersSetAdminPermissions(_arg);
    }

    //
    // route 2/team/members/set_profile
    //

    /**
     * Updates a team member's profile. Permission : Team member management
     *
     * @param arg  Exactly one of team_member_id, email, or external_id must be
     *     provided to identify the user account. At least one of new_email,
     *     new_external_id, new_given_name, and/or new_surname must be provided.
     *
     * @return Information about a team member.
     */
    TeamMemberInfo membersSetProfile(MembersSetProfileArg arg) throws MembersSetProfileErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/members/set_profile",
                                        arg,
                                        false,
                                        MembersSetProfileArg.Serializer.INSTANCE,
                                        TeamMemberInfo.Serializer.INSTANCE,
                                        MembersSetProfileError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new MembersSetProfileErrorException("2/team/members/set_profile", ex.getRequestId(), ex.getUserMessage(), (MembersSetProfileError) ex.getErrorValue());
        }
    }

    /**
     * Updates a team member's profile. Permission : Team member management
     *
     * @param user  Identity of user whose profile will be set. Must not be
     *     {@code null}.
     *
     * @return Information about a team member.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public TeamMemberInfo membersSetProfile(UserSelectorArg user) throws MembersSetProfileErrorException, DbxException {
        MembersSetProfileArg _arg = new MembersSetProfileArg(user);
        return membersSetProfile(_arg);
    }

    /**
     * Updates a team member's profile. Permission : Team member management
     *
     * @param user  Identity of user whose profile will be set. Must not be
     *     {@code null}.
     *
     * @return Request builder for configuring request parameters and completing
     *     the request.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public MembersSetProfileBuilder membersSetProfileBuilder(UserSelectorArg user) {
        MembersSetProfileArg.Builder argBuilder_ = MembersSetProfileArg.newBuilder(user);
        return new MembersSetProfileBuilder(this, argBuilder_);
    }

    //
    // route 2/team/members/suspend
    //

    /**
     * Suspend a member from a team. Permission : Team member management Exactly
     * one of team_member_id, email, or external_id must be provided to identify
     * the user account.
     *
     * @param arg  Exactly one of team_member_id, email, or external_id must be
     *     provided to identify the user account.
     */
    void membersSuspend(MembersDeactivateArg arg) throws MembersSuspendErrorException, DbxException {
        try {
            this.client.rpcStyle(this.client.getHost().getApi(),
                                 "2/team/members/suspend",
                                 arg,
                                 false,
                                 MembersDeactivateArg.Serializer.INSTANCE,
                                 com.dropbox.core.stone.StoneSerializers.void_(),
                                 MembersSuspendError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new MembersSuspendErrorException("2/team/members/suspend", ex.getRequestId(), ex.getUserMessage(), (MembersSuspendError) ex.getErrorValue());
        }
    }

    /**
     * Suspend a member from a team. Permission : Team member management Exactly
     * one of team_member_id, email, or external_id must be provided to identify
     * the user account.
     *
     * <p> The {@code wipeData} request parameter will default to {@code true}
     * (see {@link #membersSuspend(UserSelectorArg,boolean)}). </p>
     *
     * @param user  Identity of user to remove/suspend. Must not be {@code
     *     null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public void membersSuspend(UserSelectorArg user) throws MembersSuspendErrorException, DbxException {
        MembersDeactivateArg _arg = new MembersDeactivateArg(user);
        membersSuspend(_arg);
    }

    /**
     * Suspend a member from a team. Permission : Team member management Exactly
     * one of team_member_id, email, or external_id must be provided to identify
     * the user account.
     *
     * @param user  Identity of user to remove/suspend. Must not be {@code
     *     null}.
     * @param wipeData  If provided, controls if the user's data will be deleted
     *     on their linked devices.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public void membersSuspend(UserSelectorArg user, boolean wipeData) throws MembersSuspendErrorException, DbxException {
        MembersDeactivateArg _arg = new MembersDeactivateArg(user, wipeData);
        membersSuspend(_arg);
    }

    //
    // route 2/team/members/unsuspend
    //

    /**
     * Unsuspend a member from a team. Permission : Team member management
     * Exactly one of team_member_id, email, or external_id must be provided to
     * identify the user account.
     *
     * @param arg  Exactly one of team_member_id, email, or external_id must be
     *     provided to identify the user account.
     */
    void membersUnsuspend(MembersUnsuspendArg arg) throws MembersUnsuspendErrorException, DbxException {
        try {
            this.client.rpcStyle(this.client.getHost().getApi(),
                                 "2/team/members/unsuspend",
                                 arg,
                                 false,
                                 MembersUnsuspendArg.Serializer.INSTANCE,
                                 com.dropbox.core.stone.StoneSerializers.void_(),
                                 MembersUnsuspendError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new MembersUnsuspendErrorException("2/team/members/unsuspend", ex.getRequestId(), ex.getUserMessage(), (MembersUnsuspendError) ex.getErrorValue());
        }
    }

    /**
     * Unsuspend a member from a team. Permission : Team member management
     * Exactly one of team_member_id, email, or external_id must be provided to
     * identify the user account.
     *
     * @param user  Identity of user to unsuspend. Must not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public void membersUnsuspend(UserSelectorArg user) throws MembersUnsuspendErrorException, DbxException {
        MembersUnsuspendArg _arg = new MembersUnsuspendArg(user);
        membersUnsuspend(_arg);
    }

    //
    // route 2/team/reports/get_activity
    //

    /**
     * Retrieves reporting data about a team's user activity.
     *
     * @param arg  Input arguments that can be provided for most reports.
     *
     * @return Activity Report Result. Each of the items in the storage report
     *     is an array of values, one value per day. If there is no data for a
     *     day, then the value will be None.
     */
    GetActivityReport reportsGetActivity(DateRange arg) throws DateRangeErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/reports/get_activity",
                                        arg,
                                        false,
                                        DateRange.Serializer.INSTANCE,
                                        GetActivityReport.Serializer.INSTANCE,
                                        DateRangeError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new DateRangeErrorException("2/team/reports/get_activity", ex.getRequestId(), ex.getUserMessage(), (DateRangeError) ex.getErrorValue());
        }
    }

    /**
     * Retrieves reporting data about a team's user activity.
     *
     * @return Activity Report Result. Each of the items in the storage report
     *     is an array of values, one value per day. If there is no data for a
     *     day, then the value will be None.
     */
    public GetActivityReport reportsGetActivity() throws DateRangeErrorException, DbxException {
        DateRange _arg = new DateRange();
        return reportsGetActivity(_arg);
    }

    /**
     * Retrieves reporting data about a team's user activity.
     *
     * @return Request builder for configuring request parameters and completing
     *     the request.
     */
    public ReportsGetActivityBuilder reportsGetActivityBuilder() {
        DateRange.Builder argBuilder_ = DateRange.newBuilder();
        return new ReportsGetActivityBuilder(this, argBuilder_);
    }

    //
    // route 2/team/reports/get_devices
    //

    /**
     * Retrieves reporting data about a team's linked devices.
     *
     * @param arg  Input arguments that can be provided for most reports.
     *
     * @return Devices Report Result. Contains subsections for different time
     *     ranges of activity. Each of the items in each subsection of the
     *     storage report is an array of values, one value per day. If there is
     *     no data for a day, then the value will be None.
     */
    GetDevicesReport reportsGetDevices(DateRange arg) throws DateRangeErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/reports/get_devices",
                                        arg,
                                        false,
                                        DateRange.Serializer.INSTANCE,
                                        GetDevicesReport.Serializer.INSTANCE,
                                        DateRangeError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new DateRangeErrorException("2/team/reports/get_devices", ex.getRequestId(), ex.getUserMessage(), (DateRangeError) ex.getErrorValue());
        }
    }

    /**
     * Retrieves reporting data about a team's linked devices.
     *
     * @return Devices Report Result. Contains subsections for different time
     *     ranges of activity. Each of the items in each subsection of the
     *     storage report is an array of values, one value per day. If there is
     *     no data for a day, then the value will be None.
     */
    public GetDevicesReport reportsGetDevices() throws DateRangeErrorException, DbxException {
        DateRange _arg = new DateRange();
        return reportsGetDevices(_arg);
    }

    /**
     * Retrieves reporting data about a team's linked devices.
     *
     * @return Request builder for configuring request parameters and completing
     *     the request.
     */
    public ReportsGetDevicesBuilder reportsGetDevicesBuilder() {
        DateRange.Builder argBuilder_ = DateRange.newBuilder();
        return new ReportsGetDevicesBuilder(this, argBuilder_);
    }

    //
    // route 2/team/reports/get_membership
    //

    /**
     * Retrieves reporting data about a team's membership.
     *
     * @param arg  Input arguments that can be provided for most reports.
     *
     * @return Membership Report Result. Each of the items in the storage report
     *     is an array of values, one value per day. If there is no data for a
     *     day, then the value will be None.
     */
    GetMembershipReport reportsGetMembership(DateRange arg) throws DateRangeErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/reports/get_membership",
                                        arg,
                                        false,
                                        DateRange.Serializer.INSTANCE,
                                        GetMembershipReport.Serializer.INSTANCE,
                                        DateRangeError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new DateRangeErrorException("2/team/reports/get_membership", ex.getRequestId(), ex.getUserMessage(), (DateRangeError) ex.getErrorValue());
        }
    }

    /**
     * Retrieves reporting data about a team's membership.
     *
     * @return Membership Report Result. Each of the items in the storage report
     *     is an array of values, one value per day. If there is no data for a
     *     day, then the value will be None.
     */
    public GetMembershipReport reportsGetMembership() throws DateRangeErrorException, DbxException {
        DateRange _arg = new DateRange();
        return reportsGetMembership(_arg);
    }

    /**
     * Retrieves reporting data about a team's membership.
     *
     * @return Request builder for configuring request parameters and completing
     *     the request.
     */
    public ReportsGetMembershipBuilder reportsGetMembershipBuilder() {
        DateRange.Builder argBuilder_ = DateRange.newBuilder();
        return new ReportsGetMembershipBuilder(this, argBuilder_);
    }

    //
    // route 2/team/reports/get_storage
    //

    /**
     * Retrieves reporting data about a team's storage usage.
     *
     * @param arg  Input arguments that can be provided for most reports.
     *
     * @return Storage Report Result. Each of the items in the storage report is
     *     an array of values, one value per day. If there is no data for a day,
     *     then the value will be None.
     */
    GetStorageReport reportsGetStorage(DateRange arg) throws DateRangeErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/team/reports/get_storage",
                                        arg,
                                        false,
                                        DateRange.Serializer.INSTANCE,
                                        GetStorageReport.Serializer.INSTANCE,
                                        DateRangeError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new DateRangeErrorException("2/team/reports/get_storage", ex.getRequestId(), ex.getUserMessage(), (DateRangeError) ex.getErrorValue());
        }
    }

    /**
     * Retrieves reporting data about a team's storage usage.
     *
     * @return Storage Report Result. Each of the items in the storage report is
     *     an array of values, one value per day. If there is no data for a day,
     *     then the value will be None.
     */
    public GetStorageReport reportsGetStorage() throws DateRangeErrorException, DbxException {
        DateRange _arg = new DateRange();
        return reportsGetStorage(_arg);
    }

    /**
     * Retrieves reporting data about a team's storage usage.
     *
     * @return Request builder for configuring request parameters and completing
     *     the request.
     */
    public ReportsGetStorageBuilder reportsGetStorageBuilder() {
        DateRange.Builder argBuilder_ = DateRange.newBuilder();
        return new ReportsGetStorageBuilder(this, argBuilder_);
    }
}
