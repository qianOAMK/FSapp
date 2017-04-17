/**
 * Retrofit Interface for Foursquare API
 */
package com.li.qian.app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.li.qian.app.MainActivity.query;

public interface FoursquareService {

    // A request to snap the current user to a place via the Foursquare API.
    @GET("venues/search?v=20170416&limit=1")
    Call<FoursquareJSON> snapToPlace(@Query("client_id") String clientID,
                                     @Query("client_secret") String clientSecret,
                                     @Query("ll") String ll,
                                     @Query("llAcc") double llAcc);

    // A request to search for nearby venues via the Foursquare API.
    @GET("search/recommendations?v=20170416&intent=checkin")
    Call<FoursquareJSON> searchVenues(@Query("client_id") String clientID,
                                      @Query("client_secret") String clientSecret,
                                      @Query("ll") String ll,
                                      @Query("llAcc") double llAcc,
                                      @Query("query") String query);
}
