package com.bearenterprises.sofiatraffic.utilities.network;

import static com.bearenterprises.sofiatraffic.fragments.TimesSearchFragment.getQueryStringPostLollipop;
import static com.bearenterprises.sofiatraffic.utilities.db.DbUtility.getDescription;
import static com.bearenterprises.sofiatraffic.utilities.db.DbUtility.getStationsFromDatabase;

import android.util.Log;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.ApiError;
import com.bearenterprises.sofiatraffic.restClient.Line;
import com.bearenterprises.sofiatraffic.restClient.SofiaTransportApi;
import com.bearenterprises.sofiatraffic.restClient.Stop;
import com.bearenterprises.sofiatraffic.restClient.schedules.ScheduleLineTimes;
import com.bearenterprises.sofiatraffic.restClient.schedules.ScheduleTimes;
import com.bearenterprises.sofiatraffic.utilities.Utility;
import com.bearenterprises.sofiatraffic.utilities.db.DbHelper;
import com.bearenterprises.sofiatraffic.utilities.db.DbUtility;
import com.bearenterprises.sofiatraffic.utilities.parsing.Description;
import com.bearenterprises.sofiatraffic.utilities.parsing.ParseApiError;
import com.bearenterprises.sofiatraffic.utilities.registration.RegistrationUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by thalv on 05-Mar-17.
 */

public class RetrofitUtility {
    public static String TAG = RetrofitUtility.class.toString();

    public static <T> T handleUnauthorizedQuery(Call<T> call, MainActivity mainActivity) throws IOException {
        Response<T> response = call.execute();
        if (!response.isSuccessful()) {
            ApiError error = ParseApiError.parseError(response);
            if (error.getCode() != null && error.getCode().equals(Constants.UNAUTHOROZIED_USER_ID)) {
                RegistrationUtility.reRegister(mainActivity);
                Call<T> cll = call.clone();
                response = cll.execute();
            }
            Log.i("Error mf", error.getMessage());
        }
        return response.body();
    }

    public static ArrayList<Line> getLinesByStationCode(String code, MainActivity activity) {
        String query = "SELECT * FROM " + DbHelper.FeedEntry.TABLE_NAME_STATIONS + " WHERE " + DbHelper.FeedEntry.COLUMN_NAME_CODE + " =?";

        ArrayList<Stop> stationsFromDatabase = DbUtility.getStationsFromDatabase(query, new String[]{code}, activity);
        if (stationsFromDatabase == null || stationsFromDatabase.isEmpty()){
            return null;
        }
        ArrayList<Line> lines = stationsFromDatabase.get(0).getLines();
        for (Line l : lines){
            Description description = getDescription(Integer.toString(l.getType()), l.getName(), code, activity);
            l.setRouteName(description.getDirection().toUpperCase());
        }

        Collections.sort(lines, new Comparator<Line>() {
            @Override
            public int compare(Line line, Line t1) {
                return Utility.compareLineNames(line, t1);
            }
        });

        return lines;
    }
}
