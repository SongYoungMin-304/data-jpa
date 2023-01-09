package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    @Autowired
    MemberQueryRepository memberQueryRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember(){
        Member member = new Member("memberA");
        Member saveMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(saveMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
       // assertThat(findMember).isEqualTo(member);
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

    @Test
    public void pageQuery(){
        Member m1 = new Member("aaa",10);
        Member m2 = new Member("BBB",20);
        Member m3 = new Member("ccc",30);
        Member m4 = new Member("ddd",40);

        Member m5 = new Member("ddd1",40);
        Member m6 = new Member("ddd1",40);
        Member m7 = new Member("ddd1",40);
        Member m8 = new Member("ddd1",40);
        Member m9 = new Member("ddd1",40);
        Member m10 = new Member("ddd1",40);


        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m2);
        memberRepository.save(m3);
        memberRepository.save(m4);
        memberRepository.save(m5);
        memberRepository.save(m6);
        memberRepository.save(m7);
        memberRepository.save(m8);
        memberRepository.save(m9);


        PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "username");


        // Page, Slice, List 전부 다 가능
        Page<Member> byAge = memberRepository.findByAge(40, pageRequest);
        //long totalElements = byAge.getTotalElements();

        // Entity 를 직접 노출하면 안된다....
        // dto로 쉽게 변환..
        Page<MemberDto> map = byAge.map(member -> new MemberDto(member.getId(), member.getUsername(), null));



        // then
        List<Member> content = byAge.getContent();

        assertThat(content.size()).isEqualTo(3);
        assertThat(byAge.getTotalElements()).isEqualTo(6);
        assertThat(byAge.getNumber()).isEqualTo(0);  // 페이지 번호
        assertThat(byAge.getTotalPages()).isEqualTo(2);
        assertThat(byAge.isFirst()).isTrue();
        assertThat(byAge.hasNext()).isTrue();


        for (Member member : content) {
            System.out.println("member" + member);
        }

//        System.out.println("totalElement "+ totalElements);

/*
        long count = memberRepository.totalCount(10);

        assertThat(byPage.size()).isEqualTo(1);
*/

    }


    @Test
    public void bulkUpdate(){
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 20));
        memberRepository.save(new Member("member3", 30));
        memberRepository.save(new Member("member4", 40));
        memberRepository.save(new Member("member5", 50));
        memberRepository.save(new Member("member6", 60));

        int resultCOunt = memberRepository.bulkAgePlus(20);

        // 영속성 컨테이너를 날려버려야지 영속성 관리됨 영속성이랑.. db랑 고립관계가 있기 때문에 flush 등을 해야함
        //em.flush();
        //em.clear();

        List<Member> member5 = memberRepository.findByUsername("member5");

        System.out.println("테스트영민"+ member5.get(0));

        assertThat(resultCOunt).isEqualTo(5);

    }

    @Test
    public void findMemberLazy(){
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush(); // 반영
        em.clear(); // 영속성 컨테스트 날려버림

        //when N + 1
        //select Member 1
        //team N(Member 의 갯수가 N)
//        List<Member> all = memberRepository.findMemberFetchJoin();

        // entity graph 를 쓰면 fetch join 을 편리하게 사용할 수 있다..

        List<Member> all = memberRepository.findEntityGraphByUsername("member1");

        for (Member member : all) {
            System.out.println("member = "+ member.getUsername());
            System.out.println("memeber.teamClass ="+ member.getTeam().getClass());
            System.out.println("member.team = "+member.getTeam().getName());
        }


    }

    @Test
    public void queryHint(){
        // given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush(); // 영속성을 db에 동기화
        em.clear(); // 영속성을 날려버림

        //when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        System.out.println("test1"+findMember.getUsername());

        em.flush();
        em.clear();
        Member findMember2 = memberRepository.findReadOnlyByUsername("member1");

        System.out.println("test2"+findMember2.getUsername());


        em.flush(); // 변경감지..
    }
    
    @Test
    public void lock(){
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush(); // 영속성을 db에 동기화
        em.clear(); // 영속성을 날려버림

        //when
        List<Member> result = memberRepository.findLockByUsername("member1");


    }

    @Test
    public void callCustom(){
        List<Member> memberCustom = memberRepository.findMemberCustom();
    }

    @Test
    public void specBasic(){

        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);

        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        Assertions.assertThat(result.size()).isEqualTo(1);


    }

    @Test
    public void queryByExample(){
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);

        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        //Probe
        Member member = new Member("m1");
        Team team = new Team("teamA");
        member.setTeam(team);

        ExampleMatcher matching = ExampleMatcher.matching();
        matching
                .withIgnoreCase("age");

        Example<Member> example = Example.of(member, matching);

        List<Member> all = memberRepository.findAll(example);

        Assertions.assertThat(all.get(0).getUsername()).isEqualTo("m1");
    }


}
