/**
 * 
 */
package com.tenline.pinecone.platform.web.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tenline.pinecone.platform.model.User;

/**
 * @author Bill
 *
 */
public interface UserRepository extends JpaRepository<User, Long> {}
