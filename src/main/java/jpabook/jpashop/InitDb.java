package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    /**
     * 스프링 부트 시작시점에 자동으로 실행하는 방법
     */
    @PostConstruct
    public void init(){
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService{
        private final EntityManager em;
        public void dbInit1(){
            Member member = createMember("userA","서울","332","53123");
            em.persist(member);

            Book book1 = createBook("JPA1 Book",200,10000);
            em.persist(book1);

            Book book2 = createBook("JPA2 Book",100,20000);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1,10000,1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2,20000,2);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);

        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }

        private Member createMember(String user, String city, String street, String zipcode) {
            Member member = new Member();
            member.setName(user);
            member.setAddress(new Address(city,street,zipcode));
            return member;
        }

        public void dbInit2(){
            Member member = createMember("userB","부산","54","2323");
            em.persist(member);

            Book book1 = createBook("Spring1 Book",100,20000);
            em.persist(book1);

            Book book2 = createBook("Spring2 Book",200,40000);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1,20000,3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2,40000,4);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);

        }

        private Book createBook(String name, int quantity, int price) {
            Book book = new Book();
            book.setName(name);
            book.setStockQuantity(quantity);
            book.setPrice(price);
            return book;
        }
    }

}
