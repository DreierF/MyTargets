package de.dreier.mytargets.network.weather;

import de.dreier.mytargets.models.CurrentWeather;
import de.dreier.mytargets.shared.models.Environment;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public class WeatherService {
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private static final String APPID = "180ecfe968fb986c95cf0f8da8620530";

    private final OpenWeatherMapWebService mWebService;

    private interface OpenWeatherMapWebService {
        @GET("weather?units=metric")
        Observable<CurrentWeather> fetchCurrentWeather(@Query("lon") double longitude,
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
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .baseUrl(BASE_URL)
                .build()
                .create(OpenWeatherMapWebService.class);
    }

    public Observable<Environment> fetchCurrentWeather(final double longitude, final double latitude) {
        return mWebService.fetchCurrentWeather(longitude, latitude, APPID)
                .flatMap(WeatherService::filterWebServiceErrors)
                .flatMap(currentWeather -> Observable.just(currentWeather.toEnvironment()));
    }

    /**
     * The web service always returns a HTTP header code of 200 and communicates errors
     * through a 'cod' field in the JSON payload of the response body.
     */
    private static Observable<CurrentWeather> filterWebServiceErrors(CurrentWeather weather) {
        if (weather.httpCode == 200) {
            return Observable.just(weather);
        } else {
            return Observable
                    .error(new Exception("There was a problem fetching the weather data."));
        }
    }
}