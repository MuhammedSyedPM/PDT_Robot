# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\\Documents\software-installation/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep public class * {
    public protected *;
}


# RFID API3 SDK package maintained
-dontwarn android.os.ServiceManager
-dontwarn org.bouncycastle.crypto.BlockCipher
-dontwarn org.bouncycastle.crypto.CipherParameters
-dontwarn org.bouncycastle.crypto.InvalidCipherTextException
-dontwarn org.bouncycastle.crypto.engines.AESEngine
-dontwarn org.bouncycastle.crypto.engines.RFC5649WrapEngine
-dontwarn org.bouncycastle.crypto.params.KeyParameter
-dontwarn org.bouncycastle.util.encoders.Hex
-dontwarn org.apache.xerces.dom.DOMInputImpl
-dontwarn com.zebra.rfidhost.RFIDHostEventAndReason
-dontwarn com.zebra.rfidhostlib.BuildConfig
-dontwarn com.zebra.rfidserial.RfidSerial
-dontwarn com.zebra.rfidhost.IRFIDHostCallBack
-dontwarn com.zebra.rfidhost.RfidHost
-dontwarn com.zebra.rfidhost.RfidHost$ConnectionListener
-dontwarn com.zebra.rfidhost.IRFIDHostCallBack$Stub
-dontwarn com.zebra.rfidserial.RfidSerial$ConnectionListener
-dontwarn vendor.zebra.hardware.rfidserial.IPort
-dontwarn com.zebra.rfid.api3.**
-dontwarn com.zebra.rfid.api3.InvalidUsageException
-dontwarn com.zebra.rfid.api3.OperationFailureException
-dontwarn com.zebra.rfid.api3.RFIDReader
-dontwarn com.zebra.rfid.api3.Config
-dontwarn java.lang.invoke.MethodHandleProxies

-keep public class com.zebra.rfid.** { *; }
-keep public class com.zebra.rfidhost.** { *; }
-keep public class com.zebra.rfidhostlib.** { *; }
-keep public class com.zebra.rfid.**$* { *; }
-keep public class com.zebra.rfidhost.**$* { *; }
-keep public class com.zebra.rfidhostlib.**$* { *; }
-keep public class com.zebra.rfidserial.** { *; }
-keep public class com.zebra.rfidserial.**$* { *; }

-keep class com.zebra.rfidhost.** { *; }
-keep class com.zebra.rfidhostlib.** { *; }
-keep class com.zebra.rfidserial.** { *; }
-keep class vendor.zebra.hardware.rfidserial.** { *; }
-keep class com.zebra.rfid.api.** { *; }
-keep class com.zebra.rfid.api3.RFIDReader { *; }
-keep class com.zebra.rfid.api3.RFIDReader
-keep class com.zebra.rfid.api3.Config
-keep class com.zebra.rfid.api3.Config { *; }
-keep class com.zebra.demo.rfidreader.rfid.RFIDController.** { *; }
-keep class com.zebra.demo.rfidreader.rfid.RFIDController.RFIDReader.** { *; }
-keep class com.zebra.** { *; }
-keep class com.zebra.demo.rfidreader.common.PreFilters { *; }
-keep class com.zebra.rfid.api3.PreFilters { *; }
-keep class com.zebra.scannercontrol.**{ *; }
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation{ *; }
-keep class javax.lang.model.**{ *; }
-keep class com.google.errorprone.** { *; }
-keep class com.zebra.rfid.api3.HexDump.** { *; }
-keep class com.zebra.rfid.api3.** { *; }
-keep class org.apache.xerces.dom.DOMInputImpl.** { *; }

 -keep class com.zebra.rfid.api3.InvalidUsageException { *; }
 -keep public class com.zebra.rfid.api3.InvalidUsageException
 -keep public class com.zebra.rfid.api3.OperationFailureException
 -keep class com.zebra.rfid.api3.OperationFailureException { *; }
 -keep class com.zebra.rfid.api3.RFIDResults { *; }
 -keep class com.zebra.rfid.api3.ReaderDevice { *; }
 -keep class com.zebra.rfid.api3.RegionMapping { *; }

-keep class com.zebra.rfidhostlib.BuildConfig.** { *; }
-keep class **.BuildConfig { *; }
-keep class com.zebra.rfid.api3.HexDump.** { *; }
-keep class com.zebra.rfid.api3.API3ProtocolWrapper
-keep class com.zebra.rfid.api3.IRFIDLogger

-keepclassmembers class * {
      public *;
}

-keepclassmembers class com.zebra.rfid.api3.PreFilters {
    int length();
}

-keepclassmembers class com.zebra.rfid.api3.Config {
    public *;
}

-keepclassmembers class com.zebra.rfid.api3.API3Wrapper {
    public *;
}

-keepclassmembers class com.zebra.rfid.api3.API3TransportWrapper {
    public *;
}

-keepclassmembers class com.zebra.rfid.api3.Config {
    void getDeviceStatus(boolean, boolean, boolean);
}

-keepattributes Exceptions

-keep interface com.zebra.rfid.** { *; }
-keep public class * extends android.app.Application
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
