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

import com.tenline.pinecone.platform.model.Transaction;

/**
 * @author Bill
 *
 */
@Path("/api/transaction")
public interface TransactionService extends AbstractService {

	/**
	 * 
	 * @param transaction
	 * @return
	 */
	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Transaction create(Transaction transaction);
	
	/**
	 * 
	 * @param transaction
	 * @return
	 */
	@PUT
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Transaction update(Transaction transaction);
	
	/**
	 * 
	 * @param filter
	 * @return
	 */
	@GET
	@Path("/show/{filter}")
	@Produces(MediaType.APPLICATION_JSON)
	Collection<Transaction> show(@PathParam("filter") String filter);
	
	/**
	 * 
	 * @param filter
	 * @return
	 */
	@GET
	@Path("/show/@User/{filter}")
	@Produces(MediaType.APPLICATION_JSON)
	Collection<Transaction> showByUser(@PathParam("filter") String filter);
	
	/**
	 * 
	 * @param filter
	 * @return
	 */
	@GET
	@Path("/show/@Consumer/{filter}")
	@Produces(MediaType.APPLICATION_JSON)
	Collection<Transaction> showByConsumer(@PathParam("filter") String filter);
	
}
