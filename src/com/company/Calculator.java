package com.company;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    private final int workdayStartInMinutes;
    private final int workdayEndInMinutes;

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
        this.workdayStartInMinutes = calculateTimeInMinutes(startOfWorkdayHours, startOfWorkdayMinutes);
        this.workdayEndInMinutes = calculateTimeInMinutes(endOfWorkdayHours, endOfWorkdayMinutes);
    }

    /**
     * Converts input into format to work with, to be customized for different regions and/or user interface
     * Checks if date and time are in a valid format
     *
     * @param date the time and date of submitted request as String
     * @return the time and date of submitted request as Date
     */
    public Calendar timeDateConverter(String date) {
        Calendar calendar = Calendar.getInstance();
        if (!date.trim().equals("")) {
            SimpleDateFormat sdFormat = new SimpleDateFormat("HH/mm/dd/MM/yyyy");
            try {
                calendar.setTime(sdFormat.parse(date));
            } catch (ParseException e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new RuntimeException("Please provide time and date of submission");
        }
        return calendar;
    }

    /**
     * Checks is the date of the request is in the past
     *
     * @param submitDate date of request
     */
    public void isSubmitInThePast(Calendar submitDate) {
        Calendar currentDate = Calendar.getInstance();
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
    public boolean isGivenTimeInWorkingHours(Calendar timeAndDateToCheck) {
        if (isWeekend(timeAndDateToCheck))
            return false;
        int hourOfSubmit = timeAndDateToCheck.get(Calendar.HOUR_OF_DAY);
        int minuteOfSubmit = timeAndDateToCheck.get(Calendar.MINUTE);
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
    public Calendar calculateDueDateAndTime(Calendar submitTimeAndDate, int turnaround) {
        int turnaroundInMinutes = turnaround * 60;
        Calendar timeTracker = submitTimeAndDate;
        int timeOfSubmitInMinutes = calculateTimeInMinutes(timeTracker.get(Calendar.HOUR_OF_DAY), (timeTracker.get(Calendar.MINUTE)));
        int firstDaysWorkInMinutes = workdayEndInMinutes - timeOfSubmitInMinutes;
        if (firstDaysWorkInMinutes > turnaroundInMinutes) {
            timeTracker.add(Calendar.MINUTE, turnaroundInMinutes);
            return timeTracker;
        } else {
            timeTracker = skipDay(timeTracker);
            turnaroundInMinutes -= firstDaysWorkInMinutes;
        }
        int daysWork = workdayEndInMinutes - workdayStartInMinutes;
        while (turnaroundInMinutes > 0) {
            if (isWeekend(timeTracker)) {
                timeTracker = skipDay(timeTracker);
            } else if (turnaroundInMinutes >= daysWork) {
                timeTracker = skipDay(timeTracker);
                turnaroundInMinutes -= daysWork;
            } else {
                timeTracker.add(Calendar.MINUTE, turnaroundInMinutes);
                turnaroundInMinutes = 0;
            }
        }
        return timeTracker;
    }

    public Calendar skipDay(Calendar timeTracker) {
        timeTracker.add(Calendar.DATE, 1);
        timeTracker.set(Calendar.HOUR_OF_DAY, startOfWorkdayHours);
        timeTracker.set(Calendar.MINUTE, startOfWorkdayMinutes);
        return timeTracker;
    }

    /**
     * Checks if given date is on weekend
     *
     * @param weekendTracker date to check
     * @return true if date is on weekend
     */
    public boolean isWeekend(Calendar weekendTracker) {
        int weekDay = weekendTracker.get(Calendar.DAY_OF_WEEK);
        return weekDay == 1 || weekDay == 7;
    }
}
