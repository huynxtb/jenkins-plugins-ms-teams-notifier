package io.jenkins.plugins.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import hudson.model.Result;
import io.jenkins.plugins.constants.MessageConst;
import io.jenkins.plugins.enums.StatusColor;
import io.jenkins.plugins.exception.AppException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringHelper {

    public static String serializableObject(Object obj) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer();
        return ow.writeValueAsString(obj);
    }

    public static String getHeader(String result, String buildNumber) throws AppException {
        if (result.equals(Result.SUCCESS.toString())) {
            return "<h1><strong style='color: " + StatusColor.GREEN.getColor() + "'>" + MessageConst.SUCCESS + buildNumber + "</strong></h1>";
        } else if (result.equals(Result.UNSTABLE.toString())) {
            return "<h1><strong style='color: " + StatusColor.YELLOW.getColor() + "'>" + MessageConst.UNSTABLE + buildNumber + "</strong></h1>";
        } else if (result.equals(Result.FAILURE.toString())) {
            return "<h1><strong style='color: " + StatusColor.RED.getColor() + "'>" + MessageConst.FAILURE + buildNumber + "</strong></h1>";
        } else if (result.equals(Result.ABORTED.toString())) {
            return "<h1><strong style='color: " + StatusColor.GRAY.getColor() + "'>" + MessageConst.ABORTED + buildNumber + "</strong></h1>";
        } else {
            throw new AppException("Result is required. Following this way: msTeamsNotifier result: currentBuild.currentResult");
        }
    }

    public static String toDateTimeNow() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
}
