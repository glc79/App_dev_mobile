package com.example.exo4.network

import com.example.exo4.model.LaserRunCategory
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("laserrun.json")  // On met juste le chemin relatif car le BASE_URL est défini dans RetrofitInstance
    fun getLaserrunItems(): Call<List<LaserRunCategory>>
}