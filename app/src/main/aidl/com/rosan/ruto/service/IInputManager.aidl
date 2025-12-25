package com.rosan.ruto.service;

import android.graphics.PointF;

interface IInputManager {
    void click(in PointF p);

    void doubleClick(in PointF p);

    void longClick(in PointF p);

    void swipe(in PointF start, in PointF end);

    void clickBack();

    void clickHome();
}