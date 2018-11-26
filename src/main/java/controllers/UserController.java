package controllers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import model.User;
import utils.Hashing;
import utils.Log;


public class UserController {

  private static DatabaseController dbCon;

  public UserController() {
    dbCon = new DatabaseController();
  }

  public static User getUser(int id) {

    // Check for connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Build the query for DB
    String sql = "SELECT * FROM user where id=" + id;

    // Actually do the query
    ResultSet rs = dbCon.query(sql);
    User user = null;

    try {
      // Get first object, since we only have one
      if (rs.next()) {
        user =
                new User(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("password"),
                        rs.getString("email"));

        // return the create object
        return user;
      } else {
        System.out.println("No user found");
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Return null
    return user;
  }

  /**
   * Get all users in database
   *
   * @return
   */
  public static ArrayList<User> getUsers() {

    // Check for DB connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Build SQL
    String sql = "SELECT * FROM user";

    // Do the query and initialyze an empty list for use if we don't get results
    ResultSet rs = dbCon.query(sql);
    ArrayList<User> users = new ArrayList<User>();

    try {
      // Loop through DB Data
      while (rs.next()) {
        User user =
                new User(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("password"),
                        rs.getString("email"));

        // Add element to list
        users.add(user);
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Return the list of users
    return users;
  }

  public static User createUser(User user) {

    // Write in log that we've reach this step
    Log.writeLog(UserController.class.getName(), user, "Actually creating a user in DB", 0);

    // Set creation time for user.
    user.setCreatedTime(System.currentTimeMillis() / 1000L);

    // Check for DB Connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Insert the user in the DB
    // TODO: Hash the user password before saving it. (umiddelbart færdigt)
    int userID = dbCon.insert(
            "INSERT INTO user(first_name, last_name, password, email, created_at) VALUES('"
                    + user.getFirstname()
                    + "', '"
                    + user.getLastname()
                    + "', '"
                    + Hashing.sha(user.getPassword()) //tilføjet hash til password inden det gemmes.
                    + "', '"
                    + user.getEmail()
                    + "', "
                    + user.getCreatedTime()
                    + ")");

    if (userID != 0) {
      //Update the userid of the user before returning
      user.setId(userID);
    } else {
      // Return null if user has not been inserted into database
      return null;
    }

    // Return user
    return user;
  }

  // laver en funktion som kan logge en User ind.
  public static String login(User user) throws SQLException {

    if (dbCon == null) {
      dbCon = new DatabaseController();
    }
    String sql = "SELECT * FROM user WHERE email = '" + user.getEmail() + "' AND password = '" + user.getPassword() + "'";

    ResultSet rs = dbCon.query(sql);
    User foundUser = null;
    try {
      // Loop through DB Data
      while (rs.next()) {
        foundUser =
                new User(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("password"),
                        rs.getString("email"));
      }
      try {
        Algorithm algorithm = Algorithm.HMAC256("secret");
        String token = JWT.create()
                .withIssuer("auth0")
                .withClaim("userId", foundUser.getId())
                .sign(algorithm);
        return token;
      } catch (JWTCreationException exception) {
        return "";
      }
    } catch (Exception e) {
      return "";
    }
  }

  public static Boolean deleteUser(String token) {

    if (dbCon == null) {
      dbCon = new DatabaseController();

    try {
      DecodedJWT jwt = JWT.decode(token);
      int id = jwt.getClaim("userId").asInt();

      String sql = "DELETE FROM user WHERE id= '" + id + "'";
      dbCon.insert(sql);
      return true;
    } catch (JWTDecodeException exception) {
      return null; //Invalid token
    }
  }
  return false;
}

  /**
   * Denne metode updaterer en user.
   * @param user
   * @param token
   * @return
   */
  public static Boolean updateUser(User user, String token){

    if (dbCon == null) {
      dbCon = new DatabaseController(); //Laver en connection til db
    }
    try{
      DecodedJWT jwt = JWT.decode(token); //trækker userId ud af token og den sættes til en int, så han kun kan ændre sin egen profil med tilsvarende userId i db
      int id = jwt.getClaim("userId").asInt();

      try {
        PreparedStatement updateUser = dbCon.getConnection().prepareStatement("UPDATE user SET" +
                "first_name = ?, last_name = ?, password = ?, email = ? WHERE id=? "); //laver en sql statement, så man kan ændre i sine data
        updateUser.setString(1, user.getFirstname());
        updateUser.setString(2, user.getLastname());
        updateUser.setString(3, user.getPassword());
        updateUser.setString(4, user.getEmail());
        updateUser.setInt(5, id);

        int rowsAffected = updateUser.executeUpdate(); //eksekverer de updateringer user har foretager sig og gemmer i db.

        if (rowsAffected == 1){ //returnerer hvis der er lavet ændringer.
          return true;
        }

      }catch (SQLException exception){
        exception.printStackTrace();
      }
    }catch (JWTDecodeException exception){
      exception.printStackTrace();
    }
    return false; //returnerer flase, hvis der ikke er foretaget nogle ændringer.
  }
}


