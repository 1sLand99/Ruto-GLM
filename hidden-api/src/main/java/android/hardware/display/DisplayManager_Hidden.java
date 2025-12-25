package android.hardware.display;

import android.os.Build;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import dev.rikka.tools.refine.RefineAs;

@RefineAs(DisplayManager.class)
public class DisplayManager_Hidden {
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Nullable
    public static VirtualDisplay createVirtualDisplay(@NonNull String name, int width, int height, int displayIdToMirror, @Nullable Surface surface) {
        return null;
    }
}
