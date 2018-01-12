-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions

-dontnote android.net.http.**
-dontnote org.apache.commons.**
-dontnote org.apache.http.**
-dontwarn sun.misc.Unsafe

# Google Play Services
-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**
-dontnote com.google.android.gms.**

# Ignore duplicate classes in legacy android's http stuff
-dontnote org.apache.http.**
-dontnote android.net.http.**

# Support v4 lib excludes
-keep class android.support.v4.** { *; }
-dontnote android.support.v4.**
-keepattributes Signature

# Workaround for Andorid bug #78377
-keep interface android.support.v4.** { *; }
-keep class !android.support.v7.view.menu.*MenuBuilder*, android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

#Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# MPCharts
-keep class com.github.mikephil.charting.** { *; }
-dontwarn io.realm.**

# OkHttp
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

-keepattributes Annotation
-dontwarn okhttp3.**
-dontwarn okio.**

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

#DBFlow
-keep class * extends com.raizlabs.android.dbflow.config.DatabaseHolder { *; }

#Parcelize (Kotlin)
-keep class kotlin.internal.annotations.AvoidUninitializedObjectCopyingCheck { *; }
-dontwarn kotlin.internal.annotations.AvoidUninitializedObjectCopyingCheck

#Serializeable (e.g.ThreeTenABP's LocalDate)
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
