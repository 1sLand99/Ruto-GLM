package android.view;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public abstract class DisplayAddress implements Parcelable {
    @SuppressLint("ParcelCreator")
    public static final class Physical extends DisplayAddress {
        private long mPhysicalDisplayId;

        public long getPhysicalDisplayId() {
            return 0;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
        }
    }
}
