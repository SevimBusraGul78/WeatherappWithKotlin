package com.a.b.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import  androidx.appcompat.widget.SearchView

import com.a.b.weatherapp.databinding.ActivityMainBinding
import okhttp3.Response
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//{"coord":{"lon":28.9833,"lat":41.0351},"weather":[{"id":800,"main":"CLEAR","description":"CLEAR sky","icon":"01n"}],"base":"stations","main":{"temp":285.6,"feels_like":284.75,"temp_min":284.24,"temp_max":285.96,"pressure":1020,"humidity":71},"visibility":10000,"wind":{"speed":0.51,"deg":130},"clouds":{"all":0},"dt":1700163214,"sys":{"type":1,"id":6970,"country":"TR","sunrise":1700110338,"sunset":1700145927},"timezone":10800,"id":745042,"name":"Istanbul","cod":200}
//9630d5e2574a3312a83d8f01ac9e528e
class MainActivity : AppCompatActivity() {
    private  val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fetachWeatherData("Istanbul")
        SearhCity()


    }

    private fun SearhCity() {
       val searchView =binding.searchView
        searchView.setOnQueryTextListener(/* listener = */ object :SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetachWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                   return true
            }

        })

    }


    private fun fetachWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response =
            retrofit.getWeatherData(cityName, "9630d5e2574a3312a83d8f01ac9e528e", "metric")
        response.enqueue(/* callback = */ object : Callback<WeatherApp> {
            /**
             * Invoked for a received HTTP response.
             *
             *
             * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
             * Call [Response.isSuccessful] to determine if the response indicates success.
             */
            override fun onResponse(
                call: Call<WeatherApp>,
                response: retrofit2.Response<WeatherApp>
            ) {
                val responseBody = response.body()

                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    // Log.e("TAG","onResponse: $temperature")
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min




                    binding.tempclock.text = "$temperature  °C"
                    binding.weather.text = condition
                    binding.maxTerm.text = "Max Temp :$maxTemp °C "
                    binding.minTerm.text = "Min Temp :$minTemp °C "
                    binding.humidity.text = "$humidity % "
                    binding.wind.text = "$windSpeed m/s"
                    binding.sunrise.text = "${time(sunRise)}"
                    binding.sunset.text = "${time(sunset)}"
                     binding.sea.text = "$seaLevel hPa"
                    binding.conditions.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityName.text = "$cityName"
                    changeImagsAccordingToWeaterCondtion(condition)


                }

            }

            /**
             * Invoked when a network exception occurred talking to the server or when an unexpected exception
             * occurred creating the request or processing the response.
             */
            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

            }

            /**
             * Invoked when a network exception occurred talking to the server or when an unexpected exception
             * occurred creating the request or processing the response.
             */



        })

    }

    private fun changeImagsAccordingToWeaterCondtion(condition: String) {
        when(condition){


          " Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" ->{
              binding.root.setBackgroundResource(R.drawable.rain_background)
              binding.lottieAnimationView.setAnimation(R.raw.rain)
          }
            "Sunny","Clear","Clear Sky" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Clouds","Partly Clouds","Overcast","Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Snow","Light Snow" ,"Moderate Snow ","Heavy Snow","Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

        }
        binding.lottieAnimationView.playAnimation()



    }


    private fun date(): String{
        val sdf =SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))

    }
    private fun time(timestamp: Long): String{
        val sdf =SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))

    }

    fun dayName(timestamp: Long):String{
        val sdf =SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))

    }
    }

