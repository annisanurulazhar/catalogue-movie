package com.example.annisaazhar.cataloguemovie;

import android.content.Context;
import android.content.res.Resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIUtils {

    private static Map<String, String> dateMap;
    private static Resources res;

    public UIUtils(Context context) {
        res = context.getResources();

        dateMap = new HashMap<String, String>();
        dateMap.put("01", res.getString(R.string.text_month_january));
        dateMap.put("02", res.getString(R.string.text_month_february));
        dateMap.put("03", res.getString(R.string.text_month_march));
        dateMap.put("04", res.getString(R.string.text_month_april));
        dateMap.put("05", res.getString(R.string.text_month_may));
        dateMap.put("06", res.getString(R.string.text_month_june));
        dateMap.put("07", res.getString(R.string.text_month_july));
        dateMap.put("08", res.getString(R.string.text_month_august));
        dateMap.put("09", res.getString(R.string.text_month_september));
        dateMap.put("10", res.getString(R.string.text_month_october));
        dateMap.put("11", res.getString(R.string.text_month_november));
        dateMap.put("12", res.getString(R.string.text_month_december));
    }

    public String formatDate(String date) {
        String year = date.substring(0,4);
        String month = date.substring(5, 7);
        String day = date.substring(8,10);

        return day + " " + dateMap.get(month) + " " + year;

    }

    public String formatGenre(List<Movie.Genre> genres){
        String genres_formatted = new String();

        int i = 0;

        while (i < genres.size() - 1) {
            genres_formatted += genres.get(i).getName() + ", ";
            i++;
        }
        genres_formatted += genres.get(i).getName();

        return genres_formatted;
    }
}
