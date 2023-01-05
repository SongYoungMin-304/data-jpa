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

