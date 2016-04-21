-dontobfuscate
-dontwarn javax.xml.**
-dontwarn java.lang.invoke.*
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn com.google.android.gms.*
-dontwarn android.support.**


-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

-keep class !android.support.v7.internal.view.menu.**,android.support.** {*;}

-dontwarn org.jetbrains.annotations.**
-keepclassmembers class nz.bradcampbell.paperparcel.PaperParcelMapping {
  static ** FROM_ORIGINAL;
  static ** FROM_PARCELABLE;
}