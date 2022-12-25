package com.example.loadapp.Utils

sealed class ButtonState {
       object Idle: ButtonState()
       object Clicked : ButtonState()
        object Loading : ButtonState()
        object Completed : ButtonState()
    }

