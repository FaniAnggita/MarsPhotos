package com.example.android.marsphotos.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsphotos.network.MarsApi
import com.example.android.marsphotos.network.MarsPhoto
import kotlinx.coroutines.launch

enum class MarsApiStatus { LOADING, ERROR, DONE }

/**
 * [ViewModel] yang dilampirkan ke [OverviewFragment].
 */
class OverviewViewModel : ViewModel() {

    // MutableLiveData internal yang menyimpan status permintaan terbaru
    private val _status = MutableLiveData<MarsApiStatus>()

    // LiveData eksternal yang tidak dapat diubah untuk status permintaan
    val status: LiveData<MarsApiStatus> = _status

    // Secara internal, kami menggunakan MutableLiveData, karena kami akan memperbarui Daftar MarsPhoto
    // dengan nilai baru
    private val _photos = MutableLiveData<List<MarsPhoto>>()

    // Antarmuka LiveData eksternal ke properti tidak dapat diubah, jadi hanya kelas ini yang dapat memodifikasi
    val photos: LiveData<List<MarsPhoto>> = _photos

    /**
     * Panggil getMarsPhotos() pada init agar kami dapat segera menampilkan status.
     */
    init {
        getMarsPhotos()
    }

    /**
     * Mendapatkan informasi foto Mars dari layanan Mars API Retrofit dan memperbarui
     * [MarsPhoto] [Daftar] [LiveData].
     */
    private fun getMarsPhotos() {

        viewModelScope.launch {
            _status.value = MarsApiStatus.LOADING
            try {
                _photos.value = MarsApi.retrofitService.getPhotos()
                _status.value = MarsApiStatus.DONE
            } catch (e: Exception) {
                _status.value = MarsApiStatus.ERROR
                _photos.value = listOf()
            }
        }
    }
}
