package com.jfb.lecture5;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfb.lecture5.model.BusTicket;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.Set;

public class Main {
    private static int ticketAmount = 0;
    private static int validTicketAmount = 0;
    private static int timeTypeErrorCounter = 0;
    private static int dateErrorCounter = 0;
    private static int priceErrorCounter = 0;
    private static String mostPopularViolation;

    public static void main(String[] args) throws JsonProcessingException {
        int x = 0;

        do {
            String input = getInput();
            BusTicket busTicket = new ObjectMapper().readValue(input, BusTicket.class);

            validateTicket(busTicket);

            System.out.println(busTicket.toString());
            x++;

        } while (x < 5);
        printOutput();
    }

    private static String getInput() {
        return new Scanner(System.in).nextLine();
    }

    private static void validateTicket(BusTicket ticket) {
        boolean acceptablePrice = true;
        boolean acceptableStartDate = true;
        boolean acceptableType = true;

        if (!validateTicketPrice(ticket)) {
            priceErrorCounter++;
            acceptablePrice = false;
        }
        if (!validateTicketStartDate(ticket)) {
            dateErrorCounter++;
            acceptableStartDate = false;
        }
        if (!validateTicketType(ticket)) {
            timeTypeErrorCounter++;
            acceptableType = false;
        }
        if (acceptablePrice == true && acceptableStartDate == true && acceptableType == true) {
            validTicketAmount++;
        }
        ticketAmount++;
    }

    private static void printOutput() {
        System.out.println("Total = {" + ticketAmount + "}");
        System.out.println("Valid = {" + validTicketAmount + "}");
        if (timeTypeErrorCounter > dateErrorCounter && timeTypeErrorCounter > priceErrorCounter) {
            mostPopularViolation = "ticket type";
        } else if (dateErrorCounter > timeTypeErrorCounter && dateErrorCounter > priceErrorCounter) {
            mostPopularViolation = "start date";
        }
        else if (priceErrorCounter > dateErrorCounter && priceErrorCounter > timeTypeErrorCounter) {
            mostPopularViolation = "price";
        }
        else {
            mostPopularViolation = "multiple violations with equal frequency";
        }
        System.out.println("Most popular violation = {" + mostPopularViolation + "}");
    }

    private static boolean validateTicketPrice(BusTicket ticket) {
        try {
            if (ticket.getPrice() == null || ticket.getPrice().isEmpty()) {
                throw new IllegalArgumentException("price can not be empty.");
            }

            int price = Integer.parseInt(ticket.getPrice());

            if (price % 2 != 0 || price < 2) {
                throw new IllegalArgumentException("wrong price.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e);
            return false;
        }
        return true;
    }

    private static boolean validateTicketStartDate(BusTicket busTicket) {
        if (busTicket.getStartDate() == null || busTicket.getStartDate().isEmpty()) {
            return false;
        }
        try {
            LocalDate startDate = LocalDate.parse(busTicket.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            if (startDate.isAfter(LocalDate.now())) {
                System.err.println("Error: Start date can not be in the future.");
                return false;
            }
        } catch (DateTimeParseException e) {
            System.err.println("Error: Start date format should be yyyy-MM-dd.");
            return false;
        }
        return true;
    }

    private static boolean validateTicketType(BusTicket busTicket) {
        Set<String> validTicketTypes = Set.of("DAY", "WEEK", "MONTH", "YEAR");
        if (busTicket.getTicketType() == null || !validTicketTypes.contains(busTicket.getTicketType())) {
            System.err.println("Error: type should be : " + validTicketTypes);
            return false;
        }
        return true;
    }

}
