package edu.cit.auditor.paluto.core.repositories;

import edu.cit.auditor.paluto.core.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    public boolean findByEmail(String email);
}
