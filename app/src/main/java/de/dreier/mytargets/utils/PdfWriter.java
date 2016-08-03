package de.dreier.mytargets.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;

import com.android.dx.stock.ProxyBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import apps.okan.demo.webview.util.FileUtils;

/**
 * Class to Implement InvocationHandler Interface so we can invoke any privately constructed classes.
 * This Class is Used to Generate PrintDocumentAdapter's WriteResultCallback and LayoutResultCallback Classes.
 *
 * @author Okan SAYILGAN
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class PdfWriter implements InvocationHandler {

    private PrintDocumentAdapter adapter;
    private File emptyPdfFile;
    private PdfWriterCallback pdfWriterCallback;

    /** Either Application or Activity Context will work. We will only use it to access Application's File Directory
     * in order to create a Cache Destination for Dex Files.
     */
    private Context context;

    /** Print Attributes to be used to Render Pdf Document in onLayout method of the PrintDocumentAdapter */
    private PrintAttributes oldPrintAttributes;

    /** Print Attributes to be used to Render Pdf Document in onLayout method of the PrintDocumentAdapter */
    private PrintAttributes newPrintAttributes;

    /** Print Attributes to be used to Render Pdf Document in onLayout method of the PrintDocumentAdapter */
    private final PrintAttributes defaultPrintAttributes = new PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.NA_LETTER)
            .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
            .setResolution(new PrintAttributes.Resolution("1", "Foo", 300, 300))
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build();

    /**
     * Public Constructor.
     *
     * @param context       Activity or Application Context to access Applications Private File directory to create a Folder
     *                      for Caching Dex Files.
     * @param adapter
     * @param emptyPdfFile
     */
    public PdfWriter(Context context, PrintDocumentAdapter adapter, File emptyPdfFile) {
        this.context = context;
        this.adapter = adapter;
        this.emptyPdfFile = emptyPdfFile;
    }

    /**
     * Sets Old Print Attributes. If not set, default print attributes will be used.
     *
     * @param oldPrintAttributes
     */
    public void setOldPrintAttributes(PrintAttributes oldPrintAttributes) {
        this.oldPrintAttributes = oldPrintAttributes;
    }

    /**
     * Sets New Print Attributes. If not set, default print attributes will be used.
     *
     * @param newPrintAttributes
     */
    public void setNewPrintAttributes(PrintAttributes newPrintAttributes) {
        this.newPrintAttributes = newPrintAttributes;
    }

    /**
     * Retrieves final default Print Attributes.
     *
     * @return
     */
    public PrintAttributes getDefaultPrintAttributes() {
        return this.defaultPrintAttributes;
    }

    /**
     * Writes WebView to Pdf File. WebView will be represented by PrintDocumentAdapter.
     * It should be created by webView instance.
     *
     * @param pdfWriterCallback    Callback to notify whether the Write is Successful or NOT.
     * @throws IOException
     */
    public void write(PdfWriterCallback pdfWriterCallback) throws IOException {

        /* Set Pdf Writer Callback */
        this.pdfWriterCallback = pdfWriterCallback;

        /* Call onLayout Method of the Adapter Class. Which will trigger Rendering Pdf Document from WebView */
        PrintDocumentAdapter.LayoutResultCallback layoutResultCallback = getLayoutResultCallback(this);
        adapter.onLayout(
                (oldPrintAttributes == null) ? defaultPrintAttributes : oldPrintAttributes,
                (newPrintAttributes == null) ? defaultPrintAttributes : newPrintAttributes,
                null,
                layoutResultCallback,
                null);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        /* Check for the Callback Methods */
        if (method != null && "onLayoutFinished".equals(method.getName())) {

            /* Trigger onWrite method of PrintDocumentAdapter after the Layout is Finished */
            onWrite();

        } else if (method != null && "onWriteFinished".equals(method.getName()) && pdfWriterCallback != null) {
            pdfWriterCallback.onWriteFinished();
        } else if (method != null && "onWriteFailed".equals(method.getName()) && pdfWriterCallback != null) {
            pdfWriterCallback.onWriteFailed();
        }

        return null;
    }

    /**
     * Creates WriteResultCallback instance and Calls onWrite method of the PrintDocumentAdapter Instance.
     *
     * @throws IOException
     */
    private void onWrite() throws IOException {

        /* Create Write Result Callback Instance */
        PrintDocumentAdapter.WriteResultCallback writeResultCallback = getWriteResultCallback(this);

        /* Write Pdf to Given Empty Pdf File */
        ParcelFileDescriptor pfd = ParcelFileDescriptor.open(emptyPdfFile, ParcelFileDescriptor.MODE_READ_WRITE);
        this.adapter.onWrite(new PageRange[]{PageRange.ALL_PAGES}, pfd, null, writeResultCallback);
    }

    /**
     * Method to get LayoutResultCallback instance with Java Reflection API.
     *
     * @param invocationHandler
     * @return
     * @throws IOException
     */
    private PrintDocumentAdapter.LayoutResultCallback getLayoutResultCallback(InvocationHandler invocationHandler) throws  IOException {
        return ProxyBuilder.forClass(PrintDocumentAdapter.LayoutResultCallback.class)
                .dexCache(FileUtils.getDexCacheDirectory(this.context))
                .handler(invocationHandler)
                .build();
    }

    /**
     * Method to get WriteResultCallback instance with Java Reflection API.
     *
     * @param invocationHandler
     * @return
     * @throws IOException
     */
    private PrintDocumentAdapter.WriteResultCallback getWriteResultCallback(InvocationHandler invocationHandler) throws  IOException {
        return ProxyBuilder.forClass(PrintDocumentAdapter.WriteResultCallback.class)
                .dexCache(FileUtils.getDexCacheDirectory(this.context))
                .handler(invocationHandler)
                .build();
    }

    /**
     * Interface to provide an Abstraction Layer between Pdf Adapter and it's callbacks.
     * This will make the Operation more readable.
     */
    public interface PdfWriterCallback {

        /**
         * Called when the Write Operation is Successfully Finished.
         */
        void onWriteFinished();

        /**
         * Called when Write Operation is Failed.
         */
        void onWriteFailed();
    }
}