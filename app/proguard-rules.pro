# Add project-specific ProGuard rules here.
# Room: preserve entity and DAO classes
-keep class org.terst.cancan.**.data.** { *; }

# kotlinx-serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class org.terst.cancan.**$$serializer { *; }
-keepclassmembers class org.terst.cancan.** { *** Companion; }
-keepclasseswithmembers class org.terst.cancan.** { kotlinx.serialization.KSerializer serializer(...); }
