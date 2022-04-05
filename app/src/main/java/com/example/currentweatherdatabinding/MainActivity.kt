package com.example.currentweatherdatabinding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.example.currentweatherdatabinding.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.InputStream
import java.net.URL
import java.net.UnknownHostException
import java.util.*
import org.json.JSONObject
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        findViewById<Button>(R.id.getWeatherButton).setOnClickListener {
            onClick()
        }

        //binding.weatherInfo = WeatherInfo("blabla")

        /*
        TODO: реализовать отображение погоды в текстовых полях и картинках
        картинками отобразить облачность и направление ветра
        использовать data binding, библиотеки уже подключены
         */
    }
    suspend fun loadWeather() {
        val API_KEY = getString(R.string.api_key)
        val editText = findViewById<EditText>(R.id.city)
        val city = editText.getText().toString()
        binding.weatherInfo = WeatherInfo("")
        //val mImageView = findViewById<ImageView>(R.id.sky);
        //mImageView.setImageResource(R.drawable.rainy);
        //val textView = findViewById<TextView>(R.id.error_text);
        val weatherURL =
            "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$API_KEY&units=metric";
        try{
            val stream = URL(weatherURL).getContent() as InputStream
            val data = Scanner(stream).nextLine()
            val jObject = JSONObject(data)
            //Log.d("mytag", data)
            val sky = jObject.getJSONArray("weather").getJSONObject(0)["main"].toString()
            val temp = jObject.getJSONObject("main")["temp"].toString() + " °C"
            Log.d("mytag", sky)
            val wind = jObject.getJSONObject("wind")
            val windSpeed = wind["speed"].toString() + " m/s"
            Log.d("mytag", wind.toString())
            val imgs : HashMap<String, Int> = hashMapOf("Clouds" to R.drawable.clouds, "Clear" to R.drawable.clear,
                "Rain" to R.drawable.rain, "Snow" to R.drawable.snow, "Thunderstorm" to R.drawable.thunderstorm,
                "Drizzle" to R.drawable.drizzle)
            var img = -1
            if (imgs.getOrDefault(sky, "-1") == "-1"){
                img = R.drawable.other
            } else {
                img = imgs[sky]!!
            }
            binding.weatherInfo = WeatherInfo("", isSuccess = true, sky = img,
                temp = temp, windDeg = wind["deg"] as Int, windSpeed = windSpeed)
        } catch (e : UnknownHostException){
            Log.d("mytag", "No internet")
            binding.weatherInfo = WeatherInfo("No internet")
            //Toast.makeText(applicationContext, "No internet", Toast.LENGTH_LONG).show()
        } catch (e : FileNotFoundException){
            Log.d("mytag", "Bad city")
            binding.weatherInfo = WeatherInfo("Bad city")
            //textView.text = "Bad city"
            //Toast.makeText(applicationContext, "Bad city", Toast.LENGTH_LONG).show()
        } catch (e : Exception){
            Log.d("mytag", "Unknown exception: $e")
            binding.weatherInfo = WeatherInfo("Unknown exception: $e")
            //Toast.makeText(applicationContext, "Unknown exception $e", Toast.LENGTH_LONG).show()
        }


        // JSON отдаётся одной строкой,

        // TODO: предусмотреть обработку ошибок (нет сети, пустой ответ)

    }
    public fun onClick() {

        // Используем IO-диспетчер вместо Main (основного потока)
        GlobalScope.launch (Dispatchers.IO) {
            loadWeather()
        }
    }
}

data class WeatherInfo(val message:String, val isSuccess:Boolean = false,
                       val sky: Int = R.drawable.clouds, val temp:String = "",
                       val windDeg:Int = 0, val windSpeed:String = "")