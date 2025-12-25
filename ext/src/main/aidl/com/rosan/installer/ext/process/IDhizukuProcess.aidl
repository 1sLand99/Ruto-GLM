package com.rosan.installer.ext.process;

import com.rosan.app_process.ParcelableBinder;

interface IDhizukuProcess {
    boolean isAlive() = 21;

    ParcelableBinder serviceBinder(String className) = 22;
}