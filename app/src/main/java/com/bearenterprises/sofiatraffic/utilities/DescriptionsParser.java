package com.bearenterprises.sofiatraffic.utilities;

import android.content.Context;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thalv on 05-Dec-16.
 */

public class DescriptionsParser {

    public static Map<String, String> parse(Context context, String fileName){
        try {
            String source = Files.asCharSource(new File(context.getFilesDir(), fileName), Charsets.UTF_8).read();
            String[] split = source.split("\n");
            Map<String, String> m = new HashMap<>();
            for(int i = 0; i < split.length; i++){
                String[] codeDesct = split[i].split("=");
                m.put(codeDesct[0], codeDesct[1]);
            }
            return m;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
