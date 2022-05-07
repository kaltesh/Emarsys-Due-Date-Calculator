package com.company;

import java.util.Calendar;
import java.util.Date;

public class Main {

    public static void main(String[] args) {
        int START_OF_WORKDAY_HOURS = 9;
        int START_OF_WORKDAY_MINUTES = 0;
        int END_OF_WORKDAY_HOURS = 17;
        int END_OF_WORKDAY_MINUTES = 0;
        int turnAround = 2;
        String submitTimeAndDate = "09/15/03/06/2022";
        Calculator calculator = new Calculator(START_OF_WORKDAY_HOURS, START_OF_WORKDAY_MINUTES,
                END_OF_WORKDAY_HOURS, END_OF_WORKDAY_MINUTES);

        Calendar submitDate = calculator.timeDateConverter(submitTimeAndDate);

        calculator.isSubmitInThePast(submitDate);

        boolean isRequestTimeInWorkingHours = calculator.isGivenTimeInWorkingHours(submitDate);
        if (!isRequestTimeInWorkingHours) {
            System.out.println("Please submit your request during working hours");
            System.exit(0);
        }

        Calendar endDate = calculator.calculateDueDateAndTime(submitDate, turnAround);
        System.out.println("Estimated time and date for request completion:\n" + endDate.getTime());
    }
}
