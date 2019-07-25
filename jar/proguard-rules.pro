
#【引用的库的jar，用于解析injars所指定的jar类】这里替换成对应sdk

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,LocalVariable*Table,*Annotation*,Synthetic,EnclosingMethod

-libraryjars    'bin/android.jar'

## 自己的开始
-obfuscationdictionary dic.txt
-classobfuscationdictionary dic.txt
-packageobfuscationdictionary dic.txt
##所有微信的监听都要加一个这个 防止混淆

-keepnames class com.nine.remotemm.JarObject{
    public void init(...);
    public void deInit(...);
    public void addView(...);
}


-keepclassmembernames class com.nine.remotemm.entry.**{ *;}

-dontwarn com.tencent.mm.core.Entrance
-keep class com.tencent.mm.core.Entrance



-dontwarn com.tencent.mm.**
-keep class com.tencent.mm.** { *;}

-dontwarn com.nine.remotemm.Core
-keep class com.nine.remotemm.Core { *;}

## 自己的结束



-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}


################### region for xUtils
-keepattributes Signature,*Annotation*
-keep public class org.core.** {
    public protected *;
}
-keep public interface org.core.** {
    public protected *;
}
-keepclassmembers class * extends org.core.** {
    public protected *;
}
-keepclassmembers @org.core.db.annotation.* class * {*;}
-keepclassmembers @org.core.http.annotation.* class * {*;}
-keepclassmembers class * {
    @org.core.view.annotation.Event <methods>;
}
#################### end region

################### TCP
-dontwarn com.xuhao.android.libsocket.**
-keep class com.xuhao.android.socket.impl.abilities.** { *; }
-keep class com.xuhao.android.socket.impl.exceptions.** { *; }
-keep class com.xuhao.android.socket.impl.EnvironmentalManager { *; }
-keep class com.xuhao.android.socket.impl.BlockConnectionManager { *; }
-keep class com.xuhao.android.socket.impl.UnBlockConnectionManager { *; }
-keep class com.xuhao.android.socket.impl.SocketActionHandler { *; }
-keep class com.xuhao.android.socket.impl.PulseManager { *; }
-keep class com.xuhao.android.socket.impl.ManagerHolder { *; }
-keep class com.xuhao.android.socket.interfaces.** { *; }
-keep class com.xuhao.android.socket.sdk.** { *; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class com.xuhao.android.socket.sdk.OkSocketOptions$* {
    *;
}
#################### end tcp

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class com.android.vending.licensing.ILicensingService

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
