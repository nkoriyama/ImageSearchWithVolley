# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:
-dontnote

-keep class android.support.** { *; }
-keep interface android.support.** { *; }

-keep public class * extends org.nkoriyama.imagesearchwithvolley.PhotoListFragment
-keepclassmembers class * extends org.nkoriyama.imagesearchwithvolley.PhotoListFragment {
  public static org.nkoriyama.imagesearchwithvolley.PhotoListFragment newInstance(java.lang.String);
}


# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# The following line may be different
#-libraryjars <java.home>/lib/rt.jar(java/**,javax/**)

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
# Not remove unused code
-dontshrink

-optimizations !method/inlining/*

#-keep public class * extends android.app.Activity
#-keep public class * extends android.app.Application
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#-keep public class * extends android.app.backup.BackupAgentHelper
#-keep public class * extends android.preference.Preference
#-keep public class com.android.vending.licensing.ILicensingService

# Guava
-dontwarn com.google.common.**

# OkHttp
-dontwarn com.squareup.okhttp.**

# Okio
-dontwarn okio.**

# Butter Knife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# Simple XML
-dontwarn org.simpleframework.xml.stream.**
-keep public class org.simpleframework.**{ *; }
-keep class org.simpleframework.xml.**{ *; } 
-keep class org.simpleframework.xml.core.**{ *; } 
-keep class org.simpleframework.xml.util.**{ *; }

#
-keep class org.apache.commons.codec.binary.** { *; }

# Annotations and signatures
#-keepattributes *Annotation*
-keepattributes Signature
#-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

#-keepclasseswithmembernames class * {
#    native <methods>;
#}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
