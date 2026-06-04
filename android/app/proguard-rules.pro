# Notes du Secouriste — règles release (R8)

-keepattributes *Annotation*, InnerClasses, EnclosingMethod, Signature

# Kotlin / coroutines
-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# kotlinx.serialization (sectionsJson, modèles)
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}
-keep,includedescriptorclasses class com.notesdusecouriste.**$$serializer { *; }
-keepclassmembers class com.notesdusecouriste.** {
    *** Companion;
}
-keep @kotlinx.serialization.Serializable class com.notesdusecouriste.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# DataStore
-keep class androidx.datastore.*.** { *; }

# Compose (règles conservatrices)
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# App
-keep class com.notesdusecouriste.app.Hilt_* { *; }
-keep class com.notesdusecouriste.app.NotesDuSecouristeApp { *; }
-keep class com.notesdusecouriste.app.MainActivity { *; }
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
