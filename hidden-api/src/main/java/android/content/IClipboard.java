package android.content;

import android.annotation.TargetApi;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.IInterface;

public interface IClipboard extends IInterface {
    @TargetApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    void setPrimaryClip(ClipData clip, String callingPackage, String attributionTag, int userId,
                        int deviceId);

    ClipData getPrimaryClip(String pkg, String attributionTag, int userId, int deviceId);

    @TargetApi(Build.VERSION_CODES.Q)
    void setPrimaryClip(ClipData clip, String callingPackage, int userId);

    ClipData getPrimaryClip(String pkg, int userId);

    void setPrimaryClip(ClipData clip, String callingPackage);

    ClipData getPrimaryClip(String pkg);

    abstract class Stub extends Binder implements IClipboard {
        public static IClipboard asInterface(IBinder obj) {
            throw new UnsupportedOperationException();
        }
    }
}
