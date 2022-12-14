package com.ssafy.indive.view.login

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.indive.model.dto.MemberJoin
import com.ssafy.indive.model.dto.MemberLogin
import com.ssafy.indive.model.dto.Notice
import com.ssafy.indive.model.response.MemberDetailResponse
import com.ssafy.indive.repository.MemberManagerRepository
import com.ssafy.indive.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject


private const val TAG = "MemberViewModel"

@HiltViewModel
class MemberViewModel @Inject constructor(
    private val memberManagerRepository: MemberManagerRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    val email: MutableStateFlow<String> =
        MutableStateFlow("")

    val password: MutableStateFlow<String> =
        MutableStateFlow("")

    val nickname: MutableStateFlow<String> =
        MutableStateFlow("")

    private val _login: MutableStateFlow<Result<Response<String>>> =
        MutableStateFlow(Result.Unintialized)
    val login get() = _login.asStateFlow()

    private val _loginSuccess = SingleLiveEvent<String>()
    val loginSuccess get() = _loginSuccess

    private val _loginFail = SingleLiveEvent<String>()
    val loginFail get() = _loginFail

    private val _join: MutableStateFlow<Result<Response<Boolean>>> =
        MutableStateFlow(Result.Unintialized)
    val join get() = _join.asStateFlow()

    private val _emailCheck = SingleLiveEvent<Boolean>()
    val emailCheck get() = _emailCheck

    private val _profile = SingleLiveEvent<MemberDetailResponse>()
    val profile get() = _profile

    private val _notice = SingleLiveEvent<String>()
    val notice get() = _notice

    private val _noticeSuccess = SingleLiveEvent<Boolean>()
    val noticeSuccess get() = _noticeSuccess

    private val _modifySuccess = SingleLiveEvent<Boolean>()
    val modifySuccess get() = _modifySuccess

    fun memberLogin(memberLogin: MemberLogin) {
        viewModelScope.launch(Dispatchers.IO) {
            memberManagerRepository.login(memberLogin).collectLatest {
                _login.value = it
                Log.d(TAG, "memberLogin: $it")
                if (it is Result.Success) {
                    val res = it.data.body()
                    if (res == "true") {
                        _loginSuccess.postValue("????????? ??????")
                        sharedPreferences.edit()
                            .putString(JWT, it.data.headers()["Authorization"])
                            .apply()
                    } else {
                        _loginFail.postValue("????????? ??????")
                    }
                    Log.d(TAG, "memberLogin: ${it.data.body()}")
                    //Log.d(TAG, "memberLogin: ${it.data.headers()["Authorization"]!!.split(" ")[1]}")
                }
            }
        }
    }

    fun memberJoin(memberJoin: MemberJoin) {
        viewModelScope.launch(Dispatchers.IO) {
            memberManagerRepository.join(memberJoin).collectLatest {
                _join.value = it
                Log.d(TAG, "memberJoin: $it")
                if (it is Result.Success) {
                    Log.d(TAG, "memberJoin: ${it.data.body()}")
                }
            }
        }
    }

    fun memberEmailCheck(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            memberManagerRepository.emailcheck(email).collectLatest {
                if (it is Result.Success) {
                    _emailCheck.postValue(it.data.body())
                    Log.d(TAG, "memberEmailCheck body: ${it.data.body()}")
                } else if (it is Result.Error) {
                    Log.d(TAG, "memberEmailCheckError: ${it}")
                }
            }
        }
    }

    fun memberDetail(memberSeq: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            memberManagerRepository.memberDetail(memberSeq).collectLatest {
                if (it is Result.Success) {
                    Log.d(TAG, "memberDetail: ${it.data.body()}")
                    _profile.postValue(it.data.body())
                    _notice.postValue(it.data.body()?.notice)
                }
            }
        }
    }

    fun memberModify(
        memberSeq: Long,
        nickname: String,
        profileFile: MultipartBody.Part?,
        backgroundFile: MultipartBody.Part?,
        profileMessage: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "memberModify: ${profileFile}")
            Log.d(TAG, "memberModify: ${backgroundFile}")
            memberManagerRepository.modifyMember(
                memberSeq,
                nickname,
                profileFile,
                backgroundFile,
                profileMessage
            ).collectLatest {
                Log.d(TAG, "memberModify: ${it}")
                if (it is Result.Success) {
                    Log.d(TAG, "memberModify: ${it.data.body()}")
                    _modifySuccess.postValue(it.data.body())
                } else if (it is Result.Error) {
                    Log.d(TAG, "memberModify: ${it}")

                }
            }
        }
    }

    fun writeNotice(memberSeq: Long, notice: Notice) {
        viewModelScope.launch(Dispatchers.IO) {
            memberManagerRepository.writeNotice(memberSeq, notice).collectLatest {
                if (it is Result.Success) {
                    _noticeSuccess.postValue(it.data)
                }
            }
        }
    }

    fun loginMemberDetail() {
        viewModelScope.launch(Dispatchers.IO) {
            memberManagerRepository.loginMemberDetail().collectLatest {
                if (it is Result.Success) {
                    Log.d(TAG, "loginMemberDetail: ${it.data.body()}")
                    val res = it.data.body() as MemberDetailResponse
                    sharedPreferences.edit()
                        .putLong(USER, res.memberSeq)
                        .apply()
                    sharedPreferences.edit().putString(USER_EMAIL, it.data.body()!!.email).apply()
                    sharedPreferences.edit().putString(USER_ADDRESS, it.data.body()!!.wallet).apply()

                }
            }
        }
    }

}