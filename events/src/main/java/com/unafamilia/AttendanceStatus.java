package com.unafamilia;

public enum AttendanceStatus {
    TENTATIVE("tentative"),
    ABSENT("absent"),
    WAITLIST("waitlist"),
    DECLINED("declined"),
    ACCEPTED("accepted"); 


    private String status;

    private AttendanceStatus(String status) {
        this.status = status;
    }

    public String getValue() {
        return this.status;
    }
}
