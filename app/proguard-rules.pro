# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep public interface ** extends android.os.IInterface {*;}
-keep public class com.rosan.ruto.App {*;}
-keep public class com.rosan.ruto.** extends android.app.Activity

-keep class kotlin.reflect.jvm.internal.** { *; }
-keep class kotlin.metadata.** { *; }
-keepattributes RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations, Signature, *Annotation*

# 2. 保留 Kotlin 所有的元数据注解（非常关键，反射依赖这个）
-keep @interface kotlin.Metadata { *; }

-keep class rikka.shizuku.ShizukuProvider

-keep class com.fasterxml.jackson.annotation.** { *; }
-keep class com.fasterxml.jackson.databind.** { *; }

-keep @com.fasterxml.jackson.annotation.* class dev.langchain4j.** { *; }
-keepclassmembers @com.fasterxml.jackson.annotation.* class dev.langchain4j.** { *; }

-keep @com.fasterxml.jackson.databind.** class dev.langchain4j.** { *; }
-keepclassmembers @com.fasterxml.jackson.databind.** class dev.langchain4j.** { *; }

-keepclassmembers enum dev.langchain4j.** { *; }

-dontwarn **
