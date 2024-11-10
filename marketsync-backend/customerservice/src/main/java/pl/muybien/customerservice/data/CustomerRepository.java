package pl.muybien.customerservice.data;

import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository implements CrudRepository<Customer, Long> {
}
