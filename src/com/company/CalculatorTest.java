package com.company;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    int START_OF_WORKDAY_HOURS = 9;
    int START_OF_WORKDAY_MINUTES = 0;
    int END_OF_WORKDAY_HOURS = 17;
    int END_OF_WORKDAY_MINUTES = 0;
    Calculator calculator = new Calculator(START_OF_WORKDAY_HOURS, START_OF_WORKDAY_MINUTES,
            END_OF_WORKDAY_HOURS, END_OF_WORKDAY_MINUTES);


    @Test
    @DisplayName("Testing converting date and time with valid input")
    void testConvertingWithValidInput() {
        Date date;
        SimpleDateFormat sdFormat = new SimpleDateFormat("HH/mm/dd/MM/yyyy");
        try {
            date = sdFormat.parse("16/15/25/05/2022");
            assertEquals(date, calculator.timeDateConverter("16/15/25/05/2022"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Testing converting date and time with invalid input")
    public void testConvertingWithInvalidInput() {
        Throwable exception = assertThrows(
                RuntimeException.class, () -> calculator.timeDateConverter("1615/25/05/2022"));
        assertEquals("Unparseable date: \"1615/25/05/2022\"", exception.getMessage());
    }

    @Test
    @DisplayName("Testing converting date and time without input")
    public void testConvertingWithoutInput() {
        Throwable exception = assertThrows(
                RuntimeException.class, () -> calculator.timeDateConverter(""));
        assertEquals("Please provide time and date of submission", exception.getMessage());
    }

    @Test
    @DisplayName("Testing if submission is in the past")
    public void testWithPastSubmit() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, Calendar.DECEMBER, 31, 59, 59, 59);
        Date submitDate = calendar.getTime();
        Throwable exception = assertThrows(
                RuntimeException.class, () -> calculator.isSubmitInThePast(submitDate));
        assertEquals("Please provide a future date for submission", exception.getMessage());
    }

    @Test
    @DisplayName("Testing conversion of time of the day into minutes 1")
    public void calculateTimeInMinutesTest1() {
        assertEquals(404, calculator.calculateTimeInMinutes(6, 44));
    }

    @Test
    @DisplayName("Testing conversion of time of the day into minutes 2")
    public void calculateTimeInMinutesTest2() {
        assertEquals(0, calculator.calculateTimeInMinutes(0, 0));
    }

    @Test
    @DisplayName("Testing if given time is in working hours")
    public void isGivenTimeInWorkingHoursTest() {
        Date date;
        SimpleDateFormat sdFormat = new SimpleDateFormat("HH/mm/dd/MM/yyyy");
        try {
            date = sdFormat.parse("16/15/25/05/2022");
            assertTrue(calculator.isGivenTimeInWorkingHours(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Testing if given time is not in working hours")
    public void isGivenTimeNotInWorkingHoursTest() {
        Date date;
        SimpleDateFormat sdFormat = new SimpleDateFormat("HH/mm/dd/MM/yyyy");
        try {
            date = sdFormat.parse("19/15/25/05/2022");
            assertFalse(calculator.isGivenTimeInWorkingHours(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Testing due date calculation is correct with a short period")
    public void calculateDueDateAndTimeTestShort() {
        Date submitDateAndTime;
        Date dueDateAndTime;
        int turnaround = 2;
        SimpleDateFormat sdFormat = new SimpleDateFormat("HH/mm/dd/MM/yyyy");
        try {
            submitDateAndTime = sdFormat.parse("9/15/25/05/2022");
            dueDateAndTime = sdFormat.parse("11/15/25/05/2022");
            assertEquals(dueDateAndTime, calculator.calculateDueDateAndTime(submitDateAndTime, turnaround));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Testing due date calculation is correct with a 4 days long period")
    public void calculateDueDateAndTimeTestLong() {
        Date submitDateAndTime;
        Date dueDateAndTime;
        int turnaround = 32;
        SimpleDateFormat sdFormat = new SimpleDateFormat("HH/mm/dd/MM/yyyy");
        try {
            submitDateAndTime = sdFormat.parse("10/15/23/05/2022");
            dueDateAndTime = sdFormat.parse("10/15/27/05/2022");
            assertEquals(dueDateAndTime, calculator.calculateDueDateAndTime(submitDateAndTime, turnaround));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Testing due date calculation is correct with a period over weekend")
    public void calculateDueDateAndTimeTestWeekend() {
        Date submitDateAndTime;
        Date dueDateAndTime;
        int turnaround = 80;
        SimpleDateFormat sdFormat = new SimpleDateFormat("HH/mm/dd/MM/yyyy");
        try {
            submitDateAndTime = sdFormat.parse("10/15/23/05/2022");
            dueDateAndTime = sdFormat.parse("10/15/06/06/2022");
            assertEquals(dueDateAndTime, calculator.calculateDueDateAndTime(submitDateAndTime, turnaround));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
