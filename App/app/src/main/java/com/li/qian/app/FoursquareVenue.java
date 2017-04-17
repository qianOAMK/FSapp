/**
 * this is a venue object from the Foursquare API
 */
package com.li.qian.app;

public class FoursquareVenue {
    // ID of the venue
    String id;

    // name of the venue
    String name;

    //rating, if posible
    double rating;

    // a location object within the venue
    FoursquareLocation location;
}
