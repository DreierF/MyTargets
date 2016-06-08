-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

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

# RxAndroid
-dontwarn rx.internal.util.unsafe.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# Okio
-dontwarn okio.**

## Joda Time 2.3
-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *; }