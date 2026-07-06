package rougelite.BusinessLogicLayer.logic;

public class EnterUserName {

    private static String userName = "";

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String enteredName) {
        userName = enteredName;
    }
}
