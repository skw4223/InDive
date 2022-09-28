package com.ssafy.indive

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.indive.model.entity.PlayListEntity
import com.ssafy.indive.model.response.MusicDetailResponse
import com.ssafy.indive.repository.MusicManagerRepository
import com.ssafy.indive.repository.PlayListRepository
import com.ssafy.indive.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Response

@HiltViewModel
class MainViewModel @Inject constructor(
    private val playListRepository: PlayListRepository,
    private val musicManagerRepository: MusicManagerRepository
) : ViewModel() {

    private val _playList: MutableStateFlow<List<PlayListEntity>> = MutableStateFlow(listOf())
    val playList get() = _playList.asStateFlow()

    private val _musicDetails: MutableStateFlow<Result<Response<MusicDetailResponse>>> =
        MutableStateFlow(Result.Unintialized)
    val musicDetails get() = _musicDetails.asStateFlow()

    var successGetEvent = 0L


    fun insert(musicSeq: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            musicManagerRepository.getMusicDetails(musicSeq).collectLatest {
                if (it is Result.Success) {
                    _musicDetails.value = it
                    Log.d("insert", "insert: ${it.data.body()}")
                    val musicDetail = it.data.body()!!
                    val title = musicDetail.title
                    val artist = musicDetail.artist.nickname
                    val song = PlayListEntity(
                        0,
                        title,
                        "$MUSIC_HEADER$musicSeq$MUSIC_FOOTER",
                        artist,
                        "$COVER_HEADER$musicSeq$COVER_FOOTER"
                    )

                    playListRepository.insertPlayList(song)
                } else if (it is Result.Error) {
                    Log.d(TAG, "Error: ${it.exception}")

                }
            }

        }


    }


    fun getAll() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "initObserve: $this")
            Log.d("MainViewModel", "getAll:")
            playListRepository.getAllPlayList().collectLatest {
                Log.d("MainViewModel", "getAllCollectLatest: ${it}")
                if (it is Result.Success) {
                    _playList.value = it.data
                    Log.d("MainViewModel", "getAllSuccess: ${it.data}")
                } else if (it is Result.Empty) {
                    _playList.value = listOf()
                } else if (it is Result.Error) {
                    Log.d("MainViewModel", "getAllError: ${it}")
                }

            }

        }

    }
}