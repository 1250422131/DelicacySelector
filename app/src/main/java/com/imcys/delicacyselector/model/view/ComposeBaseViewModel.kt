package com.imcys.delicacyselector.model.view

import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imcys.delicacyselector.info.IViewModelHandle
import com.imcys.delicacyselector.info.UiIntent
import com.imcys.delicacyselector.info.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

abstract class ComposeBaseViewModel<S : UiState, I : UiIntent>(viewState: S) :
    IViewModelHandle<S, I>,
    ViewModel() {

    val intentChannel = Channel<I>(Channel.UNLIMITED)


    var viewStates by mutableStateOf(viewState)
        protected set


    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            intentChannel.consumeAsFlow().collect {
                handleEvent(it, viewStates)
            }
        }
    }

    fun sendIntent(viewIntent: I) {
        viewModelScope.launch {
            intentChannel.send(viewIntent)
        }
    }


}