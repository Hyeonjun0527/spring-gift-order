package gift.config;

import gift.entity.Member;
import gift.entity.MemberRole;
import gift.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminAccountInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;

    public AdminAccountInitializer(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void run(String... args) {
        String adminEmail = "admin@admin";
        String adminPassword = "admin123";

        if (memberRepository.findByEmail(adminEmail).isEmpty()) {
            Member admin = new Member(adminEmail, adminPassword, MemberRole.ADMIN);
            memberRepository.save(admin);
        }
    }
} 