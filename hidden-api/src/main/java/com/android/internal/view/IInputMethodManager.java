package com.android.internal.view;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;

public interface IInputMethodManager extends IInterface {
    void setInputMethod(IBinder token, String id);

    abstract class Stub extends Binder implements IInputMethodManager {
        public static IInputMethodManager asInterface(IBinder obj) {
            throw new UnsupportedOperationException();
        }
    }
}