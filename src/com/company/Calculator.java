package com.company;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Calculator {

    //region getters
    public int getStartOfWorkdayHours() {
        return startOfWorkdayHours;
    }

    public int getStartOfWorkdayMinutes() {
        return startOfWorkdayMinutes;
    }

    public int getEndOfWorkdayHours() {
        return endOfWorkdayHours;
    }

    public int getEndOfWorkdayMinutes() {
        return endOfWorkdayMinutes;
    }
    //endregion

    private final int startOfWorkdayHours;
    private final int startOfWorkdayMinutes;
    private final int endOfWorkdayHours;
    private final int endOfWorkdayMinutes;

    /**
     * @param startOfWorkdayHours   first working hour of working day 24-hour format
     * @param startOfWorkdayMinutes first minute of the first working hour
     * @param endOfWorkdayHours     last working hour of working day in 24-hour format
     * @param endOfWorkdayMinutes   last minute of last working hour
     */
    public Calculator(int startOfWorkdayHours, int startOfWorkdayMinutes,
                      int endOfWorkdayHours, int endOfWorkdayMinutes) {
        this.startOfWorkdayHours = startOfWorkdayHours;
        this.startOfWorkdayMinutes = startOfWorkdayMinutes;
        this.endOfWorkdayHours = endOfWorkdayHours;
        this.endOfWorkdayMinutes = endOfWorkdayMinutes;
    }

    /**
     * Converts input into format to work with, to be customized for different regions and/or user interface
     * Checks if date and time are in a valid format
     *
     * @param date the time and date of submitted request as String
     * @return the time and date of submitted request as Date
     */
    public Date timeDateConverter(String date) {
        Date submitTimeAndDate;
        if (!date.trim().equals("")) {
            SimpleDateFormat sdFormat = new SimpleDateFormat("HH/mm/dd/MM/yyyy");
            try {
                submitTimeAndDate = sdFormat.parse(date);
            } catch (ParseException e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new RuntimeException("Please provide time and date of submission");
        }
        return submitTimeAndDate;
    }

    /**
     * Checks is the date of the request is in the past
     *
     * @param submitDate date of request
     */
    public void isSubmitInThePast(Date submitDate) {
        Date currentDate = new Date();
        if (submitDate.compareTo(currentDate) < 0)
            throw new RuntimeException("Please provide a future date for submission");
    }

    /**
     * Converts time of the day into minutes
     *
     * @return time of the day in minutes
     */
    public int calculateTimeInMinutes(int hours, int minutes) {
        int timeInMinutes = hours * 60;
        timeInMinutes += minutes;
        return timeInMinutes;
    }

    /**
     * Checks if given time and date are withing working hours
     *
     * @param timeAndDateToCheck time and date to check
     * @return true if time and date are within working hours
     */
    public boolean isGivenTimeInWorkingHours(Date timeAndDateToCheck) {
        int workdayStartInMinutes = calculateTimeInMinutes(startOfWorkdayHours, startOfWorkdayMinutes);
        int workdayEndInMinutes = calculateTimeInMinutes(endOfWorkdayHours, endOfWorkdayMinutes);
        Calendar tracker = Calendar.getInstance();
        tracker.setTime(timeAndDateToCheck);
        int weekDay = tracker.get(Calendar.DAY_OF_WEEK);
        if (weekDay == 1 || weekDay == 7) {
            return false;
        }
        int hourOfSubmit = tracker.get(Calendar.HOUR_OF_DAY);
        int minuteOfSubmit = tracker.get(Calendar.MINUTE);
        int timeOfSubmitInMinutes = hourOfSubmit * 60 + minuteOfSubmit;
        return timeOfSubmitInMinutes >= workdayStartInMinutes
                && timeOfSubmitInMinutes < workdayEndInMinutes;
    }

    /**
     * Calculates the estimated time and date for request completion
     *
     * @param submitTimeAndDate starting time and date if request
     * @param turnaround        amount of time for request completion
     * @return estimated time and date for request completion
     */
    public Date calculateDueDateAndTime(Date submitTimeAndDate, int turnaround) {
        int turnaroundInMinutes = turnaround * 60;
        Calendar timeTracker = Calendar.getInstance();
        timeTracker.setTime(submitTimeAndDate);
        while (turnaroundInMinutes > 0) {
            if (isWeekend(timeTracker.getTime())) {
                timeTracker.add(Calendar.DATE, 1);
                timeTracker.set(Calendar.HOUR_OF_DAY, startOfWorkdayHours);
            } else if (!isGivenTimeInWorkingHours(timeTracker.getTime())) {
                timeTracker.add(Calendar.DATE, 1);
                timeTracker.set(Calendar.HOUR_OF_DAY, startOfWorkdayHours);
            } else {
                timeTracker.add(Calendar.MINUTE, 1);
                turnaroundInMinutes = turnaroundInMinutes - 1;
            }
        }
        return timeTracker.getTime();
    }

    /**
     * Checks if given date is on weekend
     *
     * @param submitTimeAndDate date to check
     * @return true if date is on weekend
     */
    public boolean isWeekend(Date submitTimeAndDate) {
        Calendar weekendTracker = Calendar.getInstance();
        weekendTracker.setTime(submitTimeAndDate);
        int weekDay = weekendTracker.get(Calendar.DAY_OF_WEEK);
        return weekDay == 1 || weekDay == 7;
    }
}
