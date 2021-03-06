package ua.edu.zsmu.mfi.biology.pollen.pollen;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ua.edu.zsmu.mfi.biology.pollen.util.LocalFileDataProvider;

/**
 * Created by Andrey on 17.05.2017.
 */

public final class NormalPollenConcentrationDataProvider {

    public NormalConcentration getDataFromLocalFile(Context context) {
        String json = new LocalFileDataProvider()
                .getJSONNormConcentrationFileContent(context);
        try {
            JSONObject jObject = new JSONObject(json);
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(jObject.getString("date"));
            final JSONArray jsonData= jObject.getJSONArray("data");
            return new NormalConcentration(date, buildMap(jsonData));
        } catch (JSONException | ParseException e) {
            Log.e("ERROR",e.getMessage());
        }
        throw new RuntimeException();
    }

    private Map<Integer, Double> buildMap(JSONArray jsonData) throws JSONException {
        final Map<Integer, Double> data = new HashMap<>();
        for (int i = 0; i < jsonData.length() ; i++) {
            JSONObject jsonObject = jsonData.getJSONObject(i);
            Integer day = (Integer)jsonObject.get("day");
            Double value = (Double)jsonObject.get("value");
            data.put(day, value);
        }
        return data;
    }

    // TODO
    public NormalConcentration getDataFromGoogleDrive() {
        return new NormalConcentration(new Date(), new HashMap<Integer, Double>());
    }

}
