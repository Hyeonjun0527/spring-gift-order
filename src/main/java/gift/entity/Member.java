package gift.entity;

import gift.exception.InvalidPasswordException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Entity
@Table(name = "member", uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String role;

    @OneToMany(mappedBy = "member")
    private List<WishItem> wishItems = new ArrayList<>();

    protected Member() {}

    public Member(Long id, String email, String password, String role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Member(String email, String password, String role) {
        this(null, email, password, role);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public List<WishItem> getWishItems() {
        return wishItems;
    }

    public void updatePassword(String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new InvalidPasswordException("Password cannot be null or empty");
        }
        this.password = Base64.getEncoder().encodeToString(newPassword.getBytes());
    }

    @Override
    public String toString() {
        return "Member(" + id + ") - email: " + email;
    }

}
