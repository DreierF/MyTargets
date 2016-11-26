/* DO NOT EDIT */
/* This file was generated from files_properties.stone, files.stone */

package com.dropbox.core.v2.files;

import com.dropbox.core.DbxApiException;
import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxUploader;
import com.dropbox.core.DbxWrappedException;
import com.dropbox.core.http.HttpRequestor;
import com.dropbox.core.v2.DbxDownloadStyleBuilder;
import com.dropbox.core.v2.DbxRawClientV2;
import com.dropbox.core.v2.DbxUploadStyleBuilder;
import com.dropbox.core.v2.async.LaunchEmptyResult;
import com.dropbox.core.v2.async.PollArg;
import com.dropbox.core.v2.async.PollError;
import com.dropbox.core.v2.async.PollErrorException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Routes in namespace "files".
 */
public class DbxUserFilesRequests {
    // namespace files (files_properties.stone, files.stone)

    private final DbxRawClientV2 client;

    public DbxUserFilesRequests(DbxRawClientV2 client) {
        this.client = client;
    }

    //
    // route 2/files/copy
    //

    /**
     * Copy a file or folder to a different location in the user's Dropbox. If
     * the source path is a folder all its contents will be copied.
     *
     *
     * @return Metadata for a file or folder.
     */
    Metadata copy(RelocationArg arg) throws RelocationErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/copy",
                                        arg,
                                        false,
                                        RelocationArg.Serializer.INSTANCE,
                                        Metadata.Serializer.INSTANCE,
                                        RelocationError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new RelocationErrorException("2/files/copy", ex.getRequestId(), ex.getUserMessage(), (RelocationError) ex.getErrorValue());
        }
    }

    /**
     * Copy a file or folder to a different location in the user's Dropbox. If
     * the source path is a folder all its contents will be copied.
     *
     * <p> The default values for the optional request parameters will be used.
     * See {@link CopyBuilder} for more details. </p>
     *
     * @param fromPath  Path in the user's Dropbox to be copied or moved. Must
     *     match pattern "{@code (/(.|[\\r\\n])*)|(ns:[0-9]+(/.*)?)}" and not be
     *     {@code null}.
     * @param toPath  Path in the user's Dropbox that is the destination. Must
     *     match pattern "{@code (/(.|[\\r\\n])*)|(ns:[0-9]+(/.*)?)}" and not be
     *     {@code null}.
     *
     * @return Metadata for a file or folder.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public Metadata copy(String fromPath, String toPath) throws RelocationErrorException, DbxException {
        RelocationArg _arg = new RelocationArg(fromPath, toPath);
        return copy(_arg);
    }

    /**
     * Copy a file or folder to a different location in the user's Dropbox. If
     * the source path is a folder all its contents will be copied.
     *
     * @param fromPath  Path in the user's Dropbox to be copied or moved. Must
     *     match pattern "{@code (/(.|[\\r\\n])*)|(ns:[0-9]+(/.*)?)}" and not be
     *     {@code null}.
     * @param toPath  Path in the user's Dropbox that is the destination. Must
     *     match pattern "{@code (/(.|[\\r\\n])*)|(ns:[0-9]+(/.*)?)}" and not be
     *     {@code null}.
     *
     * @return Request builder for configuring request parameters and completing
     *     the request.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public CopyBuilder copyBuilder(String fromPath, String toPath) {
        RelocationArg.Builder argBuilder_ = RelocationArg.newBuilder(fromPath, toPath);
        return new CopyBuilder(this, argBuilder_);
    }

    //
    // route 2/files/copy_batch
    //

    /**
     * Copy multiple files or folders to different locations at once in the
     * user's Dropbox. If {@link RelocationBatchArg#getAllowSharedFolder} is
     * false, this route is atomic. If on entry failes, the whole transaction
     * will abort. If {@link RelocationBatchArg#getAllowSharedFolder} is true,
     * not atomicity is guaranteed, but you will be able to copy the contents of
     * shared folders to new locations. This route will return job ID
     * immediately and do the async copy job in background. Please use {@link
     * DbxUserFilesRequests#copyBatchCheck(String)} to check the job status.
     *
     *
     * @return Result returned by methods that may either launch an asynchronous
     *     job or complete synchronously. Upon synchronous completion of the
     *     job, no additional information is returned.
     */
    LaunchEmptyResult copyBatch(RelocationBatchArg arg) throws DbxApiException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/copy_batch",
                                        arg,
                                        false,
                                        RelocationBatchArg.Serializer.INSTANCE,
                                        LaunchEmptyResult.Serializer.INSTANCE,
                                        com.dropbox.core.stone.StoneSerializers.void_());
        }
        catch (DbxWrappedException ex) {
            throw new DbxApiException(ex.getRequestId(), ex.getUserMessage(), "Unexpected error response for \"copy_batch\":" + ex.getErrorValue());
        }
    }

    /**
     * Copy multiple files or folders to different locations at once in the
     * user's Dropbox. If {@link RelocationBatchArg#getAllowSharedFolder} is
     * false, this route is atomic. If on entry failes, the whole transaction
     * will abort. If {@link RelocationBatchArg#getAllowSharedFolder} is true,
     * not atomicity is guaranteed, but you will be able to copy the contents of
     * shared folders to new locations. This route will return job ID
     * immediately and do the async copy job in background. Please use {@link
     * DbxUserFilesRequests#copyBatchCheck(String)} to check the job status.
     *
     * <p> The default values for the optional request parameters will be used.
     * See {@link CopyBatchBuilder} for more details. </p>
     *
     * @param entries  List of entries to be moved or copied. Each entry is
     *     {@link RelocationPath}. Must not contain a {@code null} item and not
     *     be {@code null}.
     *
     * @return Result returned by methods that may either launch an asynchronous
     *     job or complete synchronously. Upon synchronous completion of the
     *     job, no additional information is returned.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public LaunchEmptyResult copyBatch(List<RelocationPath> entries) throws DbxApiException, DbxException {
        RelocationBatchArg _arg = new RelocationBatchArg(entries);
        return copyBatch(_arg);
    }

    /**
     * Copy multiple files or folders to different locations at once in the
     * user's Dropbox. If {@link RelocationBatchArg#getAllowSharedFolder} is
     * false, this route is atomic. If on entry failes, the whole transaction
     * will abort. If {@link RelocationBatchArg#getAllowSharedFolder} is true,
     * not atomicity is guaranteed, but you will be able to copy the contents of
     * shared folders to new locations. This route will return job ID
     * immediately and do the async copy job in background. Please use {@link
     * DbxUserFilesRequests#copyBatchCheck(String)} to check the job status.
     *
     * @param entries  List of entries to be moved or copied. Each entry is
     *     {@link RelocationPath}. Must not contain a {@code null} item and not
     *     be {@code null}.
     *
     * @return Request builder for configuring request parameters and completing
     *     the request.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public CopyBatchBuilder copyBatchBuilder(List<RelocationPath> entries) {
        RelocationBatchArg.Builder argBuilder_ = RelocationBatchArg.newBuilder(entries);
        return new CopyBatchBuilder(this, argBuilder_);
    }

    //
    // route 2/files/copy_batch/check
    //

    /**
     * Returns the status of an asynchronous job for {@link
     * DbxUserFilesRequests#copyBatch(List)}. If success, it returns list of
     * results for each entry.
     *
     * @param arg  Arguments for methods that poll the status of an asynchronous
     *     job.
     */
    RelocationBatchJobStatus copyBatchCheck(PollArg arg) throws PollErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/copy_batch/check",
                                        arg,
                                        false,
                                        PollArg.Serializer.INSTANCE,
                                        RelocationBatchJobStatus.Serializer.INSTANCE,
                                        PollError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new PollErrorException("2/files/copy_batch/check", ex.getRequestId(), ex.getUserMessage(), (PollError) ex.getErrorValue());
        }
    }

    /**
     * Returns the status of an asynchronous job for {@link
     * DbxUserFilesRequests#copyBatch(List)}. If success, it returns list of
     * results for each entry.
     *
     * @param asyncJobId  Id of the asynchronous job. This is the value of a
     *     response returned from the method that launched the job. Must have
     *     length of at least 1 and not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public RelocationBatchJobStatus copyBatchCheck(String asyncJobId) throws PollErrorException, DbxException {
        PollArg _arg = new PollArg(asyncJobId);
        return copyBatchCheck(_arg);
    }

    //
    // route 2/files/copy_reference/get
    //

    /**
     * Get a copy reference to a file or folder. This reference string can be
     * used to save that file or folder to another user's Dropbox by passing it
     * to {@link DbxUserFilesRequests#copyReferenceSave(String,String)}.
     *
     */
    GetCopyReferenceResult copyReferenceGet(GetCopyReferenceArg arg) throws GetCopyReferenceErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/copy_reference/get",
                                        arg,
                                        false,
                                        GetCopyReferenceArg.Serializer.INSTANCE,
                                        GetCopyReferenceResult.Serializer.INSTANCE,
                                        GetCopyReferenceError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new GetCopyReferenceErrorException("2/files/copy_reference/get", ex.getRequestId(), ex.getUserMessage(), (GetCopyReferenceError) ex.getErrorValue());
        }
    }

    /**
     * Get a copy reference to a file or folder. This reference string can be
     * used to save that file or folder to another user's Dropbox by passing it
     * to {@link DbxUserFilesRequests#copyReferenceSave(String,String)}.
     *
     * @param path  The path to the file or folder you want to get a copy
     *     reference to. Must match pattern "{@code
     *     (/(.|[\\r\\n])*|id:.*)|(rev:[0-9a-f]{9,})|(ns:[0-9]+(/.*)?)}" and not
     *     be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public GetCopyReferenceResult copyReferenceGet(String path) throws GetCopyReferenceErrorException, DbxException {
        GetCopyReferenceArg _arg = new GetCopyReferenceArg(path);
        return copyReferenceGet(_arg);
    }

    //
    // route 2/files/copy_reference/save
    //

    /**
     * Save a copy reference returned by {@link
     * DbxUserFilesRequests#copyReferenceGet(String)} to the user's Dropbox.
     *
     */
    SaveCopyReferenceResult copyReferenceSave(SaveCopyReferenceArg arg) throws SaveCopyReferenceErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/copy_reference/save",
                                        arg,
                                        false,
                                        SaveCopyReferenceArg.Serializer.INSTANCE,
                                        SaveCopyReferenceResult.Serializer.INSTANCE,
                                        SaveCopyReferenceError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new SaveCopyReferenceErrorException("2/files/copy_reference/save", ex.getRequestId(), ex.getUserMessage(), (SaveCopyReferenceError) ex.getErrorValue());
        }
    }

    /**
     * Save a copy reference returned by {@link
     * DbxUserFilesRequests#copyReferenceGet(String)} to the user's Dropbox.
     *
     * @param copyReference  A copy reference returned by {@link
     *     DbxUserFilesRequests#copyReferenceGet(String)}. Must not be {@code
     *     null}.
     * @param path  Path in the user's Dropbox that is the destination. Must
     *     match pattern "{@code /(.|[\\r\\n])*}" and not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public SaveCopyReferenceResult copyReferenceSave(String copyReference, String path) throws SaveCopyReferenceErrorException, DbxException {
        SaveCopyReferenceArg _arg = new SaveCopyReferenceArg(copyReference, path);
        return copyReferenceSave(_arg);
    }

    //
    // route 2/files/create_folder
    //

    /**
     * Create a folder at a given path.
     *
     */
    FolderMetadata createFolder(CreateFolderArg arg) throws CreateFolderErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/create_folder",
                                        arg,
                                        false,
                                        CreateFolderArg.Serializer.INSTANCE,
                                        FolderMetadata.Serializer.INSTANCE,
                                        CreateFolderError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new CreateFolderErrorException("2/files/create_folder", ex.getRequestId(), ex.getUserMessage(), (CreateFolderError) ex.getErrorValue());
        }
    }

    /**
     * Create a folder at a given path.
     *
     * <p> The {@code autorename} request parameter will default to {@code
     * false} (see {@link #createFolder(String,boolean)}). </p>
     *
     * @param path  Path in the user's Dropbox to create. Must match pattern
     *     "{@code (/(.|[\\r\\n])*)|(ns:[0-9]+(/.*)?)}" and not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public FolderMetadata createFolder(String path) throws CreateFolderErrorException, DbxException {
        CreateFolderArg _arg = new CreateFolderArg(path);
        return createFolder(_arg);
    }

    /**
     * Create a folder at a given path.
     *
     * @param path  Path in the user's Dropbox to create. Must match pattern
     *     "{@code (/(.|[\\r\\n])*)|(ns:[0-9]+(/.*)?)}" and not be {@code null}.
     * @param autorename  If there's a conflict, have the Dropbox server try to
     *     autorename the folder to avoid the conflict.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public FolderMetadata createFolder(String path, boolean autorename) throws CreateFolderErrorException, DbxException {
        CreateFolderArg _arg = new CreateFolderArg(path, autorename);
        return createFolder(_arg);
    }

    //
    // route 2/files/delete
    //

    /**
     * Delete the file or folder at a given path. If the path is a folder, all
     * its contents will be deleted too. A successful response indicates that
     * the file or folder was deleted. The returned metadata will be the
     * corresponding {@link FileMetadata} or {@link FolderMetadata} for the item
     * at time of deletion, and not a {@link DeletedMetadata} object.
     *
     *
     * @return Metadata for a file or folder.
     */
    Metadata delete(DeleteArg arg) throws DeleteErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/delete",
                                        arg,
                                        false,
                                        DeleteArg.Serializer.INSTANCE,
                                        Metadata.Serializer.INSTANCE,
                                        DeleteError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new DeleteErrorException("2/files/delete", ex.getRequestId(), ex.getUserMessage(), (DeleteError) ex.getErrorValue());
        }
    }

    /**
     * Delete the file or folder at a given path. If the path is a folder, all
     * its contents will be deleted too. A successful response indicates that
     * the file or folder was deleted. The returned metadata will be the
     * corresponding {@link FileMetadata} or {@link FolderMetadata} for the item
     * at time of deletion, and not a {@link DeletedMetadata} object.
     *
     * @param path  Path in the user's Dropbox to delete. Must match pattern
     *     "{@code (/(.|[\\r\\n])*)|(ns:[0-9]+(/.*)?)}" and not be {@code null}.
     *
     * @return Metadata for a file or folder.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public Metadata delete(String path) throws DeleteErrorException, DbxException {
        DeleteArg _arg = new DeleteArg(path);
        return delete(_arg);
    }

    //
    // route 2/files/delete_batch
    //

    /**
     * Delete multiple files/folders at once. This route is asynchronous, which
     * returns a job ID immediately and runs the delete batch asynchronously.
     * Use {@link DbxUserFilesRequests#deleteBatchCheck(String)} to check the
     * job status.
     *
     *
     * @return Result returned by methods that may either launch an asynchronous
     *     job or complete synchronously. Upon synchronous completion of the
     *     job, no additional information is returned.
     */
    LaunchEmptyResult deleteBatch(DeleteBatchArg arg) throws DbxApiException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/delete_batch",
                                        arg,
                                        false,
                                        DeleteBatchArg.Serializer.INSTANCE,
                                        LaunchEmptyResult.Serializer.INSTANCE,
                                        com.dropbox.core.stone.StoneSerializers.void_());
        }
        catch (DbxWrappedException ex) {
            throw new DbxApiException(ex.getRequestId(), ex.getUserMessage(), "Unexpected error response for \"delete_batch\":" + ex.getErrorValue());
        }
    }

    /**
     * Delete multiple files/folders at once. This route is asynchronous, which
     * returns a job ID immediately and runs the delete batch asynchronously.
     * Use {@link DbxUserFilesRequests#deleteBatchCheck(String)} to check the
     * job status.
     *
     * @param entries  Must not contain a {@code null} item and not be {@code
     *     null}.
     *
     * @return Result returned by methods that may either launch an asynchronous
     *     job or complete synchronously. Upon synchronous completion of the
     *     job, no additional information is returned.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public LaunchEmptyResult deleteBatch(List<DeleteArg> entries) throws DbxApiException, DbxException {
        DeleteBatchArg _arg = new DeleteBatchArg(entries);
        return deleteBatch(_arg);
    }

    //
    // route 2/files/delete_batch/check
    //

    /**
     * Returns the status of an asynchronous job for {@link
     * DbxUserFilesRequests#deleteBatch(List)}. If success, it returns list of
     * result for each entry.
     *
     * @param arg  Arguments for methods that poll the status of an asynchronous
     *     job.
     */
    DeleteBatchJobStatus deleteBatchCheck(PollArg arg) throws PollErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/delete_batch/check",
                                        arg,
                                        false,
                                        PollArg.Serializer.INSTANCE,
                                        DeleteBatchJobStatus.Serializer.INSTANCE,
                                        PollError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new PollErrorException("2/files/delete_batch/check", ex.getRequestId(), ex.getUserMessage(), (PollError) ex.getErrorValue());
        }
    }

    /**
     * Returns the status of an asynchronous job for {@link
     * DbxUserFilesRequests#deleteBatch(List)}. If success, it returns list of
     * result for each entry.
     *
     * @param asyncJobId  Id of the asynchronous job. This is the value of a
     *     response returned from the method that launched the job. Must have
     *     length of at least 1 and not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public DeleteBatchJobStatus deleteBatchCheck(String asyncJobId) throws PollErrorException, DbxException {
        PollArg _arg = new PollArg(asyncJobId);
        return deleteBatchCheck(_arg);
    }

    //
    // route 2/files/download
    //

    /**
     * Download a file from a user's Dropbox.
     *
     * @param _headers  Extra headers to send with request.
     *
     * @return Downloader used to download the response body and view the server
     *     response.
     */
    DbxDownloader<FileMetadata> download(DownloadArg arg, List<HttpRequestor.Header> _headers) throws DownloadErrorException, DbxException {
        try {
            return this.client.downloadStyle(this.client.getHost().getContent(),
                                             "2/files/download",
                                             arg,
                                             false,
                                             _headers,
                                             DownloadArg.Serializer.INSTANCE,
                                             FileMetadata.Serializer.INSTANCE,
                                             DownloadError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new DownloadErrorException("2/files/download", ex.getRequestId(), ex.getUserMessage(), (DownloadError) ex.getErrorValue());
        }
    }

    /**
     * Download a file from a user's Dropbox.
     *
     * @param path  The path of the file to download. Must match pattern "{@code
     *     (/(.|[\\r\\n])*|id:.*)|(rev:[0-9a-f]{9,})|(ns:[0-9]+(/.*)?)}" and not
     *     be {@code null}.
     *
     * @return Downloader used to download the response body and view the server
     *     response.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public DbxDownloader<FileMetadata> download(String path) throws DownloadErrorException, DbxException {
        DownloadArg _arg = new DownloadArg(path);
        return download(_arg, Collections.<HttpRequestor.Header>emptyList());
    }

    /**
     * Download a file from a user's Dropbox.
     *
     * @param path  The path of the file to download. Must match pattern "{@code
     *     (/(.|[\\r\\n])*|id:.*)|(rev:[0-9a-f]{9,})|(ns:[0-9]+(/.*)?)}" and not
     *     be {@code null}.
     * @param rev  Deprecated. Please specify revision in the {@code path}
     *     argument to {@link DbxUserFilesRequests#download(String,String)}
     *     instead. Must have length of at least 9 and match pattern "{@code
     *     [0-9a-f]+}".
     *
     * @return Downloader used to download the response body and view the server
     *     response.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public DbxDownloader<FileMetadata> download(String path, String rev) throws DownloadErrorException, DbxException {
        if (rev != null) {
            if (rev.length() < 9) {
                throw new IllegalArgumentException("String 'rev' is shorter than 9");
            }
            if (!java.util.regex.Pattern.matches("[0-9a-f]+", rev)) {
                throw new IllegalArgumentException("String 'rev' does not match pattern");
            }
        }
        DownloadArg _arg = new DownloadArg(path, rev);
        return download(_arg, Collections.<HttpRequestor.Header>emptyList());
    }

    /**
     * Download a file from a user's Dropbox.
     *
     * @param path  The path of the file to download. Must match pattern "{@code
     *     (/(.|[\\r\\n])*|id:.*)|(rev:[0-9a-f]{9,})|(ns:[0-9]+(/.*)?)}" and not
     *     be {@code null}.
     *
     * @return Downloader builder for configuring the request parameters and
     *     instantiating a downloader.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public DownloadBuilder downloadBuilder(String path) {
        return new DownloadBuilder(this, path);
    }

    //
    // route 2/files/get_metadata
    //

    /**
     * Returns the metadata for a file or folder. Note: Metadata for the root
     * folder is unsupported.
     *
     *
     * @return Metadata for a file or folder.
     */
    Metadata getMetadata(GetMetadataArg arg) throws GetMetadataErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/get_metadata",
                                        arg,
                                        false,
                                        GetMetadataArg.Serializer.INSTANCE,
                                        Metadata.Serializer.INSTANCE,
                                        GetMetadataError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new GetMetadataErrorException("2/files/get_metadata", ex.getRequestId(), ex.getUserMessage(), (GetMetadataError) ex.getErrorValue());
        }
    }

    /**
     * Returns the metadata for a file or folder. Note: Metadata for the root
     * folder is unsupported.
     *
     * <p> The default values for the optional request parameters will be used.
     * See {@link GetMetadataBuilder} for more details. </p>
     *
     * @param path  The path of a file or folder on Dropbox. Must match pattern
     *     "{@code (/(.|[\\r\\n])*|id:.*)|(rev:[0-9a-f]{9,})|(ns:[0-9]+(/.*)?)}"
     *     and not be {@code null}.
     *
     * @return Metadata for a file or folder.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public Metadata getMetadata(String path) throws GetMetadataErrorException, DbxException {
        GetMetadataArg _arg = new GetMetadataArg(path);
        return getMetadata(_arg);
    }

    /**
     * Returns the metadata for a file or folder. Note: Metadata for the root
     * folder is unsupported.
     *
     * @param path  The path of a file or folder on Dropbox. Must match pattern
     *     "{@code (/(.|[\\r\\n])*|id:.*)|(rev:[0-9a-f]{9,})|(ns:[0-9]+(/.*)?)}"
     *     and not be {@code null}.
     *
     * @return Request builder for configuring request parameters and completing
     *     the request.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public GetMetadataBuilder getMetadataBuilder(String path) {
        GetMetadataArg.Builder argBuilder_ = GetMetadataArg.newBuilder(path);
        return new GetMetadataBuilder(this, argBuilder_);
    }

    //
    // route 2/files/get_preview
    //

    /**
     * Get a preview for a file. Currently previews are only generated for the
     * files with  the following extensions: .doc, .docx, .docm, .ppt, .pps,
     * .ppsx, .ppsm, .pptx, .pptm,  .xls, .xlsx, .xlsm, .rtf.
     *
     * @param _headers  Extra headers to send with request.
     *
     * @return Downloader used to download the response body and view the server
     *     response.
     */
    DbxDownloader<FileMetadata> getPreview(PreviewArg arg, List<HttpRequestor.Header> _headers) throws PreviewErrorException, DbxException {
        try {
            return this.client.downloadStyle(this.client.getHost().getContent(),
                                             "2/files/get_preview",
                                             arg,
                                             false,
                                             _headers,
                                             PreviewArg.Serializer.INSTANCE,
                                             FileMetadata.Serializer.INSTANCE,
                                             PreviewError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new PreviewErrorException("2/files/get_preview", ex.getRequestId(), ex.getUserMessage(), (PreviewError) ex.getErrorValue());
        }
    }

    /**
     * Get a preview for a file. Currently previews are only generated for the
     * files with  the following extensions: .doc, .docx, .docm, .ppt, .pps,
     * .ppsx, .ppsm, .pptx, .pptm,  .xls, .xlsx, .xlsm, .rtf.
     *
     * @param path  The path of the file to preview. Must match pattern "{@code
     *     (/(.|[\\r\\n])*|id:.*)|(rev:[0-9a-f]{9,})|(ns:[0-9]+(/.*)?)}" and not
     *     be {@code null}.
     *
     * @return Downloader used to download the response body and view the server
     *     response.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public DbxDownloader<FileMetadata> getPreview(String path) throws PreviewErrorException, DbxException {
        PreviewArg _arg = new PreviewArg(path);
        return getPreview(_arg, Collections.<HttpRequestor.Header>emptyList());
    }

    /**
     * Get a preview for a file. Currently previews are only generated for the
     * files with  the following extensions: .doc, .docx, .docm, .ppt, .pps,
     * .ppsx, .ppsm, .pptx, .pptm,  .xls, .xlsx, .xlsm, .rtf.
     *
     * @param path  The path of the file to preview. Must match pattern "{@code
     *     (/(.|[\\r\\n])*|id:.*)|(rev:[0-9a-f]{9,})|(ns:[0-9]+(/.*)?)}" and not
     *     be {@code null}.
     * @param rev  Deprecated. Please specify revision in the {@code path}
     *     argument to {@link DbxUserFilesRequests#getPreview(String,String)}
     *     instead. Must have length of at least 9 and match pattern "{@code
     *     [0-9a-f]+}".
     *
     * @return Downloader used to download the response body and view the server
     *     response.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public DbxDownloader<FileMetadata> getPreview(String path, String rev) throws PreviewErrorException, DbxException {
        if (rev != null) {
            if (rev.length() < 9) {
                throw new IllegalArgumentException("String 'rev' is shorter than 9");
            }
            if (!java.util.regex.Pattern.matches("[0-9a-f]+", rev)) {
                throw new IllegalArgumentException("String 'rev' does not match pattern");
            }
        }
        PreviewArg _arg = new PreviewArg(path, rev);
        return getPreview(_arg, Collections.<HttpRequestor.Header>emptyList());
    }

    /**
     * Get a preview for a file. Currently previews are only generated for the
     * files with  the following extensions: .doc, .docx, .docm, .ppt, .pps,
     * .ppsx, .ppsm, .pptx, .pptm,  .xls, .xlsx, .xlsm, .rtf.
     *
     * @param path  The path of the file to preview. Must match pattern "{@code
     *     (/(.|[\\r\\n])*|id:.*)|(rev:[0-9a-f]{9,})|(ns:[0-9]+(/.*)?)}" and not
     *     be {@code null}.
     *
     * @return Downloader builder for configuring the request parameters and
     *     instantiating a downloader.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public GetPreviewBuilder getPreviewBuilder(String path) {
        return new GetPreviewBuilder(this, path);
    }

    //
    // route 2/files/get_temporary_link
    //

    /**
     * Get a temporary link to stream content of a file. This link will expire
     * in four hours and afterwards you will get 410 Gone. Content-Type of the
     * link is determined automatically by the file's mime type.
     *
     */
    GetTemporaryLinkResult getTemporaryLink(GetTemporaryLinkArg arg) throws GetTemporaryLinkErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/get_temporary_link",
                                        arg,
                                        false,
                                        GetTemporaryLinkArg.Serializer.INSTANCE,
                                        GetTemporaryLinkResult.Serializer.INSTANCE,
                                        GetTemporaryLinkError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new GetTemporaryLinkErrorException("2/files/get_temporary_link", ex.getRequestId(), ex.getUserMessage(), (GetTemporaryLinkError) ex.getErrorValue());
        }
    }

    /**
     * Get a temporary link to stream content of a file. This link will expire
     * in four hours and afterwards you will get 410 Gone. Content-Type of the
     * link is determined automatically by the file's mime type.
     *
     * @param path  The path to the file you want a temporary link to. Must
     *     match pattern "{@code
     *     (/(.|[\\r\\n])*|id:.*)|(rev:[0-9a-f]{9,})|(ns:[0-9]+(/.*)?)}" and not
     *     be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public GetTemporaryLinkResult getTemporaryLink(String path) throws GetTemporaryLinkErrorException, DbxException {
        GetTemporaryLinkArg _arg = new GetTemporaryLinkArg(path);
        return getTemporaryLink(_arg);
    }

    //
    // route 2/files/get_thumbnail
    //

    /**
     * Get a thumbnail for an image. This method currently supports files with
     * the following file extensions: jpg, jpeg, png, tiff, tif, gif and bmp.
     * Photos that are larger than 20MB in size won't be converted to a
     * thumbnail.
     *
     * @param _headers  Extra headers to send with request.
     *
     * @return Downloader used to download the response body and view the server
     *     response.
     */
    DbxDownloader<FileMetadata> getThumbnail(ThumbnailArg arg, List<HttpRequestor.Header> _headers) throws ThumbnailErrorException, DbxException {
        try {
            return this.client.downloadStyle(this.client.getHost().getContent(),
                                             "2/files/get_thumbnail",
                                             arg,
                                             false,
                                             _headers,
                                             ThumbnailArg.Serializer.INSTANCE,
                                             FileMetadata.Serializer.INSTANCE,
                                             ThumbnailError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new ThumbnailErrorException("2/files/get_thumbnail", ex.getRequestId(), ex.getUserMessage(), (ThumbnailError) ex.getErrorValue());
        }
    }

    /**
     * Get a thumbnail for an image. This method currently supports files with
     * the following file extensions: jpg, jpeg, png, tiff, tif, gif and bmp.
     * Photos that are larger than 20MB in size won't be converted to a
     * thumbnail.
     *
     * <p> The default values for the optional request parameters will be used.
     * See {@link GetThumbnailBuilder} for more details. </p>
     *
     * @param path  The path to the image file you want to thumbnail. Must match
     *     pattern "{@code
     *     (/(.|[\\r\\n])*|id:.*)|(rev:[0-9a-f]{9,})|(ns:[0-9]+(/.*)?)}" and not
     *     be {@code null}.
     *
     * @return Downloader used to download the response body and view the server
     *     response.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public DbxDownloader<FileMetadata> getThumbnail(String path) throws ThumbnailErrorException, DbxException {
        ThumbnailArg _arg = new ThumbnailArg(path);
        return getThumbnail(_arg, Collections.<HttpRequestor.Header>emptyList());
    }

    /**
     * Get a thumbnail for an image. This method currently supports files with
     * the following file extensions: jpg, jpeg, png, tiff, tif, gif and bmp.
     * Photos that are larger than 20MB in size won't be converted to a
     * thumbnail.
     *
     * @param path  The path to the image file you want to thumbnail. Must match
     *     pattern "{@code
     *     (/(.|[\\r\\n])*|id:.*)|(rev:[0-9a-f]{9,})|(ns:[0-9]+(/.*)?)}" and not
     *     be {@code null}.
     *
     * @return Downloader builder for configuring the request parameters and
     *     instantiating a downloader.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public GetThumbnailBuilder getThumbnailBuilder(String path) {
        ThumbnailArg.Builder argBuilder_ = ThumbnailArg.newBuilder(path);
        return new GetThumbnailBuilder(this, argBuilder_);
    }

    //
    // route 2/files/list_folder
    //

    /**
     * Starts returning the contents of a folder. If the result's {@link
     * ListFolderResult#getHasMore} field is {@code true}, call {@link
     * DbxUserFilesRequests#listFolderContinue(String)} with the returned {@link
     * ListFolderResult#getCursor} to retrieve more entries. If you're using
     * {@link ListFolderArg#getRecursive} set to {@code true} to keep a local
     * cache of the contents of a Dropbox account, iterate through each entry in
     * order and process them as follows to keep your local state in sync: For
     * each {@link FileMetadata}, store the new entry at the given path in your
     * local state. If the required parent folders don't exist yet, create them.
     * If there's already something else at the given path, replace it and
     * remove all its children. For each {@link FolderMetadata}, store the new
     * entry at the given path in your local state. If the required parent
     * folders don't exist yet, create them. If there's already something else
     * at the given path, replace it but leave the children as they are. Check
     * the new entry's {@link FolderSharingInfo#getReadOnly} and set all its
     * children's read-only statuses to match. For each {@link DeletedMetadata},
     * if your local state has something at the given path, remove it and all
     * its children. If there's nothing at the given path, ignore this entry.
     *
     */
    ListFolderResult listFolder(ListFolderArg arg) throws ListFolderErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/list_folder",
                                        arg,
                                        false,
                                        ListFolderArg.Serializer.INSTANCE,
                                        ListFolderResult.Serializer.INSTANCE,
                                        ListFolderError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new ListFolderErrorException("2/files/list_folder", ex.getRequestId(), ex.getUserMessage(), (ListFolderError) ex.getErrorValue());
        }
    }

    /**
     * Starts returning the contents of a folder. If the result's {@link
     * ListFolderResult#getHasMore} field is {@code true}, call {@link
     * DbxUserFilesRequests#listFolderContinue(String)} with the returned {@link
     * ListFolderResult#getCursor} to retrieve more entries. If you're using
     * {@link ListFolderArg#getRecursive} set to {@code true} to keep a local
     * cache of the contents of a Dropbox account, iterate through each entry in
     * order and process them as follows to keep your local state in sync: For
     * each {@link FileMetadata}, store the new entry at the given path in your
     * local state. If the required parent folders don't exist yet, create them.
     * If there's already something else at the given path, replace it and
     * remove all its children. For each {@link FolderMetadata}, store the new
     * entry at the given path in your local state. If the required parent
     * folders don't exist yet, create them. If there's already something else
     * at the given path, replace it but leave the children as they are. Check
     * the new entry's {@link FolderSharingInfo#getReadOnly} and set all its
     * children's read-only statuses to match. For each {@link DeletedMetadata},
     * if your local state has something at the given path, remove it and all
     * its children. If there's nothing at the given path, ignore this entry.
     *
     * <p> The default values for the optional request parameters will be used.
     * See {@link ListFolderBuilder} for more details. </p>
     *
     * @param path  The path to the folder you want to see the contents of. Must
     *     match pattern "{@code (/(.|[\\r\\n])*)?|(ns:[0-9]+(/.*)?)}" and not
     *     be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public ListFolderResult listFolder(String path) throws ListFolderErrorException, DbxException {
        ListFolderArg _arg = new ListFolderArg(path);
        return listFolder(_arg);
    }

    /**
     * Starts returning the contents of a folder. If the result's {@link
     * ListFolderResult#getHasMore} field is {@code true}, call {@link
     * DbxUserFilesRequests#listFolderContinue(String)} with the returned {@link
     * ListFolderResult#getCursor} to retrieve more entries. If you're using
     * {@link ListFolderArg#getRecursive} set to {@code true} to keep a local
     * cache of the contents of a Dropbox account, iterate through each entry in
     * order and process them as follows to keep your local state in sync: For
     * each {@link FileMetadata}, store the new entry at the given path in your
     * local state. If the required parent folders don't exist yet, create them.
     * If there's already something else at the given path, replace it and
     * remove all its children. For each {@link FolderMetadata}, store the new
     * entry at the given path in your local state. If the required parent
     * folders don't exist yet, create them. If there's already something else
     * at the given path, replace it but leave the children as they are. Check
     * the new entry's {@link FolderSharingInfo#getReadOnly} and set all its
     * children's read-only statuses to match. For each {@link DeletedMetadata},
     * if your local state has something at the given path, remove it and all
     * its children. If there's nothing at the given path, ignore this entry.
     *
     * @param path  The path to the folder you want to see the contents of. Must
     *     match pattern "{@code (/(.|[\\r\\n])*)?|(ns:[0-9]+(/.*)?)}" and not
     *     be {@code null}.
     *
     * @return Request builder for configuring request parameters and completing
     *     the request.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public ListFolderBuilder listFolderBuilder(String path) {
        ListFolderArg.Builder argBuilder_ = ListFolderArg.newBuilder(path);
        return new ListFolderBuilder(this, argBuilder_);
    }

    //
    // route 2/files/list_folder/continue
    //

    /**
     * Once a cursor has been retrieved from {@link
     * DbxUserFilesRequests#listFolder(String)}, use this to paginate through
     * all files and retrieve updates to the folder, following the same rules as
     * documented for {@link DbxUserFilesRequests#listFolder(String)}.
     *
     */
    ListFolderResult listFolderContinue(ListFolderContinueArg arg) throws ListFolderContinueErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/list_folder/continue",
                                        arg,
                                        false,
                                        ListFolderContinueArg.Serializer.INSTANCE,
                                        ListFolderResult.Serializer.INSTANCE,
                                        ListFolderContinueError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new ListFolderContinueErrorException("2/files/list_folder/continue", ex.getRequestId(), ex.getUserMessage(), (ListFolderContinueError) ex.getErrorValue());
        }
    }

    /**
     * Once a cursor has been retrieved from {@link
     * DbxUserFilesRequests#listFolder(String)}, use this to paginate through
     * all files and retrieve updates to the folder, following the same rules as
     * documented for {@link DbxUserFilesRequests#listFolder(String)}.
     *
     * @param cursor  The cursor returned by your last call to {@link
     *     DbxUserFilesRequests#listFolder(String)} or {@link
     *     DbxUserFilesRequests#listFolderContinue(String)}. Must have length of
     *     at least 1 and not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public ListFolderResult listFolderContinue(String cursor) throws ListFolderContinueErrorException, DbxException {
        ListFolderContinueArg _arg = new ListFolderContinueArg(cursor);
        return listFolderContinue(_arg);
    }

    //
    // route 2/files/list_folder/get_latest_cursor
    //

    /**
     * A way to quickly get a cursor for the folder's state. Unlike {@link
     * DbxUserFilesRequests#listFolder(String)}, {@link
     * DbxUserFilesRequests#listFolderGetLatestCursor(String)} doesn't return
     * any entries. This endpoint is for app which only needs to know about new
     * files and modifications and doesn't need to know about files that already
     * exist in Dropbox.
     *
     */
    ListFolderGetLatestCursorResult listFolderGetLatestCursor(ListFolderArg arg) throws ListFolderErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/list_folder/get_latest_cursor",
                                        arg,
                                        false,
                                        ListFolderArg.Serializer.INSTANCE,
                                        ListFolderGetLatestCursorResult.Serializer.INSTANCE,
                                        ListFolderError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new ListFolderErrorException("2/files/list_folder/get_latest_cursor", ex.getRequestId(), ex.getUserMessage(), (ListFolderError) ex.getErrorValue());
        }
    }

    /**
     * A way to quickly get a cursor for the folder's state. Unlike {@link
     * DbxUserFilesRequests#listFolder(String)}, {@link
     * DbxUserFilesRequests#listFolderGetLatestCursor(String)} doesn't return
     * any entries. This endpoint is for app which only needs to know about new
     * files and modifications and doesn't need to know about files that already
     * exist in Dropbox.
     *
     * <p> The default values for the optional request parameters will be used.
     * See {@link ListFolderGetLatestCursorBuilder} for more details. </p>
     *
     * @param path  The path to the folder you want to see the contents of. Must
     *     match pattern "{@code (/(.|[\\r\\n])*)?|(ns:[0-9]+(/.*)?)}" and not
     *     be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public ListFolderGetLatestCursorResult listFolderGetLatestCursor(String path) throws ListFolderErrorException, DbxException {
        ListFolderArg _arg = new ListFolderArg(path);
        return listFolderGetLatestCursor(_arg);
    }

    /**
     * A way to quickly get a cursor for the folder's state. Unlike {@link
     * DbxUserFilesRequests#listFolder(String)}, {@link
     * DbxUserFilesRequests#listFolderGetLatestCursor(String)} doesn't return
     * any entries. This endpoint is for app which only needs to know about new
     * files and modifications and doesn't need to know about files that already
     * exist in Dropbox.
     *
     * @param path  The path to the folder you want to see the contents of. Must
     *     match pattern "{@code (/(.|[\\r\\n])*)?|(ns:[0-9]+(/.*)?)}" and not
     *     be {@code null}.
     *
     * @return Request builder for configuring request parameters and completing
     *     the request.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public ListFolderGetLatestCursorBuilder listFolderGetLatestCursorBuilder(String path) {
        ListFolderArg.Builder argBuilder_ = ListFolderArg.newBuilder(path);
        return new ListFolderGetLatestCursorBuilder(this, argBuilder_);
    }

    //
    // route 2/files/list_folder/longpoll
    //

    /**
     * A longpoll endpoint to wait for changes on an account. In conjunction
     * with {@link DbxUserFilesRequests#listFolderContinue(String)}, this call
     * gives you a low-latency way to monitor an account for file changes. The
     * connection will block until there are changes available or a timeout
     * occurs. This endpoint is useful mostly for client-side apps. If you're
     * looking for server-side notifications, check out our <a
     * href="https://www.dropbox.com/developers/reference/webhooks">webhooks
     * documentation</a>.
     *
     */
    ListFolderLongpollResult listFolderLongpoll(ListFolderLongpollArg arg) throws ListFolderLongpollErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getNotify(),
                                        "2/files/list_folder/longpoll",
                                        arg,
                                        true,
                                        ListFolderLongpollArg.Serializer.INSTANCE,
                                        ListFolderLongpollResult.Serializer.INSTANCE,
                                        ListFolderLongpollError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new ListFolderLongpollErrorException("2/files/list_folder/longpoll", ex.getRequestId(), ex.getUserMessage(), (ListFolderLongpollError) ex.getErrorValue());
        }
    }

    /**
     * A longpoll endpoint to wait for changes on an account. In conjunction
     * with {@link DbxUserFilesRequests#listFolderContinue(String)}, this call
     * gives you a low-latency way to monitor an account for file changes. The
     * connection will block until there are changes available or a timeout
     * occurs. This endpoint is useful mostly for client-side apps. If you're
     * looking for server-side notifications, check out our <a
     * href="https://www.dropbox.com/developers/reference/webhooks">webhooks
     * documentation</a>.
     *
     * <p> The {@code timeout} request parameter will default to {@code 30L}
     * (see {@link #listFolderLongpoll(String,long)}). </p>
     *
     * @param cursor  A cursor as returned by {@link
     *     DbxUserFilesRequests#listFolder(String)} or {@link
     *     DbxUserFilesRequests#listFolderContinue(String)}. Cursors retrieved
     *     by setting {@link ListFolderArg#getIncludeMediaInfo} to {@code true}
     *     are not supported. Must have length of at least 1 and not be {@code
     *     null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public ListFolderLongpollResult listFolderLongpoll(String cursor) throws ListFolderLongpollErrorException, DbxException {
        ListFolderLongpollArg _arg = new ListFolderLongpollArg(cursor);
        return listFolderLongpoll(_arg);
    }

    /**
     * A longpoll endpoint to wait for changes on an account. In conjunction
     * with {@link DbxUserFilesRequests#listFolderContinue(String)}, this call
     * gives you a low-latency way to monitor an account for file changes. The
     * connection will block until there are changes available or a timeout
     * occurs. This endpoint is useful mostly for client-side apps. If you're
     * looking for server-side notifications, check out our <a
     * href="https://www.dropbox.com/developers/reference/webhooks">webhooks
     * documentation</a>.
     *
     * @param cursor  A cursor as returned by {@link
     *     DbxUserFilesRequests#listFolder(String)} or {@link
     *     DbxUserFilesRequests#listFolderContinue(String)}. Cursors retrieved
     *     by setting {@link ListFolderArg#getIncludeMediaInfo} to {@code true}
     *     are not supported. Must have length of at least 1 and not be {@code
     *     null}.
     * @param timeout  A timeout in seconds. The request will block for at most
     *     this length of time, plus up to 90 seconds of random jitter added to
     *     avoid the thundering herd problem. Care should be taken when using
     *     this parameter, as some network infrastructure does not support long
     *     timeouts. Must be greater than or equal to 30 and be less than or
     *     equal to 480.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public ListFolderLongpollResult listFolderLongpoll(String cursor, long timeout) throws ListFolderLongpollErrorException, DbxException {
        if (timeout < 30L) {
            throw new IllegalArgumentException("Number 'timeout' is smaller than 30L");
        }
        if (timeout > 480L) {
            throw new IllegalArgumentException("Number 'timeout' is larger than 480L");
        }
        ListFolderLongpollArg _arg = new ListFolderLongpollArg(cursor, timeout);
        return listFolderLongpoll(_arg);
    }

    //
    // route 2/files/list_revisions
    //

    /**
     * Return revisions of a file.
     *
     */
    ListRevisionsResult listRevisions(ListRevisionsArg arg) throws ListRevisionsErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/list_revisions",
                                        arg,
                                        false,
                                        ListRevisionsArg.Serializer.INSTANCE,
                                        ListRevisionsResult.Serializer.INSTANCE,
                                        ListRevisionsError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new ListRevisionsErrorException("2/files/list_revisions", ex.getRequestId(), ex.getUserMessage(), (ListRevisionsError) ex.getErrorValue());
        }
    }

    /**
     * Return revisions of a file.
     *
     * <p> The {@code limit} request parameter will default to {@code 10L} (see
     * {@link #listRevisions(String,long)}). </p>
     *
     * @param path  The path to the file you want to see the revisions of. Must
     *     match pattern "{@code /(.|[\\r\\n])*|id:.*|(ns:[0-9]+(/.*)?)}" and
     *     not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public ListRevisionsResult listRevisions(String path) throws ListRevisionsErrorException, DbxException {
        ListRevisionsArg _arg = new ListRevisionsArg(path);
        return listRevisions(_arg);
    }

    /**
     * Return revisions of a file.
     *
     * @param path  The path to the file you want to see the revisions of. Must
     *     match pattern "{@code /(.|[\\r\\n])*|id:.*|(ns:[0-9]+(/.*)?)}" and
     *     not be {@code null}.
     * @param limit  The maximum number of revision entries returned. Must be
     *     greater than or equal to 1 and be less than or equal to 100.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public ListRevisionsResult listRevisions(String path, long limit) throws ListRevisionsErrorException, DbxException {
        if (limit < 1L) {
            throw new IllegalArgumentException("Number 'limit' is smaller than 1L");
        }
        if (limit > 100L) {
            throw new IllegalArgumentException("Number 'limit' is larger than 100L");
        }
        ListRevisionsArg _arg = new ListRevisionsArg(path, limit);
        return listRevisions(_arg);
    }

    //
    // route 2/files/move
    //

    /**
     * Move a file or folder to a different location in the user's Dropbox. If
     * the source path is a folder all its contents will be moved.
     *
     *
     * @return Metadata for a file or folder.
     */
    Metadata move(RelocationArg arg) throws RelocationErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/move",
                                        arg,
                                        false,
                                        RelocationArg.Serializer.INSTANCE,
                                        Metadata.Serializer.INSTANCE,
                                        RelocationError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new RelocationErrorException("2/files/move", ex.getRequestId(), ex.getUserMessage(), (RelocationError) ex.getErrorValue());
        }
    }

    /**
     * Move a file or folder to a different location in the user's Dropbox. If
     * the source path is a folder all its contents will be moved.
     *
     * <p> The default values for the optional request parameters will be used.
     * See {@link MoveBuilder} for more details. </p>
     *
     * @param fromPath  Path in the user's Dropbox to be copied or moved. Must
     *     match pattern "{@code (/(.|[\\r\\n])*)|(ns:[0-9]+(/.*)?)}" and not be
     *     {@code null}.
     * @param toPath  Path in the user's Dropbox that is the destination. Must
     *     match pattern "{@code (/(.|[\\r\\n])*)|(ns:[0-9]+(/.*)?)}" and not be
     *     {@code null}.
     *
     * @return Metadata for a file or folder.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public Metadata move(String fromPath, String toPath) throws RelocationErrorException, DbxException {
        RelocationArg _arg = new RelocationArg(fromPath, toPath);
        return move(_arg);
    }

    /**
     * Move a file or folder to a different location in the user's Dropbox. If
     * the source path is a folder all its contents will be moved.
     *
     * @param fromPath  Path in the user's Dropbox to be copied or moved. Must
     *     match pattern "{@code (/(.|[\\r\\n])*)|(ns:[0-9]+(/.*)?)}" and not be
     *     {@code null}.
     * @param toPath  Path in the user's Dropbox that is the destination. Must
     *     match pattern "{@code (/(.|[\\r\\n])*)|(ns:[0-9]+(/.*)?)}" and not be
     *     {@code null}.
     *
     * @return Request builder for configuring request parameters and completing
     *     the request.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public MoveBuilder moveBuilder(String fromPath, String toPath) {
        RelocationArg.Builder argBuilder_ = RelocationArg.newBuilder(fromPath, toPath);
        return new MoveBuilder(this, argBuilder_);
    }

    //
    // route 2/files/move_batch
    //

    /**
     * Move multiple files or folders to different locations at once in the
     * user's Dropbox. This route is 'all or nothing', which means if one entry
     * fails, the whole transaction will abort. This route will return job ID
     * immediately and do the async moving job in background. Please use {@link
     * DbxUserFilesRequests#moveBatchCheck(String)} to check the job status.
     *
     *
     * @return Result returned by methods that may either launch an asynchronous
     *     job or complete synchronously. Upon synchronous completion of the
     *     job, no additional information is returned.
     */
    LaunchEmptyResult moveBatch(RelocationBatchArg arg) throws DbxApiException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/move_batch",
                                        arg,
                                        false,
                                        RelocationBatchArg.Serializer.INSTANCE,
                                        LaunchEmptyResult.Serializer.INSTANCE,
                                        com.dropbox.core.stone.StoneSerializers.void_());
        }
        catch (DbxWrappedException ex) {
            throw new DbxApiException(ex.getRequestId(), ex.getUserMessage(), "Unexpected error response for \"move_batch\":" + ex.getErrorValue());
        }
    }

    /**
     * Move multiple files or folders to different locations at once in the
     * user's Dropbox. This route is 'all or nothing', which means if one entry
     * fails, the whole transaction will abort. This route will return job ID
     * immediately and do the async moving job in background. Please use {@link
     * DbxUserFilesRequests#moveBatchCheck(String)} to check the job status.
     *
     * <p> The default values for the optional request parameters will be used.
     * See {@link MoveBatchBuilder} for more details. </p>
     *
     * @param entries  List of entries to be moved or copied. Each entry is
     *     {@link RelocationPath}. Must not contain a {@code null} item and not
     *     be {@code null}.
     *
     * @return Result returned by methods that may either launch an asynchronous
     *     job or complete synchronously. Upon synchronous completion of the
     *     job, no additional information is returned.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public LaunchEmptyResult moveBatch(List<RelocationPath> entries) throws DbxApiException, DbxException {
        RelocationBatchArg _arg = new RelocationBatchArg(entries);
        return moveBatch(_arg);
    }

    /**
     * Move multiple files or folders to different locations at once in the
     * user's Dropbox. This route is 'all or nothing', which means if one entry
     * fails, the whole transaction will abort. This route will return job ID
     * immediately and do the async moving job in background. Please use {@link
     * DbxUserFilesRequests#moveBatchCheck(String)} to check the job status.
     *
     * @param entries  List of entries to be moved or copied. Each entry is
     *     {@link RelocationPath}. Must not contain a {@code null} item and not
     *     be {@code null}.
     *
     * @return Request builder for configuring request parameters and completing
     *     the request.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public MoveBatchBuilder moveBatchBuilder(List<RelocationPath> entries) {
        RelocationBatchArg.Builder argBuilder_ = RelocationBatchArg.newBuilder(entries);
        return new MoveBatchBuilder(this, argBuilder_);
    }

    //
    // route 2/files/move_batch/check
    //

    /**
     * Returns the status of an asynchronous job for {@link
     * DbxUserFilesRequests#moveBatch(List)}. If success, it returns list of
     * results for each entry.
     *
     * @param arg  Arguments for methods that poll the status of an asynchronous
     *     job.
     */
    RelocationBatchJobStatus moveBatchCheck(PollArg arg) throws PollErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/move_batch/check",
                                        arg,
                                        false,
                                        PollArg.Serializer.INSTANCE,
                                        RelocationBatchJobStatus.Serializer.INSTANCE,
                                        PollError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new PollErrorException("2/files/move_batch/check", ex.getRequestId(), ex.getUserMessage(), (PollError) ex.getErrorValue());
        }
    }

    /**
     * Returns the status of an asynchronous job for {@link
     * DbxUserFilesRequests#moveBatch(List)}. If success, it returns list of
     * results for each entry.
     *
     * @param asyncJobId  Id of the asynchronous job. This is the value of a
     *     response returned from the method that launched the job. Must have
     *     length of at least 1 and not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public RelocationBatchJobStatus moveBatchCheck(String asyncJobId) throws PollErrorException, DbxException {
        PollArg _arg = new PollArg(asyncJobId);
        return moveBatchCheck(_arg);
    }

    //
    // route 2/files/permanently_delete
    //

    /**
     * Permanently delete the file or folder at a given path (see
     * https://www.dropbox.com/en/help/40). Note: This endpoint is only
     * available for Dropbox Business apps.
     *
     */
    void permanentlyDelete(DeleteArg arg) throws DeleteErrorException, DbxException {
        try {
            this.client.rpcStyle(this.client.getHost().getApi(),
                                 "2/files/permanently_delete",
                                 arg,
                                 false,
                                 DeleteArg.Serializer.INSTANCE,
                                 com.dropbox.core.stone.StoneSerializers.void_(),
                                 DeleteError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new DeleteErrorException("2/files/permanently_delete", ex.getRequestId(), ex.getUserMessage(), (DeleteError) ex.getErrorValue());
        }
    }

    /**
     * Permanently delete the file or folder at a given path (see
     * https://www.dropbox.com/en/help/40). Note: This endpoint is only
     * available for Dropbox Business apps.
     *
     * @param path  Path in the user's Dropbox to delete. Must match pattern
     *     "{@code (/(.|[\\r\\n])*)|(ns:[0-9]+(/.*)?)}" and not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public void permanentlyDelete(String path) throws DeleteErrorException, DbxException {
        DeleteArg _arg = new DeleteArg(path);
        permanentlyDelete(_arg);
    }

    //
    // route 2/files/restore
    //

    /**
     * Restore a file to a specific revision.
     *
     */
    FileMetadata restore(RestoreArg arg) throws RestoreErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/restore",
                                        arg,
                                        false,
                                        RestoreArg.Serializer.INSTANCE,
                                        FileMetadata.Serializer.INSTANCE,
                                        RestoreError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new RestoreErrorException("2/files/restore", ex.getRequestId(), ex.getUserMessage(), (RestoreError) ex.getErrorValue());
        }
    }

    /**
     * Restore a file to a specific revision.
     *
     * @param path  The path to the file you want to restore. Must match pattern
     *     "{@code (/(.|[\\r\\n])*)|(ns:[0-9]+(/.*)?)}" and not be {@code null}.
     * @param rev  The revision to restore for the file. Must have length of at
     *     least 9, match pattern "{@code [0-9a-f]+}", and not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public FileMetadata restore(String path, String rev) throws RestoreErrorException, DbxException {
        RestoreArg _arg = new RestoreArg(path, rev);
        return restore(_arg);
    }

    //
    // route 2/files/save_url
    //

    /**
     * Save a specified URL into a file in user's Dropbox. If the given path
     * already exists, the file will be renamed to avoid the conflict (e.g.
     * myfile (1).txt).
     *
     */
    SaveUrlResult saveUrl(SaveUrlArg arg) throws SaveUrlErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/save_url",
                                        arg,
                                        false,
                                        SaveUrlArg.Serializer.INSTANCE,
                                        SaveUrlResult.Serializer.INSTANCE,
                                        SaveUrlError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new SaveUrlErrorException("2/files/save_url", ex.getRequestId(), ex.getUserMessage(), (SaveUrlError) ex.getErrorValue());
        }
    }

    /**
     * Save a specified URL into a file in user's Dropbox. If the given path
     * already exists, the file will be renamed to avoid the conflict (e.g.
     * myfile (1).txt).
     *
     * @param path  The path in Dropbox where the URL will be saved to. Must
     *     match pattern "{@code /(.|[\\r\\n])*}" and not be {@code null}.
     * @param url  The URL to be saved. Must not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public SaveUrlResult saveUrl(String path, String url) throws SaveUrlErrorException, DbxException {
        SaveUrlArg _arg = new SaveUrlArg(path, url);
        return saveUrl(_arg);
    }

    //
    // route 2/files/save_url/check_job_status
    //

    /**
     * Check the status of a {@link DbxUserFilesRequests#saveUrl(String,String)}
     * job.
     *
     * @param arg  Arguments for methods that poll the status of an asynchronous
     *     job.
     */
    SaveUrlJobStatus saveUrlCheckJobStatus(PollArg arg) throws PollErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/save_url/check_job_status",
                                        arg,
                                        false,
                                        PollArg.Serializer.INSTANCE,
                                        SaveUrlJobStatus.Serializer.INSTANCE,
                                        PollError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new PollErrorException("2/files/save_url/check_job_status", ex.getRequestId(), ex.getUserMessage(), (PollError) ex.getErrorValue());
        }
    }

    /**
     * Check the status of a {@link DbxUserFilesRequests#saveUrl(String,String)}
     * job.
     *
     * @param asyncJobId  Id of the asynchronous job. This is the value of a
     *     response returned from the method that launched the job. Must have
     *     length of at least 1 and not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public SaveUrlJobStatus saveUrlCheckJobStatus(String asyncJobId) throws PollErrorException, DbxException {
        PollArg _arg = new PollArg(asyncJobId);
        return saveUrlCheckJobStatus(_arg);
    }

    //
    // route 2/files/search
    //

    /**
     * Searches for files and folders. Note: Recent changes may not immediately
     * be reflected in search results due to a short delay in indexing.
     *
     */
    SearchResult search(SearchArg arg) throws SearchErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/search",
                                        arg,
                                        false,
                                        SearchArg.Serializer.INSTANCE,
                                        SearchResult.Serializer.INSTANCE,
                                        SearchError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new SearchErrorException("2/files/search", ex.getRequestId(), ex.getUserMessage(), (SearchError) ex.getErrorValue());
        }
    }

    /**
     * Searches for files and folders. Note: Recent changes may not immediately
     * be reflected in search results due to a short delay in indexing.
     *
     * <p> The default values for the optional request parameters will be used.
     * See {@link SearchBuilder} for more details. </p>
     *
     * @param path  The path in the user's Dropbox to search. Should probably be
     *     a folder. Must match pattern "{@code
     *     (/(.|[\\r\\n])*)?|(ns:[0-9]+(/.*)?)}" and not be {@code null}.
     * @param query  The string to search for. The search string is split on
     *     spaces into multiple tokens. For file name searching, the last token
     *     is used for prefix matching (i.e. "bat c" matches "bat cave" but not
     *     "batman car"). Must not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public SearchResult search(String path, String query) throws SearchErrorException, DbxException {
        SearchArg _arg = new SearchArg(path, query);
        return search(_arg);
    }

    /**
     * Searches for files and folders. Note: Recent changes may not immediately
     * be reflected in search results due to a short delay in indexing.
     *
     * @param path  The path in the user's Dropbox to search. Should probably be
     *     a folder. Must match pattern "{@code
     *     (/(.|[\\r\\n])*)?|(ns:[0-9]+(/.*)?)}" and not be {@code null}.
     * @param query  The string to search for. The search string is split on
     *     spaces into multiple tokens. For file name searching, the last token
     *     is used for prefix matching (i.e. "bat c" matches "bat cave" but not
     *     "batman car"). Must not be {@code null}.
     *
     * @return Request builder for configuring request parameters and completing
     *     the request.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public SearchBuilder searchBuilder(String path, String query) {
        SearchArg.Builder argBuilder_ = SearchArg.newBuilder(path, query);
        return new SearchBuilder(this, argBuilder_);
    }

    //
    // route 2/files/upload
    //

    /**
     * Create a new file with the contents provided in the request. Do not use
     * this to upload a file larger than 150 MB. Instead, create an upload
     * session with {@link DbxUserFilesRequests#uploadSessionStart(boolean)}.
     *
     *
     * @return Uploader used to upload the request body and finish request.
     */
    UploadUploader upload(CommitInfo arg) throws DbxException {
        HttpRequestor.Uploader _uploader = this.client.uploadStyle(this.client.getHost().getContent(),
                                                                   "2/files/upload",
                                                                   arg,
                                                                   false,
                                                                   CommitInfo.Serializer.INSTANCE);
        return new UploadUploader(_uploader);
    }

    /**
     * Create a new file with the contents provided in the request. Do not use
     * this to upload a file larger than 150 MB. Instead, create an upload
     * session with {@link DbxUserFilesRequests#uploadSessionStart(boolean)}.
     *
     * <p> The default values for the optional request parameters will be used.
     * See {@link UploadBuilder} for more details. </p>
     *
     * @param path  Path in the user's Dropbox to save the file. Must match
     *     pattern "{@code (/(.|[\\r\\n])*)|(ns:[0-9]+(/.*)?)}" and not be
     *     {@code null}.
     *
     * @return Uploader used to upload the request body and finish request.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public UploadUploader upload(String path) throws DbxException {
        CommitInfo _arg = new CommitInfo(path);
        return upload(_arg);
    }

    /**
     * Create a new file with the contents provided in the request. Do not use
     * this to upload a file larger than 150 MB. Instead, create an upload
     * session with {@link DbxUserFilesRequests#uploadSessionStart(boolean)}.
     *
     * @param path  Path in the user's Dropbox to save the file. Must match
     *     pattern "{@code (/(.|[\\r\\n])*)|(ns:[0-9]+(/.*)?)}" and not be
     *     {@code null}.
     *
     * @return Uploader builder for configuring request parameters and
     *     instantiating an uploader.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public UploadBuilder uploadBuilder(String path) {
        CommitInfo.Builder argBuilder_ = CommitInfo.newBuilder(path);
        return new UploadBuilder(this, argBuilder_);
    }

    //
    // route 2/files/upload_session/append
    //

    /**
     * Append more data to an upload session. A single request should not upload
     * more than 150 MB of file contents.
     *
     *
     * @return Uploader used to upload the request body and finish request.
     */
    UploadSessionAppendUploader uploadSessionAppend(UploadSessionCursor arg) throws DbxException {
        HttpRequestor.Uploader _uploader = this.client.uploadStyle(this.client.getHost().getContent(),
                                                                   "2/files/upload_session/append",
                                                                   arg,
                                                                   false,
                                                                   UploadSessionCursor.Serializer.INSTANCE);
        return new UploadSessionAppendUploader(_uploader);
    }

    /**
     * Append more data to an upload session. A single request should not upload
     * more than 150 MB of file contents.
     *
     * @param sessionId  The upload session ID (returned by {@link
     *     DbxUserFilesRequests#uploadSessionStart(boolean)}). Must not be
     *     {@code null}.
     * @param offset  The amount of data that has been uploaded so far. We use
     *     this to make sure upload data isn't lost or duplicated in the event
     *     of a network error.
     *
     * @return Uploader used to upload the request body and finish request.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     *
     * @deprecated use {@link
     *     DbxUserFilesRequests#uploadSessionAppendV2(UploadSessionCursor,boolean)}
     *     instead.
     */
    @Deprecated
    public UploadSessionAppendUploader uploadSessionAppend(String sessionId, long offset) throws DbxException {
        UploadSessionCursor _arg = new UploadSessionCursor(sessionId, offset);
        return uploadSessionAppend(_arg);
    }

    //
    // route 2/files/upload_session/append_v2
    //

    /**
     * Append more data to an upload session. When the parameter close is set,
     * this call will close the session. A single request should not upload more
     * than 150 MB of file contents.
     *
     *
     * @return Uploader used to upload the request body and finish request.
     */
    UploadSessionAppendV2Uploader uploadSessionAppendV2(UploadSessionAppendArg arg) throws DbxException {
        HttpRequestor.Uploader _uploader = this.client.uploadStyle(this.client.getHost().getContent(),
                                                                   "2/files/upload_session/append_v2",
                                                                   arg,
                                                                   false,
                                                                   UploadSessionAppendArg.Serializer.INSTANCE);
        return new UploadSessionAppendV2Uploader(_uploader);
    }

    /**
     * Append more data to an upload session. When the parameter close is set,
     * this call will close the session. A single request should not upload more
     * than 150 MB of file contents.
     *
     * <p> The {@code close} request parameter will default to {@code false}
     * (see {@link #uploadSessionAppendV2(UploadSessionCursor,boolean)}). </p>
     *
     * @param cursor  Contains the upload session ID and the offset. Must not be
     *     {@code null}.
     *
     * @return Uploader used to upload the request body and finish request.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public UploadSessionAppendV2Uploader uploadSessionAppendV2(UploadSessionCursor cursor) throws DbxException {
        UploadSessionAppendArg _arg = new UploadSessionAppendArg(cursor);
        return uploadSessionAppendV2(_arg);
    }

    /**
     * Append more data to an upload session. When the parameter close is set,
     * this call will close the session. A single request should not upload more
     * than 150 MB of file contents.
     *
     * @param cursor  Contains the upload session ID and the offset. Must not be
     *     {@code null}.
     * @param close  If true, the current session will be closed, at which point
     *     you won't be able to call {@link
     *     DbxUserFilesRequests#uploadSessionAppendV2(UploadSessionCursor,boolean)}
     *     anymore with the current session.
     *
     * @return Uploader used to upload the request body and finish request.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public UploadSessionAppendV2Uploader uploadSessionAppendV2(UploadSessionCursor cursor, boolean close) throws DbxException {
        UploadSessionAppendArg _arg = new UploadSessionAppendArg(cursor, close);
        return uploadSessionAppendV2(_arg);
    }

    //
    // route 2/files/upload_session/finish
    //

    /**
     * Finish an upload session and save the uploaded data to the given file
     * path. A single request should not upload more than 150 MB of file
     * contents.
     *
     *
     * @return Uploader used to upload the request body and finish request.
     */
    UploadSessionFinishUploader uploadSessionFinish(UploadSessionFinishArg arg) throws DbxException {
        HttpRequestor.Uploader _uploader = this.client.uploadStyle(this.client.getHost().getContent(),
                                                                   "2/files/upload_session/finish",
                                                                   arg,
                                                                   false,
                                                                   UploadSessionFinishArg.Serializer.INSTANCE);
        return new UploadSessionFinishUploader(_uploader);
    }

    /**
     * Finish an upload session and save the uploaded data to the given file
     * path. A single request should not upload more than 150 MB of file
     * contents.
     *
     * @param cursor  Contains the upload session ID and the offset. Must not be
     *     {@code null}.
     * @param commit  Contains the path and other optional modifiers for the
     *     commit. Must not be {@code null}.
     *
     * @return Uploader used to upload the request body and finish request.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public UploadSessionFinishUploader uploadSessionFinish(UploadSessionCursor cursor, CommitInfo commit) throws DbxException {
        UploadSessionFinishArg _arg = new UploadSessionFinishArg(cursor, commit);
        return uploadSessionFinish(_arg);
    }

    //
    // route 2/files/upload_session/finish_batch
    //

    /**
     * This route helps you commit many files at once into a user's Dropbox. Use
     * {@link DbxUserFilesRequests#uploadSessionStart(boolean)} and {@link
     * DbxUserFilesRequests#uploadSessionAppendV2(UploadSessionCursor,boolean)}
     * to upload file contents. We recommend uploading many files in parallel to
     * increase throughput. Once the file contents have been uploaded, rather
     * than calling {@link
     * DbxUserFilesRequests#uploadSessionFinish(UploadSessionCursor,CommitInfo)},
     * use this route to finish all your upload sessions in a single request.
     * the {@code close} argument to {@link
     * DbxUserFilesRequests#uploadSessionStart(boolean)} or the {@code close}
     * argument to {@link
     * DbxUserFilesRequests#uploadSessionAppendV2(UploadSessionCursor,boolean)}
     * needs to be true for the last {@link
     * DbxUserFilesRequests#uploadSessionStart(boolean)} or {@link
     * DbxUserFilesRequests#uploadSessionAppendV2(UploadSessionCursor,boolean)}
     * call. This route will return a job_id immediately and do the async commit
     * job in background. Use {@link
     * DbxUserFilesRequests#uploadSessionFinishBatchCheck(String)} to check the
     * job status. For the same account, this route should be executed serially.
     * That means you should not start the next job before current job finishes.
     * We allow up to 1000 entries in a single request.
     *
     *
     * @return Result returned by methods that may either launch an asynchronous
     *     job or complete synchronously. Upon synchronous completion of the
     *     job, no additional information is returned.
     */
    LaunchEmptyResult uploadSessionFinishBatch(UploadSessionFinishBatchArg arg) throws DbxApiException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/upload_session/finish_batch",
                                        arg,
                                        false,
                                        UploadSessionFinishBatchArg.Serializer.INSTANCE,
                                        LaunchEmptyResult.Serializer.INSTANCE,
                                        com.dropbox.core.stone.StoneSerializers.void_());
        }
        catch (DbxWrappedException ex) {
            throw new DbxApiException(ex.getRequestId(), ex.getUserMessage(), "Unexpected error response for \"upload_session/finish_batch\":" + ex.getErrorValue());
        }
    }

    /**
     * This route helps you commit many files at once into a user's Dropbox. Use
     * {@link DbxUserFilesRequests#uploadSessionStart(boolean)} and {@link
     * DbxUserFilesRequests#uploadSessionAppendV2(UploadSessionCursor,boolean)}
     * to upload file contents. We recommend uploading many files in parallel to
     * increase throughput. Once the file contents have been uploaded, rather
     * than calling {@link
     * DbxUserFilesRequests#uploadSessionFinish(UploadSessionCursor,CommitInfo)},
     * use this route to finish all your upload sessions in a single request.
     * the {@code close} argument to {@link
     * DbxUserFilesRequests#uploadSessionStart(boolean)} or the {@code close}
     * argument to {@link
     * DbxUserFilesRequests#uploadSessionAppendV2(UploadSessionCursor,boolean)}
     * needs to be true for the last {@link
     * DbxUserFilesRequests#uploadSessionStart(boolean)} or {@link
     * DbxUserFilesRequests#uploadSessionAppendV2(UploadSessionCursor,boolean)}
     * call. This route will return a job_id immediately and do the async commit
     * job in background. Use {@link
     * DbxUserFilesRequests#uploadSessionFinishBatchCheck(String)} to check the
     * job status. For the same account, this route should be executed serially.
     * That means you should not start the next job before current job finishes.
     * We allow up to 1000 entries in a single request.
     *
     * @param entries  Commit information for each file in the batch. Must
     *     contain at most 1000 items, not contain a {@code null} item, and not
     *     be {@code null}.
     *
     * @return Result returned by methods that may either launch an asynchronous
     *     job or complete synchronously. Upon synchronous completion of the
     *     job, no additional information is returned.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public LaunchEmptyResult uploadSessionFinishBatch(List<UploadSessionFinishArg> entries) throws DbxApiException, DbxException {
        UploadSessionFinishBatchArg _arg = new UploadSessionFinishBatchArg(entries);
        return uploadSessionFinishBatch(_arg);
    }

    //
    // route 2/files/upload_session/finish_batch/check
    //

    /**
     * Returns the status of an asynchronous job for {@link
     * DbxUserFilesRequests#uploadSessionFinishBatch(List)}. If success, it
     * returns list of result for each entry.
     *
     * @param arg  Arguments for methods that poll the status of an asynchronous
     *     job.
     */
    UploadSessionFinishBatchJobStatus uploadSessionFinishBatchCheck(PollArg arg) throws PollErrorException, DbxException {
        try {
            return this.client.rpcStyle(this.client.getHost().getApi(),
                                        "2/files/upload_session/finish_batch/check",
                                        arg,
                                        false,
                                        PollArg.Serializer.INSTANCE,
                                        UploadSessionFinishBatchJobStatus.Serializer.INSTANCE,
                                        PollError.Serializer.INSTANCE);
        }
        catch (DbxWrappedException ex) {
            throw new PollErrorException("2/files/upload_session/finish_batch/check", ex.getRequestId(), ex.getUserMessage(), (PollError) ex.getErrorValue());
        }
    }

    /**
     * Returns the status of an asynchronous job for {@link
     * DbxUserFilesRequests#uploadSessionFinishBatch(List)}. If success, it
     * returns list of result for each entry.
     *
     * @param asyncJobId  Id of the asynchronous job. This is the value of a
     *     response returned from the method that launched the job. Must have
     *     length of at least 1 and not be {@code null}.
     *
     * @throws IllegalArgumentException  If any argument does not meet its
     *     preconditions.
     */
    public UploadSessionFinishBatchJobStatus uploadSessionFinishBatchCheck(String asyncJobId) throws PollErrorException, DbxException {
        PollArg _arg = new PollArg(asyncJobId);
        return uploadSessionFinishBatchCheck(_arg);
    }

    //
    // route 2/files/upload_session/start
    //

    /**
     * Upload sessions allow you to upload a single file in one or more
     * requests, for example where the size of the file is greater than 150 MB.
     * This call starts a new upload session with the given data. You can then
     * use {@link
     * DbxUserFilesRequests#uploadSessionAppendV2(UploadSessionCursor,boolean)}
     * to add more data and {@link
     * DbxUserFilesRequests#uploadSessionFinish(UploadSessionCursor,CommitInfo)}
     * to save all the data to a file in Dropbox. A single request should not
     * upload more than 150 MB of file contents.
     *
     *
     * @return Uploader used to upload the request body and finish request.
     */
    UploadSessionStartUploader uploadSessionStart(UploadSessionStartArg arg) throws DbxException {
        HttpRequestor.Uploader _uploader = this.client.uploadStyle(this.client.getHost().getContent(),
                                                                   "2/files/upload_session/start",
                                                                   arg,
                                                                   false,
                                                                   UploadSessionStartArg.Serializer.INSTANCE);
        return new UploadSessionStartUploader(_uploader);
    }

    /**
     * Upload sessions allow you to upload a single file in one or more
     * requests, for example where the size of the file is greater than 150 MB.
     * This call starts a new upload session with the given data. You can then
     * use {@link
     * DbxUserFilesRequests#uploadSessionAppendV2(UploadSessionCursor,boolean)}
     * to add more data and {@link
     * DbxUserFilesRequests#uploadSessionFinish(UploadSessionCursor,CommitInfo)}
     * to save all the data to a file in Dropbox. A single request should not
     * upload more than 150 MB of file contents.
     *
     * <p> The {@code close} request parameter will default to {@code false}
     * (see {@link #uploadSessionStart(boolean)}). </p>
     *
     * @return Uploader used to upload the request body and finish request.
     */
    public UploadSessionStartUploader uploadSessionStart() throws DbxException {
        UploadSessionStartArg _arg = new UploadSessionStartArg();
        return uploadSessionStart(_arg);
    }

    /**
     * Upload sessions allow you to upload a single file in one or more
     * requests, for example where the size of the file is greater than 150 MB.
     * This call starts a new upload session with the given data. You can then
     * use {@link
     * DbxUserFilesRequests#uploadSessionAppendV2(UploadSessionCursor,boolean)}
     * to add more data and {@link
     * DbxUserFilesRequests#uploadSessionFinish(UploadSessionCursor,CommitInfo)}
     * to save all the data to a file in Dropbox. A single request should not
     * upload more than 150 MB of file contents.
     *
     * @param close  If true, the current session will be closed, at which point
     *     you won't be able to call {@link
     *     DbxUserFilesRequests#uploadSessionAppendV2(UploadSessionCursor,boolean)}
     *     anymore with the current session.
     *
     * @return Uploader used to upload the request body and finish request.
     */
    public UploadSessionStartUploader uploadSessionStart(boolean close) throws DbxException {
        UploadSessionStartArg _arg = new UploadSessionStartArg(close);
        return uploadSessionStart(_arg);
    }
}
