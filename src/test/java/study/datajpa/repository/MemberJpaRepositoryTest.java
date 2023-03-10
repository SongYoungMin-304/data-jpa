package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember(){
        Member member = new Member("memberA");
        Member saveMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(saveMember.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        Member findMember1 = memberJpaRepository.findByID(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findByID(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트조회 검증
        List<Member> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        // 카운트 검증
        long deletedCount = memberJpaRepository.count();
        assertThat(deletedCount).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThen(){
        Member m1 = new Member("aaa",10);
        Member m2 = new Member("aaa",20);

        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> list = memberJpaRepository.findByUsernameAndAgeGreaterThen("aaa",10);

        assertThat(list.size()).isEqualTo(1);

    }

    @Test
    public void testNamedQuery(){
        Member m1 = new Member("aaa",10);
        Member m2 = new Member("BBB",20);

        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> result = memberJpaRepository.findByUsername("aaa");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);

    }

    @Test
    public void pageQuery(){
        Member m1 = new Member("aaa",10);
        Member m2 = new Member("BBB",20);

        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);


        List<Member> byPage = memberJpaRepository.findByPage(10, 0, 2);

        long count = memberJpaRepository.totalCount(10);

        assertThat(byPage.size()).isEqualTo(1);
        assertThat(count).isEqualTo(1);

    }

    @Test
    public void bulkUpdate(){
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 20));
        memberJpaRepository.save(new Member("member3", 30));
        memberJpaRepository.save(new Member("member4", 40));
        memberJpaRepository.save(new Member("member5", 50));
        memberJpaRepository.save(new Member("member6", 60));

        int resultCOunt = memberJpaRepository.bulkAgePlus(20);

        assertThat(resultCOunt).isEqualTo(5);

    }

}