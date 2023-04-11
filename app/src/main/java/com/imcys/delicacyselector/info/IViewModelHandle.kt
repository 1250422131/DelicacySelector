package com.imcys.delicacyselector.info

interface IViewModelHandle<S : UiState, I : UiIntent> {

    fun handleEvent(event: I, state: S)

}