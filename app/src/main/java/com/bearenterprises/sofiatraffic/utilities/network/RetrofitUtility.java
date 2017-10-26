package com.bearenterprises.sofiatraffic.utilities.network;

import android.util.Log;

import com.bearenterprises.sofiatraffic.activities.MainActivity;
import com.bearenterprises.sofiatraffic.constants.Constants;
import com.bearenterprises.sofiatraffic.restClient.ApiError;
import com.bearenterprises.sofiatraffic.restClient.Line;
import com.bearenterprises.sofiatraffic.restClient.SofiaTransportApi;
import com.bearenterprises.sofiatraffic.restClient.schedules.ScheduleLineTimes;
import com.bearenterprises.sofiatraffic.restClient.schedules.ScheduleTimes;
import com.bearenterprises.sofiatraffic.utilities.Utility;
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

        }
        return response.body();
    }

    public static ArrayList<Line> getLinesByStationCode(String code, MainActivity activity) {
        SofiaTransportApi sofiaTransportApi = MainActivity.retrofit.create(SofiaTransportApi.class);
        Call<List<ScheduleLineTimes>> lineTimes = sofiaTransportApi.getScheduleLineTimes(code);
        ArrayList<Line> lines = new ArrayList<>();
        try {
            String scheduleDayType = Utility.getScheduleDayType();
            List<ScheduleLineTimes> stopLines = RetrofitUtility.handleUnauthorizedQuery(lineTimes, activity);

            if (stopLines != null) {
                for (ScheduleLineTimes slt : stopLines) {
                    Line l = new Line(slt.getType(), null, slt.getName());
                    List<ScheduleTimes> schedule = slt.getSchedule();
                    for (ScheduleTimes st : schedule) {
                        if (st.getScheduleDayTypes().contains(scheduleDayType)) {
                            l.setRouteName(st.getRouteName());
                            lines.add(l);
                        }
                    }
                }

                Collections.sort(lines, new Comparator<Line>() {
                    @Override
                    public int compare(Line line, Line t1) {
                        return Utility.compareLineNames(line, t1);
                    }
                });
                return lines;
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.d(TAG, "Error getting lines for layout adapter", e);
        }
        return null;
    }
}
