package com.swiftparcel.customerportal.model.enums;

public enum TimeSlot {
    MORNING(8,12),
    AFTERNOON(12,17),
    EVENING(17, 20);

    private final int startingHour;
    private final int endingHour;

    private TimeSlot(int startingHour, int endingHour){
        this.startingHour = startingHour;
        this.endingHour = endingHour;
    }

    public int getStartingHour() {
        return startingHour;
    }

    public int getEndingHour() {
        return endingHour;
    }
}