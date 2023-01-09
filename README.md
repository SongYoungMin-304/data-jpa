# 2023.01.05
**ENTITY 설계**

![image](https://user-images.githubusercontent.com/56577599/210739176-5c2395de-ea1c-49c2-8f69-b08751660291.png)

JPA DATA란? 

![image](https://user-images.githubusercontent.com/56577599/210739265-99920768-1d13-4cca-ae7d-2d8d5ee98378.png)

→ 인터페이스를 상속 받음으로써 기본 CRUD 기능 제공

**AS-IS**

```java
package study.datajpa.repository;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Team;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
@Repository
public class TeamJpaRepository {
 @PersistenceContext
   private EntityManager em;
   public Team save(Team team) {
   em.persist(team);
   return team;
 }
 public void delete(Team team) {
   em.remove(team);
 }
 public List<Team> findAll() {
   return em.createQuery("select t from Team t”, Team.class)
          .getResultList();
 }
 public Optional<Team> findById(Long id) {
   Team team = em.find(Team.class, id);
   return Optional.ofNullable(team);
 }
 public long count() {
   return em.createQuery("select count(t) from Team t”, Long.class)
       .getSingleResult();
 }
}
```

**TO-BE**

```java
public interface MemberRepository extends JpaRepository<Member, Long> {
}
```

![image](https://user-images.githubusercontent.com/56577599/210739405-1eb6b9ce-8de9-4fc7-985e-7ed696d0077c.png)


#2023.01.08

### 1. 메소드 이름으로 쿼리 생성

**순수 JPA**

```java
public List<Member> findByUsernameAndAgeGreaterThan(String username, int age) {
    return em.createQuery("select m from Member m where m.username = :username 
     and m.age > :age")
     .setParameter("username", username)
     .setParameter("age", age)
     .getResultList();
}
```

**JPA DATA**

```java
public interface MemberRepository extends JpaRepository<Member, Long> {
   List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
}
```

—> 메소드 이름을 분석하여서 JPQL을 생성하고 실행할 수 있다.

### 2. JPA NamedQuery

—> entity 에 @NamedQuery 으로 쿼리 정의

```java
Entity
@NamedQuery(
     name="Member.findByUsername",
     query="select m from Member m where m.username = :username")
    public class Member {
 ...
    }
```

**순수 JPA**

```java
public class MemberRepository {
 public List<Member> findByUsername(String username) {
   ...
   List<Member> resultList =
   em.createNamedQuery("Member.findByUsername", Member.class)
   .setParameter("username", username)
   .getResultList();
 }
}
```

**JPA DATA**

```java
Query(name = "Member.findByUsername")
  List<Member> findByUsername(@Param("username") String username);
```

—> 엔티티에 정의해 놓은 쿼리 실행을 통해서 처리

- 우선순위 NamedQuery → 메소드 이름으로 쿼리 생성 전략
- 보통 엔티티에 쿼리를 정의하지 않고 리파지토리 메소드에 쿼리를 직접 정의한다.

### 3. @Query, 값, DTO 조회하기

**값 하나만 조회**

```java
@Query("select m.username from Member m")
List<String> findUsernameList()
```

**DTO로 직접 조회**

```java
@Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) " +
    "from Member m join m.team t")
List<MemberDto> findMemberDto()
```

**IN 조건도 바인딩 가능**

```java
@Query("select m from Member m where m.username in :names")
List<Member> findByNames(@Param("names") List<String> names)
```

**유연한 반환 타입 사용가능**

```java
List<Member> findByUsername(String name); //컬렉션
Member findByUsername(String name); //단건
Optional<Member> findByUsername(String name); //단건 Optiona
```

컬렉션 

1) 결과 없음: 빈 컬렉션 반환

단건 

1) 조회결과 없음: null 반환
2) 결과가 2건 이상: javax.persistence.NonUniqueResultException 예외 발생

### 4. 페이징과 정렬

 

**순수 JPA**

```java
public List<Member> findByPage(int age, int offset, int limit) {
 return em.createQuery("select m from Member m where m.age = :age order by 
    m.username desc")
     .setParameter("age", age)
     .setFirstResult(offset)
     .setMaxResults(limit)
     .getResultList();
}
public long totalCount(int age) {
 return em.createQuery("select count(m) from Member m where m.age = :age",
    Long.class)
     .setParameter("age", age)
     .getSingleResult();
}
```

→ setFirstResult, setMaxResult 를 통해서 페이징 처리를 진행

→ 페이징 시 필요한 totalCount 도 계산 필요

```java
public interface MemberRepository extends Repository<Member, Long> {
      Page<Member> findByAge(int age, Pageable pageable);
}

~~~~~
// 사용 예제 실행 코드
//페이징 조건과 정렬 조건 설정
@Test
public void page() throws Exception {
 //given
 memberRepository.save(new Member("member1", 10));
 memberRepository.save(new Member("member2", 10));
 memberRepository.save(new Member("member3", 10));
 memberRepository.save(new Member("member4", 10));
 memberRepository.save(new Member("member5", 10));
 //when
 PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC,
"username"));
 Page<Member> page = memberRepository.findByAge(10, pageRequest);
 //then
 List<Member> content = page.getContent(); //조회된 데이터
 assertThat(content.size()).isEqualTo(3); //조회된 데이터 수
 assertThat(page.getTotalElements()).isEqualTo(5); //전체 데이터 수
 assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
 assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호
 assertThat(page.isFirst()).isTrue(); //첫번째 항목인가?
 assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?
}
```

