package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Test
    public void testMember(){
        Member member = new Member("memberA");
        Member saveMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(saveMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }
    
    @Test
    public void basicCrud(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        // 카운트 검증
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen(){
        Member m1 = new Member("aaa",10);
        Member m2 = new Member("aaa",20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> list = memberRepository.findByUsernameAndAgeGreaterThan("aaa",10);

        assertThat(list.size()).isEqualTo(1);

    }


    @Test
    public void findTop3(){
        List<Member> top3By = memberRepository.findTop3By();

    }

    @Test
    public void testNamedQuery(){
        Member m1 = new Member("aaa",10);
        Member m2 = new Member("aaba",20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> list = memberRepository.findByUsername("aaa");

        assertThat(list.size()).isEqualTo(1);

    }

    @Test
    public void QueryMapping(){
        Member m1 = new Member("aaa",10);
        Member m2 = new Member("aaba",20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> list = memberRepository.findUser("aaa", 10);

        assertThat(list.size()).isEqualTo(1);


    }

    @Test
    public void findUserName(){
        Member m1 = new Member("aaa",10);
        Member m2 = new Member("aaba",20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> list = memberRepository.findUsernameList("aaa");

        for (String s : list) {
            System.out.println(s);
        }

    }

    @Test
    public void findMemberDto(){

        Team t1 = new Team("team");
        teamRepository.save(t1);

        Member m1 = new Member("aaa",10);
        m1.setTeam(t1);
        memberRepository.save(m1);


        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for(MemberDto dto : memberDto){
            System.out.println("songyoungmin"+ dto);
        }
    }

    @Test
    public void findByNames(){
        Member m1 = new Member("aaa",10);
        Member m2 = new Member("aaba",20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> list = memberRepository.findByNames(Arrays.asList("aaa","aaba"));

        assertThat(list.size()).isEqualTo(2);

    }

    @Test
    public void returnType(){

        Member m1 = new Member("aaa",10);
        Member m2 = new Member("aaba",20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> listss = memberRepository.findListssByUsername("222aaa");
        System.out.println("result = "+ listss.size());  // 절대 Null 이 아님..

        Optional<Member> aaa = memberRepository.findOptionalByUsername("aaa");
        System.out.println("Optional"+ aaa);
        // 만약 결과가 두개면 Exception 이 발생함..

        Member aaa1 = memberRepository.findMemberByUsername("aaa");
        System.out.println("result2"+ aaa1); // 단건인 경우에는 null
    }



}
