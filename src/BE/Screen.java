package BE;

import java.util.List;

public class Screen {
    //TODO TEST CLASS.
    private int id;
    private String name;
    private String screenInfo;
    private List<User> assignedUsers;

    public Screen(String name) {
        this.name = name;
    }

    public Screen(String name, String screenInfo){
        this.name = name;
        this.screenInfo = screenInfo;
    }

    public Screen(int id, String screenName, String screenInfo) {
        this.id = id;
        this.name = screenName;
        this.screenInfo = screenInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getScreenInfo() {
        return screenInfo;
    }

    public void setScreenInfo(String screenInfo) {
        this.screenInfo = screenInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(List<User> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public void addUser(User user){
        this.assignedUsers.add(user);
    }

    public void removeUser(User user){
        assignedUsers.remove(user);
    }
}