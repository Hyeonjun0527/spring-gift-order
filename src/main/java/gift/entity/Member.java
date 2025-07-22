package gift.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "member") // 테이블 이름을 members로 지정 -> 굳이 안해도 됨
public class Member {

    @Id // PK 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 ID 설정
    private Long id;

    @Column(nullable = false, unique = true) // null & 중복 불가
    private String email;

    @Column(nullable = false) //null 불가
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    public Member() {}

    public Member(String email, String password, MemberRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public MemberRole getRole() { return role; }
    public void setRole(MemberRole role) {this.role = role;}
}
    
