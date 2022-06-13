-dontwarn javax.annotation.Nullable
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.OpenSSLProvider
-keep class jahrulnr.animeWatch.Class._anime { *; }
-keep class jahrulnr.animeWatch.Class._manga { *; }
-assumenosideeffects class java.io.PrintStream {
     public void println(%);
     public void println(**);
 }

 -keepattributes *Annotation*
 -keepclassmembers class * {
     @org.greenrobot.eventbus.Subscribe <methods>;
 }
 -keep enum org.greenrobot.eventbus.ThreadMode { *; }

 # If using AsyncExecutord, keep required constructor of default event used.
 # Adjust the class name if a custom failure event type is used.
# -keepclassmembers class org.greenrobot.eventbus.util.ThrowableFailureEvent {
#     <init>(java.lang.Throwable);
# }

 # Accessed via reflection, avoid renaming or removal
 -keep class org.greenrobot.eventbus.android.AndroidComponentsImpl