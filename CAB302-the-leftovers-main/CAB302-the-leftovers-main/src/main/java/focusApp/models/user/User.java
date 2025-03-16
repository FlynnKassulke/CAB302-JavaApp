package focusApp.models.user;

import javax.print.attribute.standard.RequestingUserName;

public class User {
    private int id;
    private String userName;
    private boolean parentalLock;
    private long totalFocusTime;

    public User(String userName, long totaFocusTime) {
        this.userName = userName;
        this.totalFocusTime = totaFocusTime;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String name) {
        this.userName = name;
    }

    public boolean getParentalLcok() {
        return parentalLock;
    }

    public void setParentalLock(boolean parentalLock) {
        this.parentalLock = parentalLock;
    }

    public long getTotalFocusTime() {
        return totalFocusTime;
    }

    public void setTotalFocusTime(long totalFocusTime) {
        this.totalFocusTime = totalFocusTime;
    }
}
