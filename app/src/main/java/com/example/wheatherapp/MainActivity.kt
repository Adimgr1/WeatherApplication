package com.example.wheatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup.Binding
import android.util.Log
import android.widget.SearchView
import android.widget.TextView
import com.example.wheatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects
import kotlin.math.abs

//753b42998b12816fe2804b95f0a42333

class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Jaipur")
        searchCity()


    }




    private fun fetchWeatherData( city : String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(Apiinterface::class.java)
        val response = retrofit.getWeatherData(city,"753b42998b12816fe2804b95f0a42333","metric")
        response.enqueue(object :Callback<wheatherapp>{
            override fun onResponse(call: Call<wheatherapp>, response: Response<wheatherapp>) {
                val responsebody = response.body()
                if(response.isSuccessful && responsebody != null){
                    val temp = responsebody.main.temp.toInt()
                    val numid = responsebody.main.humidity
                    val min = responsebody.main.temp_min
                    val max = responsebody.main.temp_max
                    val temprature = findViewById<TextView>(R.id.temp)
                    val sesLevel = responsebody.main.pressure
                    val condition = responsebody.weather.firstOrNull()?.main?:"unknown"
                    val windSpeed = responsebody.wind.speed
                    val sunrise = responsebody.sys.sunrise.toLong()
                    val sunset = responsebody.sys.sunset.toLong()
                    temprature.text= (abs(temp)).toString()
                    binding.humidity.text = numid.toString()
                    binding.maxtemp.text = "Max:$max°C"
                    binding.mintemp.text= "Min:$min°C"
                    binding.sea.text = sesLevel.toString()
                    binding.animationCondition.text = condition.toString()
                    binding.condition.text = condition.toString()
                    binding.windSpeed.text = windSpeed.toString()
                    binding.riseTime.text = riseTime(sunrise).toString()
                    binding.setTime.text= "${setTime(sunset)}"
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityName.text = city
                    changeback(condition)

                }
            }

            override fun onFailure(call: Call<wheatherapp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun changeback(condition : String) {
        when(condition){
            "Clear","Sunny","Clear Sky" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.animation.setAnimation(R.raw.sun)
            }
            "Partly Cloud","Clouds","Overcast","Mist","Foggy","Haze" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.animation.setAnimation(R.raw.cloud)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.animation.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.animation.setAnimation(R.raw.cloud)
            }
        }
        binding.animation.playAnimation()
    }

    fun date():String{
        val date = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return date.format((Date()))
    }
    fun dayName(timestamp: Long):String{
        val day = SimpleDateFormat("EEEE", Locale.getDefault())
        return day.format((Date()))

    }
    fun setTime(timestamp: Long):String{
        val setTime = SimpleDateFormat("HH:MM", Locale.getDefault())
        return setTime.format((Date(timestamp*1000)))

    }
    fun riseTime(timestamp: Long):String{
        val day = SimpleDateFormat("HH:MM", Locale.getDefault())
        return day.format((Date(timestamp*1000)))

    }
    private fun searchCity(){
        val searchView = binding.searchview
        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        } )
    }
}