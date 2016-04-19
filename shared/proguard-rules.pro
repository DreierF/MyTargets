-dontshrink
-dontobfuscate
-dontwarn javax.xml.**
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn com.google.android.gms.*
-dontwarn java.lang.invoke.*
-dontwarn java.nio.file.Files
-dontwarn java.nio.file.Path
-dontwarn java.nio.file.OpenOption
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn android.support.**


-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

-keep class **

-dontwarn org.jetbrains.annotations.**
-keepclassmembers class nz.bradcampbell.paperparcel.PaperParcelMapping {
  static ** FROM_ORIGINAL;
  static ** FROM_PARCELABLE;
}