package com.cbsexam;

import cache.UserCache;
import com.google.gson.Gson;
import controllers.UserController;
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


    // TODO: Add Encryption to JSON (færdig, men udkommenteret)
    // Convert the user object to json in order to return the object
    String json = new Gson().toJson(user);

    //json = Encryption.encryptDecryptXOR(json);

    // Return the user with the status code 200
    // TODO: What should happen if something breaks down?
    return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
  }

  /** @return Responses */
  @GET
  @Path("/")
  public Response getUsers() {

    // Write to log that we are here
    Log.writeLog(this.getClass().getName(), this, "Get all users", 0);

    // Get a list of users
    //ArrayList<User> users = UserController.getUsers();

      ArrayList<User> users = userCache.getUsers(false);

    // TODO: Add Encryption to JSON (færdig, men udkommenteret)
    // Transfer users to json in order to return it to the user
    String json = new Gson().toJson(users);

    //json = Encryption.encryptDecryptXOR(json);
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

  // TODO: Make the system able to login users and assign them a token to use throughout the system.
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response loginUser(String body) {

      String user = new Gson().toJson(body, User.class);
      UserController.class.login(user);

      if (body.equals(email) && body.equals(password)) {
          return Response.status(200).entity("Login granted").build();
      }else
          return Response.status(400).entity("Login failed").build();

    // Return a response with status 200 and JSON as type
    // return Response.status(400).entity("Endpoint not implemented yet").build();
  }

  // TODO: Make the system able to delete users
  public Response deleteUser(String x) {

    // Return a response with status 200 and JSON as type
    return Response.status(400).entity("Endpoint not implemented yet").build();
  }

  // TODO: Make the system able to update users
  public Response updateUser(String x) {

    // Return a response with status 200 and JSON as type
    return Response.status(400).entity("Endpoint not implemented yet").build();
  }
}
