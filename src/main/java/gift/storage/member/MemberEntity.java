package gift.storage.member;

import gift.domain.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "member")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String kakaoAccessToken;
    private int point;

    protected MemberEntity() {
    }

    public Member toDomain() {
        return new Member(id, email, password, kakaoAccessToken, point);
    }

    public static MemberEntity from(Member member) {
        MemberEntity entity = new MemberEntity();
        entity.email = member.getEmail().value();
        entity.password = extractPassword(member);
        entity.kakaoAccessToken = member.getKakaoAccessToken();
        entity.point = member.getPoint().value();
        return entity;
    }

    public void update(Member member) {
        this.email = member.getEmail().value();
        this.password = extractPassword(member);
        this.kakaoAccessToken = member.getKakaoAccessToken();
        this.point = member.getPoint().value();
    }

    private static String extractPassword(Member member) {
        if (member.getPassword() == null) {
            return null;
        }
        return member.getPassword().value();
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public int getPoint() {
        return point;
    }
}
