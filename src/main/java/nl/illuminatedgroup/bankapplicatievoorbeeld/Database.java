package nl.illuminatedgroup.bankapplicatievoorbeeld;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.BadRequestException;
       
public class Database
{

    private Connection con = null;
    private String host = "jdbc:mysql://localhost:3306/BankApplicatie";
    private String uName = "root";
    private String uPass = "illuminaticonfirmed";
    private Statement stmt = null;
    private static Database instance = null;
    private ResultSet rs;
    private PreparedStatement ps;

    private Database()
    {
        this.connect();
    }

    public static synchronized Database getInstance()
    {
        if (instance == null)
        {
            instance = new Database();
        }
        return instance;
    }

    private void connect()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(host, uName, uPass);
            stmt = con.createStatement();
        }
        catch (Exception ex)
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Kapot in connect methode!");
            //ex.printStackTrace();
        }
    }

    public long getBalance(String rekeningNummer)
    {
        long saldo = 0;
        try
        {
            ps = con.prepareStatement(
                    "SELECT saldo FROM klant WHERE rekeningNummer = ?");
            ps.setString(1, rekeningNummer);
            rs = ps.executeQuery();
            rs.next();
            saldo = rs.getLong("saldo");
        }
        catch (SQLException ex)
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        return saldo;
    }

    public boolean withdraw(String rekeningNummer, long amount)
    {
        try
        {
            ps = con.prepareStatement("SELECT saldo FROM klant WHERE rekeningNummer = ?");
            ps.setString(1, rekeningNummer);
            rs = ps.executeQuery();
            System.out.println("saldo opgehaald");
            rs.next();
            long saldo = rs.getLong("saldo");
            System.out.println("saldo is in variabele saldo");
            System.out.println("amount = " + amount + "saldo = " + saldo);
            if (saldo >= amount)
            {
                System.out.println("Saldo toereikend, mag pinnen.");
                ps = con.prepareStatement("UPDATE klant set saldo = ? WHERE rekeningNummer = ?");
                ps.setLong(1, (saldo - amount));
                ps.setString(2, rekeningNummer);
                ps.executeUpdate();
            }
            else
            {
                System.out.println("Saldo ontoereikend.");
                return false;
            }
            return true;
        }
        catch (SQLException ex)
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            throw new BadRequestException();
        }
    }
}
