package com.firecrow.windmill

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AppsObservables: ViewModel() {
    val searchCriteria: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val resetSearchCriteria: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val refreshAppList: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val appView: MutableLiveData<ScreenToken> by lazy {
        MutableLiveData<ScreenToken>()
    }
}