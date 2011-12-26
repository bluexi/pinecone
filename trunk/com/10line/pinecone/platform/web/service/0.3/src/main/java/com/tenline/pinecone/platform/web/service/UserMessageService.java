/**
 * 
 */
package com.tenline.pinecone.platform.web.service;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.tenline.pinecone.platform.model.UserMessage;

/**
 * @author Bill
 *
 */
@Path("/api/user/message")
public interface UserMessageService extends AbstractService {

	/**
	 * 
	 * @param userMessage
	 * @return
	 */
	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	UserMessage create(UserMessage userMessage);
	
	/**
	 * 
	 * @param userMessage
	 * @return
	 */
	@PUT
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	UserMessage update(UserMessage userMessage);
	
	/**
	 * 
	 * @param filter
	 * @return
	 */
	@GET
	@Path("/show/{filter}")
	@Produces(MediaType.APPLICATION_JSON)
	Collection<UserMessage> show(@PathParam("filter") String filter);
	
	/**
	 * 
	 * @param filter
	 * @return
	 */
	@GET
	@Path("/show/@Sender/{filter}")
	@Produces(MediaType.APPLICATION_JSON)
	Collection<UserMessage> showBySender(@PathParam("filter") String filter);
	
	/**
	 * 
	 * @param filter
	 * @return
	 */
	@GET
	@Path("/show/@Receiver/{filter}")
	@Produces(MediaType.APPLICATION_JSON)
	Collection<UserMessage> showByReceiver(@PathParam("filter") String filter);
	
}
