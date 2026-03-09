package edu.cit.auditor.paluto.repository;

import edu.cit.auditor.paluto.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    public boolean findByEmail(String email);
}
