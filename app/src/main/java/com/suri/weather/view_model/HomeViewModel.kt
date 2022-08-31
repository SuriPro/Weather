package com.suri.weather.view_model

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.suri.weather.R
import com.suri.weather.model.Hour
import com.suri.weather.model.Weather
import com.suri.weather.restapi.ApiClient
import com.suri.weather.restapi.ApiService
import com.suri.weather.utils.NetworkUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel constructor(private val context: Context) : ViewModel() {

    private val error = MutableLiveData<String>()
    fun getError(): LiveData<String> = error

    val isLoading: ObservableBoolean = ObservableBoolean(true)
    val loadingStatus: ObservableField<String> = ObservableField("Fetching Location")

    val weather: ObservableField<Weather> = ObservableField()
    val selectedHour: ObservableField<Hour> = ObservableField()
    val listForecast: MutableLiveData<List<Hour>> = MutableLiveData()

    fun getWeather(location: Location) {
        val request = ApiClient.buildService(ApiService::class.java)
        val disposable = CompositeDisposable()

        if (NetworkUtils(context).isNetworkAvailable()) {
            isLoading.set(true)
            loadingStatus.set("Getting weather Info")
            disposable.add(
                request.getWeather(
                    context.getString(R.string.weather_api),
                    "${location.latitude},${location.longitude}",
                    1,
                    "yes",
                    "no"
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleResponse, this::handleError)
            )
        } else
            error.value = "Check Internet Connection"


    }

    private fun handleError(throwable: Throwable) {
        Log.e("error", "" + throwable.message)
        error.value = throwable.message
        isLoading.set(false)
    }

    private fun handleResponse(response: Weather) {
        //update ui as per response
        weather.set(response)
        selectedHour.set(response.current)

        val time=Date().time

        listForecast.value=listOf(response.current) + response.forecast.forecastday[0].hour.filter { it.time_epoch*1000 > time }

        isLoading.set(false)
    }


}