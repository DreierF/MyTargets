package androidx.core.net

import android.net.Uri

/**
 * Creates a Uri from the given encoded URI string.
 *
 * @see Uri.parse
 */
inline fun String.toUri(): Uri = Uri.parse(this)
