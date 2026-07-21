-dontwarn javax.annotation.**
-dontwarn org.jetbrains.annotations.**
-keep class com.family.safety.platform.by.raja.haider.ali.model.** { *; }
-keep class com.family.safety.platform.by.raja.haider.ali.model.User { *; }
-keep class com.family.safety.platform.by.raja.haider.ali.model.Device { *; }
-keep class com.family.safety.platform.by.raja.haider.ali.model.** { *; }
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    <fields>;
}
