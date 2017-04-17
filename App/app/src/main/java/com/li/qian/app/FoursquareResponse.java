/**
 * response object from Foursquare API
 */
package com.li.qian.app;

import java.util.ArrayList;
import java.util.List;
public class FoursquareResponse {

    FoursquareGroup group;
    List<FoursquareVenue> venues = new ArrayList<>();
}
