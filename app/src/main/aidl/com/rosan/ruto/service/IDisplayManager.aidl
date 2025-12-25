package com.rosan.ruto.service;

import android.view.DisplayInfo;
import android.view.Surface;
import com.rosan.ruto.display.BitmapWrapper;

interface IDisplayManager {
    int[] getDisplayIds();

    DisplayInfo getDisplayInfo(int displayId);

    int monitor(int displayId,in Surface surface);

    BitmapWrapper capture(int displayId);

    void release(int monitorId);

    void releaseAll();
}