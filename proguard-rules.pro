-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

-keepattributes InnerClasses
-keepattributes EnclosingMethod

-dontnote android.net.http.**
-dontnote org.apache.commons.**
-dontnote org.apache.http.**

# Google Play Services
-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**

# Dropbox
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn com.squareup.okhttp.**
-dontwarn com.google.appengine.**
-dontwarn javax.servlet.**
-keep class hk.com.fgoproduction.getdroplets.Lib.OAuth.TokenResult { *; }

# Ignore duplicate classes in legacy android's http stuff
-dontnote org.apache.http.**
-dontnote android.net.http.**

# Support v4 lib excludes
-keep class android.support.v4.** { *; }
-dontnote android.support.v4.**
-keepattributes Signature

# Retrolambda excludes
-dontwarn java.lang.invoke.*

# Workaround for Andorid bug #78377
-keep interface android.support.v4.** { *; }
-keep class !android.support.v7.view.menu.*MenuBuilder*, android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

# Icepick excludes
-dontwarn icepick.**
-keep class icepick.** { *; }
-keep class **$$Icepick { *; }
-keepclasseswithmembernames class * {
    @icepick.* <fields>;
}
-keepnames class * { @icepick.State *;}

# Parceler
-keep class **$$Parcelable { *; }

#Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# MPCharts
-keep class com.github.mikephil.charting.** { *; }
-dontwarn io.realm.**

# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

-keepattributes Annotation
-keep class okhttp3.** { *; }
-keep interface okhttp3.* { *; }
-dontwarn okhttp3.**

# Butterknife
-dontwarn butterknife.internal.**
-keepattributes *Annotation*
-keep class butterknife.** { *; }
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# Okio
-dontwarn okio.**

## Joda Time 2.3
-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *; }

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**