import java.util.*;
import java.sql.*;
import java.time.*;

public class VaccineCaregiver {

    public SqlConnectionManager sqlClient;
    public String sqltext;
    public String caregiverId;


    public VaccineCaregiver(String name) throws Exception {
        this.sqlClient = new SqlConnectionManager(
                System.getenv("Server"),
                System.getenv("DBName"),
                System.getenv("UserID"),
                System.getenv("Password")
        );

        try {
            sqlClient.openConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        sqltext = "INSERT INTO CareGivers (CaregiverName) VALUES ('" + name + "')";
        caregiverId = "";
        try {
            sqlClient.executeUpdate(sqltext);
            ResultSet rs = sqlClient.executeQuery("SELECT @@IDENTITY AS 'Identity'; ");
            caregiverId = (rs.getString(1));
            System.out.println(caregiverId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<LocalDate> weeksToSchedule = new ArrayList<>();
        LocalDate now = LocalDate.now();
        weeksToSchedule.add(now);
        int weeksAhead = 4;
        int lcv = 0;
        LocalDate nextWeek = now;
        List<Integer> hoursToSchedule =new ArrayList<Integer>();
        hoursToSchedule.add(10);
        hoursToSchedule.add(11);
        int appointmentDuration = 15;


        while (lcv < weeksAhead) {
            nextWeek = nextWeek.plusWeeks(1);
            weeksToSchedule.add(nextWeek);
            lcv += 1;
        }

        String formatString = "%Y-%m-%d";
        for (LocalDate localDate : weeksToSchedule) {
            String date = localDate.toString();

            for (int hr : hoursToSchedule) {
                int startTime = 0;
                while (startTime < 60) {
                    String sqltext2 = ("INSERT INTO CareGiverSchedule (caregiverid, WorkDay, SlotHour, SlotMinute) VALUES (");
                    sqltext2 += caregiverId + ", '"  + date + "', ";
                    sqltext2 += hr + ", ";
                    sqltext2 += startTime + ")";

                    try {
                        sqlClient.executeUpdate(sqltext2);
                        startTime += appointmentDuration;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }



            }


        }

    }
}