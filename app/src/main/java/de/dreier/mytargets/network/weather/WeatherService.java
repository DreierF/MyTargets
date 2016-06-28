package de.dreier.mytargets.network.weather;

import de.dreier.mytargets.models.CurrentWeather;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class WeatherService {
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private static final String APPID = "180ecfe968fb986c95cf0f8da8620530";

    private final OpenWeatherMapWebService mWebService;

    private interface OpenWeatherMapWebService {
        @GET("weather?units=metric")
        Call<CurrentWeather> fetchCurrentWeather(@Query("lon") double longitude,
                                                 @Query("lat") double latitude,
                                                 @Query("APPID") String appId);
    }

    public WeatherService() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Accept", "application/json")
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                })
                .build();

        mWebService = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .baseUrl(BASE_URL)
                .build()
                .create(OpenWeatherMapWebService.class);
    }

    public Call<CurrentWeather> fetchCurrentWeather(final double longitude, final double latitude) {
        return mWebService.fetchCurrentWeather(longitude, latitude, APPID);
    }
}