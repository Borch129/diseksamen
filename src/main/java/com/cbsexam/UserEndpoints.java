package com.cbsexam;

import cache.UserCache;
import com.google.gson.Gson;
import controllers.UserController;

import java.sql.SQLException;
import java.util.ArrayList;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.User;
import utils.Log;

@Path("user")
public class UserEndpoints {

    // laves så der ikke laves en ny cache hver gang men der kaldes en tidligere cache
  static UserCache userCache = new UserCache();

  /**
   * @param idUser
   * @return Responses
   */
  @GET
  @Path("/{idUser}")
  public Response getUser(@PathParam("idUser") int idUser) {

    // Use the ID to get the user from the controller.
    User user = UserController.getUser(idUser);

    if (user == null){    //hvis der ikke kan findes en bruger sendes denne status 400 besked tilbage.
      return Response.status(400).type(MediaType.TEXT_PLAIN).entity("Something went wrong, can't get user").build();
    }

    // TODO: Add Encryption to JSON (FIX, men udkommenteret)
    // Convert the user object to json in order to return the object
    String json = new Gson().toJson(user);

    //json = Encryption.encryptDecryptXOR(json); //skal udkommenteres

    // Return the user with the status code 200
    // TODO: What should happen if something breaks down? (FIX)
    return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
  }

  /** @return Responses */
  @GET
  @Path("/")
  public Response getUsers() {

    // Write to log that we are here
    Log.writeLog(this.getClass().getName(), this, "Get all users", 0);

    // Get a list of users
    ArrayList<User> users = userCache.getUsers(false); //gør brug af chachinglayer når jeg henter users

    // TODO: Add Encryption to JSON (FIX, men udkommenteret)
    // Transfer users to json in order to return it to the user
    String json = new Gson().toJson(users);
    //json = Encryption.encryptDecryptXOR(json); //skal udkommenteres

    // Return the users with the status code 200
    return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
  }

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createUser(String body) {

    // Read the json from body and transfer it to a user class
    User newUser = new Gson().fromJson(body, User.class);

    // Use the controller to add the user
    User createUser = UserController.createUser(newUser);

    // Get the user back with the added ID and return it to the user
    String json = new Gson().toJson(createUser);

    // Return the data to the user
    if (createUser != null) {
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("Could not create user").build();
    }
  }

  // TODO: Make the system able to login users and assign them a token to use throughout the system. (umiddelbart færdig)
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response loginUser(String body) throws SQLException {

      User user = new Gson().fromJson(body, User.class);

      String token = UserController.login(user);

    if (token !="") {
     // String o = new Gson().toJson(user); //sørger for jeg retunerer en bruger

      //return Response.status(200).entity(o).build();
      return Response.status(200).entity(token).build();
    }else
      return Response.status(400).entity("Login failed").build();

    // Return a response with status 200 and JSON as type
    // return Response.status(400).entity("Endpoint not implemented yet").build();
  }

  // TODO: Make the system able to delete users (umiddelbart færdig)
  @DELETE
  @Path("/{userId}/{token}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response deleteUser(@PathParam("userId")int userId, @PathParam("token") String token) {

    Boolean deleted = UserController.deleteUser(token);

    if(deleted) {
      return Response.status(200).entity("User deleted").build();
    }else
      return Response.status(400).entity("User not deleted").build();

  }

  // TODO: Make the system able to update users (umiddelbart færdig)
  @PUT
  @Path("/{userId}/{token}")
  public Response updateUser( @PathParam("token") String token, String body) {

    User user = new Gson().fromJson(body, User.class);

    Boolean updated = UserController.updateUser(user, token);

    if (updated){
      return Response.status(200).entity("User is updated").build();
    }else
    return Response.status(400).entity("Something went wrong, could not update user").build();
  }
}
