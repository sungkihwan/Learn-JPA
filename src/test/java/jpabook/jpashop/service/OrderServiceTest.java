package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember();

        // 컨트롤 알트 M으로 만들기
        Book book = createBook("음머어",10000,20);

        int orderCount = 2;
        //when
        Long orderId = orderService.order(member.getId(),book.getId(),orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("상품 주문시 상태는 ORDER",OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품 종류수가 정확해야 한다.",1, getOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량이다.",10000 * orderCount, getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야 한다.",18, book.getStockQuantity());
    }

    @Test
    public void 상품주문_재고수량초과() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook("JPA",10000,10);

        int orderCount = 12;

        //when

        //then
        NotEnoughStockException thrown = assertThrows(NotEnoughStockException.class, () -> {
            orderService.order(member.getId(),item.getId(),orderCount);
        });
        assertEquals("재고가 없어용", "Need more stock", thrown.getMessage());
    }

    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook("kkeke",10000,15);

        int orderCount = 5;
        //when
        Long orderId = orderService.order(member.getId(),item.getId(),orderCount);
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("주문 취소시 상태는 CANCEL",OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("주문 취소만큼 재고수량이 늘어야 한다.",15,item.getStockQuantity());

    }

    private Book createBook(String name, int price, int quantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(quantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("케케우");
        member.setAddress(new Address("서울","관악로","10243"));
        em.persist(member);
        return member;
    }
}