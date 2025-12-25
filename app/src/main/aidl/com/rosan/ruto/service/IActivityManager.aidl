package com.rosan.ruto.service;

import android.content.Intent;

interface IActivityManager {
    void startLabel(in String label);

    void startApp(in String packageName);

    void startActivity(in Intent intent);
}