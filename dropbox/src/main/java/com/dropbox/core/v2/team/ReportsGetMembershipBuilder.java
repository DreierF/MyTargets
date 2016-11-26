/* DO NOT EDIT */
/* This file was generated by Stone */

package com.dropbox.core.v2.team;

import com.dropbox.core.DbxException;
import com.dropbox.core.util.LangUtil;

import java.util.Date;

/**
 * The request builder returned by {@link
 * DbxTeamTeamRequests#reportsGetMembershipBuilder}.
 *
 * <p> Use this class to set optional request parameters and complete the
 * request. </p>
 */
public class ReportsGetMembershipBuilder {
    private final DbxTeamTeamRequests _client;
    private final DateRange.Builder _builder;

    /**
     * Creates a new instance of this builder.
     *
     * @param _client  Dropbox namespace-specific client used to issue team
     *     requests.
     * @param _builder  Request argument builder.
     *
     * @return instsance of this builder
     */
    ReportsGetMembershipBuilder(DbxTeamTeamRequests _client, DateRange.Builder _builder) {
        if (_client == null) {
            throw new NullPointerException("_client");
        }
        this._client = _client;
        if (_builder == null) {
            throw new NullPointerException("_builder");
        }
        this._builder = _builder;
    }

    /**
     * Set value for optional field.
     *
     * @param startDate  Optional starting date (inclusive).
     *
     * @return this builder
     */
    public ReportsGetMembershipBuilder withStartDate(Date startDate) {
        this._builder.withStartDate(startDate);
        return this;
    }

    /**
     * Set value for optional field.
     *
     * @param endDate  Optional ending date (exclusive).
     *
     * @return this builder
     */
    public ReportsGetMembershipBuilder withEndDate(Date endDate) {
        this._builder.withEndDate(endDate);
        return this;
    }

    /**
     * Issues the request.
     */
    public GetMembershipReport start() throws DateRangeErrorException, DbxException {
        DateRange arg_ = this._builder.build();
        return _client.reportsGetMembership(arg_);
    }
}