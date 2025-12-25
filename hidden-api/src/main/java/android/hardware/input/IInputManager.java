package android.hardware.input;

import android.annotation.TargetApi;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.IInterface;
import android.view.InputEvent;

public interface IInputManager extends IInterface {
    boolean injectInputEvent(InputEvent ev, int mode);

    @TargetApi(Build.VERSION_CODES.TIRAMISU)
    boolean injectInputEventToTarget(InputEvent ev, int mode, int targetUid);

    abstract class Stub extends Binder implements IInputManager {
        public static IInputManager asInterface(IBinder obj) {
            throw new UnsupportedOperationException();
        }
    }
}
