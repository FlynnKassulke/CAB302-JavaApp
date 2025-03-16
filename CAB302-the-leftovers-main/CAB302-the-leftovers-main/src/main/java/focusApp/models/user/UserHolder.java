package focusApp.models.user;

/**
 * singleton class used to hold the user class for usage between files
 */
public final class UserHolder {
    private static UserHolder Instance;
    private User user;

    private UserHolder() {}

    public static UserHolder getInstance() {
        if (Instance == null) {
            Instance = new UserHolder();
        }
        return Instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User setUser) {
       user = setUser;
    }
}
