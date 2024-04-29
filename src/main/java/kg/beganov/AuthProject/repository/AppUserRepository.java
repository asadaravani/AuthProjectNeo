package kg.beganov.AuthProject.repository;

import kg.beganov.AuthProject.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findUserByEmail(String userEmail);
    Optional<AppUser> findUserByUsername(String username);
}
