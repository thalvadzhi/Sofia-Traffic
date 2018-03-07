package com.bearenterprises.sofiatraffic.utilities.parsing;

import android.content.Context;

import com.bearenterprises.sofiatraffic.utilities.ReadWholeFileAsStringKt;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thalv on 05-Dec-16.
 */

public class DescriptionsParser {

    public static Map<String, String> parse(Context context, String fileName){
        try {
            String source = ReadWholeFileAsStringKt.readFileAsString(new File(context.getFilesDir(), fileName));
            String[] split = source.split("\n");
            Map<String, String> m = new HashMap<>();
            for(int i = 0; i < split.length; i++){
                String[] codeDesct = split[i].split("=");
                m.put(codeDesct[0], codeDesct[1]);
            }
            return m;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Description> parseDescriptions(Context context, String fileName){
        try{
            List<Description> descs = new ArrayList<>();
            String source = ReadWholeFileAsStringKt.readFileAsString(new File(context.getFilesDir(), fileName));
            String[] descriptions = source.split(";");
            for (int i = 0; i < descriptions.length; i++){
                String[] description = descriptions[i].split("=");
                String direction = description[1];
                String[] ids = description[0].split(",");
                String transportationType = ids[0];
                String lineName = ids[1];
                String stopCode = ids[2];
                descs.add(new Description(transportationType, lineName, stopCode, direction));
            }
            return descs;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
