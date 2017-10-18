package com.bearenterprises.sofiatraffic.constants;

import com.bearenterprises.sofiatraffic.fragments.LoadingFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by thalv on 02-Jul-16.
 */
public class Constants {
    public static final String XML_COORDINATE_FILE = "coordinates.xml";
    public static final String JSON_COORDINATE_FILE = "coordinates.json";
    public static final String JSON_COORDINATE_FILE_NEW = "new_coordinates.json";
    public static final String SHOW_DIALOG = "show dialog";
    public static final String DISMISS_DIALOG = "dismiss dialog";
    public static final String DESCRIPTIONS_FILE_NAME = "descriptions.txt";
    public static final String DESCRIPTIONS_FILE_NAME_NEW = "new_descriptions.txt";
    public static final String HASH_COORDS_NEW = "hash_coords_new.txt";
    public static final String HASH_DESCS_NEW = "hash_descs_new.txt";
    public static final String HASH_COORDS = "hash_coords.txt";
    public static final String HASH_DESCS = "hash_descs.txt";

    public static final int NO_SUCH_LINE = -200;

    public static final String SECTION_SEARCH = "ТЪРСЕНЕ";
    public static final String SECTION_FAVOURITES = "ЛЮБИМИ";
    public static final String SECTION_LINES = "ЛИНИИ";
    public static final String SECTION_MAP_SEARCH = "КАРТА";
    public static final String SHARED_PREFERENCES_FAVOURITES = "favourites";
    public static final String SHARED_PREFERENCES_LAST_UPDATE = "last_update";
    public static final String KEY_LAST_UPDATE = "last update";
    public static final String SHARED_PREFERENCES_HELPER = "helper";
    public static final int SHARED_PREFERENCES_DEFAULT_LAST_UPDATE_TIME = -1;
    public static final long WEEK_IN_MILLISECONDS = 604800000;
    public static final long DAY_IN_MILLISECONDS = 86400000;
    public static final int REQUEST_CODE_FINE_LOCATION = 1;
    public static final float MINIMUM_ACCURACY = 20;
    public static final String BUS = "bus";
    public static final String TRAM = "tram";
    public static final String TROLLEY = "trolley";
    public static final int SECTION_SEARCH_IDX = 0;
    public static final int SECTION_FAVOURITES_IDX = 1;
    public static final int SECTION_LINES_IDX = 2;
    public static final int SECTION_MAP_SEARCH_IDX = 3;
    public static final int MAXIMUM_TIME_GPS_LOCK = 10000;
    public static final int FIVE_SECONDS_MS = 5000;
    public static final String LINE_ID_DEFAULT = "Избери линия";
    public static final String REST_QUERY_LINES = "https://api.sofiatransport.com/v2/stops/%s";
    public static final String REST_QUERY_TIMES = "https://api.sofiatransport.com/v2/stops/%s/%s";
    public static final List<String> TRANSPORTATION_TYPES = Arrays.asList("Трамвай", "Автобус", "Тролей");
    public static final int TRAM_ID = 0;
    public static final int TRAM_ID_INTERACTIVE_CARD = 1;

    public static final int BUS_ID = 1;
    public static final int BUS_ID_INTERACTIVE_CARD = 0;

    public static final int TROLLEY_ID = 2;
    public static final int TROLLEY_ID_INTERACTIVE_CARD = 2;


    public static final String VIRTUAL_TABLES_API = "virtual_table_api";
    public static final String SCHEDULES_API = "schedules_api";

    public static final String IVKOS_API_BASE_URL = "https://api.sofiatransport.com/v3/";
    public static final String SHARED_PREFERENCES_REGISTRATION = "registration";
    public static final String REGISTRATION = "reg";
    public static final String SHARED_PREFERENCES_DEFAULT_REGISTRATION = "no_reg";
    public static final String UNAUTHOROZIED_USER_ID = "UnauthorizedUserIdError";
    public static final String QUERY_METHOD_SLOW = "slow";
    public static final String QUERY_METHOD_FAST = "fast";

    public static final String WORKDAY = "WORKDAY";
    public static final String PRE_NON_WORKING_DAY = "PRE_NON_WORKING_DAY";
    public static final String NON_WORKING_DAY = "NON_WORKING_DAY";




    public static final String COORDINATES_DOWNLOAD_URL_JSON = "https://raw.githubusercontent.com/thalvadzhi/Sofia-Traffic-Stops-Getter/master/stops_getter/coordinates.json";
    public static final String DESCRIPTIONS_DOWNLOAD_URL = "https://raw.githubusercontent.com/thalvadzhi/Sofia-Traffic-Stops-Getter/master/descriptions_getter/descriptions.txt";
    public static final String COORDINATES_HASH_URL = "https://raw.githubusercontent.com/thalvadzhi/Sofia-Traffic-Stops-Getter/master/stops_getter/hash.txt";
    public static final String DESCRIPTIONS_HASH_URL = "https://raw.githubusercontent.com/thalvadzhi/Sofia-Traffic-Stops-Getter/master/descriptions_getter/hash.txt";

}
