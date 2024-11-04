package com.fsa.leaf_logic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    val userId = MutableLiveData<String>()
    val userName = MutableLiveData<String>()
    val userEmail = MutableLiveData<String>()
}
