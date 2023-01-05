# 2023.01.05

**ENTITY 설계**

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/8ea37f67-7ed7-4bd2-911b-29092393a64b/Untitled.png)

JPA DATA란? 

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/75da3116-dc52-4d76-a3a4-27dd8f5e9359/Untitled.png)

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

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/38c4b0b9-5bd2-4bab-bd47-c596dcd93b8d/Untitled.png)
