package com.rosan.installer.ext.process;

import com.rosan.app_process.ParcelableBinder;

interface IShizukuProcess {
    void destroy() = 16777114;

    boolean isAlive() = 1;

    ParcelableBinder serviceBinder(String className) = 2;
}