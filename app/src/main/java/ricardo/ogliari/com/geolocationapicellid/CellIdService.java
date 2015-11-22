package ricardo.ogliari.com.geolocationapicellid;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by ricardo on 18/09/2015.
 */
public interface CellIdService {

    @POST("/geolocation/v1/geolocate")
    void geolocate(@Body String body, @Query("key") String key, Callback<CellId> callback);//Call<List<Repo>>

}
