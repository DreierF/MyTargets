/* DO NOT EDIT */
/* This file was generated by Stone */

package com.dropbox.core.v2.files;

import com.dropbox.core.DbxException;

/**
 * The request builder returned by {@link
 * DbxUserFilesRequests#getMetadataBuilder}.
 *
 * <p> Use this class to set optional request parameters and complete the
 * request. </p>
 */
public class GetMetadataBuilder {
    private final DbxUserFilesRequests _client;
    private final GetMetadataArg.Builder _builder;

    /**
     * Creates a new instance of this builder.
     *
     * @param _client  Dropbox namespace-specific client used to issue files
     *     requests.
     * @param _builder  Request argument builder.
     *
     * @return instsance of this builder
     */
    GetMetadataBuilder(DbxUserFilesRequests _client, GetMetadataArg.Builder _builder) {
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
     * <p> If left unset or set to {@code null}, defaults to {@code false}. </p>
     *
     * @param includeMediaInfo  If true, {@link FileMetadata#getMediaInfo} is
     *     set for photo and video. Defaults to {@code false} when set to {@code
     *     null}.
     *
     * @return this builder
     */
    public GetMetadataBuilder withIncludeMediaInfo(Boolean includeMediaInfo) {
        this._builder.withIncludeMediaInfo(includeMediaInfo);
        return this;
    }

    /**
     * Set value for optional field.
     *
     * <p> If left unset or set to {@code null}, defaults to {@code false}. </p>
     *
     * @param includeDeleted  If true, {@link DeletedMetadata} will be returned
     *     for deleted file or folder, otherwise {@link LookupError#NOT_FOUND}
     *     will be returned. Defaults to {@code false} when set to {@code null}.
     *
     * @return this builder
     */
    public GetMetadataBuilder withIncludeDeleted(Boolean includeDeleted) {
        this._builder.withIncludeDeleted(includeDeleted);
        return this;
    }

    /**
     * Set value for optional field.
     *
     * <p> If left unset or set to {@code null}, defaults to {@code false}. </p>
     *
     * @param includeHasExplicitSharedMembers  If true, the results will include
     *     a flag for each file indicating whether or not  that file has any
     *     explicit members. Defaults to {@code false} when set to {@code null}.
     *
     * @return this builder
     */
    public GetMetadataBuilder withIncludeHasExplicitSharedMembers(Boolean includeHasExplicitSharedMembers) {
        this._builder.withIncludeHasExplicitSharedMembers(includeHasExplicitSharedMembers);
        return this;
    }

    /**
     * Issues the request.
     */
    public Metadata start() throws GetMetadataErrorException, DbxException {
        GetMetadataArg arg_ = this._builder.build();
        return _client.getMetadata(arg_);
    }
}
