/* DO NOT EDIT */
/* This file was generated by Stone */

package com.dropbox.core.v2.files;

import com.dropbox.core.DbxApiException;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.async.LaunchEmptyResult;

/**
 * The request builder returned by {@link
 * DbxUserFilesRequests#copyBatchBuilder}.
 *
 * <p> Use this class to set optional request parameters and complete the
 * request. </p>
 */
public class CopyBatchBuilder {
    private final DbxUserFilesRequests _client;
    private final RelocationBatchArg.Builder _builder;

    /**
     * Creates a new instance of this builder.
     *
     * @param _client  Dropbox namespace-specific client used to issue files
     *     requests.
     * @param _builder  Request argument builder.
     *
     * @return instsance of this builder
     */
    CopyBatchBuilder(DbxUserFilesRequests _client, RelocationBatchArg.Builder _builder) {
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
     * @param allowSharedFolder  If true, {@link
     *     DbxUserFilesRequests#copyBatch(java.util.List)} will copy contents in
     *     shared folder, otherwise {@link
     *     RelocationError#CANT_COPY_SHARED_FOLDER} will be returned if {@link
     *     RelocationPath#getFromPath} contains shared folder.  This field is
     *     always true for {@link
     *     DbxUserFilesRequests#moveBatch(java.util.List)}. Defaults to {@code
     *     false} when set to {@code null}.
     *
     * @return this builder
     */
    public CopyBatchBuilder withAllowSharedFolder(Boolean allowSharedFolder) {
        this._builder.withAllowSharedFolder(allowSharedFolder);
        return this;
    }

    /**
     * Set value for optional field.
     *
     * <p> If left unset or set to {@code null}, defaults to {@code false}. </p>
     *
     * @param autorename  If there's a conflict with any file, have the Dropbox
     *     server try to autorename that file to avoid the conflict. Defaults to
     *     {@code false} when set to {@code null}.
     *
     * @return this builder
     */
    public CopyBatchBuilder withAutorename(Boolean autorename) {
        this._builder.withAutorename(autorename);
        return this;
    }

    /**
     * Issues the request.
     */
    public LaunchEmptyResult start() throws DbxApiException, DbxException {
        RelocationBatchArg arg_ = this._builder.build();
        return _client.copyBatch(arg_);
    }
}