→ 페이징 객체를 넣어서 다양한 값을 가져올 수 있음

→ slice 객체를 사용하면 totalCount 를 구해오지 않음(앱에서 종종 사용하는 페이징 방식)

**참고: count 쿼리를 다음과 같이 분리할 수 있음**

```java
@Query(value = “select m from Member m”,
 countQuery = “select count(m.username) from Member m”)
Page<Member> findMemberAllCountBy(Pageable pageable);
```

**페이지를 유지하면서 엔티티를 DTO로 변환하기**

```java
Page<Member> page = memberRepository.findByAge(10, pageRequest);
Page<MemberDto> dtoPage = page.map(m -> new MemberDto());
```

### 5. 벌크성 수정 쿼리

**순수JPA**

```java
public int bulkAgePlus(int age) {
 int resultCount = em.createQuery(
 "update Member m set m.age = m.age + 1" +
 "where m.age >= :age")
 .setParameter("age", age)
 .executeUpdate();
 return resultCount;
}
```

**JPA DATA**

```java
@Modifying
@Query("update Member m set m.age = m.age + 1 where m.age >= :age")
int bulkAgePlus(@Param("age") int age);
```

**테스트코드**

```java
@Test
public void bulkUpdate() throws Exception {
 //given
 memberRepository.save(new Member("member1", 10));
 memberRepository.save(new Member("member2", 19));
 memberRepository.save(new Member("member3", 20));
 memberRepository.save(new Member("member4", 21));
 memberRepository.save(new Member("member5", 40));
 //when
 int resultCount = memberRepository.bulkAgePlus(20);
 //then
 assertThat(resultCount).isEqualTo(3);
}
```

- **영속성 컨테이너와 db와의 관계**

→ 일반적으로 업데이트를 진행할때 변경감지를 통해 진행하나 여러건의 데이터를 업데이트할때는 벌크성을 사용

```java
@Test
public void bulkUpdate() throws Exception {
 //given
 memberRepository.save(new Member("member1", 10));
 memberRepository.save(new Member("member2", 19));
 memberRepository.save(new Member("member3", 20));
 memberRepository.save(new Member("member4", 21));
 memberRepository.save(new Member("member5", 40));

 Member member = memberRepository.findByName("member3");

 log.info("member3_1 : "+member.getAge());
 int resultCount = memberRepository.bulkAgePlus(20);
 
 log.info("member3_2 : "+member.getAge());
}
```

해당 경우에는 
3_1 = 20  
3-2 = 20 

이 나타나게 된다. db  상에서는 업데이트가 되서 21인 상태이지만 영속성 초기화가 되지 않기 때문에 그대로 20으로 나타나게 된다. 

해당 경우에는 em.clear() 로 영속성을 초기화 해줄 경우 3_2 = 21로 나타나게 된다.

### 6. EntityGraph

에제 소스

```java
@Test
public void findMemberLazy() throws Exception {
 //given
     //member1 -> teamA
     //member2 -> teamB
     Team teamA = new Team("teamA");
     Team teamB = new Team("teamB");
     teamRepository.save(teamA);
     teamRepository.save(teamB);
     memberRepository.save(new Member("member1", 10, teamA));
     memberRepository.save(new Member("member2", 20, teamB));
     em.flush();
     em.clear();
 //when
     List<Member> members = memberRepository.findAll();
 //then
 for (Member member : members) {
     member.getTeam().getName();
 }
}
```

→ 지연로딩으로 인해서 쿼리가 3번 실행됨

→ 해결 방법으로 FETCH JOIN 처리 가능

```java
@Query("select m from Member m left join fetch m.team")
 List<Member> findMemberFetchJoin();
```

→ 해당 방식으로 쿼리를 한번 실행하게 튜닝 가능

```java
@Override
@EntityGraph(attributePaths = {"team"})
    List<Member> findAll();
    //JPQL + 엔티티 그래프
@EntityGraph(attributePaths = {"team"})
@Query("select m from Member m")
    List<Member> findMemberEntityGraph();
//메서드 이름으로 쿼리에서 특히 편리하다.
@EntityGraph(attributePaths = {"team"})
     List<Member> findByUsername(String username)
```

→ @EntityGraph를 통해서 Member와 Team을 연관 시켜서 한번에 가져오게 처리할 수 있다.

**NamedEntityGraph 사용 방법**

```java
@NamedEntityGraph(name = "Member.all", attributeNodes =
@NamedAttributeNode("team"))
@Entity
public class Member {}
```

```java
@EntityGraph("Member.all")
@Query("select m from Member m")
List<Member> findMemberEntityGraph();
```

→ 해당 방식으로 alias 처리 해서 사용 가능

### 7. JPA Hint & Lock

```java
@QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value =
"true"))
Member findReadOnlyByUsername(String username);
```

```java
@Test
public void queryHint() throws Exception {
 //given
 memberRepository.save(new Member("member1", 10));
 em.flush();
 em.clear();
 //when
 Member member = memberRepository.findReadOnlyByUsername("member1");
 member.setUsername("member2");
 em.flush(); //Update Query 실행X
}
```

→ 변경감지가 사용이 안됨(읽기 전용)

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
List<Member> findByUsername(String name);
```

→  SELECT FOR UPDATE 처럼 SELECT 하는 시점에서 잠가버림
