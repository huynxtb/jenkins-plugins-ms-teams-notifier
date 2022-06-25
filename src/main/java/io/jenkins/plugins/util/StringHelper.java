package io.jenkins.plugins.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.jenkins.plugins.dto.BindingControlDto;
import io.jenkins.plugins.dto.FactDto;
import io.jenkins.plugins.exception.AppException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class StringHelper {

    public static String serializableObject(Object obj) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer();
        return ow.writeValueAsString(obj);
    }

    public static String toDateTimeNow(String timeZone) {
        try {
            Date date = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone(timeZone));
            return df.format(date);
        } catch (Exception e) {
            try {
                throw new AppException("Time zone invalid.");
            } catch (AppException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static String toJsonColumns(String jobLink, String webUrl) {
        String clViewBuild = "";
        String clWebUrl = "";
        StringBuilder sb = new StringBuilder();

        if (!jobLink.equals(""))
            clViewBuild = "{\"type\":\"Column\",\"width\":\"stretch\",\"items\":[{\"type\":\"ActionSet\",\"actions\":[{\"type\":\"Action.OpenUrl\",\"title\":\"View Build\",\"style\":\"destructive\",\"url\":\"" + jobLink + "\"}]}]}";
        if (!webUrl.equals(""))
            clWebUrl = "{\"type\":\"Column\",\"width\":\"stretch\",\"items\":[{\"type\":\"ActionSet\",\"actions\":[{\"type\":\"Action.OpenUrl\",\"title\":\"Go to Website\",\"style\":\"positive\",\"url\":\"" + webUrl + "\"}]}]}";

        sb.append("[");
        sb.append(clViewBuild);
        if(!clViewBuild.equals("")) sb.append(",");
        sb.append(clWebUrl);
        sb.append("]");

        return sb.toString();
    }

    public static String toJsonMsTeams(ArrayList<FactDto> facts, BindingControlDto dto) throws JsonProcessingException {
        String jonFacts = serializableObject(facts);
        String jonColumns = toJsonColumns(dto.getJobLink(), dto.getWebUrl());
        return "{\"type\":\"message\",\"attachments\":[{\"contentType\":\"application/vnd.microsoft.card.adaptive\",\"content\":{\"schema\":\"http://adaptivecards.io/schemas/adaptive-card.json\",\"type\":\"AdaptiveCard\",\"version\":\"1.2\",\"body\":[{\"type\":\"Container\",\"items\":[{\"type\":\"ColumnSet\",\"columns\":[{\"type\":\"Column\",\"items\":[{\"type\":\"TextBlock\",\"size\":\"Large\",\"weight\":\"Bolder\",\"text\":\"**" + dto.getTitle() + "**\",\"wrap\":true,\"style\":\"heading\",\"fontType\":\"Default\",\"spacing\":\"Large\",\"horizontalAlignment\":\"Center\",\"color\":\"" + dto.getColor() + "\"}],\"width\":\"stretch\"}]}],\"bleed\":true,\"spacing\":\"Large\",\"style\":\"" + dto.getColor() + "\"},{\"type\":\"Container\",\"items\":[{\"type\":\"TextBlock\",\"text\":\"" + dto.getDescription() + "\",\"wrap\":true},{\"type\":\"FactSet\",\"facts\": " + jonFacts + "},{\"type\":\"Container\",\"items\":[{\"type\":\"ColumnSet\",\"columns\": " + jonColumns + "}]}]}]}}]}";
    }
}

