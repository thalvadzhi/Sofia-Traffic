package com.bearenterprises.sofiatraffic.utilities;

import com.bearenterprises.sofiatraffic.restClient.ApiError;
import com.bearenterprises.sofiatraffic.restClient.SofiaTransportApi;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by thalv on 19-Dec-16.
 */

public class ParseApiError {

    public static ApiError parseError(Response<?> response) {
        Converter<ResponseBody, ApiError> converter =
                SofiaTransportApi.retrofit
                        .responseBodyConverter(ApiError.class, new Annotation[0]);

        ApiError error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new ApiError();
        }

        return error;
    }
}
