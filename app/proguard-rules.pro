-dontwarn javax.annotation.Nullable
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.OpenSSLProvider
-keep class jahrulnr.animeWatch.Class.animeList { *; }
-keep class jahrulnr.animeWatch.Class.episodeList { *; }
-assumenosideeffects class java.io.PrintStream {
     public void println(%);
     public void println(**);
 }