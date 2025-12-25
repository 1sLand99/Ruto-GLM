package com.rosan.ruto.service;

interface IImeManager {
    void readyInput();

    void finishInput();

    void text(in String text);

    void print(in String code);

    void clear();
}