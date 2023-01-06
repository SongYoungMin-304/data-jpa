package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /*
     * 쿼리 메소드 ..
     *
     */
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);


    List<Member> findTop3By();

    //@Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m where m.username =:username")
    List<String> findUsernameList(@Param("username") String username);

    @Query("select new study.datajpa.dto.MemberDto(m.Id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    List<Member> findListssByUsername(String username);

    Member findMemberByUsername(String username);

    Optional<Member> findOptionalByUsername(String username);

}
