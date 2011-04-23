/**
 * 
 */
package com.tenline.pinecone.rest;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.tenline.pinecone.model.User;
import com.tenline.pinecone.persistence.UserDao;
import com.tenline.pinecone.rest.impl.UserServiceImpl;

/**
 * @author Bill
 *
 */
@RunWith(MockitoJUnitRunner.class)  
public class UserServiceTest {

	private User user;
	
	private UserServiceImpl userService; 	
	
	@Mock
	private UserDao userDao;
		
	@Before
	public void testSetup() {
		userService = new UserServiceImpl(userDao);
		user = new User();
		user.setPrimaryKey("asa");
		user.setName("bill");		
	}
	
	@After
	public void testShutdown() {	
		userService = null;
		userDao = null;
		user = null;
	}

	@Test
	public void testCreate() {
		Response result = userService.create(user);
		ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);  
		verify(userDao).save(argument.capture()); 
		assertEquals("bill", argument.getValue().getName());
		assertEquals(200, result.getStatus());
	}
	
	@Test
	public void testShow() {
		when(userDao.find(user.getPrimaryKey())).thenReturn(user);
		User result = userService.show(user.getPrimaryKey());
		verify(userDao).find(user.getPrimaryKey());
		assertEquals("bill", result.getName());
	}

}
